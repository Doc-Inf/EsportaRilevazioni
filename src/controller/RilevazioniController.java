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
	
	private String hostname;
	private int port;
	private String projectDir;
	private RilevazioniParserTXT parser;
	private boolean https;
	private boolean filterByData;
	private LocalDateTime startDate;
	
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
				log("Rilevazioni Controller Constructor Error - Porta inserita non valida" );
				throw new RuntimeException("Porta non valida, al momento è supportato solo l'https (porta 443) e l'http (porta 80)");
			}
		}
		if(!filterByData) {
			log("Rilevazioni controller - Controller Rilevazioni creato per il file: " + fileRilevazioni);
		}
	}
	
	public RilevazioniController(String hostname, int port, String projectDir, String fileRilevazioni, LocalDateTime startDate) {
		this(hostname,port,projectDir,fileRilevazioni);
		this.startDate = startDate;
		this.filterByData = true;
		log("Rilevazioni controller - Il controller Rilevazioni creato prevede un filtro con data iniziale da cui partire: " + startDate + " fornita in formato LocalDateTime");
		
	}
	
	public RilevazioniController(String hostname, int port, String projectDir, String fileRilevazioni, String startDate) {
		this(hostname,port,projectDir,fileRilevazioni);
		this.startDate = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") );		
		this.filterByData = true;
		log("Rilevazioni controller - Il controller Rilevazioni creato prevede un filtro con data iniziale da cui partire:  " + startDate + " fornita in formato Stringa");
	}	
	
	
	@Override
	public void run() {
		log("Rilevazioni controller - Controller Rilevazioni in esecuzione...");		
		List<Rilevazione> rilevazioni;
		if(filterByData) {
			if(startDate==null) {
				LocalDateTime lastDate = getLastDate();				
				rilevazioni = parser.parseFile(lastDate);
			}else {
				rilevazioni = parser.parseFile(startDate);
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
		String message = "";
		
		try{
			if(https) {
				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				s = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
			}else {
				s = new Socket(hostname,port);
			}			
			PrintWriter out = new PrintWriter(s.getOutputStream());
			
			Thread t = new Thread(
					()->{
						log("Rilevazioni Controller Response Listener - started");
						String innerMessage = "";
						try {
							BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
						
							String line = null;
							
							boolean stop = false;
							int status = 0;
							while(!stop) {
								line = in.readLine();
								if(line!=null && !line.trim().equals("")) {
									log("Rilevazioni Controller Response Listener - " + line);
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
							log("Rilevazioni Controller Response Listener - Thread Ended");
							
						} catch (IOException e) {
							innerMessage = e.getMessage();
							e.printStackTrace();
						} finally {
							if(innerMessage!= null && !innerMessage.equals("")) {
								log("Rilevazioni Controller Thread Response Listener method Exception : " + innerMessage);
							}
							innerMessage = "";
						}	
				
			});			
			t.start();
						
			LocalDateTime dateTimeRequest = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			
			out.println("POST /" + projectDir + "/ws/insert.php HTTP/1.1");
			out.println("HOST: " + hostname);
			out.println("Content-Type: text/html; charset=utf-8");
			//out.println("Auth: d3NEb2NlbnRlOkAjTWV0ZW9yaXRlMjM=");
			out.println("Cookie: DateTime=" + dateTimeRequest.format(formatter));	
			out.println("Auth: " + getAutenticationString(dateTimeRequest));			
			out.println("CONTENT-LENGTH: " + dati.length);
			out.println("Connection: close");
			out.println();
			out.println(sb.toString());
			out.println("\n");
			out.flush();
			
			
			StringBuilder lb = new StringBuilder();
			lb.append("Rilevazioni Controller - Push dei dati al Server\n");
			lb.append("Rilevazioni Controller - POST /" + projectDir + "/ws/insert.php HTTP/1.1\n");
			lb.append("Rilevazioni Controller -  HOST: " + hostname + "\n");
			lb.append("Rilevazioni Controller -  Content-Type: text/html; charset=utf-8\n");
			lb.append("Rilevazioni Controller -  Cookie: DateTime=" + dateTimeRequest.format(formatter) + "\n");
			lb.append("Rilevazioni Controller -  Auth: " + getAutenticationString(dateTimeRequest) + "\n");
			//lb.append("CLIENT: Auth: d3NEb2NlbnRlOkAjTWV0ZW9yaXRlMjM=\n");
			//lb.append("CLIENT: Authorization: Basic d3NEb2NlbnRlOkAjTWV0ZW9yaXRlMjM=\n");
			lb.append("Rilevazioni Controller -  CONTENT-LENGTH: " + dati.length + "\n");
			lb.append("Rilevazioni Controller -  Connection: close\n");
						
			boolean attesaUltimata = false;
			
			do {
				message = "";
				try {
					t.join();
					attesaUltimata = true;
				} catch (InterruptedException e) {
					message = e.getMessage();
					e.printStackTrace();
				} finally {
					if(message!= null && !message.equals("")) {
						log("Rilevazioni Controller pushData method Error - Attesa del listener interrotta\n Exception : " + message);
					}
					message = "";
				}	
			}while(!attesaUltimata);
			
			log("Rilevazioni controller - " +lb.toString());
			
		} catch (UnknownHostException e) {
			message = "Rilevazioni Controller pushData method Error - UnknownHostException\n" + e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			message = "Rilevazioni Controller pushData method Error - " + e.getMessage();
			e.printStackTrace();
		} finally {
			if(message!= null && !message.equals("")) {
				log(message);
			}
			message = "";
		}			
		

	}
	
	private LocalDateTime getLastDate() {
		LocalDateTime lastDate = null;
		String message = "";
		try {	
				Socket s;
				if(https) {
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					s = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
					log("Rilevazioni controller - Creato un ssl socket");
				}else {
					s = new Socket(hostname,port);
					log("Rilevazioni controller - Creato un socket normale");
				}	
				
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));				
				PrintWriter out = new PrintWriter(s.getOutputStream());
				
				StringBuilder sb = new StringBuilder();
				Object condition = new Object();
				StringBuilder result = new StringBuilder();
				Semaforo dataOttenuta = new Semaforo();
				log("Rilevazioni controller - ricerca data ultima rilevazione iniziata");
				new Thread(()->{
					String innerMessage = "";
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
						innerMessage = "Rilevazioni Controller getLastDate method Error - " + e.getMessage();
						e.printStackTrace();
					} finally {
						if(innerMessage!= null && !innerMessage.equals("")) {
							log(innerMessage);
						}
						innerMessage = "";
					}	
				}).start();				
								
				out.println("GET /" + projectDir + "/ws/getLastDate.php HTTP/1.1");
				out.println("HOST: " + hostname);
				out.println("Connection: close");
				out.println();
				out.flush();
				log("Rilevazioni Controller - richiesta inviata...");
				
				message = "";
				
				while(!dataOttenuta.isDone()) {
					synchronized(condition) {
						try {
							condition.wait();
						} catch (InterruptedException e) {
							message = "Rilevazioni Controller getLastDate method Error - " + e.getMessage();
							e.printStackTrace();
						} finally {
							if(message!= null && !message.equals("")) {
								log(message);
							}
							message = "";
						}	
					}
				}
				log("Risposta: " + sb.toString());
				try {
					log("Rilevazioni Controller - DATA DENTRO RESULT: " + result);
					lastDate = LocalDateTime.parse(result.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					log("Rilevazioni Controller - LAST DATE: " + lastDate);
					log("Rilevazioni Controller - Data ultima modifica: \n" + lastDate.toString());	
				}catch(Exception e) {
					message = "Rilevazioni Controller getLastDate method Error - " + e.getMessage();
					return null;
				}finally {
					if(message!= null && !message.equals("")) {
						log(message);
					}
					message = "";
				}	
			
			
		} catch (UnknownHostException e) {
			message = "Rilevazioni Controller getLastDate method Error - " + e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			message = "Rilevazioni Controller getLastDate method Error - " + e.getMessage();
			e.printStackTrace();
		} finally {
			if(message!= null && !message.equals("")) {
				log(message);
			}
			message = "";
		}	
		
		return lastDate;
	}
	
	private String getAutenticationString(LocalDateTime d) {
		String result = null;
		String message = "";
		
		try {
			List<String> lines = Files.readAllLines(Paths.get("config.txt"));
			String pw = null;
			for(int i=0; i<lines.size(); ++i) {
				int j = lines.get(i).indexOf('=');
				if(lines.get(i).substring(0,j).trim().equalsIgnoreCase("auth")) {
					pw = lines.get(i).substring(j+1).trim();
				}
			}
			
			String oraPadded = null;
			String minutePadded = null;
			String secondPadded = null;
			if(d.getHour()<10) {
				oraPadded = "0" + d.getHour();
			}else {
				oraPadded = "" + d.getHour();
			}
			if(d.getMinute()<10) {
				minutePadded = "0" + d.getMinute();
			}else {
				minutePadded = "" + d.getMinute();
			}
			if(d.getSecond()<10) {
				secondPadded = "0" + d.getSecond();
			}else {
				secondPadded = "" + d.getSecond();
			}
			pw += "" + d.getYear() + d.getMonthValue() + d.getDayOfMonth() + oraPadded +minutePadded + secondPadded;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(pw.getBytes(StandardCharsets.UTF_8));
			result = Base64.getEncoder().encodeToString(hash);
		} catch (IOException | NoSuchAlgorithmException e) {
			log("Rilevazioni Controller getAutenticationString method Error - " + e.getMessage() );
			e.printStackTrace();
		}  finally {
			if(message!= null && !message.equals("")) {
				log(message);
			}
			message = "";
		}	
		return result;
	}
}
