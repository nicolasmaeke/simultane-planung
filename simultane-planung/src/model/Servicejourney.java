package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * Klasse repraesentiert die Servicefahrten
 *
 */
public class Servicejourney implements Journey {

    private String id;
    private String fromStopId;
    private String toStopId;
    private Date sfDepTime;
    private Date sfArrTime;
    double Distance; // in Meter
    private double sfRuntime; // in Milisekunden
    private double sfVerbrauch;
    /**
     * Hilfsvariablen zur Berechnung der Runtime
     */
    public DateFormat zformat = new SimpleDateFormat("HH:mm:ss");
    private String help;
	private Date zeit;
    
	/**
	 * Konstruktor: initialisiert Variablen und berechnet Runtime sowie Verbrauch der Servicefahrt
	 * @param id
	 * @param FromStopId
	 * @param ToStopId
	 * @param DepTime
	 * @param ArrTime
	 * @param Distance
	 */
    public Servicejourney(String id, String FromStopId, String ToStopId, String DepTime, String ArrTime, double Distance){
    	this.id = id;
    	this.setFromStopId(FromStopId);
    	this.setToStopId(ToStopId);
    	this.Distance = Distance;
    	
    	help = DepTime;
    	zeit = null;

        try {
            zeit = zformat.parse(help.split(":")[1] + ":" + help.split(":")[2] + ":" + help.split(":")[3]);
        } catch (Exception e) {
            System.out.println(e);
        }
        this.sfDepTime = zeit;
        
        zeit = null;
    	help = ArrTime;

        try {
            zeit = zformat.parse(help.split(":")[1] + ":" + help.split(":")[2] + ":" + help.split(":")[3]);
        } catch (Exception e) {
            System.out.println(e);
        }
        this.sfArrTime = zeit;
    	
    	this.setRuntime((sfArrTime.getTime() - sfDepTime.getTime())); 
    	this.setSfVerbrauch(Distance/1000 * 2.0);  // Annahme: 2kWh/km
    }

	public void setSfVerbrauch(double sfVerbrauch) {
		this.sfVerbrauch = sfVerbrauch;
	}

	public double getRuntime() {
		return sfRuntime;
	}

	public void setRuntime(double sfRuntime) {
		this.sfRuntime = sfRuntime;
	}

	public String getFromStopId() {
		return fromStopId;
	}

	public void setFromStopId(String FromStopId) {
		this.fromStopId = FromStopId;
	}

	public String getToStopId() {
		return toStopId;
	}

	public void setToStopId(String ToStopId) {
		this.toStopId = ToStopId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getSfDepTime() {
		return sfDepTime;
	}

	public void setSfDepTime(Date sfDepTime) {
		this.sfDepTime = sfDepTime;
	}

	public Date getSfArrTime() {
		return sfArrTime;
	}

	public void setSfArrTime(Date sfArrTime) {
		this.sfArrTime = sfArrTime;
	}

	public double getDistance() {
		return Distance;
	}

	public void setDistance(double Distance) {
		this.Distance = Distance;
	}

	public DateFormat getZformat() {
		return zformat;
	}

	public void setZformat(DateFormat zformat) {
		this.zformat = zformat;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public Date getZeit() {
		return zeit;
	}

	public void setZeit(Date zeit) {
		this.zeit = zeit;
	}
	
	public String toString(){
		return "SF: " + id + "hat Verbrauch: " + getVerbrauch();
	}

	@Override
	public double getVerbrauch() {
		// TODO Auto-generated method stub
		return sfVerbrauch;
	}


}
