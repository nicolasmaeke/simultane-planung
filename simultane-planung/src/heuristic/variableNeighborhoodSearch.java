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
	
	/** Methode -> zufaellig die aktuelle Loesung (den aktuellen Umlaufplan) manipulieren (Verschlechterung wird zugelassen)
	 * 
	 */
	public void shaking(){ 
		int random1 = (int)(Math.random()*fahrzeugumlaeufe.size());
		int random2 = (int)(Math.random()*fahrzeugumlaeufe.size());
		while(random1 == random2){
			random2 = (int)(Math.random()*fahrzeugumlaeufe.size());
		}
		int randomI = (int)(Math.random()*fahrzeugumlaeufe.get(random1).size());
		while(randomI % 2 == 0){
			randomI = (int)(Math.random()*fahrzeugumlaeufe.get(random1).size());
		}
		int randomJ = (int)(Math.random()*fahrzeugumlaeufe.get(random2).size()-2);
		while(randomJ % 2 == 0){
			randomJ = (int)(Math.random()*fahrzeugumlaeufe.get(random2).size()-2);
		}
		
		Fahrzeugumlauf eins = fahrzeugumlaeufe.get(random1); 
		Fahrzeugumlauf zwei = fahrzeugumlaeufe.get(random2);
		
		if(validEdges.get(zwei.getAtIndex(randomJ).getId()+eins.getAtIndex(randomI).getId()) == 1){
			String deadruntimeId = zwei.getAtIndex(randomJ).getToStopId() + eins.getAtIndex(randomI).getFromStopId(); 
			Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId());
			einsNeu.addFahrten(zwei.getFahrtenVonBis(0, randomJ));
			einsNeu.addFahrt(deadruntimes.get(deadruntimeId));
			einsNeu.addFahrten(eins.getFahrtenVonBis(randomI, eins.size() - 1));
			if(einsNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
				Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
				deadruntimeId = eins.getAtIndex(randomI).getToStopId() + zwei.getAtIndex(randomJ).getFromStopId();
				zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
				zweiNeu.addFahrten(zwei.getFahrtenVonBis(randomJ, zwei.size() - 1));
				if(zweiNeu.isFeasible(stoppoints, servicejourneys, deadruntimes)){
					//double savings = 0;
					if(!eins.equals(einsNeu) && einsNeu != null){ // falls mindestens eine Verbesserung vorhanden ist, wird die Beste zurueckgegeben
						//savings = initialCostValue - currentCostValue;
						// Frequenzen der Ladungen an den Haltestellen aktualisieren
						for (int k = 0; k < eins.getLaden().size(); k++) {
							if(!eins.getLaden().contains(null)){
								int frequency = eins.getLaden().get(k).getFrequency() - 1;
								eins.getLaden().get(k).setFrequency(frequency);
							}
						}
						for (int k = 0; k < zwei.getLaden().size(); k++) {
							if(!zwei.getLaden().contains(null)){
								int frequency = zwei.getLaden().get(k).getFrequency() - 1;
								zwei.getLaden().get(k).setFrequency(frequency);
							}
						}
						for (int k = 0; k < einsNeu.getLaden().size(); k++) {
							if(!einsNeu.getLaden().contains(null)){
								int frequency = einsNeu.getLaden().get(k).getFrequency() + 1;
								einsNeu.getLaden().get(k).setFrequency(frequency);
							}
						}
						for (int k = 0; k < zweiNeu.getLaden().size(); k++) {
							if(!zweiNeu.getLaden().contains(null)){
								int frequency = zweiNeu.getLaden().get(k).getFrequency() + 1;
								zweiNeu.getLaden().get(k).setFrequency(frequency);
							}
						}
						//result = new ZweiOptVerbesserung(savings, betterEins, betterZwei, random1, random2);
					}
					//return result;
				}
			}
		}
	}
	
	/** Methode zum Bestimmen der best moeglichen Verbesserung innerhalb einem Umlaufplan
	 * 
	 */
	public void bestImprovement(){
		// waehle zufaellig zwei Fahrzeugumlaeufe aus
		int random1 = (int)(Math.random()*fahrzeugumlaeufe.size());
		int random2 = (int)(Math.random()*fahrzeugumlaeufe.size());	
		//int random1 = 69;
		//int random2 = 50;
		System.out.println(random1);
		System.out.println(random2);
		while(random1 == random2){
			random2 = (int)(Math.random()*fahrzeugumlaeufe.size()); //random2 ungleich random1
		}
		ZweiOptVerbesserung verbesserung1 = zweiOpt(random1, random2);
		if(verbesserung1 == null && fahrzeugumlaeufe.size() > 2){
			int random3 = (int)(Math.random()*fahrzeugumlaeufe.size());
			//int random3 = 18;
			System.out.println(random3);
			ZweiOptVerbesserung verbesserung2 = null;
			while(random1 == random3 || random2 == random3){
				random3 = (int)(Math.random()*fahrzeugumlaeufe.size());
			}
			verbesserung1 = zweiOpt(random1, random3);
			verbesserung2 = zweiOpt(random2, random3);
			if(verbesserung1 == null && verbesserung2 == null && fahrzeugumlaeufe.size() > 3){
				ZweiOptVerbesserung verbesserung3 = null;
				int random4 = (int)(Math.random()*fahrzeugumlaeufe.size());
				//int random4 = 75;
				System.out.println(random4);
				while(random1 == random4 || random2 == random4 || random3 == random4){
					random4 = (int)(Math.random()*fahrzeugumlaeufe.size());
				}
				verbesserung1 = zweiOpt(random1, random4);
				verbesserung2 = zweiOpt(random2, random4);
				verbesserung3 = zweiOpt(random3, random4);
				if(verbesserung1 == null && verbesserung2 == null && verbesserung3 == null){
					return;
				}
				else{ // Fall mit Nachbargschaftsgroesse 4
					ArrayList<ZweiOptVerbesserung> list = new ArrayList<>();
					ZweiOptVerbesserung best = new ZweiOptVerbesserung(0.0, null, null, 0, 0);
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
						if(list.get(i).getCosts() >= best.getCosts()){
							best = list.get(i);
						}
					}
					String id2 = fahrzeugumlaeufe.get(best.getIndexAltZwei()).getId();
					fahrzeugumlaeufe.remove(best.getIndexAltEins());
					for (int i = 0; i <= best.getIndexAltZwei(); i++) {
						if(fahrzeugumlaeufe.get(i).getId().equals(id2)){
							fahrzeugumlaeufe.remove(i);
							break;
						}
					}
					fahrzeugumlaeufe.add(best.getEins());
					fahrzeugumlaeufe.add(best.getZwei());
				}
			}
			else{ // Fall mit Nachbarschaftgroesse 3
				ArrayList<ZweiOptVerbesserung> list = new ArrayList<>();
				ZweiOptVerbesserung best = new ZweiOptVerbesserung(0.0, null, null, 0, 0);
				if(verbesserung1 != null){
					list.add(verbesserung1);
				}
				if(verbesserung2 != null){
					list.add(verbesserung2);
				}
				
				if(list.isEmpty()){ // beide sind null
					return;
				}
				
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).getCosts() >= best.getCosts()){
						best = list.get(i);
					}
				}
				String id2 = fahrzeugumlaeufe.get(best.getIndexAltZwei()).getId();
				fahrzeugumlaeufe.remove(best.getIndexAltEins());
				for (int i = 0; i <= best.getIndexAltZwei(); i++) {
					if(fahrzeugumlaeufe.get(i).getId().equals(id2)){
						fahrzeugumlaeufe.remove(i);
						break;
					}
				}
				fahrzeugumlaeufe.add(best.getEins());
				fahrzeugumlaeufe.add(best.getZwei());
			}
				
		
		}
		else if(verbesserung1 != null){ // Fall mit Nachbarschaftgroesse 2
			// neuer Umlaufplan
			String id2 = fahrzeugumlaeufe.get(random2).getId();
			fahrzeugumlaeufe.remove(random1);
			for (int i = 0; i <= random2; i++) {
				if(fahrzeugumlaeufe.get(i).getId().equals(id2)){
					fahrzeugumlaeufe.remove(i);
					break;
				}
			}
			fahrzeugumlaeufe.add(verbesserung1.getEins());
			fahrzeugumlaeufe.add(verbesserung1.getZwei());
		}
			
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
		
		Fahrzeugumlauf eins = fahrzeugumlaeufe.get(random1); 
		Fahrzeugumlauf zwei = fahrzeugumlaeufe.get(random2);
		
		double currentCostValue = eins.getKosten() + zwei.getKosten(); //aktuelle Gesamtkosten von Fahrzeugumlauf eins und zwei
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
								double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten(); //neue Kosten durch einsNeu und zweiNeu
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
								double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten();
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
									double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten();
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
								double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten(); //neue Kosten durch einsNeu und zweiNeu
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
								double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten();
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
									double newCostValue = einsNeu.getKosten() + zweiNeu.getKosten();
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
			// Frequenzen der Ladungen an den Haltestellen aktualisieren
			for (int k = 0; k < eins.getLaden().size(); k++) {
				if(!eins.getLaden().contains(null)){
					int frequency = eins.getLaden().get(k).getFrequency() - 1;
					eins.getLaden().get(k).setFrequency(frequency);
				}
			}
			for (int k = 0; k < zwei.getLaden().size(); k++) {
				if(!zwei.getLaden().contains(null)){
					int frequency = zwei.getLaden().get(k).getFrequency() - 1;
					zwei.getLaden().get(k).setFrequency(frequency);
				}
			}
			for (int k = 0; k < betterEins.getLaden().size(); k++) {
				if(!betterEins.getLaden().contains(null)){
					int frequency = betterEins.getLaden().get(k).getFrequency() + 1;
					betterEins.getLaden().get(k).setFrequency(frequency);
				}
			}
			for (int k = 0; k < betterZwei.getLaden().size(); k++) {
				if(!betterZwei.getLaden().contains(null)){
					int frequency = betterZwei.getLaden().get(k).getFrequency() + 1;
					betterZwei.getLaden().get(k).setFrequency(frequency);
				}
			}
			result = new ZweiOptVerbesserung(savings, betterEins, betterZwei, random1, random2);
		}
		return result;
	}
}
