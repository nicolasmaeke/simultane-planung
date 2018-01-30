package output;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import model.Fahrzeugumlauf;
import model.Stoppoint;

public class Schedule {
	
	Vector<Fahrzeugumlauf> umlaufplan;
	private double kosten; 
	private int anzahlLadestationen;
	private int anzahlBusse;
	private double variableKosten;
	private HashMap<String, Stoppoint> stoppoints;

	
	public Schedule(Vector<Fahrzeugumlauf> fahrzeugumlaeufe, HashMap<String,Stoppoint> stoppoints){
		this.umlaufplan = fahrzeugumlaeufe;
		this.anzahlBusse = umlaufplan.size();
		for (Map.Entry e: stoppoints.entrySet()){
			Stoppoint i = stoppoints.get(e.getKey());
			if (i.isLadestation()) {
				anzahlLadestationen ++;
			}
		}
		for (int i = 0; i < umlaufplan.size(); i++) {
				variableKosten = variableKosten + umlaufplan.get(i).getKostenMitLadestationen();
		}
		this.stoppoints = stoppoints;
	}
	
	public double berechneKosten(){
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

	public void setAnzahlLadestationen(int anzahlLadestationen) {
		this.anzahlLadestationen = anzahlLadestationen;
	}

	public int getAnzahlBusse() {
		return anzahlBusse;
	}

	public void setAnzahlBusse(int anzahlBusse) {
		this.anzahlBusse = anzahlBusse;
	}

	public double getVariableKosten() {
		return variableKosten;
	}

	public void setVariableKosten(double variableKosten) {
		this.variableKosten = variableKosten;
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
	
}
