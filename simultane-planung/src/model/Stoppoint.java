package model;

public class Stoppoint {

	private int id;
	private int code;
	private String name;
	
	public Stoppoint(int i){
		this.setId(i);
		this.setCode(i);
		this.setName("Haltestelle " + i);
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
