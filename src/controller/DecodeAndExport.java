package controller;

import static view.AppLogger.log;

public class DecodeAndExport implements Runnable{
	
	public DecodeAndExport() {
		
	}
	
	
	@Override
	public void run() {
		DirectoryScanner dirScanner = new DirectoryScanner();
		String directory = dirScanner.loadDirName();
		if(directory == null) {
			log("");
			return ;
		}
		log("La directory è " + directory);
		String lastWlkFile = dirScanner.getLastDataFile(directory);
		log("L'ultimo file wlk è " + lastWlkFile);
		
		WDAT5_Decoder decoder = new WDAT5_Decoder();
		decoder.decode(lastWlkFile);
		
	}

}
