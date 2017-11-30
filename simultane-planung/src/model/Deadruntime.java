package model;

/**
 * 
 * Klasse repraesentiert die Leerfahrten
 * 
 */
public class Deadruntime implements Journey {

	private String id;
	private String fromStopId;
    private String toStopId;
    int distance;
    int runtime;
    private double verbrauch;
    
    /**
     * Konstruktor: initialisiert die Variablen, berechnet den Verbrauch
     * und erstellt eine eindeutige ID aus Start- und Endhaltestelle
     * @param fromStopId
     * @param toStopId
     * @param distance
     * @param runtime
     */
    public Deadruntime(String fromStopId, String toStopId, int distance, int runtime){
    	this.fromStopId = fromStopId;
    	this.toStopId = toStopId;
    	this.distance = distance; // in Meter
    	this.runtime = runtime * 1000; // von eingelesenen Sekunden in Milisekunde
    	
    	this.setVerbrauch(distance/1000 * 1.5); // Annahme: 1,5kWh/km
    	this.setId(""+fromStopId+toStopId);
    	
    }

	public double getVerbrauch() {
		return verbrauch;
	}

	public void setVerbrauch(double verbrauch) {
		this.verbrauch = verbrauch;
	}

	public String getFromStopId() {
		return fromStopId;
	}

	public void setFromStopId(String fromStopId) {
		this.fromStopId = fromStopId;
	}

	public String getToStopId() {
		return toStopId;
	}

	public void setToStopId(String toStopId) {
		this.toStopId = toStopId;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String toString(){
		return "Leerfahrt von Haltestelle " + fromStopId + " zu Haltestelle " + toStopId +
				" hat einen Verbraucht von " + verbrauch + " kWh";
	}
}
