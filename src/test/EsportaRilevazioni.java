package test;

import controller.DecodeAndExport;
import controller.RilevazioniController;
import controller.WDAT5_Decoder;

import static view.AppLogger.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EsportaRilevazioni {
	
	private static String configFilename = "config.txt";
	private static String hostname = null;
	private static int port = -1;
	private static String projectDir = null;
	private static String startDate = null;
	private static String filename = null;

	public static void main(String[] args) {		
		
		log("Questa applicazione decodificherà ed esporterà i dati della stazione");
		String richiesta = read("Digitare:\n"
				+ "ENTER oppure 1 per caricare le impostazioni di default (Host remoto www.itisvallauri.net)\n"
				+ "2 per lavorare in localhost\n"
				+ "3 per configurare manualmente i parametri\n"
				+ "4 per inviare i dati di un file wdat scelto, selezionando solo le rilevazione oltre una specifica data\n");
		boolean terminate = false;
		do {
			switch(richiesta.trim().toLowerCase()) {
			case "":
			case "1":{
				terminate = true;
				loadRemoteConfig();				
				break;
			}
			case "2":{
				terminate = true;
				loadLocalhostConfig();
				break;
			}
			case "3":{
				terminate = true;
				loadUserConfig(false,false);				
				break;
			}
			case "4":{
				boolean end = false;
				do {
					String serverDestinazione = read("Inserire:\n1) Per inviare i dati verso il server remoto di default\n"
							+ "2) Per inviare i dati al server in locale di default\n"
							+ "3) Per specificare host e porta\n\n");
					switch(serverDestinazione) {
					case "1":{
						end = true;
						loadRemoteConfig();
						filename = read("Inserire il nome del file wdat da cui prendere i dati");
						startDate = read("Inserire la data di partenza da cui inviare le rilevazioni\n(nel formato:anno-mese-giorno ore:minuti:secondi, considerando sia il mese, il giorno, ore, minuti e secondi formati da due caratteri, l'anno da 4)");
						String decodedFilename = getDecodedFilename(filename);
						
						WDAT5_Decoder decoder = new WDAT5_Decoder();
						decoder.decode(filename,decodedFilename);
						new Thread( new RilevazioniController(hostname,port,projectDir,decodedFilename,startDate) ).start();
						break;
					}
					case "2":{
						end = true;
						loadLocalhostConfig();
						break;
					}
					case "3":{
						end = true;
						loadUserConfig(true,true);
						String decodedFilename = getDecodedFilename(filename);
						
						WDAT5_Decoder decoder = new WDAT5_Decoder();
						decoder.decode(filename,decodedFilename);
						new Thread( new RilevazioniController(hostname,port,projectDir,decodedFilename,startDate) ).start();					
						break;
					}
					default:{
						log("Comando inserito errato...\n\n");
					}
					}
				}while(!end);
				break;
			}
			default:{
				log("Comando inserito errato...\n\n");
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
	
	private static void loadRemoteConfig() {
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
	}

	
	private static void loadLocalhostConfig() {
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
	}
	
	private static void loadUserConfig(boolean getFilename, boolean getStartDate) {		
		hostname = read("Inserire l'hostname");
		port = readInt("Inserire la porta del server");
		projectDir = read("Inserire il path da richiedere al server");
		if(getFilename) {
			filename = read("Inserire il nome del file wdat da cui prendere i dati");
		}
		if(getStartDate) {
			startDate = read("Inserire la data di partenza da cui inviare le rilevazioni (nel formato:anno-mese-giorno ore:minuti:secondi, considerando sia il mese, il giorno, ore, minuti e secondi formati da due caratteri, l'anno da 4)");
		}		
	}
	
	private static String getDecodedFilename(String sourceFilename) {		
		int index = sourceFilename.lastIndexOf("/");
		if(index == -1) {
			index = sourceFilename.lastIndexOf("\\");
		}
		String decodedFilename = sourceFilename.substring(index+1);
		decodedFilename = decodedFilename.split("\\.")[0] + ".txt";
		return decodedFilename;
	}
}
