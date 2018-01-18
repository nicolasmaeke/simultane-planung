package start;

import java.util.Map;

import heuristic.variableNeighborhoodSearch;
import model.Stoppoint;
import output.Schedule;
import parser.ProjectReadInWithInitialSolution;

public class StartAfterInitialSolution {

	public static void main(String[] args) {
		
		ProjectReadInWithInitialSolution test = new ProjectReadInWithInitialSolution("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_sample_real_433_SF_207_stoppoints_initialloesung.txt");
		
		//System.out.println(test.validEdges);
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			System.out.println(test.fahrzeugumlaeufe.get(i).toString());
		}
		int numberOfLoadingStations = 0;
		
		/**
		for (Map.Entry e: test.stoppoints.entrySet()){
			Stoppoint i1 = test.stoppoints.get(e.getKey());
			if (i1.isLadestation()) {
				numberOfLoadingStations ++;
			}
		}
		*/
		
		Schedule solution = new Schedule(test.fahrzeugumlaeufe, test.stoppoints);
		numberOfLoadingStations = solution.getAnzahlLadestationen();
		Double initialkosten = solution.berechneKosten();
		System.out.println(initialkosten);
		System.out.println(numberOfLoadingStations);
		System.out.println();
		
		Schedule solution2 = null;
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			if(!test.fahrzeugumlaeufe.get(i).isFeasible(test.stoppoints, test.servicejourneys, test.deadruntimes)){
				System.err.println("Is not Feasible!");
			}
		}
		
		int counter = 0;
		double neueKosten = initialkosten;
		do {
			variableNeighborhoodSearch verbesserung = new variableNeighborhoodSearch(test.fahrzeugumlaeufe, test.validEdges, test.deadruntimes, test.servicejourneys, test.stoppoints);
			verbesserung.shaking();
			verbesserung.bestImprovement(4);
			solution2 = new Schedule(test.fahrzeugumlaeufe, test.stoppoints);
			neueKosten = solution2.berechneKosten();
			counter ++;
			System.err.println(counter);
		} while (counter < 50);
		
		numberOfLoadingStations = solution2.getAnzahlLadestationen();
		
		/**
		for (Map.Entry e: test.stoppoints.entrySet()){
			Stoppoint i1 = test.stoppoints.get(e.getKey());
			//System.out.println(""+ i1.isLadestation() + i1.getFrequency());
			if (i1.isLadestation()) {
				numberOfLoadingStations ++;
			}
		*/
		
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			if(!test.fahrzeugumlaeufe.get(i).isFeasible(test.stoppoints, test.servicejourneys, test.deadruntimes)){
				System.err.println("Is not Feasible!");
			}
		}
	
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			System.out.println(test.fahrzeugumlaeufe.get(i).toString());
			System.out.println(test.fahrzeugumlaeufe.get(i).getLadenString());
		}
		
		System.out.println(neueKosten);
		System.out.println();
		System.out.println(initialkosten - neueKosten);
		System.out.println(numberOfLoadingStations);
	}
}
