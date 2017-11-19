package model;

/**
 * 
 * Klasse repraesentiert die Haltestellen
 *
 */
public class Stoppoint {

	private int id;
	private int code;
	private String name;
	
	/**
	 * Konstruktor
	 * @param id
	 */
	public Stoppoint(int id){
		this.setId(id);
		this.setCode(id);
		this.setName("Haltestelle " + id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
