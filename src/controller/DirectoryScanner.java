package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static view.AppLogger.*;

public class DirectoryScanner {
	
	public String loadDirName() {
		try(BufferedReader in = new BufferedReader(new FileReader("config.txt"))){
			String line = null;
			String prop =  null;			
			while( (line = in.readLine()) != null) {
				prop = line.split("=")[0];
				if(prop.equalsIgnoreCase("dirRilevazioni")) {					
					return line.split("=")[1];
				}
			}			
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getLastDataFile(String directory) {
		Path dir = Paths.get(directory);
		if(!Files.exists(dir)) {
			if(Files.notExists(dir)) {
				log("La directory specificata non esiste");
				throw new RuntimeException("La directory specificata non esiste");
			}else {
				log("La directory specificata non è accessibile");
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
			log("Lista file wlk trovati:");
			fileWLK.forEach(s->log(s));
			int maxYear = -1, maxMonth = -1;
			log("Numero di file wlk trovati nella directory: " + fileWLK.size());
			for(int i=0; i<fileWLK.size(); ++i) {
				String date = fileWLK.get(i).split("\\.")[0];
				int year = Integer.parseInt( date.split("-")[0] );
				int month = Integer.parseInt( date.split("-")[1] );
				
				if(i==0 || maxYear < year) {
					maxYear = year;
					maxMonth = month;
				}else {
					if(maxYear == year && maxMonth < month) {
						maxMonth = month;
					}					
				}
			}
			
			if( maxYear != -1 ) {
				return directory + FileSystems.getDefault().getSeparator() + maxYear + "-" + String.format("%02d", maxMonth) + ".wlk";
			}else {
				return null;
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
