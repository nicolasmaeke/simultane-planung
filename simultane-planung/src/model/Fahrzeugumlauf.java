package model;

import java.util.LinkedList;

/**
 * 
 * @author nicolasmaeke
 * Ein Fahrzeugumlauf besteht aus einer wechselnden Folge von Servicefahrten und Leerfahrten.
 * Er muss mit einer Servicefahrt beginnen und enden.
 */
public class Fahrzeugumlauf {
	
	int id;
	private LinkedList<Journey> fahrten;
	
	public Fahrzeugumlauf(int i){
		this.setFahrten(new LinkedList<Journey>());
		this.id = i;
	}

	public void addFahrt(Journey j){
		fahrten.add(j);
	}
	
	public void addFahrtAfterFahrt(int index, Journey j){
		fahrten.add(index, j);
	}
	
	public LinkedList<Journey> getFahrten() {
		return fahrten;
	}

	public void setFahrten(LinkedList<Journey> fahrten) {
		this.fahrten = fahrten;
	}
	
	public boolean isFeasible(){
		boolean result = true;
		if (!(fahrten.get(0) instanceof Deadruntime) || !(fahrten.get(fahrten.size()-1) instanceof Deadruntime)){
			result = false;
		}
		return result;
	}
	
	public String toString(){
		String result = "Fahrzeugumlauf " + id + " beinhaltet folgende Fahrten: " + getFahrten();
		return result;
	}
	
	public int size(){
		return fahrten.size();
	}

}
