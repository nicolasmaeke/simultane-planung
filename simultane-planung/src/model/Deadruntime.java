package model;


public class Deadruntime implements Journey {

	private String id;
	private String fromStopId;
    private String toStopId;
    double distance;
    int runtime;
    private double verbrauch;
    
    public Deadruntime(String fromStopId, String toStopId, double distance, int runtime){
    	this.fromStopId = fromStopId;
    	this.toStopId = toStopId;
    	this.distance = distance;
    	this.runtime = runtime;
    	
    	this.setVerbrauch(distance * 1.5); // Annahme: 1,5kWh/km
    	this.setId(""+fromStopId+toStopId);
    	
    }

	public double getVerbrauch() {
		return verbrauch;
	}

	public void setVerbrauch(double verbrauch) {
		this.verbrauch = verbrauch;
	}
	
	public String toString(){
		return "Leerfahrt von Haltestelle " + fromStopId + " zu Haltestelle " + toStopId +
				" hat einen Verbraucht von " + verbrauch + " kWh";
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

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
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
}
