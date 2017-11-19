package model;

import java.util.LinkedList;

/**
 * 
 * Ein Fahrzeugumlauf besteht aus einer wechselnden Folge von Servicefahrten und Leerfahrten.
 * Er muss mit einer Leerfahrt vom Depot zu einer Servicefahrt beginnen
 * und mit einer Leerfahrt von einer Servicefahrt zum Depot enden.
 */
public class Fahrzeugumlauf {
	
	int id;
	// Typ der Liste ist Journey, damit sowohl Servicefahrten als auch Leerfahrten hinzugefuegt werden koennen
	private LinkedList<Journey> fahrten; 
	
	/**
	 * Konstruktor
	 * @param i
	 */
	public Fahrzeugumlauf(int i){
		this.setFahrten(new LinkedList<Journey>());
		this.id = i;
	}

	/**
	 * fuegt eine Fahrt am Ende des Fahrzeugumlaufs hinzu
	 * @param j
	 */
	public void addFahrt(Journey j){
		fahrten.add(j);
	}
	
	/**
	 * fuegt eine Fahrt nach der Stelle index zum Fahrzeugumlauf hinzu
	 * @param index
	 * @param j
	 */
	public void addFahrtAfterFahrt(int index, Journey j){
		fahrten.add(index, j);
	}
	
	public LinkedList<Journey> getFahrten() {
		return fahrten;
	}

	public void setFahrten(LinkedList<Journey> fahrten) {
		this.fahrten = fahrten;
	}
	
	/**
	 * prueft, ob der Fahrzeugumlauf die Bedingung erfuellt, dass erste und letzte Fahrt Leerfahrten sind
	 * hier noch ergaenzen: 
	 * erste Fahrt vom Depot?
	 * letzte Fahrt zum Depot?
	 * @return
	 */
	public boolean isFeasible(){
		boolean result = true;
		if (!(fahrten.get(0) instanceof Deadruntime) || !(fahrten.get(fahrten.size()-1) instanceof Deadruntime)){
			result = false;
		}
		return result;
	}
	
	/**
	 * gibt die Anzahl der Fahrten im Fahrzeugumlauf zurueck
	 * @return
	 */
	public int size(){
		return fahrten.size();
	}
	
	public String toString(){
		String result = "Fahrzeugumlauf " + id + " beinhaltet folgende Fahrten: " + getFahrten();
		return result;
	}

}
