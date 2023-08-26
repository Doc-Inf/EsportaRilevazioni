package test;

import controller.DecodeAndExport;

import static view.AppLogger.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EsportaRilevazioni {

	public static void main(String[] args) {
		
		String configFilename = "config.txt";
		String hostname = null;
		int port = -1;
		String projectDir = null;		
		
		log("Questa applicazione decodificherà ed esporterà i dati della stazione");
		String richiesta = read("Digitare:\n"
				+ "ENTER oppure 1 per caricare le impostazioni di default (Host remoto www.itisvallauri.net)\n"
				+ "2 per lavorare in localhost\n"
				+ "3 per configurare manualmente i parametri\n");
		boolean terminate = false;
		do {
			switch(richiesta.trim().toLowerCase()) {
			case "":
			case "1":{
				terminate = true;
				try {
					List<String> lines = Files.readAllLines(Paths.get(configFilename));
					int numInfo = 0;
					String line = null;
					String[] data = null; 
					String info = null;
					for(int i=0; i<lines.size(); i++) {
						line = lines.get(i);
						data = line.split("=");
						info = data[0];
						switch(info.trim()) {
						case "remoteHostname":{
							hostname = data[1];
							++numInfo;
							if(numInfo==3) {
								break;
							}
							break;
						}
						case "remotePort":{
							port = Integer.parseInt( data[1] );
							++numInfo;
							if(numInfo==3) {
								break;
							}
							break;
						}
						case "remoteProjectDir":{
							projectDir = data[1];
							++numInfo;
							if(numInfo==3) {
								break;
							}
							break;
						}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				break;
			}
			case "2":{
				terminate = true;
				hostname = "localhost";
				int numInfo = 0;
				String line = null;
				String[] data = null; 
				String info = null;
				List<String> lines;
				try {
					lines = Files.readAllLines(Paths.get(configFilename));
					for(int i=0; i<lines.size(); i++) {
						line = lines.get(i);
						data = line.split("=");
						info = data[0];
						switch(info.trim()) {
						case "localhostDir":{
							projectDir = data[1];
							++numInfo;
							if(numInfo==2) {
								break;
							}
							break;
						}
						case "localhostPort":{
							port = Integer.parseInt( data[1] );
							++numInfo;
							if(numInfo==2) {
								break;
							}
							break;
						}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}					
				break;
			}
			case "3":{
				terminate = true;
				hostname = read("Inserire l'hostname");
				port = readInt("Inserire la porta del server");
				projectDir = read("Inserire il path da richiedere al server");
				break;
			}
			default:{
				log("Comando inserito errato...\n");
			}
			}
		}while(!terminate);	
		
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new DecodeAndExport(hostname,port,projectDir), 0, 5, TimeUnit.MINUTES);
		
		terminate = false;
		
		do {
			richiesta = read("L'applicazione ripeterà il proprio lavoro di decodifica ed export dei dati ad intervalli periodici, digitare exit per terminarla");
			if(richiesta.trim().equalsIgnoreCase("exit")) {
				terminate = true;
				executor.shutdown();
				log("Richiesta di terminazione ricevuta con successo, l'applicazione completerà le operazioni in corso e poi si arresterà. Buona giornata!");
			}
		}while(!terminate);
		
	}

}
