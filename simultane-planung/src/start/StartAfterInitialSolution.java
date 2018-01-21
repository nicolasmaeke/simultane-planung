package start;

import java.util.Map;

import heuristic.variableNeighborhoodSearch;
import model.Fahrzeugumlauf;
import model.Stoppoint;
import output.Schedule;
import parser.ProjectReadInWithInitialSolution;

public class StartAfterInitialSolution {

	public static void main(String[] args) {
		
		ProjectReadInWithInitialSolution test = new ProjectReadInWithInitialSolution("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_sample_real_867_SF_207_stoppoints.txt");
		
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
		
		Schedule globalSolution = new Schedule(test.fahrzeugumlaeufe, test.stoppoints);
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		Double initialCost = globalSolution.berechneKosten();
		System.out.println(initialCost);
		System.out.println(numberOfLoadingStations);
		System.out.println();
		
		Schedule shakingSolution = null;
		Schedule localSolution = null;
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			if(!test.fahrzeugumlaeufe.get(i).isFeasible(test.stoppoints, test.servicejourneys, test.deadruntimes)){
				System.err.println("Is not Feasible!");
			}
		}
		
		int counter = 0;
		double globalCost = initialCost;
		do {
			variableNeighborhoodSearch verbesserung = new variableNeighborhoodSearch(test.fahrzeugumlaeufe, test.validEdges, test.deadruntimes, test.servicejourneys, test.stoppoints);
			shakingSolution = verbesserung.shaking();
			localSolution = verbesserung.bestImprovement(4, shakingSolution);
			double localCost = localSolution.berechneKosten();
			if(localCost < globalCost){
				globalCost = localCost;
				globalSolution = localSolution;
			}
			counter ++;
			System.err.println(counter);
		} while (counter < 50);
		
		numberOfLoadingStations = localSolution.getAnzahlLadestationen();
		
		for (Map.Entry e: test.stoppoints.entrySet()){
			Stoppoint i1 = test.stoppoints.get(e.getKey());
			System.out.println(""+ i1.isLadestation() + i1.getFrequency());
		}
		
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			if(!test.fahrzeugumlaeufe.get(i).isFeasible(test.stoppoints, test.servicejourneys, test.deadruntimes)){
				System.err.println("Is not Feasible!");
			}
		}
	
		int anzahlUmlaeufe = 0;
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			System.out.println(test.fahrzeugumlaeufe.get(i).toString());
			System.out.println(test.fahrzeugumlaeufe.get(i).getLadenString());
			anzahlUmlaeufe ++;
		}
		
		System.out.println(globalCost);
		System.out.println(anzahlUmlaeufe);
		System.out.println(initialCost - globalCost);
		System.out.println(numberOfLoadingStations);
	}
}
