package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Servicejourney implements Journey {

    private String id;
    private String sfFromStopId;
    private String sfToStopId;
    Date sfDepTime;
    Date sfArrTime;
    double sfDistance; // in Kilometer
    private double sfRuntime; // in Milisekunden
    private double sfVerbrauch;
    public DateFormat zformat = new SimpleDateFormat("HH:mm:ss");
    private String help;
	private Date zeit;
    
    public Servicejourney(String id, String FromStopId, String ToStopId, String DepTime, String ArrTime, double Distance){
    	this.id = id;
    	this.setSfFromStopId(FromStopId);
    	this.setSfToStopId(ToStopId);
    	this.sfDistance = Distance;
    	
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
    	
    	this.setSfRuntime((sfArrTime.getTime() - sfDepTime.getTime())/1000); // rechnet Runtime in Sekunden um
    	this.setSfVerbrauch(sfDistance * 2.0);  // Annahme: 2kWh/km
    }

	public double getSfVerbrauch() {
		return sfVerbrauch;
	}

	public void setSfVerbrauch(double sfVerbrauch) {
		this.sfVerbrauch = sfVerbrauch;
	}

	public double getSfRuntime() {
		return sfRuntime;
	}

	public void setSfRuntime(double sfRuntime) {
		this.sfRuntime = sfRuntime;
	}
    
	public String toString(){
		return "Servicejourney " + id + " hat eine Dauer von " + sfRuntime +
				" Sekunden und verbraucht dabei " + sfVerbrauch + " kWh";
	}

	public String getSfFromStopId() {
		return sfFromStopId;
	}

	public void setSfFromStopId(String sfFromStopId) {
		this.sfFromStopId = sfFromStopId;
	}

	public String getSfToStopId() {
		return sfToStopId;
	}

	public void setSfToStopId(String sfToStopId) {
		this.sfToStopId = sfToStopId;
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

	public double getSfDistance() {
		return sfDistance;
	}

	public void setSfDistance(double sfDistance) {
		this.sfDistance = sfDistance;
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
    
}
