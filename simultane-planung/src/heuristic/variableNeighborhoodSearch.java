package heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import helper.ZweiOptVerbesserung;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Servicejourney;
import model.Stoppoint;
import output.Schedule;

public class variableNeighborhoodSearch {
	
	List<Schedule> solutions = new ArrayList<Schedule>(); // aktuelle Loseung, beste Loesung und neue Loesung speichern
	Vector<Fahrzeugumlauf> fahrzeugumlaeufe;
	HashMap<String, Integer> validEdges;
	public HashMap<String, Deadruntime> deadruntimes;
	public HashMap<String, Servicejourney> servicejourneys;
	public HashMap<String, Stoppoint> stoppoints;
	private int sizeNeighborhood = 2; // Anzahl der Fahrzeugumlaeufe die gleichzeitig betrachtet werden

	public variableNeighborhoodSearch(Vector<Fahrzeugumlauf> fahrzeugumlaeufe, HashMap<String, Integer> validEdges, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Servicejourney> servicejourneys, HashMap<String, Stoppoint> stoppoints){
		this.fahrzeugumlaeufe = fahrzeugumlaeufe;
		this.validEdges = validEdges;
		this.deadruntimes = deadruntimes;
		this.stoppoints = stoppoints;
		this.servicejourneys = servicejourneys;
	}
	
	public void shaking(){ // zufaellig die aktuelle Loesung manipulieren; kann auch schlechter werden
		
	}
	
	public void bestImprovement(){
		// waehle zufaellig zwei Fahrzeugumlaeufe aus
		int random1 = (int)Math.random()*fahrzeugumlaeufe.size();
		int random2 = (int)Math.random()*fahrzeugumlaeufe.size();		
		while(random1 == random2){
			random2 = (int)Math.random()*fahrzeugumlaeufe.size();
		}
		ZweiOptVerbesserung verbesserung1 = zweiOpt(random1, random2);
		if(verbesserung1 == null){
			int random3 = (int)Math.random()*fahrzeugumlaeufe.size();
			ZweiOptVerbesserung verbesserung2 = null;
			while(random1 == random3 || random2 == random3){
				random3 = (int)Math.random()*fahrzeugumlaeufe.size();
			}
			verbesserung1 = zweiOpt(random1, random3);
			verbesserung2 = zweiOpt(random2, random3);
			if(verbesserung1 == null && verbesserung2 == null){
				ZweiOptVerbesserung verbesserung3 = null;
				int random4 = (int)Math.random()*fahrzeugumlaeufe.size();
				while(random1 == random4 || random2 == random4 || random3 == random4){
					random4 = (int)Math.random()*fahrzeugumlaeufe.size();
				}
				verbesserung1 = zweiOpt(random1, random4);
				verbesserung2 = zweiOpt(random2, random4);
				verbesserung3 = zweiOpt(random3, random4);
				if(verbesserung1 == null && verbesserung2 == null && verbesserung3 == null){
					return;
				}
				else{ // Fall mit Nachbargschaftsgroesse 4
					ArrayList<ZweiOptVerbesserung> list = null;
					ZweiOptVerbesserung best = new ZweiOptVerbesserung(0, null, null, 0, 0);
					if(verbesserung1 != null){
						list.add(verbesserung1);
					}
					if(verbesserung2 != null){
						list.add(verbesserung2);
					}
					if(verbesserung3 != null){
						list.add(verbesserung3);
					}
					
					if(list.isEmpty()){ // alle drei sind null
						return;
					}
					
					for (int i = 0; i < list.size(); i++) {
						if(list.get(i).getCosts() < best.getCosts()){
							best = list.get(i);
						}
					}
					fahrzeugumlaeufe.remove(best.getIndexAltEins());
					fahrzeugumlaeufe.remove(best.getIndexAltZwei());
					fahrzeugumlaeufe.add(best.getEins());
					fahrzeugumlaeufe.add(best.getZwei());
				}
			}
			else{ // Fall mit Nachbarschaftgroesse 3
				if(verbesserung1 == null){
					// neuer Umlaufplan
					fahrzeugumlaeufe.remove(random2);
					fahrzeugumlaeufe.remove(random3);
					fahrzeugumlaeufe.add(verbesserung2.getEins());
					fahrzeugumlaeufe.add(verbesserung2.getZwei());
				}
				else if(verbesserung2 == null || verbesserung2.getCosts() > verbesserung1.getCosts()){
					// neuer Umlaufplan
					fahrzeugumlaeufe.remove(random1);
					fahrzeugumlaeufe.remove(random3);
					fahrzeugumlaeufe.add(verbesserung1.getEins());
					fahrzeugumlaeufe.add(verbesserung1.getZwei());
				}
				else{
					// neuer Umlaufplan
					fahrzeugumlaeufe.remove(random2);
					fahrzeugumlaeufe.remove(random3);
					fahrzeugumlaeufe.add(verbesserung2.getEins());
					fahrzeugumlaeufe.add(verbesserung2.getZwei());
				}
			}
		}
		else{ // Fall mit Nachbarschaftgroesse 2
			// neuer Umlaufplan
			fahrzeugumlaeufe.remove(random1);
			fahrzeugumlaeufe.remove(random2);
			fahrzeugumlaeufe.add(verbesserung1.getEins());
			fahrzeugumlaeufe.add(verbesserung1.getZwei());
		}
			
	}
	
	public void firstImprovement(){
		
	}

	
	public ZweiOptVerbesserung zweiOpt(int random1, int random2){ // id der Fahrzeugumlaeufe aktuell noch String, wird zu Integer von 1...n geaendert
		
		ZweiOptVerbesserung result = null;
		
		Fahrzeugumlauf eins = fahrzeugumlaeufe.get(random1);
		Fahrzeugumlauf zwei = fahrzeugumlaeufe.get(random2);
		
		double currentCostValue = eins.getKosten() + zwei.getKosten();
		
		Fahrzeugumlauf betterEins = null;
		Fahrzeugumlauf betterZwei = null;
		
		for (int i = -1; i < eins.size(); i = i + 2) { // i = 1, weil bei der zweiten Leerfahrt begonnen wird; es werden nur Servicefahrten betrachte, daher i+2
			if(i == -1){
				for (int j = 3; j < zwei.size(); j = j + 2) {
					if(validEdges.get(zwei.getAtIndex(j-2).getId()+eins.getAtIndex(i+2).getId()) == 1){
						String deadruntimeId = zwei.getAtIndex(j-2).getToStopId() + eins.getAtIndex(i+2).getFromStopId(); 
						Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId());
						einsNeu.addFahrten(zwei.getFahrtenVonBis(0, j-2));
						einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
						einsNeu.addFahrten(eins.getFahrtenVonBis(i+2, eins.size() - 1));
						if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
							deadruntimeId = "00001" + zwei.getAtIndex(j).getFromStopId();
							zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
							zweiNeu.addFahrten(zwei.getFahrtenVonBis(j, zwei.size() - 1));
							if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								// neue Umlaeufe speichern, falls besser
								double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten();
								if(newCostValue <= currentCostValue){
									currentCostValue = newCostValue;
									betterEins = einsNeu;
									betterZwei = zweiNeu;
								}
							}
						}
					}
				}
			}
			else{
				String id = eins.getAtIndex(i).getId();
			for (int j = 1; j <= zwei.size(); j = j + 2) {
				if(j == zwei.size() && i > eins.size() - 4){
					break;
				}
				id = id + zwei.getAtIndex(j).getId();
				if (validEdges.get(id) == 1) {
					if (j < 2) { // weil dann Depotkante geloescht wird
						Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId());
						einsNeu.addFahrten(eins.getFahrtenVonBis(0, i));
						String deadruntimeId = eins.getAtIndex(i).getToStopId() + zwei.getAtIndex(j).getFromStopId(); 
						einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
						einsNeu.addFahrten(zwei.getFahrtenVonBis(j, zwei.size()-1));
						if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
							deadruntimeId = "00001" + eins.getAtIndex(i+2).getFromStopId(); // neue Depotkante muss hinzugefuegt werden
							zweiNeu.addFahrt(deadruntimes.get(id));
							zweiNeu.addFahrten(eins.getFahrtenVonBis(i+2, eins.size()-1));
							if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								// neue Umlaeufe speichern, falls besser
								double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten();
								if(newCostValue <= currentCostValue){
									currentCostValue = newCostValue;
									betterEins = einsNeu;
									betterZwei = zweiNeu;
							}
						}	
					}
					}
					else{
						if(validEdges.get(zwei.getAtIndex(j-2).getId() + eins.getAtIndex(i+2).getId()) == 1){
							Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId());
							einsNeu.addFahrten(eins.getFahrtenVonBis(0, i));
							String deadruntimeId = eins.getAtIndex(i).getToStopId() + zwei.getAtIndex(j).getFromStopId(); 
							einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
							einsNeu.addFahrten(zwei.getFahrtenVonBis(j, zwei.size()-1));
							if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
								zweiNeu.addFahrten(zwei.getFahrtenVonBis(0, j-2));
								deadruntimeId = zwei.getAtIndex(j-2).getToStopId() + eins.getAtIndex(i+2).getFromStopId();
								zweiNeu.addFahrt(deadruntimes.get(id));
								zweiNeu.addFahrten(eins.getFahrtenVonBis(i+2, eins.size()-1));
								if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
									// neue Umlaeufe speichern, falls besser
									double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten();
									if(newCostValue <= currentCostValue){
										currentCostValue = newCostValue;
										betterEins = einsNeu;
										betterZwei = zweiNeu;
								}
							}	
						}
					}
				}
			
			}
			}
			}
		}
		if(!eins.equals(betterEins)){
			result = new ZweiOptVerbesserung(currentCostValue, betterEins, betterZwei, random1, random2);
		}
		return result;
	}
}
