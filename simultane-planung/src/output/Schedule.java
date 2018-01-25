package output;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
	private boolean isFeasible;
	private HashMap<String,Stoppoint> stoppoints;
	HashMap<String, Servicejourney> servicejourneys;
	HashMap<String, Deadruntime> deadruntimes;
	
	public Schedule(Vector<Fahrzeugumlauf> fahrzeugumlaeufe, HashMap<String,Stoppoint> stoppoints, HashMap<String, Servicejourney> servicejourneys, HashMap<String, Deadruntime> deadruntimes){
		this.umlaufplan = fahrzeugumlaeufe;
		this.anzahlBusse = umlaufplan.size();
		this.stoppoints = stoppoints;
		this.servicejourneys = servicejourneys;
		this.deadruntimes = deadruntimes;
		this.isFeasible = true;
		for (Map.Entry e: stoppoints.entrySet()){
			Stoppoint i = stoppoints.get(e.getKey());
			if (i.isLadestation()) {
				anzahlLadestationen ++;
			}
		}
		for (int i = 0; i < umlaufplan.size(); i++) {
				if(!umlaufplan.get(i).isFeasible(stoppoints, servicejourneys, deadruntimes)){
					this.isFeasible = false;
					//System.out.println("not feasible");
				}
				variableKosten = variableKosten + umlaufplan.get(i).getKostenMitLadestationen();
		}
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

	public boolean isFeasible() {
		return isFeasible;
	}

	public void setFeasible() {
		for (int i = 0; i < umlaufplan.size(); i++) {
			if(!umlaufplan.get(i).isFeasible(stoppoints, servicejourneys, deadruntimes)){
				this.isFeasible = false;
			}
		}
	}

	
}
