package start;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import heuristic.Initialloesung;
import model.Fahrzeugumlauf;
import model.Stoppoint;
import parser.ProjectReadIn;

/**
 * Klasse startet die Eroeffnungsheuristik.
 * @input: Eine Datei mit Rohdaten muss eingelesen werden.
 * @output: Am Ende wird eine neue Datei mit Rohdaten und Initialloesung geschrieben.
 *
 */
public class StartInitialloeung {

	public static void main(String[] args) {

		//Lese Daten ein (fuer den Pfad siehe data --> Rechtsklick auf die gewuenschte Datei --> Properties)
		ProjectReadIn test = new ProjectReadIn("/Users/XuanSon/Desktop/Java/simultane-planung/simultane-planung/data/full_sample_real_1296_SF_88_stoppoints.txt");
		
		Initialloesung p = new Initialloesung(test.deadruntimes, test.servicejourneys, test.stoppoints);
		Vector<Fahrzeugumlauf> initialloesung = p.erstelleInitialloesung();
		HashMap<String, Double> savings;
		int iteration = 0;
		double valueSaving = 0.0;
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			// waehle Zielpfad und Name der Ergebnis-Datei aus
			fw = new FileWriter("/Users/XuanSon/Desktop/Java/simultane-planung/simultane-planung/data/1296_SF_88_HS_initialloesung_mitAlternativenKostenLadestation.txt", true);
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		bw = new BufferedWriter(fw); 
		pw = new PrintWriter(bw);
		
		
		do {
			valueSaving = 0.0;
			
			savings = p.savings(test.validEdges);
		
			System.out.println(iteration);
			
			p.neuerUmlaufplan(savings, iteration);
			
			iteration ++;

			for (Entry<String, Double> e: savings.entrySet()){ 
				if(e.getValue() > valueSaving){
					valueSaving = e.getValue();
				}
			}
		// Terminierungskriterium: Die Savings-Matrix ist leer oder es sind keine positiven Savings mehr vorhanden
		}while(!savings.isEmpty() && !(valueSaving <= 0)) ;
	
		/**
		 * Erzeuge in der Datei eine neue Relation fuer die Haltestellen
		 */
		pw.println("*;;;;;;;;;;");
		pw.println("* Initialloesung;;;;;;;;;;");
		pw.println("*;;;;;;;;;;");
		pw.println("$INITIALSTOPPOINT:ID;isLoadingstation;frequency");
		
		/**
		 * Durchlaufe alle Haltestellen und pruefe, ob eine Ladestation gebaut wurde
		 * und wie die Frequentierung an dieser Ladestation ist.
		 * Erzeuge daraus String, um sie in die Datei zu schreiben.
		 */
		for (Map.Entry e: test.stoppoints.entrySet()){
			Stoppoint i1 = test.stoppoints.get(e.getKey());
			String stoppointId = i1.getId();
			String isLoadingstation;
			int counter = 0;
			String frequency = "0";
			if (i1.isLadestation()) {
				isLoadingstation = "true";
				for (int i = 0; i < p.getInitialloesung().size(); i++) {
					for (int j = 0; j < p.getInitialloesung().get(i).getLaden().size(); j++) {
						if(p.getInitialloesung().get(i).getLaden().get(j).getId().equals(i1.getId())){
							counter ++;
						}
					}
				}
				frequency = "" + counter + "";
			}
			else{
				isLoadingstation = "false";
			}
			if(counter == 0){
				isLoadingstation = "false";
			}
			pw.println(stoppointId + ";" + isLoadingstation + ";" + frequency);
			pw.flush();
		}
		
		/**
		 * Erzeuge in der Datei eine neue Relation für den Umlaufplan der Initialloesung
		 */
		pw.println("*;;;;;;;;;;");
		pw.println("* Initialloesung;;;;;;;;;;");
		pw.println("*;;;;;;;;;;");
		pw.println("$Umlauf:ID;Fahrten;;;;;;;;");
		
		for (int j = 0; j < initialloesung.size(); j++) {
			String umlaufId = String.valueOf(j);
			System.out.println(umlaufId + ";" + initialloesung.get(j).toStringIds());
			pw.println(umlaufId + ";" + initialloesung.get(j).toStringIds() + ";" + initialloesung.get(j).getLadenString());
			pw.flush();
		}
	}

}
