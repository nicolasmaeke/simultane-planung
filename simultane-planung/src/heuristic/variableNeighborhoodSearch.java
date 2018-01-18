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

/** Klasse beinhaltet Methode für Variable Neighborhood Search
 */
public class variableNeighborhoodSearch {
	
	Schedule globalBest;
	Schedule localBest;
	Schedule shaking;
	HashMap<String, Integer> validEdges;
	public HashMap<String, Deadruntime> deadruntimes;
	public HashMap<String, Servicejourney> servicejourneys;
	public HashMap<String, Stoppoint> stoppoints;

	public variableNeighborhoodSearch(Vector<Fahrzeugumlauf> fahrzeugumlaeufe, HashMap<String, Integer> validEdges, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Servicejourney> servicejourneys, HashMap<String, Stoppoint> stoppoints){
		this.validEdges = validEdges;
		this.deadruntimes = deadruntimes;
		this.stoppoints = stoppoints;
		this.servicejourneys = servicejourneys;
		this.globalBest =  new Schedule(fahrzeugumlaeufe, stoppoints);
	}
	
	/** Methode -> zufaellig die aktuelle Loesung (den aktuellen Umlaufplan) manipulieren (Verschlechterung wird zugelassen)
	 * 
	 */
	public Schedule shaking(){ 

		int counter = 0;
		boolean condition = true;
		while(condition) {
			int random1 = (int)(Math.random()*globalBest.getUmlaufplan().size());
			int random2 = (int)(Math.random()*globalBest.getUmlaufplan().size());
			while(random1 == random2){
				random2 = (int)(Math.random()*globalBest.getUmlaufplan().size());
			}
			int randomI = (int)(Math.random()*globalBest.getUmlaufplan().get(random1).size());
			while(randomI % 2 == 0){
				randomI = (int)(Math.random()*globalBest.getUmlaufplan().get(random1).size());
			}
			int randomJ = (int)(Math.random()*(globalBest.getUmlaufplan().get(random2).size()-2));
			if(globalBest.getUmlaufplan().get(random2).size() <= 3){ //falls der Umlauf nur noch eine Servicefahrt beinhaltet
				randomJ = 1;
			}
			else{
				while(randomJ % 2 == 0){
					randomJ = (int)(Math.random()*(globalBest.getUmlaufplan().get(random2).size()-2));
				}
			}
			
			Fahrzeugumlauf eins = globalBest.getUmlaufplan().get(random1); 
			Fahrzeugumlauf zwei = globalBest.getUmlaufplan().get(random2);
			
			shaking = new Schedule(globalBest.getUmlaufplan(), stoppoints);
			
			if(validEdges.get(zwei.getAtIndex(randomJ).getId() + eins.getAtIndex(randomI).getId()) == 1){
				String deadruntimeId = zwei.getAtIndex(randomJ).getToStopId() + eins.getAtIndex(randomI).getFromStopId(); 
				Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId());
				einsNeu.addFahrten(zwei.getFahrtenVonBis(0, randomJ));
				einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
				einsNeu.addFahrten(eins.getFahrtenVonBis(randomI, eins.size() - 1));
				if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
					Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
					zweiNeu.addFahrten(eins.getFahrtenVonBis(0, randomI));
					deadruntimeId = eins.getAtIndex(randomI).getToStopId() + zwei.getAtIndex(randomJ).getFromStopId();
					zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
					zweiNeu.addFahrten(zwei.getFahrtenVonBis(randomJ, zwei.size() - 1));
					if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
						if(!eins.equals(einsNeu) && einsNeu != null){ // falls mindestens eine Verbesserung vorhanden ist, wird die Beste zurueckgegeben
							// Frequenzen der Ladungen an den Haltestellen aktualisieren
							for (int k = 0; k < eins.getLaden().size(); k++) {
								if(!eins.getLaden().contains(null)){
									int frequency = eins.getLaden().get(k).getFrequency() - 1;
									eins.getLaden().get(k).setFrequency(frequency);
									if(eins.getLaden().get(k).getFrequency() == 0){
										eins.getLaden().get(k).setLadestation(false);
									}
								}
							}
							for (int k = 0; k < zwei.getLaden().size(); k++) {
								if(!zwei.getLaden().contains(null)){
									int frequency = zwei.getLaden().get(k).getFrequency() - 1;
									zwei.getLaden().get(k).setFrequency(frequency);
									if(zwei.getLaden().get(k).getFrequency() == 0){
										zwei.getLaden().get(k).setLadestation(false);
									}
								}
							}
							for (int k = 0; k < einsNeu.getLaden().size(); k++) {
								if(!einsNeu.getLaden().contains(null)){
									int frequency = einsNeu.getLaden().get(k).getFrequency() + 1;
									einsNeu.getLaden().get(k).setFrequency(frequency);
									einsNeu.getLaden().get(k).setLadestation(true);
								}
							}
							for (int k = 0; k < zweiNeu.getLaden().size(); k++) {
								if(!zweiNeu.getLaden().contains(null)){
									int frequency = zweiNeu.getLaden().get(k).getFrequency() + 1;
									zweiNeu.getLaden().get(k).setFrequency(frequency);
									zweiNeu.getLaden().get(k).setLadestation(true);
								}
							}
							String id2 = shaking.getUmlaufplan().get(random2).getId();
							shaking.getUmlaufplan().remove(random1);
							for (int i = 0; i <= random2; i++) {
								if(shaking.getUmlaufplan().get(i).getId().equals(id2)){
									shaking.getUmlaufplan().remove(i);
									break;
								}
							}
							shaking.getUmlaufplan().add(einsNeu);
							shaking.getUmlaufplan().add(zweiNeu);
						}
					}
				}
			}
			
			counter ++;
			//System.out.println(counter);
			if(counter >= 100){
				condition = false;
			}
			if(!globalBest.getUmlaufplan().equals(shaking.getUmlaufplan())){
				condition = false;
			}
		}
		return shaking; 
	}
	
	/** Methode zum Bestimmen der best moeglichen Verbesserung innerhalb einem Umlaufplan
	 * 
	 */
	public Schedule bestImprovement(int kMax, Schedule shaking){
		// waehle zufaellig zwei Fahrzeugumlaeufe aus
		int random1 = (int)(Math.random()*globalBest.getUmlaufplan().size());
		
		ArrayList<Integer> randoms = new ArrayList<Integer>();
		randoms.add(random1);
		ZweiOptVerbesserung best = new ZweiOptVerbesserung(0.0, null, null, 0, 0);
		int nachbarschaft = 2;
		while(nachbarschaft <= kMax){
			int randomNeu = (int)(Math.random()*globalBest.getUmlaufplan().size());	
			while(randoms.contains(randomNeu)){
				randomNeu = (int)(Math.random()*globalBest.getUmlaufplan().size()); 
			}
			randoms.add(randomNeu);
			for (int i = 0; i < randoms.size()-1; i++) {
				ZweiOptVerbesserung temp = zweiOpt(randoms.get(i), randoms.get(randoms.size()-1));
				if(temp != null){
					if(temp.getCosts() > best.getCosts()){
						best = temp;
					}
				}
			}
			if(best.getCosts() == 0){
				nachbarschaft++;
			}
			else{
				Fahrzeugumlauf altEins = globalBest.getUmlaufplan().get(best.getIndexAltEins());
				for (int k = 0; k < altEins.getLaden().size(); k++) {
					if(!altEins.getLaden().contains(null)){
						int frequency = altEins.getLaden().get(k).getFrequency() - 1;
						altEins.getLaden().get(k).setFrequency(frequency);
						if(altEins.getLaden().get(k).getFrequency() == 0){
							altEins.getLaden().get(k).setLadestation(false);
						}
					}
				}
				Fahrzeugumlauf altZwei = globalBest.getUmlaufplan().get(best.getIndexAltZwei());
				for (int k = 0; k < altZwei.getLaden().size(); k++) {
					if(!altZwei.getLaden().contains(null)){
						int frequency = altZwei.getLaden().get(k).getFrequency() - 1;
						altZwei.getLaden().get(k).setFrequency(frequency);
						if(altZwei.getLaden().get(k).getFrequency() == 0){
							altZwei.getLaden().get(k).setLadestation(false);
						}
					}
				}
				localBest = globalBest;
				String id2 = localBest.getUmlaufplan().get(best.getIndexAltZwei()).getId();
				localBest.getUmlaufplan().remove(best.getIndexAltEins());
				for (int i = 0; i <= best.getIndexAltZwei(); i++) {
					if(localBest.getUmlaufplan().get(i).getId().equals(id2)){
						localBest.getUmlaufplan().remove(i);
						break;
					}
				}
				localBest.getUmlaufplan().add(best.getEins());
				localBest.getUmlaufplan().add(best.getZwei());

				for (int k = 0; k < best.getEins().getLaden().size(); k++) {
					if(!best.getEins().getLaden().contains(null)){
						int frequency = best.getEins().getLaden().get(k).getFrequency() + 1;
						best.getEins().getLaden().get(k).setFrequency(frequency);
						best.getEins().getLaden().get(k).setLadestation(true);
					}
				}
				for (int k = 0; k < best.getZwei().getLaden().size(); k++) {
					if(!best.getZwei().getLaden().contains(null)){
						int frequency = best.getZwei().getLaden().get(k).getFrequency() + 1;
						best.getZwei().getLaden().get(k).setFrequency(frequency);
						best.getZwei().getLaden().get(k).setLadestation(true);
					}
				}
				//System.out.println(nachbarschaft);
				break;
			}
		}
		return localBest;
	}
		
	
	public void firstImprovement(){
		
	}

	/** Methode gibt zurück, ob eine Verbesserung zwischen 2 unterschiedlichen Fahrzeugumläufen möglich ist (durch Kantentausch)
	 * 
	 * @param random1: ID des ersten Fahrzeugumlaufs 
	 * @param random2: ID des zweiten Fahrzeugumlaufs 
	 * @return
	 */
	
	public ZweiOptVerbesserung zweiOpt(int random1, int random2){
		
		ZweiOptVerbesserung result = null;
		
		Fahrzeugumlauf eins = globalBest.getUmlaufplan().get(random1); 
		Fahrzeugumlauf zwei = globalBest.getUmlaufplan().get(random2);
		
		double currentCostValue = eins.getKostenMitLadestationen() + zwei.getKostenMitLadestationen(); //aktuelle Gesamtkosten von Fahrzeugumlauf eins und zwei
		double initialCostValue = currentCostValue;
		
		Fahrzeugumlauf betterEins = null;
		Fahrzeugumlauf betterZwei = null;
		
		for (int i = -1; i < eins.size()-2; i = i + 2) { //es werden nur Servicefahrten betrachtet, daher i+2
			if(i == -1){ //falls i = -1 (Depotknoten im ersten Umlauf)
				for (int j = 3; j < zwei.size(); j = j + 2) { //die erste LF von j darf nicht geloescht werden
					if(validEdges.get(zwei.getAtIndex(j-2).getId()+eins.getAtIndex(i+2).getId()) == 1){
						//falls zeitlich von (j-2) zu (i+2) möglich ist -> verbinden
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
								double newCostValue = einsNeu.getKostenMitLadestationen() + zweiNeu.getKostenMitLadestationen(); //neue Kosten durch einsNeu und zweiNeu
								if(newCostValue < currentCostValue){ //wenn gespart wird
									currentCostValue = newCostValue; //
									betterEins = einsNeu;
									betterZwei = zweiNeu;
								}
							}
						}
					}
				}
			}
			else{ // falls i ungleich -1, also SF vom ersten Umlauf
				String id = eins.getAtIndex(i).getId(); 
				for (int j = 1; j < zwei.size(); j = j + 2) { // alle SF vom zweiten Umlauf
				id = eins.getAtIndex(i).getId();
				if(j == zwei.size() && i > eins.size() - 4){ // die letzten SF duerfen nicht miteinander verbunden werden
					break;
				}
				id = id + zwei.getAtIndex(j).getId();
				if (validEdges.get(id) == 1) { // falls zeitlich von i zu j möglich ist 
					if (j < 2) { // die erste SF vom zweiten Umlauf, weil dann Depotkante geloescht wird
						Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId());
						einsNeu.addFahrten(eins.getFahrtenVonBis(0, i));
						String deadruntimeId = eins.getAtIndex(i).getToStopId() + zwei.getAtIndex(j).getFromStopId(); 
						einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
						einsNeu.addFahrten(zwei.getFahrtenVonBis(j, zwei.size()-1));
						if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
							deadruntimeId = "00001" + eins.getAtIndex(i+2).getFromStopId(); // neue Depotkante muss hinzugefuegt werden
							zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
							zweiNeu.addFahrten(eins.getFahrtenVonBis(i+2, eins.size()-1));
							if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								// neue Umlaeufe speichern, falls besser
								double newCostValue = einsNeu.getKostenMitLadestationen() + zweiNeu.getKostenMitLadestationen();
								if(newCostValue < currentCostValue){
									currentCostValue = newCostValue;
									betterEins = einsNeu;
									betterZwei = zweiNeu;
							}
						}	
					}
					}
					else{ // ab dem zweiten SF vom zweiten Umlauf
						//if(validEdges.get(zwei.getAtIndex(j-2).getId() + eins.getAtIndex(i+2).getId()) == 1){ // kann raus, wurde schon beim if ueberprueft
							Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId());
							einsNeu.addFahrten(eins.getFahrtenVonBis(0, i));
							String deadruntimeId = eins.getAtIndex(i).getToStopId() + zwei.getAtIndex(j).getFromStopId(); 
							einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
							einsNeu.addFahrten(zwei.getFahrtenVonBis(j, zwei.size()-1));
							if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
								zweiNeu.addFahrten(zwei.getFahrtenVonBis(0, j-2));
								deadruntimeId = zwei.getAtIndex(j-2).getToStopId() + eins.getAtIndex(i+2).getFromStopId();
								zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
								zweiNeu.addFahrten(eins.getFahrtenVonBis(i+2, eins.size()-1));
								if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
									// neue Umlaeufe speichern, falls besser
									double newCostValue = einsNeu.getKostenMitLadestationen() + zweiNeu.getKostenMitLadestationen();
									if(newCostValue < currentCostValue){
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
		// Kopie von oben; es werden eins und zwei vertauscht, um auch Rueckwaertskanten zu betrachten
		for (int i = -1; i < zwei.size()-2; i = i + 2) { //es werden nur Servicefahrten betrachtet, daher i+2
			if(i == -1){ //falls i = -1 (Depotknoten im ersten Umlauf)
				for (int j = 3; j < eins.size(); j = j + 2) { //die erste LF von j darf nicht geloescht werden
					if(validEdges.get(eins.getAtIndex(j-2).getId()+zwei.getAtIndex(i+2).getId()) == 1){
						//falls zeitlich von (j-2) zu (i+2) möglich ist -> verbinden
						String deadruntimeId = eins.getAtIndex(j-2).getToStopId() + zwei.getAtIndex(i+2).getFromStopId(); 
						Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(zwei.getId());
						einsNeu.addFahrten(eins.getFahrtenVonBis(0, j-2));
						einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
						einsNeu.addFahrten(zwei.getFahrtenVonBis(i+2, zwei.size() - 1));
						if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(eins.getId());
							deadruntimeId = "00001" + eins.getAtIndex(j).getFromStopId();
							zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
							zweiNeu.addFahrten(eins.getFahrtenVonBis(j, eins.size() - 1));
							if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								// neue Umlaeufe speichern, falls besser
								double newCostValue = einsNeu.getKostenMitLadestationen() + zweiNeu.getKostenMitLadestationen(); //neue Kosten durch einsNeu und zweiNeu
								if(newCostValue < currentCostValue){ //wenn gespart wird
									currentCostValue = newCostValue; //
									betterEins = einsNeu;
									betterZwei = zweiNeu;
								}
							}
						}
					}
				}
			}
			else{ // falls i ungleich -1, also SF vom ersten Umlauf
				String id = zwei.getAtIndex(i).getId(); 
				for (int j = 1; j < eins.size(); j = j + 2) { // alle SF vom zweiten Umlauf
				id = zwei.getAtIndex(i).getId();
				if(j == eins.size() && i > zwei.size() - 4){ // die letzten SF duerfen nicht miteinander verbunden werden
					break;
				}
				id = id + eins.getAtIndex(j).getId();
				if (validEdges.get(id) == 1) { // falls zeitlich von i zu j möglich ist 
					if (j < 2) { // die erste SF vom zweiten Umlauf, weil dann Depotkante geloescht wird
						Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(zwei.getId());
						einsNeu.addFahrten(zwei.getFahrtenVonBis(0, i));
						String deadruntimeId = zwei.getAtIndex(i).getToStopId() + eins.getAtIndex(j).getFromStopId(); 
						einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
						einsNeu.addFahrten(eins.getFahrtenVonBis(j, eins.size()-1));
						if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(eins.getId());
							deadruntimeId = "00001" + zwei.getAtIndex(i+2).getFromStopId(); // neue Depotkante muss hinzugefuegt werden
							zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
							zweiNeu.addFahrten(zwei.getFahrtenVonBis(i+2, zwei.size()-1));
							if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								// neue Umlaeufe speichern, falls besser
								double newCostValue = einsNeu.getKostenMitLadestationen() + zweiNeu.getKostenMitLadestationen();
								if(newCostValue < currentCostValue){
									currentCostValue = newCostValue;
									betterEins = einsNeu;
									betterZwei = zweiNeu;
							}
						}	
					}
					}
					else{ // ab dem zweiten SF vom zweiten Umlauf
						//if(validEdges.get(zwei.getAtIndex(j-2).getId() + eins.getAtIndex(i+2).getId()) == 1){ // kann raus, wurde schon beim if ueberprueft
							Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(zwei.getId());
							einsNeu.addFahrten(zwei.getFahrtenVonBis(0, i));
							String deadruntimeId = zwei.getAtIndex(i).getToStopId() + eins.getAtIndex(j).getFromStopId(); 
							einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
							einsNeu.addFahrten(eins.getFahrtenVonBis(j, eins.size()-1));
							if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(eins.getId());
								zweiNeu.addFahrten(eins.getFahrtenVonBis(0, j-2));
								deadruntimeId = eins.getAtIndex(j-2).getToStopId() + zwei.getAtIndex(i+2).getFromStopId();
								zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
								zweiNeu.addFahrten(zwei.getFahrtenVonBis(i+2, zwei.size()-1));
								if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
									// neue Umlaeufe speichern, falls besser
									double newCostValue = einsNeu.getKostenMitLadestationen() + zweiNeu.getKostenMitLadestationen();
									if(newCostValue < currentCostValue){
										currentCostValue =  newCostValue;
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
		double savings = 0;
		if(!eins.equals(betterEins) && betterEins != null){ // falls mindestens eine Verbesserung vorhanden ist, wird die Beste zurueckgegeben
			savings = initialCostValue - currentCostValue;
			result = new ZweiOptVerbesserung(savings, betterEins, betterZwei, random1, random2);
		}
		return result;
	}
}
