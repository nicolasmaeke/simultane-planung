package construction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
			endknotenVonFu1 = fahrzeugumlaeufe.get(i).getAtIndex(fahrzeugumlaeufe.get(i).getFahrten().size()-2); // letzte Servicefahrt im Fahrzeugumlauf i
			keyEkFu1 = endknotenVonFu1.getId();
			for (int j = 0; j < fahrzeugumlaeufe.size(); j++) {
				if (i != j){
					startknotenVonFu2 = fahrzeugumlaeufe.get(j).getAtIndex(1); // erste Servicefahrt im Fahrzeugumlauf j
					keySkFu2 = startknotenVonFu2.getId();
					endknotenVonFu2 = fahrzeugumlaeufe.get(j).getAtIndex(fahrzeugumlaeufe.get(j).getFahrten().size()-2); // letzte Servicefahrt im Fahrzeugumlauf j
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
		int d2 = i.getFahrten().getLast().getDistance(); // Distanz zwischen Servicefahrt und Depot
		saving = d1 + d2 - deadrun.getDistance() + 400000; // Distanz zwischen beiden Servicefahrten
		return saving;
	}
	
	public Vector<Fahrzeugumlauf> neuerUmlaufplan(HashMap<String, Integer> savings,  HashMap<String, Deadruntime> deadruntimes, HashMap<String, Stoppoint> stoppoints, HashMap<String, Servicejourney> servicejourneys){
		
		String temp;
		List<String> keys = new ArrayList<String>(); // Keys die schon dran waren
		LinkedList<Journey> neu = null;
		HashMap <String, ArrayList<Stoppoint>> numberOfNewLoadingStations = null;
		if(savings.isEmpty()){
			return fahrzeugumlaeufe;
		}
		do {
			temp = getHighestSaving(savings);
			if(savings.get(temp) <= 0){
				return fahrzeugumlaeufe;
			}
			if(!keys.contains(temp)){
				neu = umlaeufeZusammenlegen(temp, deadruntimes);
				numberOfNewLoadingStations = newLoadingstations(neu, temp, deadruntimes, stoppoints, servicejourneys);
				if (numberOfNewLoadingStations.get(temp) == null){
					savings.put(temp, 0);
				}else{
					int kosten = numberOfNewLoadingStations.get(temp).size() * 250000;
					int neueSavings = savings.get(temp) - kosten;
					savings.put(temp, neueSavings);
				}
			}
			keys.add(temp);
		} while (temp != getHighestSaving(savings));
		//fahrzeugumlaeufe aktualisieren
		ArrayList<Fahrzeugumlauf> umlaeufe = umlaeufeFinden(temp.substring(0, 5), temp.substring(5, 10));
		for (int i = 0; i < fahrzeugumlaeufe.size(); i++) {
			if(fahrzeugumlaeufe.get(i).getId() == umlaeufe.get(0).getId()){
				fahrzeugumlaeufe.get(i).setFahrten(neu);
			}
		}
		for(int i = 0; i < fahrzeugumlaeufe.size(); i++){
			if(fahrzeugumlaeufe.get(i).getId() == umlaeufe.get(1).getId()){
				fahrzeugumlaeufe.remove(i);
			}
		}
		ArrayList<Stoppoint> buildLoadingStations = numberOfNewLoadingStations.get(temp);
		// Setzen der Ladestationen an den Haltestellen
		for (int i = 0; i < buildLoadingStations.size(); i++) {
			buildLoadingStations.get(i).setLadestation(true);
		}
		return fahrzeugumlaeufe;
	}
	
	private ArrayList <Fahrzeugumlauf> umlaeufeFinden(String key1, String key2){
		Fahrzeugumlauf eins = null;
		Fahrzeugumlauf zwei = null;
		
		// while-Schleife drum machen: solange eins und zwei null sind
		for (int i = 0; i < fahrzeugumlaeufe.size(); i++) { // suche die beiden Fahrzeugumläufe die verbunden werden sollen
			for (int j = 0; j < fahrzeugumlaeufe.get(i).getFahrten().size(); j++) {
				if(fahrzeugumlaeufe.get(i).getFahrten().get(j).getId().equals(key1)){
					eins = fahrzeugumlaeufe.get(i);
				}
				if(fahrzeugumlaeufe.get(i).getFahrten().get(j).getId().equals(key2)){
					zwei = fahrzeugumlaeufe.get(i);
				}
			}
		}
		ArrayList umlaeufe = new ArrayList<Fahrzeugumlauf>();
		umlaeufe.add(eins);
		umlaeufe.add(zwei);
		return umlaeufe;
	}
	
	private LinkedList<Journey> umlaeufeZusammenlegen(String keyOfHighestSavings, HashMap<String, Deadruntime> deadruntimes){

		String key = keyOfHighestSavings;
		String key1 = key.substring(0, 5);
		String key2 = key.substring(5, 10);
		LinkedList<Journey> neu = null;
		
		ArrayList<Fahrzeugumlauf> umlaeufe = umlaeufeFinden(key1, key2);
	
		LinkedList<Journey> eins = new LinkedList<Journey>();
		LinkedList<Journey> zwei = new LinkedList<Journey>();
		eins.addAll(umlaeufe.get(0).getFahrten());
		zwei.addAll(umlaeufe.get(1).getFahrten());
		eins.removeLast();
		zwei.removeFirst();
		neu = eins;
		neu.add(deadruntimes.get(eins.getLast().getToStopId()+zwei.getFirst().getFromStopId()));
		neu.addAll(zwei);
		
		return neu;
	}
	
	private HashMap<String, ArrayList<Stoppoint>> newLoadingstations(LinkedList<Journey> neu, String keyOfHighestValue, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Stoppoint> stoppoints, HashMap<String, Servicejourney> servicejourneys){
		
		double kapazitaet = 80.0; //kWh
		HashMap <String, ArrayList<Stoppoint>> numberOfNewStations = new HashMap<String, ArrayList<Stoppoint>>();
		ArrayList<Stoppoint> list = new ArrayList<Stoppoint>();
		
		for (int i = 0; i < neu.size(); i++) { // zusammengesetzter Fahrzeugumlauf
			int letzteLadung = 0;
			if (kapazitaet - neu.get(i).getVerbrauch() < 0){
				if(neu.get(i) instanceof Servicejourney){
					int x = 0;
					while(neu.get(i-2-x) != neu.get(1) || ((i - 2 - x) <= letzteLadung)){
						if(feasibilityHelper.zeitpufferFuerLadezeit(neu.get(i-2-x).getId(), neu.get(i-x).getId(), deadruntimes, servicejourneys, kapazitaet)){
							if (!stoppoints.get(neu.get(i-x).getId()).isLadestation()){ // i - x ist die Starthaltestelle der Servicefahrt i
								list.add(stoppoints.get(neu));
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
								list.add(stoppoints.get(neu));
								kapazitaet = 80;
								i = 1;
								letzteLadung = 1;
							}
						}
						else{
							list.clear();
							list.add(null);
							numberOfNewStations.put(keyOfHighestValue, list);
							return numberOfNewStations; // es wird zum zweiten mal versucht vor Servicefahrt 1 zu laden --> Endlosschleife: Fahrzeugumlauf nicht moeglich 
						}
					}
				}	

				if(neu.get(i) instanceof Deadruntime){
					int x = 0;
					int y = 1;
					while(neu.get(i-2-x-y) != neu.get(1) || ((i - 2 - x - y) <= letzteLadung)){
						if(feasibilityHelper.zeitpufferFuerLadezeit(neu.get(i-2-x-y).getId(), neu.get(i-x-y).getId(), deadruntimes, servicejourneys, kapazitaet)){
							if (!stoppoints.get(neu.get(i-x-y).getId()).isLadestation()){ // i - x ist die Starthaltestelle der Servicefahrt i
								list.add(stoppoints.get(neu));
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
						y = 0;
						x = x + 2;
					}
					if(kapazitaet != 80){ // wenn nicht geladen werden konnte, dann lade vor Servicefahrt 1 (da geht es zeitlich immer)
						if(letzteLadung != 1){ // schon einmal vor Servicefahrt 1 geladen?
							if (!stoppoints.get(neu.get(1).getId()).isLadestation()){
								list.add(stoppoints.get(neu));
								kapazitaet = 80;
								i = 1;
								letzteLadung = 1;
							}
						}
						else{
							list.clear();
							list.add(null);
							numberOfNewStations.put(keyOfHighestValue, list);
							return numberOfNewStations; // es wird zum zweiten mal versucht vor Servicefahrt 1 zu laden --> Endlosschleife: Fahrzeugumlauf nicht moeglich 
						}
				}
				
				}	
				kapazitaet = kapazitaet - neu.get(i).getVerbrauch();
				
			}
		numberOfNewStations.put(keyOfHighestValue, list);
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
