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
	 * (Pendeltouren für jede Servicefahrt erstellen)- Methode fuegt jedem Fahrzeugumlauf die beiden Leerfahrten vom- und zum Depot sowie eine Servicefahrt hinzu
	 * 
	 * @param servicejourneys
	 * @param deadruntimes
	 * @return eine Liste von Fahrzeugumläufen
	 */
	public Vector<Fahrzeugumlauf> erstelleInitialloesung(HashMap<String, Servicejourney> servicejourneys, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Stoppoint> stoppoints){
		
		for (Entry<String, Servicejourney> i: servicejourneys.entrySet()){ //für jede Servicefahrt i
			Fahrzeugumlauf j = new Fahrzeugumlauf(i.getKey()); //erstelle einen neuen Fahrzeugumlauf mit dieser Servicefahrt
			String depot = "00001"; //Depot = 00001
			Servicejourney test = servicejourneys.get(i.getKey()); 
			
			String key = depot + test.getFromStopId(); // key ist 00001 + Starthaltestelle von Servicefarht i 
			
			j.addFahrtAfterFahrt(0, deadruntimes.get(key)); // die erste Fahrt ist eine Leerfahrt
			
			j.addFahrtAfterFahrt(1,test); // dann folgen Servicefahrt i
			// jedem Fahrzeugumlauf wird genau eine Servicefahrt zugewiesen 
			
			key = test.getToStopId() + depot; // key ist Endhaltestelle von Servicefahrt i + 00001

			j.addFahrtAfterFahrt(j.size(), deadruntimes.get(key));
			
			boolean feasibility = feasibilityHelper.isUmlaufFeasible(j, stoppoints);
			if (!feasibility) {
				System.out.println("nicht möglich!");
			}
			
			fahrzeugumlaeufe.add(j); // füge den Fahrzeugumlauf j zu der Gesamtliste 
		}
		
		return fahrzeugumlaeufe; // Gesamtliste von Fahrzeugumläufen zurückgeben
		
	}
	
	
	/**
	 * Methode erstellt eine Saving-Matrix als HashMap. 
	 * Darin werden die Einsparpotenziale gespeichert, für den Fall, 
	 * dass zwei Servicefahrten in einem Fahrzeugumlauf bedient werden können.
	 * @param validEdges
	 * @param deadruntimes
	 * @return ein Hashmap mit key ist zwei IDs von SF, value ist der Saving
	 */
	public HashMap<String, Double> savings(HashMap<String, Integer> validEdges, HashMap<String, Deadruntime> deadruntimes){
		
		// Key: IDs der beiden Servicefahrten, die zusammengelegt werden sollen
		// Value: Savings, falls die beiden Servicefahrten zuammengelegt werden
		HashMap <String, Double> savings = new HashMap<String, Double>();  
		
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
		for (int i = 0; i < fahrzeugumlaeufe.size(); i++) { // für jeden Fahrzeugumlauf i
			startknotenVonFu1 = fahrzeugumlaeufe.get(i).getAtIndex(1); // erste Servicefahrt im Fahrzeugumlauf i
			keySkFu1 = startknotenVonFu1.getId(); // ID der ersten Servicefahrt von Fahrzeugumlauf i
			endknotenVonFu1 = fahrzeugumlaeufe.get(i).getAtIndex(fahrzeugumlaeufe.get(i).getFahrten().size()-2); // letzte Servicefahrt im Fahrzeugumlauf i
			keyEkFu1 = endknotenVonFu1.getId(); // ID der letzten Servicefahrt von Fahrzeugumlauf i
			for (int j = 0; j < fahrzeugumlaeufe.size(); j++) { // für jeden Fahrzeugumlauf j
				if (i != j){ // falls i ungleich j
					startknotenVonFu2 = fahrzeugumlaeufe.get(j).getAtIndex(1); // erste Servicefahrt im Fahrzeugumlauf j
					keySkFu2 = startknotenVonFu2.getId(); // ID der ersten Servicefahrt von Fahrzeugumlauf j
					endknotenVonFu2 = fahrzeugumlaeufe.get(j).getAtIndex(fahrzeugumlaeufe.get(j).getFahrten().size()-2); // letzte Servicefahrt im Fahrzeugumlauf j
					keyEkFu2 = endknotenVonFu2.getId(); // ID der letzten Servicefahrt von Fahrzeugumlauf j
					
					/**
					 * Falls es eine zulässige Verbindung zwischen der letzten Servicefahrt von Fahrzeugumlauf i
					 * mit der ersten Servicefahrt von Fahrzeugumlauf j gibt, werden die Savings berechnet.
					 */
					if(validEdges.get(""+keyEkFu1+keySkFu2) == 1){ // falls Verbindung zwischen letzter SF von i und erster SF von j zulässig
						neu = deadruntimes.get(""+endknotenVonFu1.getToStopId()+startknotenVonFu2.getFromStopId()); //neue Leerfahrt hinzufügen
						savings.put(""+keyEkFu1+keySkFu2, calculateSavings(fahrzeugumlaeufe.get(i),fahrzeugumlaeufe.get(j), neu)); //berechne Saving
					}
					
					/**
					 * Falls es eine zulässige Verbindung zwischen der letzten Servicefahrt von Fahrzeugumlauf j
					 * mit der ersten Servicefahrt von Fahrzeugumlauf i gibt, werden die Savings berechnet.
					 */
					if(validEdges.get(""+keyEkFu2+keySkFu1) == 1){ // falls Verbindung zwischen letzter SF von j und erster SF von i zulässig
						neu = deadruntimes.get(""+endknotenVonFu2.getToStopId()+startknotenVonFu1.getFromStopId()); //neue Leerfarht hinzufügen
						savings.put(""+keyEkFu2+keySkFu1, calculateSavings(fahrzeugumlaeufe.get(j),fahrzeugumlaeufe.get(i), neu)); //berechne Saving
					}
				}	
			}
		}
		return savings; // Saving-Matrix zurückgeben
	}
	

	/**
	 * Methode zum Berechnen der Savings, falls zwei Servicefahrten in einem Fahrzeugumlauf bedient werden.
	 * 
	 * @param i
	 * @param j
	 * @param deadrun
	 * @return Saving
	 */
	private double calculateSavings(Fahrzeugumlauf i, Fahrzeugumlauf j, Deadruntime deadrun) {
		double saving = 0;
		double d1 = j.getFahrten().getFirst().getDistance(); // Distanz zwischen Depot und Servicefahrt
		double d2 = i.getFahrten().getLast().getDistance(); // Distanz zwischen Servicefahrt und Depot
		saving = d1 + d2 - deadrun.getDistance() + 400000; // Distanz zwischen beiden Servicefahrten
		return saving;
	}
	
	
	/**
	 * Methode zum Erstellen neuen Umlaufplan, solange ein positiver Saving vorhanden ist.
	 * 
	 * @param savings
	 * @param deadruntimes
	 * @param stoppoints
	 * @param servicejourneys
	 * @return eine Liste von Fahrzeugumläufen
	 */
	public Vector<Fahrzeugumlauf> neuerUmlaufplan(HashMap<String, Double> savings, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Stoppoint> stoppoints, HashMap<String, Servicejourney> servicejourneys){
		
		String temp;
		int n;
		List<String> keys = new ArrayList<String>(); // Keys die schon dran waren
		LinkedList<Journey> neu = null; // neu ist eine Liste von Fahrten, die zusammengelegt werden sollen
		HashMap <String, ArrayList<Stoppoint>> numberOfNewLoadingStations = null;
		if(savings.isEmpty()){ 
			return fahrzeugumlaeufe; // hört auf wenn größter Saving ≤ 0
		}
		do {
			temp = getHighestSaving(savings); // temp ist die ID vom zusammengelegten Umlauf mit größtem Saving
			n = temp.length()/2; 
			if(savings.get(temp) <= 0){ // falls der größte Saving ≤ 0
				return fahrzeugumlaeufe; // hört auf & aktuelle Fahrzeugumlaeufe zurückgeben
			}
			if(!keys.contains(temp)){ // falls temp noch nicht berücksichtigt wird
				neu = umlaeufeZusammenlegen(temp, deadruntimes); // Fahrten von temp werden zusammengelegt und in neu eingepackt
				numberOfNewLoadingStations = newLoadingstations(neu, temp, deadruntimes, stoppoints, servicejourneys); 
				if (numberOfNewLoadingStations.get(temp) == null){ // falls keine Ladestation gebaut werden kann
					savings.put(temp, 0.0); // Saving von temp ist 0
				}else{ // falls Ladestation gebaut werden kann
					int kosten = numberOfNewLoadingStations.get(temp).size() * 250000; // berechne Fixkosten von Ladestationen
					double neueSavings = savings.get(temp) - kosten; // aktualisiere Saving von temp
					savings.put(temp, neueSavings); // fügt den neuen Saving von temp hinzu
				}
			}
			keys.add(temp); // temp wird als berücksichtigt gespeichert 
		} while (temp != getHighestSaving(savings)); //solange temp nicht der größte Saving ist
		
		
		/**
		fahrzeugumlaeufe aktualisieren
		*/
		ArrayList<Fahrzeugumlauf> umlaeufe = umlaeufeFinden(temp.substring(0, n), temp.substring(n, temp.length()));
		for (int i = 0; i < fahrzeugumlaeufe.size(); i++) { // für jeden Fahrzeugumlauf i
			if(fahrzeugumlaeufe.get(i).getId() == umlaeufe.get(0).getId()){ // falls ID von i gleich ID vom ersten der zusammengelegten Umläufen
				fahrzeugumlaeufe.get(i).setFahrten(neu); //aktualisiere die Fahrten von Fahrzeugumlauf i
			}
		}
		for(int i = 0; i < fahrzeugumlaeufe.size(); i++){ // für jeden Fahrzeugumlauf i
			if(fahrzeugumlaeufe.get(i).getId() == umlaeufe.get(1).getId()){ // falls ID von i gleich ID vom zweiten der zusammengelegten Umläufen
				fahrzeugumlaeufe.remove(i); // lösche i aus der Liste der Fahrzeugumläufe
			}
		}
		ArrayList<Stoppoint> buildLoadingStations = numberOfNewLoadingStations.get(temp);
		// Setzen der Ladestationen an den Haltestellen
		for (int i = 0; i < buildLoadingStations.size(); i++) {
			buildLoadingStations.get(i).setLadestation(true);
		}
		return fahrzeugumlaeufe;
	}
	
	
	/**
	 * Methode zum Herausfinden, zu welchem Fahrzeugumlauf die zu zusammenlegenden Servicefahrten gehören
	 * 
	 * @param key1 (ID der ersten SF)
	 * @param deadruntimes (ID der zweiten SF)
	 * @return eine Liste von zwei Fahrzeugumläufen, welche jeweils die beiden SF beinhalten
	 */
	private ArrayList <Fahrzeugumlauf> umlaeufeFinden(String key1, String key2){
		Fahrzeugumlauf eins = null;
		Fahrzeugumlauf zwei = null;
		
		// while-Schleife drum machen: solange eins und zwei null sind
		for (int i = 0; i < fahrzeugumlaeufe.size(); i++) { // für jeden Fahrzeugumlauf i
			for (int j = 0; j < fahrzeugumlaeufe.get(i).getFahrten().size(); j++) { // für jede Fahrt j im Fahrzeugumlauf i 
				if(fahrzeugumlaeufe.get(i).getFahrten().get(j).getId().equals(key1)){  
					eins = fahrzeugumlaeufe.get(i); // erster Umlauf beinhaltet key1
				}
				if(fahrzeugumlaeufe.get(i).getFahrten().get(j).getId().equals(key2)){
					zwei = fahrzeugumlaeufe.get(i); // zweiter Umlauf beinhaltet key2
				}
			}
		}
		ArrayList umlaeufe = new ArrayList<Fahrzeugumlauf>(); // eine ArrayList von Fahrzeugumläufen
		umlaeufe.add(eins);
		umlaeufe.add(zwei);
		return umlaeufe; // eine Liste von zwei Fahrzeugumläufen, welche jeweils key1 und key2 beinhalten 
	}
	
	
	/**
	 * Methode zum Zusammenlegen von Fahrzeugumläufen
	 * 
	 * @param keyOfHighestSavings
	 * @param deadruntimes
	 * @return zusammengelegter Umlauf als Liste von Fahrten (Depot - SF1 - SF2 - Depot)
	 */	
	private LinkedList<Journey> umlaeufeZusammenlegen(String keyOfHighestSavings, HashMap<String, Deadruntime> deadruntimes){

		String key = keyOfHighestSavings; //Schlüssel vom größten Saving 
		int n = key.length()/2;
		String key1 = key.substring(0, n); //erster Umlauf von diesem Schlüssel
		String key2 = key.substring(n, key.length()); //zweiter Umlauf von diesem Schlüssel
		LinkedList<Journey> neu = null;
		
		ArrayList<Fahrzeugumlauf> umlaeufe = umlaeufeFinden(key1, key2); // Liste von zwei Fahrzeugumläufen, welche jeweils key1 und key2 beinhalten
	
		LinkedList<Journey> eins = new LinkedList<Journey>(); // eins ist eine LinkedList von Fahrten
		LinkedList<Journey> zwei = new LinkedList<Journey>(); // zwei ist eine LinkedList von Fahrten
		eins.addAll(umlaeufe.get(0).getFahrten()); // alle Fahrten vom Umlauf, welcher key1 beinhaltet
		zwei.addAll(umlaeufe.get(1).getFahrten()); // alle Fahrten vom Umlauf, welcher key2 beinhaltet
		eins.removeLast(); // lösche letzte Fahrt von eins (ein Leerfahrt zum Depot)
		zwei.removeFirst(); // lösche erste Fahrt von zwei (ein Leerfahrt aus Depot)
		neu = eins;
		neu.add(deadruntimes.get(eins.getLast().getToStopId()+zwei.getFirst().getFromStopId())); // neu entstehende Leerfarht zwsichen eins und zwei
		neu.addAll(zwei);
		
		return neu; // neu ist der zusammengelegte Umlauf (als LinkedList von Fahrten gespeichert): Depot - key1 - key2 - Depot 
	}
	
	/**
	 * 
	 * 
	 * @param 
	 * @param 
	 * @return
	 */	

	private HashMap<String, ArrayList<Stoppoint>> newLoadingstations(LinkedList<Journey> neu, String keyOfHighestValue, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Stoppoint> stoppoints, HashMap<String, Servicejourney> servicejourneys){
		
		double kapazitaet = 80.0; // Batteriekapazität in kWh
		HashMap <String, ArrayList<Stoppoint>> numberOfNewStations = new HashMap<String, ArrayList<Stoppoint>>();
		ArrayList<Stoppoint> list = new ArrayList<Stoppoint>(); // eine Liste aller Haltestellen
		
		for (int i = 0; i < neu.size(); i++) { // für jede Fahrt i im zusammengesetzten Fahrzeugumlauf
			int letzteLadung = 0; // ID der Fahrt im Fahrzeugumlauf, wo zuletzt geladen wird
			if (kapazitaet - neu.get(i).getVerbrauch() < 0){ // falls Verbrauch von Fahrt i die Restkapazität nicht abdeckt
				
				if(neu.get(i) instanceof Servicejourney){ // falls Fahrt i eine Servicefahrt ist
					int x = 0;
					while((i-2-x) > letzteLadung){ //solange wir nicht die erste SF oder die LetzteLadung erreichen
						if(feasibilityHelper.zeitpufferFuerLadezeit(neu.get(i-2-x).getId(), neu.get(i-x).getId(), deadruntimes, servicejourneys, kapazitaet)){
							//wenn genug Zeit zum Laden vorhanden ist
							if(x==0){ //falls direkt bei der betroffenen SF geladen werden kann
								if (!stoppoints.get(neu.get(i).getFromStopId()).isLadestation()){ //falls noch keine Ladestation an dieser Stelle vorhanden ist
									list.add(stoppoints.get(neu.get(i).getFromStopId())); //füge die betroffene Haltestelle in die Liste hinzu
									kapazitaet = 80; // Kapazität wieder voll geladen
									letzteLadung = i; // merkt sich, an i die letzte Ladung erfolgt ist
									stoppoints.get(neu.get(i).getFromStopId()).setLadestation(true); // setzt eine Ladestation an der Starthaltestelle von SF i
									break; 
								}else{ // es ist schon eine Ladestation vorhanden an Haltestelle i - x
									kapazitaet = 80; // Kapazität wieder voll geladen
									letzteLadung = i; // merkt sich, an i die letzte Ladung erfolgt ist
									break;
								} 
							}else{ // falls nicht direkt in i geladen werden kann und damit die vorherigen SF anschauen muss
								if (!stoppoints.get(neu.get(i-x).getToStopId()).isLadestation()){ // 
									list.add(stoppoints.get(neu.get(i-x).getToStopId()));
									kapazitaet = 80; // Kapazität wieder voll geladen
									letzteLadung = i - x; // merkt sich, an welcher Stelle die letzte Ladung erfolgt ist
									stoppoints.get(neu.get(i-x).getFromStopId()).setLadestation(true); //setzt eine Ladestation an der Starthaltestelle von SF i-x
									i = i - x; // i muss zurueckgesetzt werden, um dort zu starten, wo die Kapazitaet wieder bei 80 ist
									break;
								}else{ // es ist schon eine Ladestation vorhanden an Haltestelle i - x
									kapazitaet = 80; // Kapazität wieder voll geladen
									letzteLadung = i - x; 
									i = i - x;
									break;
								} 
							}
						}
						x = x + 2;
						//System.out.println("x = " + x + "; i = " + i);
					}
					if(kapazitaet != 80){ // wenn nicht geladen werden konnte, dann lade vor Servicefahrt 1 (da geht es zeitlich immer)
						if(letzteLadung == 0){ // schon einmal vor Servicefahrt 1 geladen?
							if (!stoppoints.get(neu.get(1).getFromStopId()).isLadestation()){ // falls vor SF1 noch keine Ladestation gebaut wird
								list.add(stoppoints.get(neu.get(1).getFromStopId()));
								kapazitaet = 80;
								letzteLadung = 1;
								stoppoints.get(neu.get(1).getFromStopId()).setLadestation(true);
								i = 1;
								break;
							}else{  // an der Haltestelle ist schon eine Ladestation -> Laden
								kapazitaet = 80;
								i = 1;
								letzteLadung = 1;
							}
						}
						else{ // es wird zum zweiten mal versucht an der gleichen Haltestelle zu laden --> Endlosschleife: Fahrzeugumlauf nicht moeglich
							list.clear(); 
							list.add(null); // Liste der Haltestelle mit Ladestationen löschen und null zurückgeben
							numberOfNewStations.put(keyOfHighestValue, list);
							return numberOfNewStations;  
						}
					}
				}	

				if(neu.get(i) instanceof Deadruntime){ // falls Fahrt i eine Leerfahrt ist
					int x = 0;
					
					while(((i - x - 1) > letzteLadung)){ //solange die LetzteLadung nicht wieder erreicht wird
						if(i == neu.size()-1){ //falls i die letzte Leerfahrt ist
							if (!stoppoints.get(neu.get(i-1).getToStopId()).isLadestation()){ //falls keine Ladestation vorhanden an Endhaltestelle von SF (i-1)
								list.add(stoppoints.get(neu.get(i-1).getToStopId()));
								kapazitaet = 80;
								letzteLadung = i - 1;
								stoppoints.get(neu.get(i-1).getToStopId()).setLadestation(true);
								i = i - 1;
								break;
							}else{ // es ist schon eine Ladestation vorhanden an Endhaltestelle von SF (i-1)
								kapazitaet = 80;
								letzteLadung = i - 1;
								i = i - 1;
								break;
							} 
						}
						else if(x==0){
							if(feasibilityHelper.zeitpufferFuerLadezeit(neu.get(i-1).getId(), neu.get(i+1).getId(), deadruntimes, servicejourneys, kapazitaet)){					
								if (!stoppoints.get(neu.get(i-1).getToStopId()).isLadestation()){ // i ist die Starthaltestelle der Servicefahrt i
									list.add(stoppoints.get(neu.get(i-1).getToStopId()));
									kapazitaet = 80;
									letzteLadung = i-1;
									stoppoints.get(neu.get(i-1).getToStopId()).setLadestation(true);
									break;
								}else{ // es ist schon eine Ladestation vorhanden an Haltestelle i 
									kapazitaet = 80;
									letzteLadung = i-1;
									break;
								} 
							}
							x = x + 2;
						}else{
							if(feasibilityHelper.zeitpufferFuerLadezeit(neu.get(i-2-x+1).getId(), neu.get(i-x+1).getId(), deadruntimes, servicejourneys, kapazitaet)){
								if (!stoppoints.get(neu.get(i-x-1).getToStopId()).isLadestation()){ // i - x ist die Starthaltestelle der Servicefahrt i
									list.add(stoppoints.get(neu.get(i-x-1).getToStopId()));
									kapazitaet = 80;
									letzteLadung = i - x; // merkt sich, an welcher Stelle die letzte Ladung erfolgt ist
									i = i - x; // i muss zurueckgesetzt werden, um dort zu starten, wo die Kapazitaet wieder bei 80 ist
									stoppoints.get(neu.get(i-x-1).getToStopId()).setLadestation(true);
									break;
								}else{ // es ist schon eine Ladestation vorhanden an Haltestelle i - x
									kapazitaet = 80;
									letzteLadung = i - x;
									i = i - x;
									break;
								} 
							}
							x = x + 2;
						}
						//System.out.println("x = " + x + "; i = " + i);
					}	
					if(kapazitaet != 80){ // wenn nicht geladen werden konnte, dann lade vor Servicefahrt 1 (da geht es zeitlich immer)
						if(letzteLadung == 0){ // schon einmal vor Servicefahrt 1 geladen?
							if (!stoppoints.get(neu.get(1).getFromStopId()).isLadestation()){
								list.add(stoppoints.get(neu.get(1).getFromStopId()));
								kapazitaet = 80;
								i = 1;
								letzteLadung = 1;
								stoppoints.get(neu.get(1).getFromStopId()).setLadestation(true);
							}else{ // an der Haltestelle ist schon eine Ladestation: Laden
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
			}
			kapazitaet = kapazitaet - neu.get(i).getVerbrauch(); // aktualisiere die Kapazität nach Fahrt i, falls Fahrt i noch gefahren werden kann
		}
		numberOfNewStations.put(keyOfHighestValue, list); 
		return numberOfNewStations;	
	}
	
	private HashMap<String, Deadruntime> getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * Methode zum Finden der Schlüssel vom größten Saving
	 * 
	 * @param savings
	 * @return Schlüssel vom größten Saving als String
	 */
	private String getHighestSaving(HashMap<String, Double> savings){
		double temp = 0;
		String key = "";
		for (Entry<String, Double> e: savings.entrySet()){ 
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
