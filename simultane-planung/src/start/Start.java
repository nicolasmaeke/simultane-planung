package start;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import construction.Initialloesung;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Stoppoint;
import output.Schedule;
import parser.ProjectReadIn;

public class Start {

	public static void main(String[] args) {

		ProjectReadIn test = new ProjectReadIn("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_sample_real_867_SF_207_stoppoints.txt");
		
		Initialloesung p = new Initialloesung();
		Vector<Fahrzeugumlauf> initialloesung = p.erstelleInitialloesung(test.servicejourneys, test.deadruntimes);
		HashMap<String, Double> savings;
		int numberOfLoadingStations = 0;
		
		do {
			/**
			for (int j = 0; j < initialloesung.size(); j++) {
				System.out.println(initialloesung.get(j).getFahrten().toString());
			}
			*/
			savings = p.savings(test.validEdges, test.deadruntimes);
			
			
			System.out.println("Savings-Matrix: " + savings);
			
			for (Map.Entry e: test.stoppoints.entrySet()){
				Stoppoint i1 = test.stoppoints.get(e.getKey());
				if (i1.isLadestation()) {
					System.out.println("Ladestationen Haltestelle: " + i1.getId());
					numberOfLoadingStations ++;
				}
			}
		
			Schedule ergebnis = new Schedule(p.getInitialloesung(), test.stoppoints);
			
			System.out.println("Kosten für den Umlaufplan: " + ergebnis.berechneKosten());
			
			System.out.println("Anzahl Ladestationen: " + numberOfLoadingStations);
			
			numberOfLoadingStations = 0;

			System.out.println();
			
		}while(!savings.isEmpty());
	
	}

}
