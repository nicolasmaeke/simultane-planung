package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Servicejourney implements Journey {

    int id;
    int lineId;
    int sfFromStopId;
    int sfToStopId;
    Date sfDepTime;
    Date sfArrTime;
    double sfDistance; // in Kilometer
    private double sfRuntime; // in Milisekunden
    private double sfVerbrauch;
    public DateFormat zformat = new SimpleDateFormat("HH:mm:ss");
    private String help;
	private Date zeit;
    
    public Servicejourney(int id, int lineId, int FromStopId, int ToStopId, String DepTime, String ArrTime, double Distance){
    	this.id = id;
    	this.lineId = lineId;
    	this.sfFromStopId = FromStopId;
    	this.sfToStopId = ToStopId;
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
    
}
