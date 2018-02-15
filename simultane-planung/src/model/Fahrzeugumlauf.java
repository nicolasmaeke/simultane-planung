package model;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Ein Fahrzeugumlauf besteht aus einer wechselnden Folge von Servicefahrten und Leerfahrten.
 * Er muss mit einer Leerfahrt vom Depot zu einer Servicefahrt beginnen
 * und mit einer Leerfahrt von einer Servicefahrt zum Depot enden.
 */
public class Fahrzeugumlauf {
	
	private String id;
	// Typ der Liste ist Journey, damit sowohl Servicefahrten als auch Leerfahrten hinzugefuegt werden koennen
	private LinkedList<Journey> fahrten; 
	private double laenge = 0;
	//private long kosten = 0;
	private LinkedList<Stoppoint> laden;
	private LinkedList<Integer> stellen;
	
	/**
	 * Konstruktor
	 * @param i
	 */
	public Fahrzeugumlauf(String i){
		this.setFahrten(new LinkedList<Journey>());
		this.id = i;
		for (int j = 0; j < fahrten.size(); j++) {
			laenge = laenge + fahrten.get(j).getDistance();
		}
		this.laden = new LinkedList<Stoppoint>();
		this.stellen = new LinkedList<Integer>();
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
	
	public Journey getAtIndex(int index){
		Journey result = fahrten.get(index);
		return result;
	}
	
	
	public LinkedList<Journey> getFahrten() {
		return fahrten;
	}

	public void setFahrten(LinkedList<Journey> fahrten) {
		this.fahrten = fahrten;
	}
	
	
	/**
	 * gibt die Anzahl der Fahrten im Fahrzeugumlauf zurueck
	 * @return
	 */
	public int size(){
		return fahrten.size();
	}
	
	public String toString(){
		String result = "Fahrzeugumlauf " + id + " : " + getFahrten();
		return result;
	}
	
	public String toStringIds(){
		String result = "[";
		for (int i = 0; i < fahrten.size(); i++) {
			result = result + fahrten.get(i).getId() + ", ";
		}
		result = result.substring(0, result.length()-2);
		result = result + "]";
		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Journey> getFahrtenVonBis(int i, int j) {
		LinkedList<Journey> fahrten = new LinkedList<Journey>();
		for (int k = i; k <= j; k++) {
			fahrten.add(this.fahrten.get(k));
		}
		return fahrten;
	}


	public double getKosten() {
		double verbrauchsKosten = 0;
		Servicejourney sEins = (Servicejourney) fahrten.get(1);
		Servicejourney sZwei = (Servicejourney) fahrten.get(fahrten.size()-2);
		Deadruntime dEins = (Deadruntime) fahrten.get(0);
		Deadruntime dZwei = (Deadruntime) fahrten.get(fahrten.size()-1);
		double personalkosten = (sZwei.getSfArrTime().getTime() + dZwei.getRuntime()) - (sEins.getSfDepTime().getTime() - dEins.getRuntime());
		for (int i = 0; i < fahrten.size(); i++) {
			verbrauchsKosten = verbrauchsKosten + fahrten.get(i).getVerbrauch();
		}
		return verbrauchsKosten * 0.1 + personalkosten * 20 / 60 / 1000 / 1000;
	}
	
	public double getKostenMitLadestationen() {
		double verbrauchsKosten = 0;
		double ladestationsAnteil = 0;
		
		for (int i = 0; i < this.getLaden().size(); i++) {
			
			if(!this.getLaden().contains(null)){
				int test = this.getLaden().get(i).getFrequency();
				if(test == 0){
					System.err.println();
				}
				ladestationsAnteil = ladestationsAnteil + 250000*(1.0/test);// Kosten fuer Ladestationen werden anteilig auf die nutzenden Fahrzeugumlaeufe verteilt
			}	 
		}
		double personalkosten = 0;
		for (int i = 0; i < fahrten.size(); i++) {
			verbrauchsKosten = verbrauchsKosten + fahrten.get(i).getVerbrauch();
			personalkosten = personalkosten + fahrten.get(i).getRuntime();
		}
		personalkosten = personalkosten * 20 / 1000 / 60 / 60;
		return verbrauchsKosten * 0.1 + personalkosten + ladestationsAnteil;
	}

	public void addFahrten(List<Journey> list) {
		fahrten.addAll(list);
	}

	public double getLaenge() {
		return laenge;
	}

	public void setLaenge(double laenge) {

	}


	public String getLadenString() {
		String result = "[";
		for (int i = 0; i < laden.size(); i++) {
			result = result + laden.get(i).getId() + ", ";
		}
		if(result.length() > 1){
			result = result.substring(0, result.length()-2);
		}
		result = result + "]";
		return result;
	}
	
	public LinkedList<Stoppoint> getLaden(){
		return laden;
	}

	public void setLaden(LinkedList<Stoppoint> laden) {
		this.laden = laden;
	}

	public LinkedList<Integer> getStellen() {
		return stellen;
	}

	public void setStellen(LinkedList<Integer> stellen) {
		this.stellen = stellen;
	}

}
