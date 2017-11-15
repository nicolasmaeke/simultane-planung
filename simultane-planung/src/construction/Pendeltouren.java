package construction;

import java.util.Vector;

import model.Fahrzeugumlauf;
import model.Servicejourney;

public class Pendeltouren {
	
	private Vector<Fahrzeugumlauf> initialloesung;
	
	public Pendeltouren(){
		initialloesung = new Vector<Fahrzeugumlauf>();
	}
	
	public Vector<Fahrzeugumlauf> erstelleInitialloesung(Vector<Servicejourney> servicejourneys){
		
		for (int i = 0; i < servicejourneys.size(); i++) {
			Fahrzeugumlauf j = new Fahrzeugumlauf(i);
			j.addFahrt(servicejourneys.get(i)); // jedem Fahrzeugumlauf wird genau eine Servicefahrt zugewiesen  
			initialloesung.add(j);
		}
		
		return initialloesung;
		
	}
	
}
