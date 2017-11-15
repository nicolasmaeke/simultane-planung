package model;

public class Line {

	private int id;
	private int code;
	private String name;
	
	public Line(int i){
		this.setId(i);
		this.setCode(i);
		this.setName("Linie " + i);
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
