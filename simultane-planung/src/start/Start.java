package start;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import construction.Initialloesung;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import parser.ProjectReadIn;

public class Start {

	public static void main(String[] args) {

		ProjectReadIn test = new ProjectReadIn("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_toy_6_9.txt");
		//test.Problem();
		
		
		
		
		/**
		for (Map.Entry e: test.validEdges.entrySet()){
			System.out.println(e.getKey() + " = " + e.getValue());
		}
		*/
		
		Initialloesung p = new Initialloesung();
		Vector<Fahrzeugumlauf> initialloesung = p.erstelleInitialloesung(test.servicejourneys, test.deadruntimes);
		HashMap<String, Integer> savings;
		
		for (int i = 0; i < 5; i++) {
			savings = p.savings(test.validEdges, test.deadruntimes);
			System.out.println(savings);
			
			for (int j = 0; j < initialloesung.size(); j++) {
				System.out.println(initialloesung.get(j).getFahrten().toString());
			}
			System.out.println();
			
			p.neuerUmlaufplan(savings, test.deadruntimes, test.stoppoints, test.servicejourneys);
		}
	
	}

}
