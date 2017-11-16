package construction;

import java.util.HashMap;
import java.util.Vector;

import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Servicejourney;

public class Pendeltouren {
	
	private Vector<Fahrzeugumlauf> initialloesung;
	
	public Pendeltouren(){
		initialloesung = new Vector<Fahrzeugumlauf>();
	}

	public Vector<Fahrzeugumlauf> erstelleInitialloesung(Vector<Servicejourney> servicejourneys, HashMap<String, Deadruntime> deadruntimes){
		
		for (int i = 0; i < servicejourneys.size(); i++) {
			Fahrzeugumlauf j = new Fahrzeugumlauf(i);
			
			String depot = "00001";
			String key = depot + servicejourneys.get(i).getSfFromStopId();
			
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
