package model;

/**
 * 
 * Klasse repraesentiert die Haltestellen
 *
 */
public class Stoppoint {

	private String id;
	private String code;
	private String name;
	private boolean ladestation;
	private int frequency;
	
	/**
	 * Konstruktor
	 * @param id
	 */
	public Stoppoint(String id){
		this.setId(id);
		this.setCode(id);
		this.setName("Haltestelle " + id);
		setLadestation(false);
		this.frequency = 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String id2) {
		this.code = id2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLadestation() {
		return ladestation;
	}

	public void setLadestation(boolean ladestation) {
		this.ladestation = ladestation;
	}
	
	public String toString(){
		if(isLadestation()){
			return this.name + " hat eine Ladestation";
		}else{
			return this.name + " hat keine Ladestation";
		}
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
