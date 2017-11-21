package construction;

import java.util.HashMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.SortedMap;

import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Journey;
import model.Servicejourney;

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
	
	public HashMap<String, Double> savings(HashMap<String, Integer> validEdges, HashMap<String, Deadruntime> deadruntimes){
		
		HashMap <String, Double> savings = new HashMap<String, Double>();
		
		Journey startknotenVonFu1 = null;
		String keySkFu1 = "";
		Journey endknotenVonFu1 = null;
		String keyEkFu1 = "";
		
		Journey startknotenVonFu2 = null;
		String keySkFu2 = "";
		Journey endknotenVonFu2 = null;
		String keyEkFu2 = "";
		
		Deadruntime neu = null;
		
		for (int i = 0; i < fahrzeugumlaeufe.size(); i++) {
			startknotenVonFu1 = fahrzeugumlaeufe.get(i).getAtIndex(1);
			keySkFu1 = startknotenVonFu1.getId();
			endknotenVonFu1 = fahrzeugumlaeufe.get(i).getAtIndex(fahrzeugumlaeufe.get(i).size()-2);
			keyEkFu1 = endknotenVonFu1.getId();
			for (int j = 0; j < fahrzeugumlaeufe.size(); j++) {
				if (i != j){
					startknotenVonFu2 = fahrzeugumlaeufe.get(j).getAtIndex(1);
					keySkFu2 = startknotenVonFu2.getId();
					endknotenVonFu2 = fahrzeugumlaeufe.get(j).getAtIndex(fahrzeugumlaeufe.get(i).size()-2);
					keyEkFu2 = endknotenVonFu2.getId();
					
					if(validEdges.get(""+keyEkFu1+keySkFu2) == 1){
						neu = deadruntimes.get(""+endknotenVonFu1.getToStopId()+startknotenVonFu2.getFromStopId());
						savings.put(""+keyEkFu1+keySkFu2, calculateSavings(fahrzeugumlaeufe.get(i),fahrzeugumlaeufe.get(j), neu));
					}
					
					if(validEdges.get(""+keyEkFu2+keySkFu1) == 1){
						neu = deadruntimes.get(""+endknotenVonFu2.getToStopId()+startknotenVonFu1.getFromStopId());
						savings.put(""+keyEkFu2+keySkFu1, calculateSavings(fahrzeugumlaeufe.get(j),fahrzeugumlaeufe.get(i), neu));
					}
				}	
			}
		}
		return savings;
	}
	

	private Double calculateSavings(Fahrzeugumlauf i, Fahrzeugumlauf j, Deadruntime deadrun) {
		double saving = 0.0;
		double d1 = i.getFahrten().getFirst().getDistance();
		double d2 = j.getFahrten().get(j.size()-1).getDistance();
		saving = d1 + d2 - deadrun.getDistance(); 
		return saving;
	}

	public Vector<Fahrzeugumlauf> getInitialloesung() {
		return fahrzeugumlaeufe;
	}

	public void setInitialloesung(Vector<Fahrzeugumlauf> initialloesung) {
		this.fahrzeugumlaeufe = initialloesung;
	}
	
}
