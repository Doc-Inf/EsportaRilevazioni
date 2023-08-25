package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Rilevazione {
	
	private LocalDate data;
	private LocalTime ora;
	private double tempOut;
	private double hiTemp;
	private double lowTemp;
	private int outHum;
	private double devPt;
	private double windSpeed;
	private String windDir;
	private double windRun;
	private double hiSpeed;
	private String hiDir;
	private double chillWind;
	private double heatIndex;
	private double thwIndex;
	private double bar;
	private double rain;
	private double rainRate;
	private double heatDD;
	private double coolDD;
	private double inTemp;
	private int inHum;
	private double inDew;
	private double inHeat;
	private double inEMC;
	private double inAirDensity;
	private int windSamp;
	private int windTx;
	private double issRecept;
	private int arcInt;
	
	public Rilevazione(LocalDate data, LocalTime ora, double tempOut, double hiTemp, double lowTemp, int outHum,
			double devPt, double windSpeed, String windDir, double windRun, double hiSpeed, String hiDir,
			double chillWind, double heatIndex, double thwIndex, double bar, double rain, double rainRate,
			double heatDD, double coolDD, double inTemp, int inHum, double inDew, double inHeat, double inEMC,
			double inAirDensity, int windSamp, int windTx, double issRecept, int arcInt) {
		super();
		this.data = data;
		this.ora = ora;
		this.tempOut = tempOut;
		this.hiTemp = hiTemp;
		this.lowTemp = lowTemp;
		this.outHum = outHum;
		this.devPt = devPt;
		this.windSpeed = windSpeed;
		this.windDir = windDir;
		this.windRun = windRun;
		this.hiSpeed = hiSpeed;
		this.hiDir = hiDir;
		this.chillWind = chillWind;
		this.heatIndex = heatIndex;
		this.thwIndex = thwIndex;
		this.bar = bar;
		this.rain = rain;
		this.rainRate = rainRate;
		this.heatDD = heatDD;
		this.coolDD = coolDD;
		this.inTemp = inTemp;
		this.inHum = inHum;
		this.inDew = inDew;
		this.inHeat = inHeat;
		this.inEMC = inEMC;
		this.inAirDensity = inAirDensity;
		this.windSamp = windSamp;
		this.windTx = windTx;
		this.issRecept = issRecept;
		this.arcInt = arcInt;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getOra() {
		return ora;
	}

	public void setOra(LocalTime ora) {
		this.ora = ora;
	}

	public double getTempOut() {
		return tempOut;
	}

	public void setTempOut(double tempOut) {
		this.tempOut = tempOut;
	}

	public double getHiTemp() {
		return hiTemp;
	}

	public void setHiTemp(double hiTemp) {
		this.hiTemp = hiTemp;
	}

	public double getLowTemp() {
		return lowTemp;
	}

	public void setLowTemp(double lowTemp) {
		this.lowTemp = lowTemp;
	}

	public int getOutHum() {
		return outHum;
	}

	public void setOutHum(int outHum) {
		this.outHum = outHum;
	}

	public double getDevPt() {
		return devPt;
	}

	public void setDevPt(double devPt) {
		this.devPt = devPt;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getWindDir() {
		return windDir;
	}

	public void setWindDir(String windDir) {
		this.windDir = windDir;
	}

	public double getWindRun() {
		return windRun;
	}

	public void setWindRun(double windRun) {
		this.windRun = windRun;
	}

	public double getHiSpeed() {
		return hiSpeed;
	}

	public void setHiSpeed(double hiSpeed) {
		this.hiSpeed = hiSpeed;
	}

	public String getHiDir() {
		return hiDir;
	}

	public void setHiDir(String hiDir) {
		this.hiDir = hiDir;
	}

	public double getChillWind() {
		return chillWind;
	}

	public void setChillWind(double chillWind) {
		this.chillWind = chillWind;
	}

	public double getHeatIndex() {
		return heatIndex;
	}

	public void setHeatIndex(double heatIndex) {
		this.heatIndex = heatIndex;
	}

	public double getThwIndex() {
		return thwIndex;
	}

	public void setThwIndex(double thwIndex) {
		this.thwIndex = thwIndex;
	}

	public double getBar() {
		return bar;
	}

	public void setBar(double bar) {
		this.bar = bar;
	}

	public double getRain() {
		return rain;
	}

	public void setRain(double rain) {
		this.rain = rain;
	}

	public double getRainRate() {
		return rainRate;
	}

	public void setRainRate(double rainRate) {
		this.rainRate = rainRate;
	}

	public double getHeatDD() {
		return heatDD;
	}

	public void setHeatDD(double heatDD) {
		this.heatDD = heatDD;
	}

	public double getCoolDD() {
		return coolDD;
	}

	public void setCoolDD(double coolDD) {
		this.coolDD = coolDD;
	}

	public double getInTemp() {
		return inTemp;
	}

	public void setInTemp(double inTemp) {
		this.inTemp = inTemp;
	}

	public int getInHum() {
		return inHum;
	}

	public void setInHum(int inHum) {
		this.inHum = inHum;
	}

	public double getInDew() {
		return inDew;
	}

	public void setInDew(double inDew) {
		this.inDew = inDew;
	}

	public double getInHeat() {
		return inHeat;
	}

	public void setInHeat(double inHeat) {
		this.inHeat = inHeat;
	}

	public double getInEMC() {
		return inEMC;
	}

	public void setInEMC(double inEMC) {
		this.inEMC = inEMC;
	}

	public double getInAirDensity() {
		return inAirDensity;
	}

	public void setInAirDensity(double inAirDensity) {
		this.inAirDensity = inAirDensity;
	}

	public int getWindSamp() {
		return windSamp;
	}

	public void setWindSamp(int windSamp) {
		this.windSamp = windSamp;
	}

	public int getWindTx() {
		return windTx;
	}

	public void setWindTx(int windTx) {
		this.windTx = windTx;
	}

	public double getIssRecept() {
		return issRecept;
	}

	public void setIssRecept(double issRecept) {
		this.issRecept = issRecept;
	}

	public int getArcInt() {
		return arcInt;
	}

	public void setArcInt(int arcInt) {
		this.arcInt = arcInt;
	}

	private String json() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
		sb.append("\"data\":\"" + data + "\", ");
		sb.append("\"ora\":\"" + ora + "\", ");
		sb.append("\"tempOut\":\"" + tempOut + "\", ");
		sb.append("\"hiTemp\":\"" + hiTemp + "\", ");
		sb.append("\"lowTemp\":\"" + lowTemp + "\", ");
		sb.append("\"outHum\":\"" + outHum + "\", ");
		sb.append("\"devPt\":\"" + devPt + "\", ");
		sb.append("\"windSpeed\":\"" + windSpeed + "\", ");
		sb.append("\"windDir\":\"" + windDir + "\", ");
		sb.append("\"windRun\":\"" + windRun + "\", ");
		sb.append("\"hiSpeed\":\"" + hiSpeed + "\", ");
		sb.append("\"hiDir\":\"" + hiDir + "\", ");
		sb.append("\"chillWind\":\"" + chillWind + "\", ");
		sb.append("\"heatIndex\":\"" + heatIndex + "\", ");
		sb.append("\"thwIndex\":\"" + thwIndex + "\", ");
		sb.append("\"bar\":\"" + bar + "\", ");
		sb.append("\"rain\":\"" + rain + "\", ");
		sb.append("\"rainRate\":\"" + rainRate + "\", ");
		sb.append("\"heatDD\":\"" + heatDD + "\", ");
		sb.append("\"coolDD\":\"" + coolDD + "\", ");
		sb.append("\"inTemp\":\"" + inTemp + "\", ");
		sb.append("\"inHum\":\"" + inHum + "\", ");
		sb.append("\"inDew\":\"" + inDew + "\", ");
		sb.append("\"inHeat\":\"" + inHeat + "\", ");
		sb.append("\"inEMC\":\"" + inEMC + "\", ");
		sb.append("\"inAirDensity\":\"" + inAirDensity + "\", ");
		sb.append("\"windSamp\":\"" + windSamp + "\", ");
		sb.append("\"windTx\":\"" + windTx + "\", ");
		sb.append("\"issRecept\":\"" + issRecept + "\", ");
		sb.append("\"arcInt\":\"" + arcInt + "\"");		
		sb.append(" }");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return 	json();
	}
	
	
	
}
