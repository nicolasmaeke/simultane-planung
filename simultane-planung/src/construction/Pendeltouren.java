package construction;

import java.util.HashMap;
import java.util.Vector;

import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Servicejourney;

/**
 * 
 * Klasse repraesentiert die Initialloesung mit Pendeltouren, d.h.
 * dass jede Servicefahrt genau einem Fahrzeugumlauf zugewiesen wird.
 * Ein Fahrzeugumlauf besteht dann genau aus der Fahrt vom Depot zur Servicefahrt,
 * der Servicefahrt selbst und der Fahrt von der Servicefahrt zurueck zum Depot.
 *
 */
public class Pendeltouren {
	
	private Vector<Fahrzeugumlauf> initialloesung;
	
	public Pendeltouren(){
		initialloesung = new Vector<Fahrzeugumlauf>();
	}

	/**
	 * Methode fuegt jedem Fahrzeugumlauf die beiden Leerfahrten vom- und zum Depot sowie eine Servicefahrt hinzu
	 * @param servicejourneys
	 * @param deadruntimes
	 * @return
	 */
	public Vector<Fahrzeugumlauf> erstelleInitialloesung(Vector<Servicejourney> servicejourneys, HashMap<String, Deadruntime> deadruntimes){
		
		for (int i = 0; i < servicejourneys.size(); i++) {
			Fahrzeugumlauf j = new Fahrzeugumlauf(i);
			
			String depot = "00001";
			String key = depot + servicejourneys.get(i).getSfFromStopId(); // 
			
			j.addFahrtAfterFahrt(0, deadruntimes.get(key));
			
			j.addFahrtAfterFahrt(1,servicejourneys.get(i)); // jedem Fahrzeugumlauf wird genau eine Servicefahrt zugewiesen 
			
			key = servicejourneys.get(i).getSfToStopId() + depot;

			j.addFahrtAfterFahrt(j.size(), deadruntimes.get(key));
			
			initialloesung.add(j);
		}
		
		return initialloesung;
		
	}

	public Vector<Fahrzeugumlauf> getInitialloesung() {
		return initialloesung;
	}

	public void setInitialloesung(Vector<Fahrzeugumlauf> initialloesung) {
		this.initialloesung = initialloesung;
	}
	
}
