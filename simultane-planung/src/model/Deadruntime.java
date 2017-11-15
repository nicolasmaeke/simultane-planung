package model;


public class Deadruntime implements Journey {

	int fromStopId;
    int toStopId;
    double distance;
    int runtime;
    private double verbrauch;
    
    public Deadruntime(int fromStopId, int toStopId, double distance, int runtime){
    	this.fromStopId = fromStopId;
    	this.toStopId = toStopId;
    	this.distance = distance;
    	this.runtime = runtime;
    	
    	this.setVerbrauch(distance * 1.5); // Annahme: 1,5kWh/km
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
}
