package start;

import java.util.Map;
import java.util.Vector;

import heuristic.variableNeighborhoodSearch;
import model.Fahrzeugumlauf;
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
		
		Schedule globalSolution = new Schedule(test.fahrzeugumlaeufe, test.stoppoints, test.servicejourneys, test.deadruntimes);
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		Double initialCost = globalSolution.berechneKosten();
		System.out.println(initialCost);
		System.out.println(numberOfLoadingStations);
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			if(!globalSolution.getUmlaufplan().get(i).isFeasible(test.stoppoints, test.servicejourneys, test.deadruntimes)){
				System.err.println("Is not Feasible!");
			}
		}
		System.out.println();
		
		Schedule shakingSolution = null;
		Schedule localSolution = null;
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			if(!test.fahrzeugumlaeufe.get(i).isFeasible(test.stoppoints, test.servicejourneys, test.deadruntimes)){
				System.err.println("Is not Feasible!");
			}
		}
		
		long start = System.currentTimeMillis();
		long end = (System.currentTimeMillis()) + (60*1000);
		
		int counter = 0;
		double globalCost = initialCost;
		do {
			start = System.currentTimeMillis();
			variableNeighborhoodSearch verbesserung = new variableNeighborhoodSearch(globalSolution.getUmlaufplan(), test.validEdges, test.deadruntimes, test.servicejourneys, test.stoppoints);
			shakingSolution = verbesserung.shaking();
			localSolution = verbesserung.bestImprovement(20, shakingSolution);
			double localCost = localSolution.berechneKosten();
			if(localCost < globalCost){
				globalCost = localCost;
				globalSolution = localSolution;
			}
			counter ++;
			System.err.println(counter);

		} while (counter < 10000);
		
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		
		for (Map.Entry e: test.stoppoints.entrySet()){
			Stoppoint i1 = test.stoppoints.get(e.getKey());
			System.out.println(""+ i1.isLadestation() + i1.getFrequency());
		}
		
		
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			if(!globalSolution.getUmlaufplan().get(i).isFeasible(test.stoppoints, test.servicejourneys, test.deadruntimes)){
				System.err.println("Is not Feasible!");
			}
		}
	
		int anzahlUmlaeufe = 0;
		
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			System.out.println(globalSolution.getUmlaufplan().get(i).toString());
			System.out.println(globalSolution.getUmlaufplan().get(i).getLadenString());
			anzahlUmlaeufe ++;
		}
		
		System.out.println(globalCost);
		System.out.println(anzahlUmlaeufe);
		System.out.println(initialCost - globalCost);
		System.out.println(numberOfLoadingStations);
	}
}
