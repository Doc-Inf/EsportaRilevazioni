package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.Rilevazione;

import static view.AppLogger.*;

public class RilevazioniParserTXT implements RilevazioniParser{

	private String filename;
		
	public RilevazioniParserTXT(String filename) {
		this.filename = filename; 
	}
	
	@Override
	public List<Rilevazione> parseFile() {
		log("Parser in esecuzione...");
		List<Rilevazione> rilevazioni = new ArrayList<>();
		String line = null;
		int lineNumber = 0;
		try(BufferedReader in = new BufferedReader( new FileReader(filename) );) {
			while( (line=in.readLine()) != null ) {
				if(lineNumber > 2) {
					rilevazioni.add(parseRilevazione(line));
				}		
				++lineNumber;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log("Parsing terminato");
		return rilevazioni;
	}
	
	@Override
	public List<Rilevazione> parseFile(LocalDateTime startDate) {
		log("Parser in esecuzione...");
		List<Rilevazione> rilevazioni = new ArrayList<>();
		String line = null;
		int lineNumber = 0;
		Rilevazione currentRilevazione = null;
		try(BufferedReader in = new BufferedReader( new FileReader(filename) );) {
			while( (line=in.readLine()) != null ) {
				if(lineNumber > 2) {
					currentRilevazione = parseRilevazione(line, startDate);
					if(currentRilevazione!=null) {
						rilevazioni.add(currentRilevazione);
					}	
				}		
				++lineNumber;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log("Parsing terminato");
		return rilevazioni;
	}
	
	private Rilevazione parseRilevazione(String line) {
		String[] dati = line.split("\\s+");
		LocalDate data = LocalDate.parse(dati[0], DateTimeFormatter.ofPattern("dd/MM/yy")) ;
		String[] time = dati[1].split(":");
		int ore = Integer.parseInt(time[0]);
		int minuti = Integer.parseInt(time[1]);
		LocalTime ora = LocalTime.of(ore, minuti);
		double tempOut = Double.parseDouble(dati[2]);
		double hiTemp = Double.parseDouble(dati[3]);
		double lowTemp = Double.parseDouble(dati[4]);
		int outHum = Integer.parseInt(dati[5]);
		double devPt = Double.parseDouble(dati[6]);
		double windSpeed = Double.parseDouble(dati[7]);
		String windDir = dati[8];
		double windRun = Double.parseDouble(dati[9]);
		double hiSpeed = Double.parseDouble(dati[10]);
		String hiDir = dati[11];
		double chillWind = Double.parseDouble(dati[12]);
		double heatIndex = Double.parseDouble(dati[13]);
		double thwIndex = Double.parseDouble(dati[14]);
		double bar = Double.parseDouble(dati[15]);
		double rain = Double.parseDouble(dati[16]);
		double rainRate = Double.parseDouble(dati[17]);
		double heatDD = Double.parseDouble(dati[18]);
		double coolDD = Double.parseDouble(dati[19]);
		double inTemp = Double.parseDouble(dati[20]);
		int inHum = Integer.parseInt(dati[21]);
		double inDew = Double.parseDouble(dati[22]);
		double inHeat = Double.parseDouble(dati[23]);
		double inEMC = Double.parseDouble(dati[24]);
		double inAirDensity = Double.parseDouble(dati[25]);
		int windSamp = Integer.parseInt(dati[30]);
		int windTx = Integer.parseInt(dati[31]);
		double issRecept = Double.parseDouble(dati[32]);
		int arcInt = Integer.parseInt(dati[33]);
		/*log(data + " "  +ora + " "  +tempOut + " "  +hiTemp + " "  +lowTemp + " "  +outHum + 
				" "  +devPt + " "  +windSpeed + " "  +windDir + " "  +windRun + " "  +hiSpeed + " "  +
				hiDir + " "  +chillWind + " "  +heatIndex + " "  +thwIndex + " "  +bar + " "  +rain + " "  +
				rainRate + " "  +heatDD + " "  +coolDD + " "  +inTemp + " "  +inHum + " "  +inDew + " "  +
				inHeat + " "  +inEMC + " "  +inAirDensity + " "  +windSamp + " "  +windTx + " "  +
				issRecept + " "  +arcInt);*/
		return new Rilevazione(data, ora, tempOut, hiTemp, lowTemp, outHum, devPt, windSpeed, windDir, 
				windRun, hiSpeed, hiDir, chillWind, heatIndex, thwIndex, bar, rain, rainRate, heatDD,
				coolDD, inTemp, inHum, inDew, inHeat, inEMC, inAirDensity, windSamp, windTx, issRecept,
				arcInt
				);
	}
	
	private Rilevazione parseRilevazione(String line, LocalDateTime startDate) {
		String[] dati = line.split("\\s+");
		LocalDate data = LocalDate.parse(dati[0], DateTimeFormatter.ofPattern("dd/MM/yy")) ;
		String[] time = dati[1].split(":");
		int ore = Integer.parseInt(time[0]);
		int minuti = Integer.parseInt(time[1]);
		LocalTime ora = LocalTime.of(ore, minuti);
		if(LocalDateTime.of(data.getYear(), data.getMonthValue(), data.getDayOfMonth(), ora.getHour(), ora.getMinute()).isAfter(startDate)) {
			double tempOut = Double.parseDouble(dati[2]);
			double hiTemp = Double.parseDouble(dati[3]);
			double lowTemp = Double.parseDouble(dati[4]);
			int outHum = Integer.parseInt(dati[5]);
			double devPt = Double.parseDouble(dati[6]);
			double windSpeed = Double.parseDouble(dati[7]);
			String windDir = dati[8];
			double windRun = Double.parseDouble(dati[9]);
			double hiSpeed = Double.parseDouble(dati[10]);
			String hiDir = dati[11];
			double chillWind = Double.parseDouble(dati[12]);
			double heatIndex = Double.parseDouble(dati[13]);
			double thwIndex = Double.parseDouble(dati[14]);
			double bar = Double.parseDouble(dati[15]);
			double rain = Double.parseDouble(dati[16]);
			double rainRate = Double.parseDouble(dati[17]);
			double heatDD = Double.parseDouble(dati[18]);
			double coolDD = Double.parseDouble(dati[19]);
			double inTemp = Double.parseDouble(dati[20]);
			int inHum = Integer.parseInt(dati[21]);
			double inDew = Double.parseDouble(dati[22]);
			double inHeat = Double.parseDouble(dati[23]);
			double inEMC = Double.parseDouble(dati[24]);
			double inAirDensity = Double.parseDouble(dati[25]);
			int windSamp = Integer.parseInt(dati[30]);
			int windTx = Integer.parseInt(dati[31]);
			double issRecept = Double.parseDouble(dati[32]);
			int arcInt = Integer.parseInt(dati[33]);
			/*log(data + " "  +ora + " "  +tempOut + " "  +hiTemp + " "  +lowTemp + " "  +outHum + 
					" "  +devPt + " "  +windSpeed + " "  +windDir + " "  +windRun + " "  +hiSpeed + " "  +
					hiDir + " "  +chillWind + " "  +heatIndex + " "  +thwIndex + " "  +bar + " "  +rain + " "  +
					rainRate + " "  +heatDD + " "  +coolDD + " "  +inTemp + " "  +inHum + " "  +inDew + " "  +
					inHeat + " "  +inEMC + " "  +inAirDensity + " "  +windSamp + " "  +windTx + " "  +
					issRecept + " "  +arcInt);*/
			return new Rilevazione(data, ora, tempOut, hiTemp, lowTemp, outHum, devPt, windSpeed, windDir, 
					windRun, hiSpeed, hiDir, chillWind, heatIndex, thwIndex, bar, rain, rainRate, heatDD,
					coolDD, inTemp, inHum, inDew, inHeat, inEMC, inAirDensity, windSamp, windTx, issRecept,
					arcInt
					);
		}else {
			return null;
		}
	}

}
