package test;

import controller.DecodeAndExport;

import static view.AppLogger.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EsportaRilevazioni {

	public static void main(String[] args) {
		
		String configFilename = "config.txt";
		
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
				
				break;
			}
			case "2":{
				terminate = true;
				break;
			}
			case "3":{
				terminate = true;
				break;
			}
			default:{
				log("Comando inserito errato...\n");
			}
			}
		}while(!terminate);	
		
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new DecodeAndExport(), 0, 5, TimeUnit.MINUTES);
		
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
