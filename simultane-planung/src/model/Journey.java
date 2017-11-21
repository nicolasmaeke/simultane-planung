package model;

public interface Journey {

	public String getId();

	public void setId(String id);

	public Double getDistance();
	
	public String getFromStopId();
	
	public String getToStopId();
}
