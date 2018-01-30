package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import helper.feasibilityHelper;

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
	private double energieVerbrauch = 0;
	private long kosten = 0;
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
			energieVerbrauch = energieVerbrauch + fahrten.get(j).getVerbrauch();
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

	/**
	public boolean isFeasible(HashMap<String, Servicejourney> servicejourneys,
			HashMap<String, Deadruntime> deadruntimes) {
		
		if (!(fahrten.get(0) instanceof Deadruntime) || !(fahrten.get(fahrten.size()-1) instanceof Deadruntime)){
			return false;
		}
		laden.clear();
		stellen.clear();
		double kapazitaet = 80.0; // Batteriekapazitaet in kWh 
		int letzteLadung = 0; // ID der Fahrt im Fahrzeugumlauf, wo zuletzt geladen wird
		
		for (int i = 0; i < fahrten.size(); i++) { // fuer jede Fahrt i im zusammengesetzten Fahrzeugumlauf
			
			if (kapazitaet - fahrten.get(i).getVerbrauch() < 0){ // falls Verbrauch von Fahrt i die Restkapazitaet nicht abdeckt
				
				if(fahrten.get(i) instanceof Servicejourney){ // falls Fahrt i eine Servicefahrt ist 
					int x = 0;
					while((i-2-x) > letzteLadung){ //solange wir nicht die erste SF oder die LetzteLadung erreichen
						if(feasibilityHelper.zeitpufferFuerLadezeit(fahrten.get(i-2-x).getId(), fahrten.get(i-x).getId(), deadruntimes, servicejourneys, kapazitaet)){
							//wenn genug Zeit zum Laden vorhanden ist
							if(x==0){ //falls direkt bei der betroffenen SF geladen werden kann
								if (stoppoints.get(fahrten.get(i).getFromStopId()).isLadestation()){ //falls noch keine Ladestation an dieser Stelle vorhanden ist
									kapazitaet = 80; // Kapazitaet wieder voll geladen
									letzteLadung = i; // merkt sich, an i die letzte Ladung erfolgt ist
									laden.add(stoppoints.get(fahrten.get(i).getFromStopId()));
									stellen.add(letzteLadung);									
									break;
								} 
							}else{ // falls nicht direkt in i geladen werden kann und damit die vorherigen SF anschauen muss
								if (stoppoints.get(fahrten.get(i-2-x).getToStopId()).isLadestation()){ // 
									kapazitaet = 80; // Kapazitaet wieder voll geladen
									laden.add(stoppoints.get(fahrten.get(i-2-x).getToStopId()));
									letzteLadung = i - 2 - x; 
									stellen.add(letzteLadung);
									i = letzteLadung + 1;	
									break;
								} 
							}
						}
						x = x + 2;
					}
					if(kapazitaet != 80){ // wenn nicht geladen werden konnte, dann lade vor Servicefahrt 1 (da geht es zeitlich immer)
						if(letzteLadung == 0){ // schon einmal vor Servicefahrt 1 geladen?
							if (stoppoints.get(fahrten.get(1).getFromStopId()).isLadestation()){ // falls vor SF1 noch keine Ladestation gebaut wird
								kapazitaet = 80;
								laden.add(stoppoints.get(fahrten.get(1).getFromStopId()));
								i = 1;
								letzteLadung = 1;
								stellen.add(letzteLadung);
							}
						}
						else{ // es wird zum zweiten mal versucht an der gleichen Haltestelle zu laden --> Endlosschleife: Fahrzeugumlauf nicht moeglich
							laden.clear();
							stellen.clear();
							return false;
						}
					}
				}	

				if(fahrten.get(i) instanceof Deadruntime){ // falls Fahrt i eine Leerfahrt ist
					int x = 0;
					while(((i - x - 1) > letzteLadung)){ //solange die LetzteLadung nicht wieder erreicht wird
						if(i == fahrten.size()-1 && x == 0){ //falls i die letzte Leerfahrt ist
							if (stoppoints.get(fahrten.get(i-1).getToStopId()).isLadestation()){ //falls keine Ladestation vorhanden an Endhaltestelle von SF (i-1)
								kapazitaet = 80;
								laden.add(stoppoints.get(fahrten.get(i-1).getToStopId()));
								letzteLadung = i - 1;
								stellen.add(letzteLadung);
								i = i - 1;
								break;
							}
							else{
								x = x + 2;
							}
						}
						else if(x==0){
							if(feasibilityHelper.zeitpufferFuerLadezeit(fahrten.get(i-1).getId(), fahrten.get(i+1).getId(), deadruntimes, servicejourneys, kapazitaet)){					
								if (stoppoints.get(fahrten.get(i-1).getToStopId()).isLadestation()){
									kapazitaet = 80;
									laden.add(stoppoints.get(fahrten.get(i-1).getToStopId()));
									letzteLadung = i-1;
									stellen.add(letzteLadung);
									break;
								} 
							}
							x = x + 2;
						}else{
							if(feasibilityHelper.zeitpufferFuerLadezeit(fahrten.get(i-2-x+1).getId(), fahrten.get(i-x+1).getId(), deadruntimes, servicejourneys, kapazitaet)){
								if (stoppoints.get(fahrten.get(i-x-1).getToStopId()).isLadestation()){ // i - x ist die Starthaltestelle der Servicefahrt i
									kapazitaet = 80;
									laden.add(stoppoints.get(fahrten.get(i-x-1).getToStopId()));
									letzteLadung = i - x - 1;
									stellen.add(letzteLadung);
									i = i - x;
									break;
								} 
							}
							x = x + 2;
						}
					}	
					if(kapazitaet != 80){ // wenn nicht geladen werden konnte, dann lade vor Servicefahrt 1 (da geht es zeitlich immer)
						if(letzteLadung == 0){ // schon einmal vor Servicefahrt 1 geladen?
							if (stoppoints.get(fahrten.get(1).getFromStopId()).isLadestation()){
								laden.add(stoppoints.get(fahrten.get(1).getFromStopId()));
								kapazitaet = 80;
								i = 1;
								letzteLadung = 1;
								stellen.add(letzteLadung);
							}
						}
						else{
							laden.clear();
							stellen.clear();
							return false; // es wird zum zweiten mal versucht vor Servicefahrt 1 zu laden --> Endlosschleife: Fahrzeugumlauf nicht moeglich 
						}
					}
				}	
			}
			kapazitaet = kapazitaet - fahrten.get(i).getVerbrauch(); // aktualisiere die Kapazitaet nach Fahrt i, falls Fahrt i noch gefahren werden kann
		}
		return true;
	}
	*/
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
		double personalkosten = (sZwei.getSfArrTime().getTime() + dZwei.runtime) - (sEins.getSfDepTime().getTime() - dEins.runtime);
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
				ladestationsAnteil = ladestationsAnteil + 250000*(1/(this.getLaden().get(i).getFrequency()));// Kosten fuer Ladestationen werden anteilig auf die nutzenden Fahrzeugumlaeufe verteilt
			}	 
		}
		Servicejourney sEins = (Servicejourney) fahrten.get(1);
		Servicejourney sZwei = (Servicejourney) fahrten.get(fahrten.size()-2);
		Deadruntime dEins = (Deadruntime) fahrten.get(0);
		Deadruntime dZwei = (Deadruntime) fahrten.get(fahrten.size()-1);
		double personalkosten = (sZwei.getSfArrTime().getTime() + dZwei.runtime) - (sEins.getSfDepTime().getTime() - dEins.runtime);
		for (int i = 0; i < fahrten.size(); i++) {
			verbrauchsKosten = verbrauchsKosten + fahrten.get(i).getVerbrauch();
		}
		
		return verbrauchsKosten * 0.1 + (personalkosten * 20 / 60 / 1000 / 1000) + ladestationsAnteil;
	}

	public void addFahrten(List<Journey> list) {
		fahrten.addAll(list);
	}

	public double getLaenge() {
		return laenge;
	}

	public void setLaenge(double laenge) {
		this.laenge = laenge;
	}

	public double getEnergieVerbrauch() {
		return energieVerbrauch;
	}

	public void setEnergieVerbrauch(double energieVerbrauch) {
		this.energieVerbrauch = energieVerbrauch;
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

	public void setKosten(long kosten) {
		this.kosten = kosten;
	}

	public LinkedList<Integer> getStellen() {
		return stellen;
	}

	public void setStellen(LinkedList<Integer> stellen) {
		this.stellen = stellen;
	}

}
