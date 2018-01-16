package start;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import construction.Initialloesung;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Stoppoint;
import output.Schedule;
import parser.ProjectReadIn;

public class Start {

	public static void main(String[] args) {

		ProjectReadIn test = new ProjectReadIn("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_toy_6_9.txt");
		
		Initialloesung p = new Initialloesung();
		Vector<Fahrzeugumlauf> initialloesung = p.erstelleInitialloesung(test.servicejourneys, test.deadruntimes, test.stoppoints);
		HashMap<String, Double> savings;
		int numberOfLoadingStations = 0;
		int iteration = 0;
		double valueSaving = 0.0;
		
		//neu
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		
		//neu
		try {
			fw = new FileWriter("/Users/XuanSon/Desktop/full_sample_real_433_SF_207_stoppoints.txt", true);
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		bw = new BufferedWriter(fw); 
		pw = new PrintWriter(bw);
		
		
		do {
			valueSaving = 0.0;
			/**
			for (int j = 0; j < initialloesung.size(); j++) {
				System.out.println(initialloesung.get(j).getFahrten().toString());
			}
			*/
			savings = p.savings(test.validEdges, test.deadruntimes);
			
			
			//System.out.println("Savings-Matrix: " + savings);
			
			/**
			for (Map.Entry e: test.stoppoints.entrySet()){
				Stoppoint i1 = test.stoppoints.get(e.getKey());
				if (i1.isLadestation()) {
					System.out.println("Ladestationen Haltestelle: " + i1.getId());
					numberOfLoadingStations ++;
				}
			}
			
			*/
			//Schedule ergebnis = new Schedule(p.getInitialloesung(), test.stoppoints);
			
			//System.out.println("Kosten für den Umlaufplan: " + ergebnis.berechneKosten());
			
			//System.out.println("Anzahl Ladestationen: " + numberOfLoadingStations);
			
			//numberOfLoadingStations = 0;

			System.out.println(iteration);
			
			
			p.neuerUmlaufplan(savings, test.deadruntimes, test.stoppoints, test.servicejourneys, iteration);
			
			iteration ++;
			
			if(iteration == 225){
				iteration = 225;
			}	
			
			for (Entry<String, Double> e: savings.entrySet()){ 
				if(e.getValue() > valueSaving){
					valueSaving = e.getValue();
				}
			}
			
		}while(!savings.isEmpty() && !(valueSaving <= 0)) ;
	
		
		for (Map.Entry e: test.stoppoints.entrySet()){
			Stoppoint i1 = test.stoppoints.get(e.getKey());
			if (i1.isLadestation()) {
				System.out.println("Ladestationen Haltestelle: " + i1.getId());
				numberOfLoadingStations ++;
			}
		}
		
		
		Schedule ergebnis = new Schedule(p.getInitialloesung(), test.stoppoints);
		
		System.out.println("Kosten für den Umlaufplan: " + ergebnis.berechneKosten());
		
		System.out.println("Anzahl Ladestationen: " + numberOfLoadingStations);
		
		numberOfLoadingStations = 0;
		
		pw.println();
		pw.println("*;;;;;;;;;;");
		pw.println("* Initialloesung;;;;;;;;;;");
		pw.println("*;;;;;;;;;;");
		pw.println("$Umlauf:ID;Fahrten;;;;;;;;");
		
		//neu
		for (int j = 0; j < initialloesung.size(); j++) {
			String umlaufId = String.valueOf(j);
			System.out.println(umlaufId + ";" + initialloesung.get(j).getFahrten().toString());
			pw.println(umlaufId + ";" + initialloesung.get(j).getFahrten().toString());
			pw.flush();
		}
	}

}
