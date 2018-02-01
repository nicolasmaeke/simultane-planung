package start;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.rits.cloning.Cloner;
import heuristic.variableNeighborhoodSearch;
import model.Servicejourney;
import model.Stoppoint;
import output.Schedule;
import parser.ProjectReadInWithInitialSolution;

public class StartAfterInitialSolution {

	public static void main(String[] args) {
		
		ProjectReadInWithInitialSolution test = new ProjectReadInWithInitialSolution("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_sample_real_433_SF_207_stoppoints_initialloesung.txt");
		
		//System.out.println(test.validEdges);
		
		for (int i = 0; i < test.global.getUmlaufplan().size(); i++) {
			if(!test.global.isFeasible(test.global.getUmlaufplan().get(i))){
				System.err.println("Is not Feasible!");
			}
		}
		
		for (int i = 0; i < test.global.getUmlaufplan().size(); i++) {
			System.out.println(test.global.getUmlaufplan().get(i).toString());
		}
		int numberOfLoadingStations = 0;
		
		Schedule globalSolution = test.global;
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		Double initialCost = globalSolution.berechneKosten();
		System.out.println(initialCost);
		System.out.println(globalSolution.getVariableKosten() - numberOfLoadingStations*250000);
		System.out.println(numberOfLoadingStations);
		System.out.println();
		
		Schedule shakingSolution = null;
		Schedule localSolution = test.local;
		
		for (int i = 0; i < test.global.getUmlaufplan().size(); i++) {
			if(!test.global.isFeasible(test.global.getUmlaufplan().get(i))){
				System.err.println("Is not Feasible!");
			}
		}
		
		int counter = 0;
		double globalCost = initialCost;
		do {

			variableNeighborhoodSearch verbesserung = new variableNeighborhoodSearch(localSolution, test.validEdges, test.deadruntimes, test.servicejourneys);
			
			shakingSolution = verbesserung.shaking(); // starte shaking
			
			localSolution = verbesserung.bestImprovement(14, shakingSolution); // starte bestImrpvement mit der shaking-Loesung
			
			double localCost = localSolution.berechneKosten();
			for (int i = 0; i < localSolution.getUmlaufplan().size(); i++) {
				if(!localSolution.isFeasible(localSolution.getUmlaufplan().get(i))){
					System.err.println(i + "Local is not Feasible!");
				}
			}
			if(localCost < globalCost){ // wenn die Kosten der lokalen Loesung geringer, dann überschreibe globale Loesung
				globalCost = localCost;
				
				/**
				Vector<Fahrzeugumlauf> copy = new Vector<Fahrzeugumlauf>();
				for (int i = 0; i < localSolution.getUmlaufplan().size(); i++) {
					Fahrzeugumlauf neu = new Fahrzeugumlauf(localSolution.getUmlaufplan().get(i).getId());
					neu.addFahrten(localSolution.getUmlaufplan().get(i).getFahrten());
					neu.setLaden(new LinkedList<Stoppoint>(localSolution.getUmlaufplan().get(i).getLaden()));
					copy.add(neu);
				}
				HashMap<String, Stoppoint> stoppoints = new  HashMap<String, Stoppoint>();
				
				for (Map.Entry e: test.global.getStoppoints().entrySet()){
					Stoppoint neu = new Stoppoint(test.global.getStoppoints().get(e.getKey()).getId());
					stoppoints.put(test.global.getStoppoints().get(e.getKey()).getId(), neu);
				}
				// Erstelle Kopie der lokalen Loesung zum Ueberschreiben der globalen Loesung
				Schedule globalCopy = new Schedule(copy, test.servicejourneys, test.deadruntimes, stoppoints);
				
				globalCopy.berechneFrequenzen();
				
				for (int i = 0; i < globalCopy.getUmlaufplan().size(); i++) {
					if(!globalCopy.isFeasible(globalCopy.getUmlaufplan().get(i))){
						System.err.println("Is not Feasible!");
					}
				}
				
				globalSolution = globalCopy;
				*/
				
				Cloner clone = new Cloner();
				globalSolution = clone.deepClone(localSolution);
				for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
					if(!globalSolution.isFeasible(globalSolution.getUmlaufplan().get(i))){
						System.err.println(i + "Global is not Feasible!");
					}
				}
				System.out.println("global aktualisiert!");
			}
			
			counter ++;
			System.err.println(counter);

		} while (counter < 350); // Abbruchkriterium fuer Heuristik
		
		globalSolution.berechneFrequenzen();
		globalSolution.setAnzahlLadestationen();
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		List<Stoppoint> ladestationen = new ArrayList<Stoppoint>();
		
		for (Map.Entry e: globalSolution.getStoppoints().entrySet()){
			Stoppoint i1 = globalSolution.getStoppoints().get(e.getKey());
			System.out.println("Haltestelle " + i1.getId() + " hat Ladestation: " + i1.isLadestation() + " " + i1.getFrequency());
			if(i1.isLadestation()){
				ladestationen.add(i1);
			}
		}
		
		System.out.println("List of Loadingstations: " + ladestationen);
		
		int anzahlUmlaeufe = 0;
		
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			System.out.println(globalSolution.getUmlaufplan().get(i).toString());
			System.out.println("Ladestationen: " + globalSolution.getUmlaufplan().get(i).getLadenString());
			System.out.println("An welcher SF wird geladen: " + globalSolution.getUmlaufplan().get(i).getStellen());
			System.out.println();
			anzahlUmlaeufe ++;
		}
		
		System.out.println("Kosten nach Verbesserung: " + globalCost);
		System.out.println("Variable Kosten: " + (globalSolution.getVariableKosten() - numberOfLoadingStations*250000));
		System.out.println("Anzahl Umlaeufe: " + anzahlUmlaeufe);
		System.out.println("Ersparnis: " + (initialCost - globalCost));
		System.out.println("Anzahl Ladestationen: " + numberOfLoadingStations);
		
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			if(!globalSolution.isFeasible(globalSolution.getUmlaufplan().get(i))){
				System.err.println(globalSolution.getUmlaufplan().get(i).getId() + " Is not Feasible!");
			}
		}
	
		
		// teste, ob die Anzahl der SF noch korrekt ist und ob keine SF doppelt vorkommt
		int anzahlSF = 0;
		List<String> sf = new LinkedList<String>();
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			for (int j = 0; j < globalSolution.getUmlaufplan().get(i).getFahrten().size(); j++) {
				if(globalSolution.getUmlaufplan().get(i).getFahrten().get(j) instanceof Servicejourney){
					anzahlSF++;
					if(sf.contains(globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getId())){
						System.out.println("Fehler"+ globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getId());
					}
					else{
						sf.add(globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getId());
					}
				}
			}	
		}
		System.out.println("Anzahl Servicefahrten: " + anzahlSF);
		
	}
	
	
}
