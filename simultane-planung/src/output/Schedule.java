package output;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import helper.feasibilityHelper;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Servicejourney;
import model.Stoppoint;

public class Schedule {
	
	Vector<Fahrzeugumlauf> umlaufplan;
	private double kosten; 
	private int anzahlLadestationen;
	private int anzahlBusse;
	private double variableKosten;
	private HashMap<String, Stoppoint> stoppoints;
	HashMap<String, Servicejourney> servicejourneys;
	HashMap<String, Deadruntime> deadruntimes;
	
	public Schedule(Vector<Fahrzeugumlauf> fahrzeugumlaeufe, HashMap<String, Servicejourney> servicejourneys,
			HashMap<String, Deadruntime> deadruntimes, HashMap<String, Stoppoint> stoppoints){
		this.umlaufplan = fahrzeugumlaeufe;
		this.anzahlBusse = umlaufplan.size();
		this.servicejourneys = servicejourneys;
		this.deadruntimes = deadruntimes;
		this.setStoppoints(stoppoints);
		for (Map.Entry e: stoppoints.entrySet()){
			Stoppoint i = stoppoints.get(e.getKey());
			if (i.isLadestation()) {
				anzahlLadestationen ++;
			}
		}
		for (int i = 0; i < umlaufplan.size(); i++) {
				variableKosten = variableKosten + umlaufplan.get(i).getKostenMitLadestationen();
		}
	}
	
	public double berechneKosten(){
		kosten = 0;
		kosten = anzahlBusse * 400000 + variableKosten;
		return kosten;
	}

	public double getKosten() {
		return kosten;
	}

	public void setKosten(double kosten) {
		this.kosten = kosten;
	}

	public Vector<Fahrzeugumlauf> getUmlaufplan() {
		return umlaufplan;
	}

	public void setUmlaufplan(Vector<Fahrzeugumlauf> umlaufplan) {
		this.umlaufplan = umlaufplan;
	}

	public int getAnzahlLadestationen() {
		return anzahlLadestationen;
	}

	public void setAnzahlLadestationen() {
		anzahlLadestationen = 0;
		for (Map.Entry e: stoppoints.entrySet()){
			Stoppoint i = stoppoints.get(e.getKey());
			if (i.isLadestation()) {
				anzahlLadestationen ++;
			}
		}
	}

	public int getAnzahlBusse() {
		return anzahlBusse;
	}

	public void setAnzahlBusse() {
		anzahlBusse = umlaufplan.size();
	}

	public double getVariableKosten() {
		return variableKosten;
	}

	public void setVariableKosten() {
		variableKosten = 0;
		for (int i = 0; i < umlaufplan.size(); i++) {
			variableKosten = variableKosten + umlaufplan.get(i).getKostenMitLadestationen();
	}
	}

	public void berechneFrequenzen(){
		for (Map.Entry e: stoppoints.entrySet()){
			Stoppoint i1 = stoppoints.get(e.getKey());
			int counter = 0;
			for (int i = 0; i < umlaufplan.size(); i++) {
				for (int j = 0; j < umlaufplan.get(i).getLaden().size(); j++) {
					if(umlaufplan.get(i).getLaden().get(j).getId().equals(i1.getId())){
						counter ++;
					}
				}
			}
			if(counter == 0){
				i1.setFrequency(counter);
				i1.setLadestation(false);
			}
			else{
				i1.setFrequency(counter);
				i1.setLadestation(true);
			}
			
		}
	}

	public HashMap<String, Stoppoint> getStoppoints() {
		return stoppoints;
	}

	public void setStoppoints(HashMap<String, Stoppoint> stoppoints) {
		this.stoppoints = stoppoints;
	}
	
	public boolean isFeasible(Fahrzeugumlauf umlauf) {
		
		if (!(umlauf.getFahrten().get(0) instanceof Deadruntime) || !(umlauf.getFahrten().get(umlauf.size()-1) instanceof Deadruntime)){
			return false;
		}
		
		for (int i = 1; i < umlauf.size()-3; i = i + 2) {
			Servicejourney temp = (Servicejourney) umlauf.getAtIndex(i);
			Servicejourney next = (Servicejourney) umlauf.getAtIndex(i+2);
			if((temp.getSfArrTime().getTime() + umlauf.getAtIndex(i+1).getRuntime()) > next.getSfDepTime().getTime()){
				return false;
			}
		}
		
		umlauf.getLaden().clear();
		umlauf.getStellen().clear();
		double kapazitaet = 80.0; // Batteriekapazitaet in kWh 
		int letzteLadung = 0; // ID der Fahrt im Fahrzeugumlauf, wo zuletzt geladen wird
		
		for (int i = 0; i < umlauf.getFahrten().size(); i++) { // fuer jede Fahrt i im zusammengesetzten Fahrzeugumlauf
			
			if (kapazitaet - umlauf.getFahrten().get(i).getVerbrauch() < 0){ // falls Verbrauch von Fahrt i die Restkapazitaet nicht abdeckt
				
				if(umlauf.getFahrten().get(i) instanceof Servicejourney){ // falls Fahrt i eine Servicefahrt ist 
					int x = 0;
					while((i-2-x) > letzteLadung){ //solange wir nicht die erste SF oder die LetzteLadung erreichen
						if(feasibilityHelper.zeitpufferFuerLadezeit(umlauf.getFahrten().get(i-2-x).getId(), umlauf.getFahrten().get(i-x).getId(), deadruntimes, servicejourneys, kapazitaet)){
							//wenn genug Zeit zum Laden vorhanden ist
							if(x==0){ //falls direkt bei der betroffenen SF geladen werden kann
								if (stoppoints.get(umlauf.getFahrten().get(i).getFromStopId()).isLadestation()){ //falls noch keine Ladestation an dieser Stelle vorhanden ist
									kapazitaet = 80; // Kapazitaet wieder voll geladen
									letzteLadung = i; // merkt sich, an i die letzte Ladung erfolgt ist
									umlauf.getLaden().add(stoppoints.get(umlauf.getFahrten().get(i).getFromStopId()));
									umlauf.getStellen().add(letzteLadung);									
									break;
								} 
							}else{ // falls nicht direkt in i geladen werden kann und damit die vorherigen SF anschauen muss
								if (stoppoints.get(umlauf.getFahrten().get(i-2-x).getToStopId()).isLadestation()){ // 
									kapazitaet = 80; // Kapazitaet wieder voll geladen
									umlauf.getLaden().add(stoppoints.get(umlauf.getFahrten().get(i-2-x).getToStopId()));
									letzteLadung = i - 2 - x; 
									umlauf.getStellen().add(letzteLadung);
									i = letzteLadung + 1;	
									break;
								} 
							}
						}
						x = x + 2;
					}
					if(kapazitaet != 80){ // wenn nicht geladen werden konnte, dann lade vor Servicefahrt 1 (da geht es zeitlich immer)
						if(letzteLadung == 0){ // schon einmal vor Servicefahrt 1 geladen?
							if (stoppoints.get(umlauf.getFahrten().get(1).getFromStopId()).isLadestation()){ // falls vor SF1 noch keine Ladestation gebaut wird
								kapazitaet = 80;
								umlauf.getLaden().add(stoppoints.get(umlauf.getFahrten().get(1).getFromStopId()));
								i = 1;
								letzteLadung = 1;
								umlauf.getStellen().add(letzteLadung);
							}
							else{
								umlauf.getLaden().clear();
								umlauf.getStellen().clear();
								umlauf.getStellen().add(100000000);
								return false;
							}
						}
						else{ // es wird zum zweiten mal versucht an der gleichen Haltestelle zu laden --> Endlosschleife: Fahrzeugumlauf nicht moeglich
							umlauf.getLaden().clear();
							umlauf.getStellen().clear();
							umlauf.getStellen().add(1000000);
							return false;
						}
					}
				}	

				if(umlauf.getFahrten().get(i) instanceof Deadruntime){ // falls Fahrt i eine Leerfahrt ist
					int x = 0;
					while(((i - x - 1) > letzteLadung)){ //solange die LetzteLadung nicht wieder erreicht wird
						if(i == umlauf.getFahrten().size()-1 && x == 0){ //falls i die letzte Leerfahrt ist
							if (stoppoints.get(umlauf.getFahrten().get(i-1).getToStopId()).isLadestation()){ //falls keine Ladestation vorhanden an Endhaltestelle von SF (i-1)
								kapazitaet = 80;
								umlauf.getLaden().add(stoppoints.get(umlauf.getFahrten().get(i-1).getToStopId()));
								letzteLadung = i - 1;
								umlauf.getStellen().add(letzteLadung);
								i = i - 1;
								break;
							}
							else{
								x = x + 2;
							}
						}
						else if(x==0){
							if(feasibilityHelper.zeitpufferFuerLadezeit(umlauf.getFahrten().get(i-1).getId(), umlauf.getFahrten().get(i+1).getId(), deadruntimes, servicejourneys, kapazitaet)){					
								if (stoppoints.get(umlauf.getFahrten().get(i-1).getToStopId()).isLadestation()){
									kapazitaet = 80;
									umlauf.getLaden().add(stoppoints.get(umlauf.getFahrten().get(i-1).getToStopId()));
									letzteLadung = i-1;
									umlauf.getStellen().add(letzteLadung);
									break;
								} 
							}
							x = x + 2;
						}else{
							if(feasibilityHelper.zeitpufferFuerLadezeit(umlauf.getFahrten().get(i-2-x+1).getId(), umlauf.getFahrten().get(i-x+1).getId(), deadruntimes, servicejourneys, kapazitaet)){
								if (stoppoints.get(umlauf.getFahrten().get(i-x-1).getToStopId()).isLadestation()){ // i - x ist die Starthaltestelle der Servicefahrt i
									kapazitaet = 80;
									umlauf.getLaden().add(stoppoints.get(umlauf.getFahrten().get(i-x-1).getToStopId()));
									letzteLadung = i - x - 1;
									umlauf.getStellen().add(letzteLadung);
									i = i - x;
									break;
								} 
							}
							x = x + 2;
						}
					}	
					if(kapazitaet != 80){ // wenn nicht geladen werden konnte, dann lade vor Servicefahrt 1 (da geht es zeitlich immer)
						if(letzteLadung == 0){ // schon einmal vor Servicefahrt 1 geladen?
							if (stoppoints.get(umlauf.getFahrten().get(1).getFromStopId()).isLadestation()){
								umlauf.getLaden().add(stoppoints.get(umlauf.getFahrten().get(1).getFromStopId()));
								kapazitaet = 80;
								i = 1;
								letzteLadung = 1;
								umlauf.getStellen().add(letzteLadung);
							}
							else{
								umlauf.getLaden().clear();
								umlauf.getStellen().clear();
								umlauf.getStellen().add(100000000);
								return false;
							}
						}
						else{
							umlauf.getLaden().clear();
							umlauf.getStellen().clear();
							umlauf.getStellen().add(100000000);
							return false; // es wird zum zweiten mal versucht vor Servicefahrt 1 zu laden --> Endlosschleife: Fahrzeugumlauf nicht moeglich 
						}
					}
				}	
			}
			kapazitaet = kapazitaet - umlauf.getFahrten().get(i).getVerbrauch(); // aktualisiere die Kapazitaet nach Fahrt i, falls Fahrt i noch gefahren werden kann
		}
		return true;
	}

	public HashMap<String, Servicejourney> getServicejourneys() {
		return servicejourneys;
	}

	public void setServicejourneys(HashMap<String, Servicejourney> servicejourneys) {
		this.servicejourneys = servicejourneys;
	}

	public HashMap<String, Deadruntime> getDeadruntimes() {
		return deadruntimes;
	}

	public void setDeadruntimes(HashMap<String, Deadruntime> deadruntimes) {
		this.deadruntimes = deadruntimes;
	}
}
