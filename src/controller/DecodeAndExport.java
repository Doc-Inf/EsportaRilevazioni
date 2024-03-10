package controller;

import static view.AppLogger.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class DecodeAndExport implements Runnable{
	
	private String hostname;
	private int port;
	private String projectDir;
	private String dirRilevazioni;
	
	public DecodeAndExport(String hostname, int port, String projectDir, String dirRilevazioni) {
		this.hostname = hostname;
		this.port = port;
		this.projectDir = projectDir;
		this.dirRilevazioni = dirRilevazioni;
		log("Decode and Export Thread - Thread creato, ma non ancora in esecuzione");
	}
	
	
	@Override
	public void run() {
		log("Decode and Export Thread - Thread in esecuzione ");
		LocalDateTime lastDate = null;
		switch(port) {
		case 443:{
			lastDate = getLastDate(true);		
			break;
		}
		case 80:{
			lastDate = getLastDate(false);		
			break;
		}
		default:{
			log("Decode and Export Constructor Error - Porta specificata non riconosciuta" );
			throw new RuntimeException("Porta specificata non riconosciuta, sono valide solo la 443 e la porta 80");
		}
				
		}
		if(dirRilevazioni !=null) {
			log("Decode and Export Thread - La directory con le rilevazioni è " + dirRilevazioni);
			if( lastDate != null) {
				int yearLastRilevazione = lastDate.getYear();
				int monthLastRilevazione = lastDate.getMonthValue();
				LocalDate now = LocalDate.now();
				int currentYear = now.getYear();
				int currentMonth = now.getMonthValue();
				
				for(int i=yearLastRilevazione; i<=currentYear; ++i) {
					if(yearLastRilevazione==currentYear) {
						for(int  j=monthLastRilevazione; j<=currentMonth; ++j) {
							String month;
							if(j<10) {
								month = "0" + j;
							}else {
								month = "" + j;
							}
							String wlkFilePath = dirRilevazioni + "/" + i + "-" + month + ".wlk";
							String decodedFilename = i + "-" + month + ".txt";
							WDAT5_Decoder decoder = new WDAT5_Decoder();
							decoder.decode(wlkFilePath,decodedFilename);
							Thread t = new Thread( new RilevazioniController(hostname,port,projectDir,decodedFilename,lastDate) );
							t.start();
							boolean attesaUltimata = false;
							do {
								try {
									t.join();
									attesaUltimata = true;
								} catch (InterruptedException e) {
									log("Decode and Export Run method Error - " + e.getMessage() );
									e.printStackTrace();
								}
							}while(!attesaUltimata);
							log("Decode and Export Thread - Attesa Thread Rilevazioni Controller per il file " + decodedFilename + " ultimata");
						}
					}else {
						if(i < currentYear) {
							for(int  j=monthLastRilevazione; j<=12; ++j) {
								String month;
								if(j<10) {
									month = "0" + j;
								}else {
									month = "" + j;
								}
								String wlkFilePath = dirRilevazioni + "/" + i + "-" + month + ".wlk";
								String decodedFilename = i + "-" + month + ".txt";
								WDAT5_Decoder decoder = new WDAT5_Decoder();
								decoder.decode(wlkFilePath,decodedFilename);
								Thread t = new Thread( new RilevazioniController(hostname,port,projectDir,decodedFilename,lastDate) );
								t.start();
								boolean attesaUltimata = false;
								do {
									try {
										t.join();
										attesaUltimata = true;
									} catch (InterruptedException e) {
										log("Decode and Export Run method Error - " + e.getMessage() );
										e.printStackTrace();
									}
								}while(!attesaUltimata);
								log("Decode and Export Thread - Attesa Thread Rilevazioni Controller per il file " + decodedFilename + " ultimata");
							}
						}else {
							for(int  j=1; j<=currentMonth; ++j) {
								String month;
								if(j<10) {
									month = "0" + j;
								}else {
									month = "" + j;
								}
								String wlkFilePath = dirRilevazioni + "/" + i + "-" + month + ".wlk";
								String decodedFilename = i + "-" + month + ".txt";
								WDAT5_Decoder decoder = new WDAT5_Decoder();
								decoder.decode(wlkFilePath,decodedFilename);
								Thread t = new Thread( new RilevazioniController(hostname,port,projectDir,decodedFilename,lastDate) );
								t.start();
								boolean attesaUltimata = false;
								do {
									try {
										t.join();
										attesaUltimata = true;
									} catch (InterruptedException e) {
										log("Decode and Export Run method Error - " + e.getMessage() );
										e.printStackTrace();
									}
								}while(!attesaUltimata);
								log("Decode and Export Thread - Attesa Thread Rilevazioni Controller per il file " + decodedFilename + " ultimata");
							}
						}
					}
					
				}
			}else {
				Path dir = Paths.get(dirRilevazioni);
				if(!Files.exists(dir)) {
					if(Files.notExists(dir)) {
						log("Decode and Export Thread - La directory specificata non esiste");
						throw new RuntimeException("La directory specificata non esiste");
					}else {
						log("Decode and Export Thread - La directory specificata non è accessibile");
						throw new RuntimeException("La directory specificata non è accessibile");
					}
				}
				try {
					
					List<String> fileWLK = Files.walk(dir).filter(file->!Files.isDirectory(file)).map(file->file.getFileName().toString()).filter(filename->{
						String[] fileParts = filename.split("\\.");
						if(fileParts.length>1 && fileParts[1].equalsIgnoreCase("wlk")) {
							return true;
						}
						return false;
					}).toList();		
					log("Decode and Export Thread - Lista file wlk trovati:");
					fileWLK.forEach(s->log("Decode and Export Thread - " + s));
					
					log("Decode and Export Thread - Numero di file wlk trovati nella directory: " + fileWLK.size());
					for(int i=0; i<fileWLK.size(); ++i) {
						String date = fileWLK.get(i).split("\\.")[0];
						int year = Integer.parseInt( date.split("-")[0] );
						int month = Integer.parseInt( date.split("-")[1] );
						
						String wlkFilePath = dirRilevazioni + FileSystems.getDefault().getSeparator() + fileWLK.get(i);
						String decodedFilename = year + "-" + month + ".txt";
						WDAT5_Decoder decoder = new WDAT5_Decoder();
						decoder.decode(wlkFilePath,decodedFilename);
						Thread t = new Thread( new RilevazioniController(hostname,port,projectDir,decodedFilename) );
						t.start();
						boolean attesaUltimata = false;
						do {
							try {
								t.join();
								attesaUltimata = true;
							} catch (InterruptedException e) {
								log("Decode and Export Run method Error - " + e.getMessage() );
								e.printStackTrace();
							}
						}while(!attesaUltimata);
						log("Decode and Export Thread - Attesa Thread Rilevazioni Controlloer " + i + " ultimata");
					}					
					
				} catch (IOException e) {
					log("Decode and Export Run method Error - " + e.getMessage() );
					e.printStackTrace();
				}
				
			}
		}else {
			log("Decode and Export - Dir rilevazioni è NULL!");
			throw new RuntimeException("dirRilevazioni è null!");
		}
		
		
		/*
		DirectoryScanner dirScanner = new DirectoryScanner();
		String directory = dirScanner.loadDirName();
		if(directory == null) {
			log("");
			return ;
		}
		log("La directory è " + directory);
		String lastWlkFile = dirScanner.getLastDataFile(directory);
		log("L'ultimo file wlk è " + lastWlkFile);
		String decodedFilename = getDecodedFilename(lastWlkFile);
		
		WDAT5_Decoder decoder = new WDAT5_Decoder();
		decoder.decode(lastWlkFile,decodedFilename);
				
		new Thread( new RilevazioniController(hostname,port,projectDir,decodedFilename,true) ).start();*/
	}
	
	
	private LocalDateTime getLastDate(boolean https) {
		LocalDateTime lastDate = null;
		try {	
				Socket s;
				if(https) {
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					s = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
					log("Decode and Export Thread - Get Last Date: Creato un ssl socket");
				}else {
					s = new Socket(hostname,port);
					log("Decode and Export Thread - Get Last Date: Creato un socket normale");
				}	
				
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));				
				PrintWriter out = new PrintWriter(s.getOutputStream());
				
				StringBuilder sb = new StringBuilder();
				Object condition = new Object();
				StringBuilder result = new StringBuilder();
				Semaforo dataOttenuta = new Semaforo();
				log("Decode and Export Thread - ricerca data ultima rilevazione iniziata");
				new Thread(()->{
					try {
						log("Decode and Export -> ListenerThread - in attesa di risposta...");
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
										log("Decode and Export -> ListenerThread - Data letta: " + result.toString());										
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
						log("Decode and Export getLastDate method Error - " + e.getMessage() );
						e.printStackTrace();
					}
				}).start();				
								
				out.println("GET /" + projectDir + "/ws/getLastDate.php HTTP/1.1");
				out.println("HOST: " + hostname);
				out.println("Connection: close");
				out.println();
				out.flush();
				log("Decode and Export Thread - richiesta inviata...");
				while(!dataOttenuta.isDone()) {
					synchronized(condition) {
						try {
							condition.wait();
						} catch (InterruptedException e) {
							log("Decode and Export getLastDate method Error - " + e.getMessage() );
							e.printStackTrace();
						}
					}
				}
				log("Decode and Export Thread - Risposta: " + sb.toString());
				try {
					//log("Decode and Export Thread - DATA DENTRO RESULT: " + result);
					lastDate = LocalDateTime.parse(result.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					log("Decode and Export Thread - Data ultima modifica: " + lastDate.toString());	
				}catch(Exception e) {
					log("Decode and Export getLastDate method Error - " + e.getMessage() );
					return null;
				}
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return lastDate;
	}

}
