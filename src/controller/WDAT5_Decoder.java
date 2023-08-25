package controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.RecordInfo;

public class WDAT5_Decoder {	
	
	private List<RecordInfo> dataSurvey;
	
	public WDAT5_Decoder() {
		dataSurvey = new ArrayList<>();
		dataSurvey.add(new RecordInfo("signed char","dataType"));
		dataSurvey.add(new RecordInfo("signed char","archiveInterval"));
		dataSurvey.add(new RecordInfo("signed char","iconFlags"));
		dataSurvey.add(new RecordInfo("signed char","moreFlags"));		
		dataSurvey.add(new RecordInfo("short","packedTime"));
		dataSurvey.add(new RecordInfo("short","outsideTemp"));
		dataSurvey.add(new RecordInfo("short","hiOutsideTemp"));
		dataSurvey.add(new RecordInfo("short","lowOutsideTemp"));
		dataSurvey.add(new RecordInfo("short","insideTemp"));
		dataSurvey.add(new RecordInfo("short","barometer"));
		dataSurvey.add(new RecordInfo("short","outsideHum"));
		dataSurvey.add(new RecordInfo("short","insideHum"));
		dataSurvey.add(new RecordInfo("unsigned short","rain"));		
		dataSurvey.add(new RecordInfo("short","hiRainRate"));
		dataSurvey.add(new RecordInfo("short","windSpeed"));
		dataSurvey.add(new RecordInfo("short","hiWindSpeed"));        
		dataSurvey.add(new RecordInfo("signed char","windDirection"));
		dataSurvey.add(new RecordInfo("signed char","hiWindDirection"));		
		dataSurvey.add(new RecordInfo("short","numWindSamples"));
		dataSurvey.add(new RecordInfo("short","solarRad"));
		dataSurvey.add(new RecordInfo("short","hiSolarRad"));        
		dataSurvey.add(new RecordInfo("unsigned char","UV"));
		dataSurvey.add(new RecordInfo("unsigned char","hiUV"));		
		dataSurvey.add(new RecordInfo("signed char","leafTemp1"));
		dataSurvey.add(new RecordInfo("signed char","leafTemp2"));
		dataSurvey.add(new RecordInfo("signed char","leafTemp3"));
		dataSurvey.add(new RecordInfo("signed char","leafTemp4"));		
		dataSurvey.add(new RecordInfo("short","extraRad"));
		dataSurvey.add(new RecordInfo("short","newSensors1"));
		dataSurvey.add(new RecordInfo("short","newSensors2"));
		dataSurvey.add(new RecordInfo("short","newSensors3"));
		dataSurvey.add(new RecordInfo("short","newSensors4"));
		dataSurvey.add(new RecordInfo("short","newSensors5"));
		dataSurvey.add(new RecordInfo("short","newSensors6"));		
		dataSurvey.add(new RecordInfo("signed char","forecast"));
		dataSurvey.add(new RecordInfo("unsigned char","ET"));      
		dataSurvey.add(new RecordInfo("signed char","soilTemp1"));
		dataSurvey.add(new RecordInfo("signed char","soilTemp2"));
		dataSurvey.add(new RecordInfo("signed char","soilTemp3"));
		dataSurvey.add(new RecordInfo("signed char","soilTemp4"));	
		dataSurvey.add(new RecordInfo("signed char","soilTemp5"));
		dataSurvey.add(new RecordInfo("signed char","soilTemp6"));
		dataSurvey.add(new RecordInfo("signed char","soilMoisture1"));
		dataSurvey.add(new RecordInfo("signed char","soilMoisture2"));	
		dataSurvey.add(new RecordInfo("signed char","soilMoisture3"));
		dataSurvey.add(new RecordInfo("signed char","soilMoisture4"));
		dataSurvey.add(new RecordInfo("signed char","soilMoisture5"));
		dataSurvey.add(new RecordInfo("signed char","soilMoisture6"));	
		dataSurvey.add(new RecordInfo("signed char","leafWetness1"));
		dataSurvey.add(new RecordInfo("signed char","leafWetness2"));
		dataSurvey.add(new RecordInfo("signed char","leafWetness3"));
		dataSurvey.add(new RecordInfo("signed char","leafWetness4"));	        
		dataSurvey.add(new RecordInfo("signed char","extraTemp1"));	
		dataSurvey.add(new RecordInfo("signed char","extraTemp2"));
		dataSurvey.add(new RecordInfo("signed char","extraTemp3"));	
		dataSurvey.add(new RecordInfo("signed char","extraTemp4"));
		dataSurvey.add(new RecordInfo("signed char","extraTemp5"));
		dataSurvey.add(new RecordInfo("signed char","extraTemp6"));
		dataSurvey.add(new RecordInfo("signed char","extraTemp7"));	
		dataSurvey.add(new RecordInfo("signed char","extraHum1"));
		dataSurvey.add(new RecordInfo("signed char","extraHum2"));
		dataSurvey.add(new RecordInfo("signed char","extraHum3"));
		dataSurvey.add(new RecordInfo("signed char","extraHum4"));
		dataSurvey.add(new RecordInfo("signed char","extraHum5"));
		dataSurvey.add(new RecordInfo("signed char","extraHum6"));
		dataSurvey.add(new RecordInfo("signed char","extraHum7"));	
	}
	
	
	public void decode(String filename, String destinationFilename) {
		/**	La lettura dei primi 212 byte contiene per i primi 20 byte informazioni sul formato ed altro da capire
		 *  Dal byte 20, ogni 6 byte, ci sono le dataSurveyrmazioni dei dati salvati relativamente ad un singolo giorno
		 *  I primi due di questi 6 byte contengono il numero di rilevazioni salvate per il giorno in questione
		 *  Gli altri 4 byte tengono la posizione iniziale dei dati relativi a quel giorno
		 *  Entrambe le dataSurveyrmazioni precedenti richiedono l'inversione dell'array di byte per essere lette 
		 */
		byte[] dataBuffer = new byte[212];
		
		try (BufferedInputStream in = new BufferedInputStream( new FileInputStream(filename))){
			in.read(dataBuffer);
			println( new String(dataBuffer, 0, 6) );
			br();
			int year,month;
			println("Filename: " + Paths.get(filename).getFileName().toString());
			String data = Paths.get(filename).getFileName().toString().split("\\.")[0];
			year = Integer.parseInt(data.split("-")[0]);
			month = Integer.parseInt(data.split("-")[1]);
			println("Anno: " + year + " Mese: " + month);
			boolean headerPrinted = false;
			
			for(int day=1; day<32; ++day) {
				int i = 20 + (day * 6); 
				int j = i + 6;
				byte[] dayIndexByteArray = extract(dataBuffer, i, j);
				println("Day: " + day + " Day index: " + getExaString(dayIndexByteArray));
						
				int recordsInDay = ByteBuffer.wrap(reverse(extract(dayIndexByteArray,0,2))).getShort();
				int startPosition = ByteBuffer.wrap(reverse(extract(dayIndexByteArray,2,6))).getInt();
				
				if(recordsInDay!=0) {
					println("----------------------------------------------------------");
					println("Numero record: " + recordsInDay + " Posizione dati: " + startPosition);					
					
					for(int numRecord=0; numRecord<recordsInDay; ++numRecord) {
						byte[] recordData = in.readNBytes(88);
						//println(getExaString(recordData, 0, 1));
						if(recordData[0] != 1) {
							continue;
						}
						decode(recordData);
						dataSurvey.add(new RecordInfo("LocalDateTime","timestamp"));
						
						if( (Integer)dataSurvey.get(4).getValue() < 1440) {
							dataSurvey.get(dataSurvey.size()-1).setValue( LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.ofSecondOfDay( Long.valueOf( (Integer)dataSurvey.get(4).getValue() * 60 ))));
						}else {
							dataSurvey.get(dataSurvey.size()-1).setValue( LocalDateTime.of(LocalDate.of(year, month, day+1), LocalTime.ofSecondOfDay( 0 )));
						}
						
						saveData("2023-05_Dati_Originali.txt");
						
						if(!headerPrinted) {
							convertAndSaveData(destinationFilename,true);	
							headerPrinted = true;
						}else {
							convertAndSaveData(destinationFilename,false);	
						}						
						dataSurvey.remove(dataSurvey.size()-1);
					}
				}
				
			}		
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String getExaString(byte[] array, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for(int i=start;i<end;++i) {
			sb.append(String.format("%02x ",array[i]));
		}
		return sb.toString();
	}
	
	private String getExaString(byte[] array) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<array.length;++i) {
			sb.append(String.format("%02x ",array[i]));
		}
		return sb.toString();
	}
	
	private void br() {
		System.out.println();
	}

	private void println(Object o) {
		System.out.println(o);
	}
	
	void saveData(String filename) {
		try (PrintWriter out = new PrintWriter(new FileWriter(filename,true))){
			for(int k=0; k<dataSurvey.size(); ++k) {
				RecordInfo measure = dataSurvey.get(k);
				out.println(measure.getType() + "\t" + measure.getName() + ":\t" + measure.getValue());
			}
			out.println("");
		}catch(IOException e) {
			e.printStackTrace();
		}
	
	}
	
	private void convertAndSaveData(String filename, boolean printHeader) {
		try(PrintWriter out = new PrintWriter(new FileWriter(filename,true))){
			RecordInfo measure = dataSurvey.get(dataSurvey.size()-1);
			LocalDateTime measureDate = (LocalDateTime) measure.getValue();
			if(printHeader) {
				out.println( "Formato file originario: WDAT5 ");
				out.println( "Data conversione: " + LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd/MM/yy HH:mm") ) );
			}			
			out.print( measureDate.format(DateTimeFormatter.ofPattern("dd/MM/yy")) + "\t");
			out.print( measureDate.format(DateTimeFormatter.ofPattern("HH:mm")) + "\t");
			
			DecimalFormat decimalFormatOne = new DecimalFormat("0.0");
			DecimalFormat decimalFormatTwo = new DecimalFormat("0.00");
			// outsideTemp
			out.print( decimalFormatOne.format( dataSurvey.get(5).getValue() ) + "\t");
			// hiOutsideTemp
			out.print( decimalFormatOne.format( dataSurvey.get(6).getValue() ) + "\t");
			// lowOutsideTemp
			out.print( decimalFormatOne.format( dataSurvey.get(7).getValue() ) + "\t");
			// outsideHum
			out.print( Math.round( (double) dataSurvey.get(10).getValue() ) + "\t");
			// Dev Pt. 
			// TO DO
			out.print( "0.0\t");
			// Wind Speed
			out.print( decimalFormatOne.format( (double)dataSurvey.get(14).getValue() * 3.6 ) + "\t");
			// Wind Dir
			out.print( convertWindDirection( (double)dataSurvey.get(16).getValue() )  + "\t");
			// Wind Run
			out.print( decimalFormatTwo.format( (double)dataSurvey.get(14).getValue() * 1.8 ) + "\t");
			// Hi Speed
			out.print( decimalFormatOne.format( (double)dataSurvey.get(15).getValue() * 3.6 ) + "\t");
			// Hi Dir
			out.print( convertWindDirection( (double)dataSurvey.get(17).getValue() )  + "\t");
			// Wind Chill
			double windChill = 13.12 + 0.6215 * (double)dataSurvey.get(5).getValue() - 11.37 * Math.pow((double)dataSurvey.get(14).getValue() * 3.6, 0.16) + 0.3965 * (double)dataSurvey.get(5).getValue() * Math.pow((double)dataSurvey.get(14).getValue() * 3.6, 0.16);     
			out.print( decimalFormatOne.format( windChill ) + "\t");
			// Heat Index
			// TO DO
			out.print( "0.0\t");
			// THW Index
			// TO DO
			out.print( "0.0\t");
			// Bar
			out.print( decimalFormatOne.format( dataSurvey.get(9).getValue() ) + "\t");
			// Rain
			out.print( decimalFormatTwo.format( dataSurvey.get(12).getValue() ) + "\t");
			// Rain Rate
			out.print( decimalFormatOne.format( dataSurvey.get(13).getValue() ) + "\t");
			// Heat D-D
			// TO DO
			out.print( "0.000\t");
			// Cool D-D
			// TO DO
			out.print( "0.000\t");
			// In Temp
			out.print( decimalFormatOne.format( dataSurvey.get(8).getValue() ) + "\t");
			// In Hum
			out.print( Math.round( (double) dataSurvey.get(11).getValue() ) + "\t");
			// In Dev
			// TO DO
			out.print( "0.0\t");
			// In Heat
			// TO DO
			out.print( "0.0\t");
			// In EMC
			// TO DO
			out.print( "0.00\t");
			// In Air Density
			// TO DO
			out.print( "0.0000\t");
			// Temp 2nd
			out.print( "---\t");
			// Temp 3rd
			out.print( "---\t");
			// Hum 2nd
			out.print( "---\t");
			// Hum 3rd
			out.print( "---\t");
			// Wind Samp
			out.print( Math.round( (int) dataSurvey.get(18).getValue() ) + "\t");
			// Wind Tx
			out.print( "1\t");
			// ISS Recept
			out.print( "100.0\t");
			// Arc. Int
			out.print( "30\t");
			 
			/*
			 * Ordine dei dati da riprodurre in output secondo il salvataggio dell'app weather link
			 * 
			 Date
			 Time
			 TempOut
			 HiTemp
			 LowTemp
			 OutHum
			 Dev Pt.
			 Wind Speed
			 Wind Dir
			 Wind Run
			 Hi Speed
			 Hi Dir
			 Wind Chill
			 Heat Index
			 THW Index
			 Bar
			 Rain
			 Rain Rate
			 Heat D-D
			 Cool D-D
			 In Temp
			 In Hum
			 In Dev
			 In Heat
			 In EMC
			 In Air Density
			 Temp 2nd
			 Temp 3rd
			 Hum 2nd
			 Hum 3rd
			 Wind Samp
			 Wind Tx
			 ISS Recept
			 Arc. Int
			  
			 */
			
			
			out.println("");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private byte[] extract(byte[] array, int start, int end) {
		int size = end - start;
		byte[] result = new byte[size];
		for(int i = 0; i < size; ++i) {
			result[i] = array[start+i];
		}
		return result;
	}
	
	private byte[] reverse(byte[] array) {		
	      int i = 0;
	      int j = array.length - 1;
	      byte tmp;
	      while (j > i) {
	          tmp = array[j];
	          array[j] = array[i];
	          array[i] = tmp;
	          j--;
	          i++;
	      }		
	      return array;
	}
	
	private String convertWindDirection(double angle) {
		int value = (int) Math.round(angle);
		if(value < 180) {
			if(value < 90) {
				if(value < 45) {
					if(value == 0) {
						return "N";
					}else {
						return "NNE";
					}
				}else {
					if(value == 45) {
						return "NE";
					}else {
						return "ENE";
					}					
				}
			}else {
				if(value < 135) {
					if(value == 90) {
						return "E";
					}else {
						return "ESE";
					}
				}else {
					if(value == 135) {
						return "SE";
					}else {
						return "SSE";
					}
				}
			}
		}else {
			if(value < 270) {
				if(value < 225) {
					if(value == 180) {
						return "S";
					}else {
						return "SSW";
					}
				}else {
					if(value == 225) {
						return "SW";
					}else {
						return "WSW";
					}					
				}
			}else {
				if(value < 315) {
					if(value == 270) {
						return "W";
					}else {
						return "WNW";
					}
				}else {
					if(value == 315) {
						return "NW";
					}else {
						return "NNW";
					}
				}
			}
		}
		
	}
	
	private void decode(byte[] data) {
		int offset = 0;
		int value;
				
		for(int i=0; i<dataSurvey.size(); ++i) {
			RecordInfo measure =  dataSurvey.get(i);
			String type = measure.getType();
			switch(type.toLowerCase()) {
			case "signed char":{
				value = data[offset];
				switch(measure.getName()) {
				case "windDirection":
				case "hiWindDirection":{
					double result = value / 16.0 * 360;
					measure.setValue( result );
					break;
				}
				case "extraTemp1":
				case "extraTemp2":
				case "extraTemp3":
				case "extraTemp4":
				case "extraTemp5":
				case "extraTemp6":
				case "extraTemp7":
				case "soilTemp1":
				case "soilTemp2":
				case "soilTemp3":
				case "soilTemp4":
				case "soilTemp5":
				case "soilTemp6":
				case "leafTemp1":
				case "leafTemp2":
				case "leafTemp3":
				case "leafTemp4":{
					measure.setValue( ((value - 90) - 32) * 5 / 9.0);
					break;
				}
				case "soilMoisture1":
				case "soilMoisture2":
				case "soilMoisture3":
				case "soilMoisture4":
				case "soilMoisture5":
				case "soilMoisture6":
				case "soilMoisture7":{
					measure.setValue(value / 9.80638);					
					break;
				}
				default:{
					measure.setValue(value);	
				}
				}				
				offset++;
				break;
			}
			case "unsigned char":{				
				value = data[offset] & 0x000000FF;
							
				switch(measure.getName()) {
				case "UV":
				case "hiUV":{
					measure.setValue(value/10.0);
					break;
				}
				case "ET":{
					measure.setValue(value/1000.0);
					break;
				}
				default:{
					measure.setValue(value);
				}
				}
				
				offset++;
				break;
			}
			case "unsigned short":{
				byte[] packet = { 0x00, 0x00, data[offset+1],data[offset]};
				/*value = ByteBuffer.wrap(reverse(extract(data,offset,offset+2))).getShort();
				value = value >= 0 ? value : 0x10000 + value; */
				value = ByteBuffer.wrap(packet).getInt();
				switch(measure.getName()) {
				case "rain":{
					/*
				    rain_collector_type = value & 0xF000
				    rain_clicks = value & 0x0FFF
				    depth_per_click = {
				        0x0000: 0.1 * 25.4,
				        0x1000: 0.01 * 25.4,
				        0x2000: 0.2,
				        0x3000: 1.0,
				        0x6000: 0.1,
				    }[rain_collector_type]
				    depth = depth_per_click * rain_clicks
				    value = depth
				    rate = result["hirainrate"] * depth_per_click
				    result["hirainrate"] = rate
				    
				    * DOvrebbe venire 0.0 per rain
				    */
					
					
					//measure.setValue(value);
					measure.setValue(0.00);
					break;
			    }
				default:{
					measure.setValue(value);	
				}
				}
			    
				offset+=2;
				break;
			}
			case "short":{
				value = ByteBuffer.wrap(reverse(extract(data,offset,offset+2))).getShort();
				//println(measure.getName() + ": " + signedShort + "\t Offset: " + offset);
				switch(measure.getName()) {
				case "outsideTemp":
				case "hiOutsideTemp":
				case "lowOutsideTemp":
				case "insideTemp":{
					measure.setValue( ((value / 10.0) - 32) * 5 / 9.0 );
					break;
				}
				case "barometer":{
					measure.setValue( value / 1000.0 * 25.4 * 1.33322387415);
					break;
				}
				case "insideHum":
				case "outsideHum":{
					measure.setValue( value / 10.0 );
					break;
				}
				case "windSpeed":
				case "hiWindSpeed":{
					measure.setValue( value / 10.0 * 1609.344 / 3600 );
					break;
				}
				default:{
					measure.setValue(value);	
				}
				}				
				offset+=2;
				break;
			}
			default:{
				println("Tipo di informazione non riconosciuto");
			}
			}
		}
		
	}
	
}
