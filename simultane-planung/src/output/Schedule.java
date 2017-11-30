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
			for (int j = 0; j < umlaufplan.get(i).getFahrten().size(); j = j + 2) { // es werden nur Leerfahrten beruecksichtigt
				variableKosten = variableKosten + umlaufplan.get(i).getFahrten().get(j).getVerbrauch();
			}
		}
	}
	
	public double berechneKosten(){
		kosten = anzahlBusse * 400000 + anzahlLadestationen * 250000 + variableKosten * 0.10;
		return kosten;
	}

	public double getKosten() {
		return kosten;
	}

	public void setKosten(double kosten) {
		this.kosten = kosten;
	}

	
}
