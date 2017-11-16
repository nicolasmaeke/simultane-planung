package start;

import java.util.Map;
import java.util.Vector;

import construction.Pendeltouren;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import parser.ProjectReadIn;

public class Start {

	public static void main(String[] args) {

		ProjectReadIn test = new ProjectReadIn("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/sample_toy_6_SF_9_stoppoints.txt");
		test.Problem();
		
		/**
		for (int i = 0; i < test.servicejeourneys.size(); i++) {
			System.out.println(test.servicejeourneys.get(i).toString());
		}
		*/
		
		for (Map.Entry e: test.validEdges.entrySet()){
			System.out.println(e.getKey() + " = " + e.getValue());
		}
		
		
		Pendeltouren p = new Pendeltouren();
		Vector<Fahrzeugumlauf> initialloesung = p.erstelleInitialloesung(test.servicejourneys, test.deadruntimes);
		
		for (int i = 0; i < initialloesung.size(); i++) {
			System.out.println(initialloesung.get(i).isFeasible());
		}
		
		
		
	}

}
