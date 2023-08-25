package controller;

import static view.AppLogger.log;

public class DecodeAndExport implements Runnable{
	
	private String hostname;
	private int port;
	private String projectDir;
	
	public DecodeAndExport(String hostname, int port, String projectDir) {
		this.hostname = hostname;
		this.port = port;
		this.projectDir = projectDir;
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
		String decodedFilename = getDecodedFilename(lastWlkFile);
		
		WDAT5_Decoder decoder = new WDAT5_Decoder();
		decoder.decode(lastWlkFile,decodedFilename);
				
		new Thread( new RilevazioniController(hostname,port,projectDir,decodedFilename,true) ).start();
	}
	
	private String getDecodedFilename(String sourceFilename) {		
		int index = sourceFilename.lastIndexOf("/");
		if(index == -1) {
			index = sourceFilename.lastIndexOf("\\");
		}
		String decodedFilename = sourceFilename.substring(index+1);
		decodedFilename = decodedFilename.split("\\.")[0] + ".txt";
		return decodedFilename;
	}

}
