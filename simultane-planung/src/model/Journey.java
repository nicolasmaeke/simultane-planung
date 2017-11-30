package model;

public interface Journey {

	public String getId();

	public void setId(String id);

	public double getDistance();
	
	public String getFromStopId();
	
	public String getToStopId();
	
	public double getVerbrauch();
}
