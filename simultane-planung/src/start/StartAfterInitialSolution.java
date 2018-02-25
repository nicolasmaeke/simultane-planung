package start;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.rits.cloning.Cloner;
import heuristic.variableNeighborhoodSearch;
import model.Stoppoint;
import output.Schedule;
import parser.ProjectReadInWithInitialSolution;

/**
 * Klasse startet die Verbesserungsheuristik.
 * @input: Eine Datei mit Initialloesung muss eingelesen werden.
 * @output: Eine Ergebnisdatei wird neu geschrieben.
 *  
 */
public class StartAfterInitialSolution {

	public static void main(String[] args) {
		
		//Lese Initialleosung ein (fuer den Pfad siehe data --> Rechtsklick auf die gewuenschte Datei --> Properties)
		ProjectReadInWithInitialSolution test = new ProjectReadInWithInitialSolution("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_sample_real_867_SF_207_stoppoints_initialloesung.txt");
		
		//Teste, ob die eingelesene Initialloesung zulaessig ist
		for (int i = 0; i < test.global.getUmlaufplan().size(); i++) {
			if(!test.global.isFeasible(test.global.getUmlaufplan().get(i))){
				System.err.println("Is not Feasible!");
			}
		}
		
		//Gebe Initialloesung aus
		for (int i = 0; i < test.global.getUmlaufplan().size(); i++) {
			System.out.println(test.global.getUmlaufplan().get(i).toString());
		}
		
		//Setze und initialisiere Variablen
		double verbrauchInit = 0;
		double zeitInit = 0;
		int numberOfLoadingStations = 0;
		Schedule globalSolution = test.global;
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		Double initialCost = globalSolution.berechneKosten();
		Schedule shakingSolution = null;
		Schedule localSolution = test.local;
		
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			for (int j = 0; j < globalSolution.getUmlaufplan().get(i).getFahrten().size(); j++) {
				verbrauchInit = verbrauchInit + globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getVerbrauch();
				zeitInit = zeitInit + globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getRuntime();
			}
		}
		
		//Gebe Kennzahlen vor der Verbesserung aus
		System.out.println("Gesamtverbrauch in kWh: " + verbrauchInit);
		System.out.println("Zeitdauer: " + zeitInit/1000/60/60);
		System.out.println(initialCost);
		System.out.println(globalSolution.getVariableKosten() - numberOfLoadingStations*250000);
		System.out.println(numberOfLoadingStations);
		System.out.println();
		System.out.println(1.0512355987399999E7-numberOfLoadingStations*250000);

		//Schreibe eine neue Datei mit der Loesung
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		try {
			// waehle Zielpfad und Name der Ergebnis-Datei aus
			fw = new FileWriter("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_sample_real_867_SF_207_stoppoints_initialloesung_ergebnis.txt", true);
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		bw = new BufferedWriter(fw); 
		pw = new PrintWriter(bw);
		
		pw.println("*;;;;;;;;;;");
		pw.println("* Ergebnis;;;;;;;;;;");
		pw.println("*;;;;;;;;;;");
		pw.println("Ergebnis: Iteration; Kosten; Fahrzeuge; Ladestationen");
		
		int counter = 0;
		double globalCost = initialCost;
		
		pw.println(counter + ";" + globalSolution.berechneKosten() + ";" + globalSolution.getAnzahlBusse() + ";" + globalSolution.getAnzahlLadestationen());
		pw.flush();

		do {

			variableNeighborhoodSearch verbesserung = new variableNeighborhoodSearch(localSolution, test.validEdges, test.deadruntimes, test.servicejourneys);
			
			shakingSolution = verbesserung.shaking(); // starte shaking
			
			localSolution = verbesserung.bestImprovement(50, shakingSolution); // starte bestImrpvement mit der shaking-Loesung
			
			double localCost = localSolution.berechneKosten();
			for (int i = 0; i < localSolution.getUmlaufplan().size(); i++) {
				if(!localSolution.isFeasible(localSolution.getUmlaufplan().get(i))){
					System.err.println(i + "Local is not Feasible!");
				}
			}
			if(localCost < globalCost){ // wenn die Kosten der lokalen Loesung geringer, dann überschreibe globale Loesung
				globalCost = localCost;
				
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
			
			pw.println(counter + ";" + globalCost + ";" + globalSolution.getAnzahlBusse() + ";" + globalSolution.getAnzahlLadestationen());
			pw.flush();

		} while (counter < 1000); // Abbruchkriterium fuer Heuristik
		
		globalSolution.berechneFrequenzen();
		globalSolution.setAnzahlLadestationen();
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		
		//Erstelle eine Liste der Ladestationen mit ihren Ladefrequenzen und gebe sie aus
		List<Stoppoint> ladestationen = new ArrayList<Stoppoint>();
		for (Map.Entry e: globalSolution.getStoppoints().entrySet()){
			Stoppoint i1 = globalSolution.getStoppoints().get(e.getKey());
			System.out.println("Haltestelle " + i1.getId() + " hat Ladestation: " + i1.isLadestation() + " " + i1.getFrequency());
			if(i1.isLadestation()){
				ladestationen.add(i1);
			}
		}
		System.out.println("List of Loadingstations: " + ladestationen);
		
		//Berechne Anzahl Umlaeufe und gebe Umlaeufe aus
		int anzahlUmlaeufe = 0;
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			System.out.println(globalSolution.getUmlaufplan().get(i).toString());
			System.out.println("Ladestationen: " + globalSolution.getUmlaufplan().get(i).getLadenString());
			System.out.println("An welcher SF wird geladen: " + globalSolution.getUmlaufplan().get(i).getStellen());
			System.out.println();
			anzahlUmlaeufe ++;
		}
		
		//Gebe Kennzahlen nach der Verbesserung aus
		System.out.println("Kosten nach Verbesserung: " + globalCost);
		System.out.println("Variable Kosten: " + (globalSolution.getVariableKosten() - numberOfLoadingStations*250000));
		System.out.println("Anzahl Umlaeufe: " + anzahlUmlaeufe);
		System.out.println("Ersparnis: " + (initialCost - globalCost));
		System.out.println("Anzahl Ladestationen: " + numberOfLoadingStations);
		
		//Berechne den Verbrauch und die Dauer des Umlaufplans
		double verbrauch = 0;
		double	zeit = 0;
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			for (int j = 0; j < globalSolution.getUmlaufplan().get(i).getFahrten().size(); j++) {
				verbrauch = verbrauch + globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getVerbrauch();
				zeit = zeit + globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getRuntime();
			}
			if(!globalSolution.isFeasible(globalSolution.getUmlaufplan().get(i))){
				System.err.println(globalSolution.getUmlaufplan().get(i).getId() + " Is not Feasible!");
			}
			if(globalSolution.getUmlaufplan().get(i).size() > 20 && globalSolution.getUmlaufplan().get(i).getLaden().size() < 1){
				globalSolution.isFeasible(globalSolution.getUmlaufplan().get(i));
			}
		}
		System.out.println("Gesamtverbrauch in kWh: " + verbrauch);
		System.out.println("Zeitdauer: " + zeit/1000/60/60);
		
		//Berechne die durchschnittliche Laenge der Fahrzeugumlaeufe
		double durchschnittLaengeFahrzeugumlaeufe = 0.0;
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			for (int j = 0; j < globalSolution.getUmlaufplan().get(i).getFahrten().size(); j++) {
				durchschnittLaengeFahrzeugumlaeufe = durchschnittLaengeFahrzeugumlaeufe + globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getDistance();
			}
		}
		durchschnittLaengeFahrzeugumlaeufe = durchschnittLaengeFahrzeugumlaeufe * 1/(globalSolution.getAnzahlBusse());
		System.out.println("durchschnittliche Laenge eines Umlaufs: " + durchschnittLaengeFahrzeugumlaeufe);
	}
}
