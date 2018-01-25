package heuristic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import helper.SFumlegen;
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
		this.globalBest =  new Schedule(new Vector<Fahrzeugumlauf>(fahrzeugumlaeufe), stoppoints, servicejourneys, deadruntimes);
		this.localBest = new Schedule(new Vector<Fahrzeugumlauf>(globalBest.getUmlaufplan()), stoppoints, servicejourneys, deadruntimes);

	}
	
	/** Methode -> zufaellig die aktuelle Loesung (den aktuellen Umlaufplan) manipulieren (Verschlechterung wird zugelassen)
	 * 
	 */
	public Schedule shaking(){ 

		int counter = 0;
		boolean condition = true;
		while(condition) {
			int random1 = (int)(Math.random()*localBest.getUmlaufplan().size());
			int random2 = (int)(Math.random()*localBest.getUmlaufplan().size());
			while(random1 == random2){
				random2 = (int)(Math.random()*localBest.getUmlaufplan().size());
			}
			int randomI = (int)(Math.random()*localBest.getUmlaufplan().get(random1).size());
			while(randomI % 2 == 0){
				randomI = (int)(Math.random()*localBest.getUmlaufplan().get(random1).size());
			}
			int randomJ = (int)(Math.random()*(localBest.getUmlaufplan().get(random2).size()-2));
			if(localBest.getUmlaufplan().get(random2).size() <= 3){ //falls der Umlauf nur noch eine Servicefahrt beinhaltet
				randomJ = 1;
			}
			else{
				while(randomJ % 2 == 0){
					randomJ = (int)(Math.random()*(localBest.getUmlaufplan().get(random2).size()-2));
				}
			}
			/**
			random1 = 0;
			random2 = 1;
			randomI = 1;
			randomJ = 1;
			*/

			Fahrzeugumlauf eins = new Fahrzeugumlauf(localBest.getUmlaufplan().get(random1).getId()); 
			eins.addFahrten(localBest.getUmlaufplan().get(random1).getFahrten());
			Fahrzeugumlauf zwei = new Fahrzeugumlauf(localBest.getUmlaufplan().get(random2).getId()); 
			zwei.addFahrten(localBest.getUmlaufplan().get(random2).getFahrten());
			
			shaking = new Schedule(new Vector<Fahrzeugumlauf>(localBest.getUmlaufplan()), stoppoints, servicejourneys, deadruntimes);
			
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
			if(!localBest.getUmlaufplan().equals(shaking.getUmlaufplan())){
				condition = false;
			}
		}
		shaking.setFeasible();
		if(!shaking.isFeasible()){
			System.out.println();
		}
		return shaking; 
	}
	
	/** Methode zum Bestimmen der best moeglichen Verbesserung innerhalb einem Umlaufplan
	 * 
	 */
	public Schedule bestImprovement(int kMax, Schedule shaking){
		// waehle zufaellig zwei Fahrzeugumlaeufe aus
		//int random1 = (int)(Math.random()*shaking.getUmlaufplan().size());
		int random1 = 0;
		
		for (int i = 1; i < shaking.getUmlaufplan().size(); i++) {
			if(shaking.getUmlaufplan().get(i).size() < shaking.getUmlaufplan().get(random1).size()){
				random1 = i;
			}
		}
		
		localBest.setUmlaufplan(new Vector<Fahrzeugumlauf>(shaking.getUmlaufplan()));
		Schedule currentBest = new Schedule(new Vector<Fahrzeugumlauf>(localBest.getUmlaufplan()), stoppoints, servicejourneys, deadruntimes);

		ArrayList<Integer> randoms = new ArrayList<Integer>();
		randoms.add(random1);
		ZweiOptVerbesserung best = new ZweiOptVerbesserung(0.0, null, null, 0, 0);
		int nachbarschaft = 2;
		SFumlegen sfUmlegen = new SFumlegen(0.0, null, null, 0, 0);
		while(nachbarschaft <= kMax){
			
			int randomNeu = (int)(Math.random()*currentBest.getUmlaufplan().size()-1);	
			while(randoms.contains(randomNeu)){
				randomNeu = (int)(Math.random()*currentBest.getUmlaufplan().size()-1); 
			}
			
			//int randomNeu = nachbarschaft - 1;
			randoms.add(randomNeu);
			for (int i = 0; i < randoms.size()-1; i++) {
				int klein = 0;
				int gross = 0;
				if (currentBest.getUmlaufplan().get(randoms.get(i)).size() <= currentBest.getUmlaufplan().get(randoms.get(randoms.size()-1)).size()) {
					klein = randoms.get(i);
					gross = randoms.get(randoms.size()-1);
				}
				else{
					klein = randoms.get(randoms.size()-1);
					gross = randoms.get(i);
				}
				
				sfUmlegen = sfUmlegen(currentBest, klein, gross);
				
				ZweiOptVerbesserung temp = zweiOpt(randoms.get(i), randoms.get(randoms.size()-1));
				
				if(temp != null){
					if(i == 0){
						temp.setCosts(temp.getCosts()-100000);
					}
					if(temp.getCosts() > best.getCosts()){
						best = temp;
					}
				}
				
			}
			if(best.getCosts() == 0 && sfUmlegen.getCosts() == 0){
				nachbarschaft++;
			}
			else if(best.getCosts() > sfUmlegen.getCosts() && best.getEins().isFeasible(stoppoints, servicejourneys, deadruntimes) && best.getZwei().isFeasible(stoppoints, servicejourneys, deadruntimes)){
				Fahrzeugumlauf altEins = localBest.getUmlaufplan().get(best.getIndexAltEins());
				for (int k = 0; k < altEins.getLaden().size(); k++) {
					if(!altEins.getLaden().contains(null)){
						int frequency = altEins.getLaden().get(k).getFrequency() - 1;
						altEins.getLaden().get(k).setFrequency(frequency);
						if(altEins.getLaden().get(k).getFrequency() == 0){
							altEins.getLaden().get(k).setLadestation(false);
						}
					}
				}
				Fahrzeugumlauf altZwei = localBest.getUmlaufplan().get(best.getIndexAltZwei());
				for (int k = 0; k < altZwei.getLaden().size(); k++) {
					if(!altZwei.getLaden().contains(null)){
						int frequency = altZwei.getLaden().get(k).getFrequency() - 1;
						altZwei.getLaden().get(k).setFrequency(frequency);
						if(altZwei.getLaden().get(k).getFrequency() == 0){
							altZwei.getLaden().get(k).setLadestation(false);
						}
					}
				}
				// localBest = globalBest;
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
				System.out.println("Kantentausch");
				break;
			}
			else if(sfUmlegen.getEins() != null){ 
				if(sfUmlegen.getEins().isFeasible(stoppoints, servicejourneys, deadruntimes) && sfUmlegen.getZwei().isFeasible(stoppoints, servicejourneys, deadruntimes)){
				Fahrzeugumlauf altEins = localBest.getUmlaufplan().get(sfUmlegen.getIndexAltEins());
				for (int k = 0; k < altEins.getLaden().size(); k++) {
					if(!altEins.getLaden().contains(null)){
						int frequency = altEins.getLaden().get(k).getFrequency() - 1;
						altEins.getLaden().get(k).setFrequency(frequency);
						if(altEins.getLaden().get(k).getFrequency() == 0){
							altEins.getLaden().get(k).setLadestation(false);
						}
					}
				}
				Fahrzeugumlauf altZwei = localBest.getUmlaufplan().get(sfUmlegen.getIndexAltZwei());
				for (int k = 0; k < altZwei.getLaden().size(); k++) {
					if(!altZwei.getLaden().contains(null)){
						int frequency = altZwei.getLaden().get(k).getFrequency() - 1;
						altZwei.getLaden().get(k).setFrequency(frequency);
						if(altZwei.getLaden().get(k).getFrequency() == 0){
							altZwei.getLaden().get(k).setLadestation(false);
						}
					}
				}
				// localBest = globalBest;
				String id2 = localBest.getUmlaufplan().get(sfUmlegen.getIndexAltZwei()).getId();
				localBest.getUmlaufplan().remove(sfUmlegen.getIndexAltEins());
				for (int i = 0; i <= sfUmlegen.getIndexAltZwei(); i++) {
					if(localBest.getUmlaufplan().get(i).getId().equals(id2)){
						localBest.getUmlaufplan().remove(i);
						break;
					}
				}
				localBest.getUmlaufplan().add(sfUmlegen.getEins());
				localBest.getUmlaufplan().add(sfUmlegen.getZwei());

				for (int k = 0; k < sfUmlegen.getEins().getLaden().size(); k++) {
					if(!sfUmlegen.getEins().getLaden().contains(null)){
						int frequency = sfUmlegen.getEins().getLaden().get(k).getFrequency() + 1;
						sfUmlegen.getEins().getLaden().get(k).setFrequency(frequency);
						sfUmlegen.getEins().getLaden().get(k).setLadestation(true);
					}
				}
				for (int k = 0; k < sfUmlegen.getZwei().getLaden().size(); k++) {
					if(!sfUmlegen.getZwei().getLaden().contains(null)){
						int frequency = sfUmlegen.getZwei().getLaden().get(k).getFrequency() + 1;
						sfUmlegen.getZwei().getLaden().get(k).setFrequency(frequency);
						sfUmlegen.getZwei().getLaden().get(k).setLadestation(true);
					}
				}
				System.out.println("sfumlegen");
				break;
			}
		}
			else{
				if(sfUmlegen.getEins().isFeasible(stoppoints, servicejourneys, deadruntimes) && sfUmlegen.getZwei().isFeasible(stoppoints, servicejourneys, deadruntimes)){
					Fahrzeugumlauf altEins = localBest.getUmlaufplan().get(sfUmlegen.getIndexAltEins());
					for (int k = 0; k < altEins.getLaden().size(); k++) {
						if(!altEins.getLaden().contains(null)){
							int frequency = altEins.getLaden().get(k).getFrequency() - 1;
							altEins.getLaden().get(k).setFrequency(frequency);
							if(altEins.getLaden().get(k).getFrequency() == 0){
								altEins.getLaden().get(k).setLadestation(false);
							}
						}
					}
					Fahrzeugumlauf altZwei = localBest.getUmlaufplan().get(sfUmlegen.getIndexAltZwei());
					for (int k = 0; k < altZwei.getLaden().size(); k++) {
						if(!altZwei.getLaden().contains(null)){
							int frequency = altZwei.getLaden().get(k).getFrequency() - 1;
							altZwei.getLaden().get(k).setFrequency(frequency);
							if(altZwei.getLaden().get(k).getFrequency() == 0){
								altZwei.getLaden().get(k).setLadestation(false);
							}
						}
					}
					// localBest = globalBest;
					String id2 = localBest.getUmlaufplan().get(sfUmlegen.getIndexAltZwei()).getId();
					localBest.getUmlaufplan().remove(sfUmlegen.getIndexAltEins());
					for (int i = 0; i <= sfUmlegen.getIndexAltZwei(); i++) {
						if(localBest.getUmlaufplan().get(i).getId().equals(id2)){
							localBest.getUmlaufplan().remove(i);
							break;
						}
					}

					localBest.getUmlaufplan().add(sfUmlegen.getZwei());

					for (int k = 0; k < sfUmlegen.getZwei().getLaden().size(); k++) {
						if(!sfUmlegen.getZwei().getLaden().contains(null)){
							int frequency = sfUmlegen.getZwei().getLaden().get(k).getFrequency() + 1;
							sfUmlegen.getZwei().getLaden().get(k).setFrequency(frequency);
							sfUmlegen.getZwei().getLaden().get(k).setLadestation(true);
						}
					}
					System.out.println("sfumlegen");
					break;
			}
		}
		}
		return localBest;
	}
		
	
	public SFumlegen sfUmlegen(Schedule currentBest, int randomKlein, int randomGross){
		double savings = 0.0;
		
		ArrayList<Servicejourney> sfVonKlein = new ArrayList<Servicejourney>();
		Fahrzeugumlauf klein = currentBest.getUmlaufplan().get(randomKlein);
		Fahrzeugumlauf gross = currentBest.getUmlaufplan().get(randomGross);
		SFumlegen result = new SFumlegen(savings, null, null, randomKlein, randomGross);
		for (int i = 1; i < klein.size()-1; i = i + 2) {
			sfVonKlein.add((Servicejourney)klein.getAtIndex(i));
		}

		for (int k = 0; k < sfVonKlein.size(); k++) { 
			Servicejourney kleinSf = sfVonKlein.get(k);
			
			for (int i = 3; i < gross.size()-3; i = i + 2) {
				int index = 0; // ist der Index im Fahrzeugumlauf
				for (int j = 0; j < klein.size(); j++) {
					if(kleinSf.equals(klein.getFahrten().get(j))){
						break;
					}
					index ++;
				}
				Fahrzeugumlauf neuGross = new Fahrzeugumlauf(gross.getId());
				Fahrzeugumlauf neuKlein = new Fahrzeugumlauf(klein.getId());
				Servicejourney temp = (Servicejourney) gross.getAtIndex(i);
				if(kleinSf.getSfArrTime().getTime() <= temp.getSfDepTime().getTime()){ // passt die Servicefahrt zeitlich
					Deadruntime nachSf = deadruntimes.get(kleinSf.getToStopId() + temp.getFromStopId());
					if (kleinSf.getSfArrTime().getTime()+nachSf.getRuntime() <= temp.getSfDepTime().getTime()) { // passt die Sf + die Leerfahrt danach zeitlich
						temp = (Servicejourney) gross.getAtIndex(i-2);
						Deadruntime vorSf = deadruntimes.get(temp.getToStopId() + kleinSf.getFromStopId());
						if (temp.getSfArrTime().getTime() + vorSf.getRuntime() <= kleinSf.getSfDepTime().getTime()) { // passt die Leerfahrt davor + die Sf zeitlich
							neuGross.addFahrten(gross.getFahrtenVonBis(0, i-2));
							neuGross.addFahrt(vorSf);
							neuGross.addFahrt(kleinSf);
							neuGross.addFahrt(nachSf);
							neuGross.addFahrten(gross.getFahrtenVonBis(i, gross.size()-1));
							if(klein.size() > 3){ // neuKlein wird nur gebaut wenn klein mehr als eine SF hat
								if(index >= 3 && index <= klein.size()-3){ // eine mittlere SF wird geloescht
									neuKlein.addFahrten(klein.getFahrtenVonBis(0, (index)-2));
									neuKlein.addFahrt(deadruntimes.get(klein.getAtIndex((index)-2).getToStopId() + klein.getAtIndex((index)+2).getFromStopId()));
									neuKlein.addFahrten(klein.getFahrtenVonBis((index)+2, klein.size()-1));
									if(!neuKlein.isFeasible(stoppoints, servicejourneys, deadruntimes)){
										break;
									}
								}
								else if(index == 1){ // die erste SF wird geloescht
									neuKlein.addFahrt(deadruntimes.get("00001" + klein.getAtIndex(3).getFromStopId()));
									neuKlein.addFahrten(klein.getFahrtenVonBis(3, klein.size()-1));
									if(!neuKlein.isFeasible(stoppoints, servicejourneys, deadruntimes)){
										break;
									}
								}
								else if(index == klein.size()-2){ // die letzte SF wird geloescht
									neuKlein.addFahrten(klein.getFahrtenVonBis(0, (index)-2));
									neuKlein.addFahrt(deadruntimes.get(klein.getAtIndex(klein.size()-4).getToStopId() + "00001"));
									if(!neuKlein.isFeasible(stoppoints, servicejourneys, deadruntimes)){
										break;
									}
								}
								else{
									System.out.println();
									break;
								} 
									
							}
							if(neuGross.isFeasible(stoppoints, servicejourneys, deadruntimes)){
								result.setEins(neuKlein);
								result.setZwei(neuGross);
								/**
								for (int j = 0; j < currentBest.getUmlaufplan().size(); j++) {
									if(currentBest.getUmlaufplan().get(j).getId().equals(gross.getId())){
										currentBest.getUmlaufplan().remove(j);
										currentBest.getUmlaufplan().add(neuGross);
										for (int x = 0; x < gross.getLaden().size(); x++) {
											if(!gross.getLaden().contains(null)){
												int frequency = gross.getLaden().get(x).getFrequency() - 1;
												gross.getLaden().get(x).setFrequency(frequency);
												if(gross.getLaden().get(x).getFrequency() == 0){
													gross.getLaden().get(x).setLadestation(false);
												}
											}
										}
										for (int x = 0; x < neuGross.getLaden().size(); x++) {
											if(!neuGross.getLaden().contains(null)){
												int frequency = neuGross.getLaden().get(x).getFrequency() + 1;
												neuGross.getLaden().get(x).setFrequency(frequency);
												neuGross.getLaden().get(x).setLadestation(true);
											}
										}
									}
								}
								gross.getFahrten().clear();
								gross.addFahrten(neuGross.getFahrten());
								gross.setLaden(neuGross.getLaden());

								if(klein.size() > 3){
									for (int j = 0; j < currentBest.getUmlaufplan().size(); j++) {
										if(currentBest.getUmlaufplan().get(j).getId().equals(klein.getId())){
											currentBest.getUmlaufplan().remove(j);
											currentBest.getUmlaufplan().add(neuKlein);
											for (int x = 0; x < neuKlein.getLaden().size(); x++) {
												if(!neuKlein.getLaden().contains(null)){
													int frequency = neuKlein.getLaden().get(x).getFrequency() + 1;
													neuKlein.getLaden().get(x).setFrequency(frequency);
													neuKlein.getLaden().get(x).setLadestation(true);
												}
											}
										}
									}
								}
								else{
									for (int j = 0; j < currentBest.getUmlaufplan().size(); j++) {
										if(currentBest.getUmlaufplan().get(j).getId().equals(klein.getId())){
											currentBest.getUmlaufplan().remove(j);
										}
									}
								}
								for (int j = 0; j < klein.getLaden().size(); j++) {
									if(!klein.getLaden().contains(null)){
										int frequency = klein.getLaden().get(j).getFrequency() - 1;
										klein.getLaden().get(j).setFrequency(frequency);
										if(klein.getLaden().get(j).getFrequency() == 0){
											klein.getLaden().get(j).setLadestation(false);
										}
									}
								}
								if (klein.size() > 3) {
									klein.getFahrten().clear();
									klein.addFahrten(neuKlein.getFahrten());
									klein.setLaden(neuKlein.getLaden());
								}
								*/
							}
							
							else{
								break;
							}
							
							savings = savings + 400000/((klein.getFahrten().size())-1)/2;
							
						}
						
					}
				}
			}
			
		}
		result.setCosts(savings);
		return result;
	}

	/** Methode gibt zurück, ob eine Verbesserung zwischen 2 unterschiedlichen Fahrzeugumläufen möglich ist (durch Kantentausch)
	 * 
	 * @param random1: ID des ersten Fahrzeugumlaufs 
	 * @param random2: ID des zweiten Fahrzeugumlaufs 
	 * @return
	 */
	
	public ZweiOptVerbesserung zweiOpt(int random1, int random2){
		
		ZweiOptVerbesserung result = null;

		Fahrzeugumlauf eins = localBest.getUmlaufplan().get(random1); 
		Fahrzeugumlauf zwei = localBest.getUmlaufplan().get(random2);
		eins.isFeasible(stoppoints, servicejourneys, deadruntimes);
		zwei.isFeasible(stoppoints, servicejourneys, deadruntimes);
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
