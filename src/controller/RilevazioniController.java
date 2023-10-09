package controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;

import model.Rilevazione;
import static view.AppLogger.*;

public class RilevazioniController implements Runnable{
	
	private static final String DEFAULT_FILENAME = "./misurazioni/rilevazioni.txt";
	private String hostname;
	private int port;
	private String projectDir;
	private RilevazioniParserTXT parser;
	private boolean https;
	private boolean filterByData;
	private String startDate;
	
	public RilevazioniController(String hostname, int port, String projectDir, String fileRilevazioni) {
		this.hostname = hostname;
		this.port = port;
		this.projectDir = projectDir;
		this.parser = new RilevazioniParserTXT(fileRilevazioni);
		if(port == 443) {
			this.https = true;
		}else {
			if(port == 80) {
				this.https = false;
			}else {
				throw new RuntimeException("Porta non valida, al momento è supportato solo l'https (porta 443) e l'http (porta 80)");
			}
		}
		
	}
	
	public RilevazioniController(String hostname, int port, String projectDir, String fileRilevazioni, boolean filterByData) {
		this(hostname,port,projectDir,fileRilevazioni);
		this.filterByData = filterByData;
	}
	
	public RilevazioniController(String hostname, int port, String projectDir, String fileRilevazioni, String startDate) {
		this(hostname,port,projectDir,fileRilevazioni);
		this.filterByData = true;
		this.startDate = startDate;
	}
	
	public RilevazioniController(String hostname, int port, String projectDir, boolean filterByData) {
		this(hostname,port, projectDir, DEFAULT_FILENAME,filterByData);
		this.hostname = hostname;		
	}
	
	public RilevazioniController(String hostname, int port, String projectDir) {
		this(hostname,port, projectDir, DEFAULT_FILENAME, true);
		log("Creazione controller avviata");			
	}
	
	@Override
	public void run() {
		log("Controller Rilevazioni in esecuzione...");		
		List<Rilevazione> rilevazioni;
		if(filterByData) {
			if(startDate==null) {
				LocalDateTime lastDate = getLastDate();				
				rilevazioni = parser.parseFile(lastDate);
			}else {
				rilevazioni = parser.parseFile(LocalDateTime.parse( startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			}			
		}else {
			rilevazioni = parser.parseFile();
		}
		//rilevazioni.stream().forEach(r->System.out.println(r));
		pushData(rilevazioni, https);
	}

	private void pushData(List<Rilevazione> rilevazioni,boolean https) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0;i<rilevazioni.size();++i) {
			if(i<rilevazioni.size()-1) {
				sb.append(rilevazioni.get(i)+",");
			}else {
				sb.append(rilevazioni.get(i));
			}
		}
		
		sb.append("]");
		log(sb.toString());
		byte[] dati = sb.toString().getBytes();
		final Socket s;
		
		try{
			if(https) {
				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				s = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
			}else {
				s = new Socket(hostname,port);
			}			
			PrintWriter out = new PrintWriter(s.getOutputStream());
			
			new Thread(
					()->{
						try {
							BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
						
							String line = null;
							
							boolean stop = false;
							int status = 0;
							while(!stop) {
								line = in.readLine();
								if(line!=null && !line.trim().equals("")) {
									log("SERVER: " + line);
									if(line.matches("[Cc][Oo][Nn][Tt][Ee][Nn][Tt]-[Ll][Ee][Nn][Gg][Tt][Hh].*")) {
										int sep = line.indexOf(':');
										int numByte = Integer.parseInt( (line.trim()).substring(sep+2) );
										if(numByte>0) {
											status=2;
										}else {
											if(status==0) {
												status=1;
											}											
										}										
									}else {
										if(status==0) {
											status++;
										}										
									}									
								}else {
									if(status==1) {
										stop=true;
									}else {
										if(status==2) {
											status++;
										}else {
											if(status>=3) {
												stop=true;
											}
										}
									}
								}
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						}
				
			}).start();
			
			out.println("POST /" + projectDir + "/ws/insert.php HTTP/1.1");
			out.println("HOST: " + hostname);
			out.println("Content-Type: text/html; charset=utf-8");
			//out.println("Auth: d3NEb2NlbnRlOkAjTWV0ZW9yaXRlMjM=");
			out.println("Auth: " + getAutenticationString());
			out.println("CONTENT-LENGTH: " + dati.length);
			out.println("Connection: close");
			out.println();
			out.println(sb.toString());
			out.println("\n");
			out.flush();
			
			
			StringBuilder lb = new StringBuilder();
			lb.append("CLIENT: POST /" + projectDir + "/ws/insert.php HTTP/1.1\n");
			lb.append("CLIENT: HOST: " + hostname + "\n");
			lb.append("CLIENT: Content-Type: text/html; charset=utf-8\n");
			lb.append("CLIENT: Auth: " + getAutenticationString() + "\n");
			//lb.append("CLIENT: Auth: d3NEb2NlbnRlOkAjTWV0ZW9yaXRlMjM=\n");
			//lb.append("CLIENT: Authorization: Basic d3NEb2NlbnRlOkAjTWV0ZW9yaXRlMjM=\n");
			lb.append("CLIENT: CONTENT-LENGTH: " + dati.length + "\n");
			lb.append("CLIENT: Connection: close\n");
						
			log(lb.toString());
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 		
		

	}
	
	private LocalDateTime getLastDate() {
		LocalDateTime lastDate = null;
		try {	
				Socket s;
				if(https) {
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					s = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
					log("Creato un ssl socket");
				}else {
					s = new Socket(hostname,port);
					log("Creato un socket normale");
				}	
				
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));				
				PrintWriter out = new PrintWriter(s.getOutputStream());
				
				StringBuilder sb = new StringBuilder();
				Object condition = new Object();
				StringBuilder result = new StringBuilder();
				Semaforo dataOttenuta = new Semaforo();
				log("ricerca data ultima rilevazione iniziata");
				new Thread(()->{
					try {
						log("in attesa di risposta...");
						boolean done = false;
						int status = 0;
						String line = null;
						while(!done) {
							line = in.readLine();							
							switch(status) {
								case 0:{
									if(line!=null && !line.trim().equals("")) {
										++status;
										sb.append(line + "\n");										
									}
									break;
								}
								case 1:{
									if(line!=null && !line.trim().equals("")) {
										sb.append(line+ "\n");										
									}else{	
										sb.append("\n");
										++status;
									}	
									break;
								}
								case 2:{
									if(line!=null && !line.trim().equals("")) {
										sb.append(line+ "\n");
										if( !(line.trim().equals("13") || line.trim().equals("0")) ) {
											result.append(line);
										}
																				
									}else {		
										log("Data letta: " + result.toString());										
										sb.append("\n");
										++status;
										done=true;
										dataOttenuta.setDone(true);
									}
									break;
								}								
							}
												
						}
						
						synchronized(condition) {
							condition.notify();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}).start();				
								
				out.println("GET /" + projectDir + "/ws/getLastDate.php HTTP/1.1");
				out.println("HOST: " + hostname);
				out.println("Connection: close");
				out.println();
				out.flush();
				log("richiesta inviata...");
				while(!dataOttenuta.isDone()) {
					synchronized(condition) {
						try {
							condition.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				log("Risposta: " + sb.toString());
				try {
					log("DATA DENTRO RESULT: " + result);
					lastDate = LocalDateTime.parse(result.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					log("LAST DATE: " + lastDate);
					log("Data ultima modifica: \n" + lastDate.toString());	
				}catch(Exception e) {
					return null;
				}
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return lastDate;
	}
	
	private String getAutenticationString() {
		String result = null;
		try {
			List<String> lines = Files.readAllLines(Paths.get("config.txt"));
			String pw = null;
			for(int i=0; i<lines.size(); ++i) {
				int j = lines.get(i).indexOf('=');
				if(lines.get(i).substring(0,j).trim().equalsIgnoreCase("auth")) {
					pw = lines.get(i).substring(j+1).trim();
				}
			}
			LocalDateTime d = LocalDateTime.now();
			pw += "" + d.getYear() + d.getMonthValue() + d.getDayOfMonth() + d.getHour();
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(pw.getBytes(StandardCharsets.UTF_8));
			result = Base64.getEncoder().encodeToString(hash);
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
}
