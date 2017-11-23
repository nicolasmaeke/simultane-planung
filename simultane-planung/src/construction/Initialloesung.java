package construction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import helper.feasibilityHelper;

import java.util.Map.Entry;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Journey;
import model.Servicejourney;
import model.Stoppoint;

/**
 * 
 * Klasse repraesentiert die Initialloesung mit Pendeltouren, d.h.
 * dass jede Servicefahrt genau einem Fahrzeugumlauf zugewiesen wird.
 * Ein Fahrzeugumlauf besteht dann genau aus der Fahrt vom Depot zur Servicefahrt,
 * der Servicefahrt selbst und der Fahrt von der Servicefahrt zurueck zum Depot.
 *
 */
public class Initialloesung {
	
	private Vector<Fahrzeugumlauf> fahrzeugumlaeufe;
	
	public Initialloesung(){
		fahrzeugumlaeufe = new Vector<Fahrzeugumlauf>();
	}

	/**
	 * Methode fuegt jedem Fahrzeugumlauf die beiden Leerfahrten vom- und zum Depot sowie eine Servicefahrt hinzu
	 * @param servicejourneys
	 * @param deadruntimes
	 * @return
	 */
	public Vector<Fahrzeugumlauf> erstelleInitialloesung(HashMap<String, Servicejourney> servicejourneys, HashMap<String, Deadruntime> deadruntimes){
		
		for (Entry<String, Servicejourney> i: servicejourneys.entrySet()){
			Fahrzeugumlauf j = new Fahrzeugumlauf(i.getKey());
			String depot = "00001";
			Servicejourney test = servicejourneys.get(i.getKey());
			
			String key = depot + test.getFromStopId();  
			
			j.addFahrtAfterFahrt(0, deadruntimes.get(key));
			
			j.addFahrtAfterFahrt(1,test); // jedem Fahrzeugumlauf wird genau eine Servicefahrt zugewiesen 
			
			key = test.getToStopId() + depot;

			j.addFahrtAfterFahrt(j.size(), deadruntimes.get(key));
			
			fahrzeugumlaeufe.add(j);
		}
		
		return fahrzeugumlaeufe;
		
	}
	
	/**
	 * Methode erstellt eine Savings-Matrix als HashMap. 
	 * Darin werden die Einsparpotenziale gespeichert, für den Fall, 
	 * dass zwei Servicefahrten in einem Fahrzeugumlauf bedient werden.
	 * @param validEdges
	 * @param deadruntimes
	 * @return
	 */
	public HashMap<String, Integer> savings(HashMap<String, Integer> validEdges, HashMap<String, Deadruntime> deadruntimes){
		
		// Key: ID's der beiden Servicefahrten, die zusammengelegt werden sollen
		// Value: Savings, falls die beiden Servicefahrten zuammengelegt werden
		HashMap <String, Integer> savings = new HashMap<String, Integer>();  
		
		Journey startknotenVonFu1 = null; // die erste Servicefahrt im Fahrzeugumlauf 1
		String keySkFu1 = ""; // der Schlüssel der ersten Servicefahrt im Fahrzeugumlauf 1
		Journey endknotenVonFu1 = null; // die letzte Servicefahrt im Fahrzeugumlauf 1
		String keyEkFu1 = ""; // der Schlüssel der letzten Servicefahrt im Fahrzeugumlauf 1
		
		Journey startknotenVonFu2 = null; // die erste Servicefahrt im Fahrzeugumlauf 2
		String keySkFu2 = ""; // der Schlüssel der ersten Servicefahrt im Fahrzeugumlauf 2
		Journey endknotenVonFu2 = null; // die letzte Servicefahrt im Fahrzeugumlauf 2
		String keyEkFu2 = ""; // der Schlüssel der letzten Servicefahrt im Fahrzeugumlauf 2
		
		Deadruntime neu = null; // die Leerfahrt, die in den Fahrzeugumlauf hinzugefügt werden muss, um die beiden Servicefahrten zu verbinden
		
		/**
		 * In der doppelten For-Schleife werden immer zwei Fahrzeugumlaeufe betrachtet.
		 * Von den beiden Fahrzeugumlaeufen werden immer die erste und die letzte Servicefahrt betrachtet.
		 */
		for (int i = 0; i < fahrzeugumlaeufe.size(); i++) {
			startknotenVonFu1 = fahrzeugumlaeufe.get(i).getAtIndex(1); // erste Servicefahrt im Fahrzeugumlauf i
			keySkFu1 = startknotenVonFu1.getId();
			endknotenVonFu1 = fahrzeugumlaeufe.get(i).getAtIndex(fahrzeugumlaeufe.get(i).size()-2); // letzte Servicefahrt im Fahrzeugumlauf i
			keyEkFu1 = endknotenVonFu1.getId();
			for (int j = 0; j < fahrzeugumlaeufe.size(); j++) {
				if (i != j){
					startknotenVonFu2 = fahrzeugumlaeufe.get(j).getAtIndex(1); // erste Servicefahrt im Fahrzeugumlauf j
					keySkFu2 = startknotenVonFu2.getId();
					endknotenVonFu2 = fahrzeugumlaeufe.get(j).getAtIndex(fahrzeugumlaeufe.get(i).size()-2); // letzte Servicefahrt im Fahrzeugumlauf j
					keyEkFu2 = endknotenVonFu2.getId();
					
					/**
					 * Falls es eine zulässige Verbindung zwischen der letzten Servicefahrt von Fahrzeugumlauf i
					 * mit der ersten Servicefahrt von Fahrzeugumlauf j gibt, werden die Savings berechnet.
					 */
					if(validEdges.get(""+keyEkFu1+keySkFu2) == 1){
						neu = deadruntimes.get(""+endknotenVonFu1.getToStopId()+startknotenVonFu2.getFromStopId());
						savings.put(""+keyEkFu1+keySkFu2, calculateSavings(fahrzeugumlaeufe.get(i),fahrzeugumlaeufe.get(j), neu));
					}
					
					/**
					 * Falls es eine zulässige Verbindung zwischen der letzten Servicefahrt von Fahrzeugumlauf j
					 * mit der ersten Servicefahrt von Fahrzeugumlauf i gibt, werden die Savings berechnet.
					 */
					if(validEdges.get(""+keyEkFu2+keySkFu1) == 1){
						neu = deadruntimes.get(""+endknotenVonFu2.getToStopId()+startknotenVonFu1.getFromStopId());
						savings.put(""+keyEkFu2+keySkFu1, calculateSavings(fahrzeugumlaeufe.get(j),fahrzeugumlaeufe.get(i), neu));
					}
				}	
			}
		}
		return savings;
	}
	

	/**
	 * Methode zum berechnen der Savings, falls zwei Servicefahrten in einem Fahrzeugumlauf bedient werden.
	 * 
	 * @param i
	 * @param j
	 * @param deadrun
	 * @return
	 */
	private int calculateSavings(Fahrzeugumlauf i, Fahrzeugumlauf j, Deadruntime deadrun) {
		int saving = 0;
		int d1 = j.getFahrten().getFirst().getDistance(); // Distanz zwischen Depot und Servicefahrt
		int d2 = i.getFahrten().get(j.size()-1).getDistance(); // Distanz zwischen Servicefahrt und Depot
		saving = d1 + d2 - deadrun.getDistance() + 400000; // Distanz zwischen beiden Servicefahrten
		return saving;
	}
	
	private int newLoadingstations(HashMap<String, Integer> savings, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Stoppoint> stoppoints, HashMap<String, Servicejourney> servicejourneys){
		int numberOfNewStations = 0;
		
		String key = getHighestSaving(savings);
		String key1 = key.substring(0, 4);
		String key2 = key.substring(5, 9);
		Fahrzeugumlauf eins = null;
		Fahrzeugumlauf zwei = null;
		LinkedList<Journey> neu = null;
		double kapazitaet = 80.0; //kWh
		
		for (int i = 0; i < fahrzeugumlaeufe.size(); i++) {
			for (int j = 0; j < fahrzeugumlaeufe.get(i).getFahrten().size(); j++) {
				if(fahrzeugumlaeufe.get(i).getFahrten().get(j).getId() == key1){
					eins = fahrzeugumlaeufe.get(i);
				}
				if(fahrzeugumlaeufe.get(i).getFahrten().get(j).getId() == key2){
					zwei = fahrzeugumlaeufe.get(i);
				}
			}
		}
		
		eins.getFahrten().removeLast();
		zwei.getFahrten().removeFirst();
		neu = eins.getFahrten();
		neu.add(deadruntimes.get(eins.getFahrten().getLast().getId()+zwei.getFahrten().getFirst().getId()));
		neu.addAll(zwei.getFahrten());
		
		for (int i = 0; i < neu.size(); i++) { // zusammengesetzter Fahrzeugumlauf
			int letzteLadung = 0;
			if (kapazitaet - neu.get(i).getVerbrauch() < 0){
				if(neu.get(i) instanceof Servicejourney){
					int x = 0;
					while(neu.get(i-2-x) != neu.get(1) || ((i - 2 - x) <= letzteLadung)){
						if(feasibilityHelper.zeitpufferFuerLadezeit(neu.get(i-2-x).getId(), neu.get(i-x).getId(), deadruntimes, servicejourneys, kapazitaet)){
							if (!stoppoints.get(neu.get(i-x).getId()).isLadestation()){ // i - x ist die Starthaltestelle der Servicefahrt i
								stoppoints.get(neu.get(i-x).getId()).setLadestation(true);
								numberOfNewStations ++;
								kapazitaet = 80;
								i = i - x; // i muss zurueckgesetzt werden, um dort zu starten, wo die Kapazitaet wieder bei 80 ist
								letzteLadung = i - x; // merkt sich, an welcher Stelle die letzte Ladung erfolgt ist
								break;
							}else{ // es ist schon eine Ladestation vorhanden an Haltestelle i - x
								kapazitaet = 80;
								i = i - x;
								letzteLadung = i - x;
								break;
							} 
						}
						x = x + 2;
					}
					if(kapazitaet != 80){ // wenn nicht geladen werden konnte, dann lade vor Servicefahrt 1 (da geht es zeitlich immer)
						if(letzteLadung != 1){ // schon einmal vor Servicefahrt 1 geladen?
							if (!stoppoints.get(neu.get(1).getId()).isLadestation()){
								stoppoints.get(neu.get(1).getId()).setLadestation(true);
								numberOfNewStations ++;
								kapazitaet = 80;
								i = 1;
								letzteLadung = 1;
							}
						}
						else{
							return -1; // es wird zum zweiten mal versucht vor Servicefahrt 1 zu laden --> Endlosschleife: Fahrzeugumlauf nicht moeglich 
						}
					}
				}	
				}	
				kapazitaet = kapazitaet - neu.get(i).getVerbrauch();
				
				if(neu.get(i) instanceof Deadruntime){
					
				}
		}
		return numberOfNewStations;
	}
	
	private String getHighestSaving(HashMap<String, Integer> savings){
		int temp = 0;
		String key = "";
		for (Entry<String, Integer> e: savings.entrySet()){
			if(e.getValue() > temp){
				temp = e.getValue();
				key = e.getKey();
			}
		}
		return key;
	}

	public Vector<Fahrzeugumlauf> getInitialloesung() {
		return fahrzeugumlaeufe;
	}

	public void setInitialloesung(Vector<Fahrzeugumlauf> initialloesung) {
		this.fahrzeugumlaeufe = initialloesung;
	}
	
}
