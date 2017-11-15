package start;

import java.util.Vector;

import construction.Pendeltouren;
import model.Fahrzeugumlauf;
import parser.ProjectReadIn;

public class Start {

	public static void main(String[] args) {

		ProjectReadIn test = new ProjectReadIn("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/sample_toy_10_SF_20_stoppoints.txt");
		test.Problem();
		
		/**
		for (int i = 0; i < test.servicejeourneys.size(); i++) {
			System.out.println(test.servicejeourneys.get(i).toString());
		}
		for (int i = 0; i < test.deadruntimes.size(); i++) {
			System.out.println(test.deadruntimes.get(i).toString());
		}
		*/
		
		Pendeltouren p = new Pendeltouren();
		Vector<Fahrzeugumlauf> initialloesung = p.erstelleInitialloesung(test.servicejeourneys);
		
		for (int i = 0; i < initialloesung.size(); i++) {
			System.out.println(initialloesung.get(i));
		}
	}

}
