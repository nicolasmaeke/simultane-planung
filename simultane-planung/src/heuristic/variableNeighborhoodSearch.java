package heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import helper.ZweiOptVerbesserung;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Servicejourney;
import output.Schedule;

/** Klasse beinhaltet Methode für Variable Neighborhood Search
 */
public class variableNeighborhoodSearch {
	
	Schedule localBest; // lokal beste Löoesung
	Schedule shaking; // Loesung nach dem Shaking
	HashMap<String, Integer> validEdges;
	public HashMap<String, Deadruntime> deadruntimes;
	public HashMap<String, Servicejourney> servicejourneys;

	public variableNeighborhoodSearch(Schedule globalCopy, HashMap<String, Integer> validEdges, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Servicejourney> servicejourneys){
		this.validEdges = validEdges;
		this.deadruntimes = deadruntimes;
		this.servicejourneys = servicejourneys;
		for (int i = 0; i < globalCopy.getUmlaufplan().size(); i++) {
			if(!globalCopy.isFeasible(globalCopy.getUmlaufplan().get(i))){
				System.out.println();
			}
		}
		this.localBest = new Schedule(globalCopy.getUmlaufplan(), servicejourneys, deadruntimes, globalCopy.getStoppoints());
	}
	
	/** Shaking: Methode -> zufaellig die aktuelle Loesung (den aktuellen Umlaufplan) manipulieren (Verschlechterung wird zugelassen)
	 * 
	 */
	public Schedule shaking(){ 

		int counter = 0;
		boolean condition = true;
		while(condition) {
			// Zwei beliebige unterschiedliche Umlaeufe aus Globalbest auswaehlen
			int random1 = (int)(Math.random()*localBest.getUmlaufplan().size()); // Index eines beliebigen Umlaufs aus Globalbest
			while(localBest.getUmlaufplan().get(random1).size() <= 3){
				random1 = (int)(Math.random()*localBest.getUmlaufplan().size());
			}
			int random2 = (int)(Math.random()*localBest.getUmlaufplan().size()); // Index des zweiten beliebigen Umlaufs aus Globalbest
			while(random1 == random2 || localBest.getUmlaufplan().get(random2).size() <= 3){
				random2 = (int)(Math.random()*localBest.getUmlaufplan().size()); // nicht den gleichen Umlauf nehmen
			}
			
			// Zwei beliebige Servicefahrten aus den zwei gewaehlten Umlaefen 
			int randomI = (int)((Math.random()*localBest.getUmlaufplan().get(random1).size())); // Index einer beliebigen SF aus Umlauf mit der ID ramdom1
			while(randomI % 2 == 0){ 
				randomI = (int)(Math.random()*localBest.getUmlaufplan().get(random1).size());
			}
			if(randomI == 1){
				randomI = 3;
			}
			
			int randomJ = (int)(Math.random()*(localBest.getUmlaufplan().get(random2).size()-2)); // Index einer beliebigen SF aus Umlauf mit der ID random2
			if(localBest.getUmlaufplan().get(random2).size() <= 3){ // falls der Umlauf nur noch eine Servicefahrt beinhaltet
				randomJ = 1; // ID = 1
			}
			else{
				while(randomJ % 2 == 0){
					randomJ = (int)(Math.random()*(localBest.getUmlaufplan().get(random2).size()-2));
				}
			}
			
			Fahrzeugumlauf eins = localBest.getUmlaufplan().get(random1); // eins ist der erste gewaehlte Umlauf
			Fahrzeugumlauf zwei = localBest.getUmlaufplan().get(random2); // zwei ist der zweite gewaehlte Umlauf
			
			for (int i = 0; i < localBest.getUmlaufplan().size(); i++) {
				if(!localBest.isFeasible(localBest.getUmlaufplan().get(i))){
					System.out.println();
				}
			}
			shaking = new Schedule(new Vector<Fahrzeugumlauf>(localBest.getUmlaufplan()), servicejourneys, deadruntimes, localBest.getStoppoints()); // neuer Fahrzeugumlauf für Shaking 
			
			if(validEdges.get(zwei.getAtIndex(randomJ).getId() + eins.getAtIndex(randomI).getId()) == 1){ // j mit i verbinden
				// falls zwischen zwei gewaehlten SFs eine Verbindung zeitlich moeglich ist
				String deadruntimeId = zwei.getAtIndex(randomJ).getToStopId() + eins.getAtIndex(randomI).getFromStopId(); // ID der Leerfahrt
				Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId()); // Neuer Umlauf mit der ID von eins
				einsNeu.addFahrten(zwei.getFahrtenVonBis(0, randomJ)); // Aus zwei: Anfang bis einschließlich SF randomJ
				einsNeu.addFahrt(deadruntimes.get(deadruntimeId)); // Leerfahrt von randomJ bis randomI
				einsNeu.addFahrten(eins.getFahrtenVonBis(randomI, eins.size() - 1)); // Aus eins: randomI bis Ende
				if(localBest.isFeasible(einsNeu)){ // falls einsNeu feasible ist
					Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId()); // Neuer Umlauf mit der ID von zwei
					zweiNeu.addFahrten(eins.getFahrtenVonBis(0, randomI-2)); // Aus eins: Anfang bis .... 
					deadruntimeId = eins.getAtIndex(randomI).getToStopId() + zwei.getAtIndex(randomJ).getFromStopId();
					zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));  
					zweiNeu.addFahrten(zwei.getFahrtenVonBis(randomJ+2, zwei.size() - 1)); 
					if(localBest.isFeasible(zweiNeu)){ // falls zweiNeu feasible ist
						if(!eins.equals(einsNeu) && einsNeu != null){ // falls einsNeu ungleich eins und einsNeu nicht leer ist						// falls mindestens eine Verbesserung vorhanden ist, wird die Beste zurueckgegeben
							
							String id2 = shaking.getUmlaufplan().get(random2).getId(); // ID des Umlaufs zwei
							shaking.getUmlaufplan().remove(random1); // entferne Umlauf eins aus shaking
							for (int i = 0; i <= random2; i++) { 
								if(shaking.getUmlaufplan().get(i).getId().equals(id2)){
									shaking.getUmlaufplan().remove(i); // entferne Umlauf zwei aus shaking
									break;
								}
							}
							if(((eins.size()+zwei.size())) - (einsNeu.size()+zweiNeu.size()) != 0){
								System.out.println();
							}
							shaking.getUmlaufplan().add(einsNeu); // fuege einsNeu in Shaking hinzu
							if(!shaking.isFeasible(einsNeu)){
								System.out.println("Not Feasible Shaking!");
							}
							shaking.getUmlaufplan().add(zweiNeu); // fuege zweiNeu in Shaking hinzu
							if(!shaking.isFeasible(zweiNeu)){
								System.out.println("Not Feasible Shaking!");
							}
							shaking.berechneFrequenzen();
						}
					}
				}
			}
			
			counter ++;
			//System.out.println(counter);
			if(counter >= 100){
				condition = false;
			}
			if(!localBest.getUmlaufplan().equals(shaking.getUmlaufplan())){ // wenn nicht Feasible, dann noch einmal Shaking
				condition = false;
			}
			
		}
		
		return shaking;  // den Umlaufplan nach dem Shaking zurueckgeben
	}
	
	/** BestImprovement: Methode zum Bestimmen der best moeglichen Verbesserung innerhalb einem Umlaufplan
	 * 
	 * @param kMax - maximale Anzahl der Nachbarschaften
	 * @param shaking - Umlaufplan nach dem Shaking
	 * @return
	 */
	public Schedule bestImprovement(int kMax, Schedule shaking){
		if(kMax > shaking.getUmlaufplan().size()){
			kMax = shaking.getUmlaufplan().size();
		}
		// waehle zufaellig zwei Fahrzeugumlaeufe aus
		int random1 = (int)(Math.random()*shaking.getUmlaufplan().size()); // index eines beliebigen Umlaufs aus dem geshakten Umlaufplan
		localBest = shaking; // initialisiere Umlaufplan Lokal beste Loesung
		ArrayList<Integer> randoms = new ArrayList<Integer>(); // Liste der randoms Werte
		randoms.add(random1); // fuege random1 in die Liste randoms hinzu
		ZweiOptVerbesserung best = new ZweiOptVerbesserung(0.0, null, null, 0, 0); // initialisiere best
		int nachbarschaft = 2; // fange mit 2-Nachbarschaft
		while(nachbarschaft <= kMax){ // solange kMax nicht erreicht
			int randomNeu = (int)(Math.random()*shaking.getUmlaufplan().size()); // index eines zweiten beliebigen Umlaufs aus dem geshakten Umlaufplan	
			while(randoms.contains(randomNeu)){
				randomNeu = (int)(Math.random()*shaking.getUmlaufplan().size()); 
			}
			randoms.add(randomNeu); // fuege randomNeu in die Liste randoms hinzu
			
			for (int i = 0; i < randoms.size()-1; i++) { // fuer jede random Wert bis zu dem letzten
				int anzahlSF = 0;
				for (int i1 = 0; i1 < localBest.getUmlaufplan().size(); i1++) {
					for (int j = 0; j < localBest.getUmlaufplan().get(i1).size(); j++) {
						if(localBest.getUmlaufplan().get(i1).getFahrten().get(j) instanceof Servicejourney){
							anzahlSF++;
						}
					}
				}				
				ZweiOptVerbesserung temp = zweiOpt(randoms.get(i), randoms.get(randoms.size()-1)); // setze Methode Zweioptverbesserung ein
				int anzahlSFNach = 0;
				for (int i1 = 0; i1 < localBest.getUmlaufplan().size(); i1++) {
					for (int j = 0; j < localBest.getUmlaufplan().get(i1).size(); j++) {
						if(localBest.getUmlaufplan().get(i1).getFahrten().get(j) instanceof Servicejourney){
							anzahlSFNach++;
						}
					}
				}
				if(anzahlSF - anzahlSFNach != 0){
					System.out.println();
				}
				
				
				
				if(temp != null){ // falls eine Verbesserung vorhanden ist
					if(temp.getCosts() > best.getCosts()){ // falls durch temp mehr gespart wird als durch best
						best = temp; // ersetzte best durch temp
					}
				}
			}
			if(best.getCosts() == 0){ // wenn durch die aktuelle Nachbarschaft nichts gespart wird
				nachbarschaft++; // erhoehe die Nachbarschaft um 1
			}
			else{ // wenn durch die aktuelle Nachbarschaft gespart wird
				
				// localBest = globalBest;
				String id2 = localBest.getUmlaufplan().get(best.getIndexAltZwei()).getId();
				localBest.getUmlaufplan().remove(best.getIndexAltEins()); // entferne altEins aus der lokal besten Loesung
				for (int i = 0; i <= best.getIndexAltZwei(); i++) { 
					if(localBest.getUmlaufplan().get(i).getId().equals(id2)){
						localBest.getUmlaufplan().remove(i); // entferne altZwei aus der lokal besten Loesung
						break; // hoert auf sobald altZwei gefunden wird
					}
				}
				localBest.getUmlaufplan().add(best.getEins()); // fuege Eins in Lokalbest hinzu
				if(!localBest.isFeasible(best.getEins())){
					System.out.println("Not Feasible zweiOpt!");
				}
				localBest.getUmlaufplan().add(best.getZwei()); // fuege Zwei in Lokalbest hinzu
				if(!localBest.isFeasible(best.getZwei())){
					System.out.println("Not Feasible zweiOpt!");
				}
				localBest.berechneFrequenzen();

				System.out.println(nachbarschaft);
				break;
			}
		}
		
		List<Fahrzeugumlauf> minimal = new ArrayList<Fahrzeugumlauf>();
		minimal.add(localBest.getUmlaufplan().get(0)); // initialisiere den minimalen Umlauf in Lokalbest
		minimal.add(localBest.getUmlaufplan().get(1));
		minimal.add(localBest.getUmlaufplan().get(2));
		minimal.add(localBest.getUmlaufplan().get(3));
		minimal.add(localBest.getUmlaufplan().get(4));
		for (int i = 5; i < localBest.getUmlaufplan().size(); i++) {
			for (int j = 0; j < minimal.size(); j++) {
				if(localBest.getUmlaufplan().get(i).size() < minimal.get(j).size()){
					if(!minimal.contains(localBest.getUmlaufplan().get(i))){
						minimal.remove(j);
						minimal.add(localBest.getUmlaufplan().get(i));	
					}			
				}
			}
		}
		int randomMinimal = (int)(Math.random()*minimal.size());
		int gross = random1;
		while(localBest.getUmlaufplan().get(gross).equals(minimal.get(randomMinimal))){
			gross = (int)(Math.random()*localBest.getUmlaufplan().size()); // index eines beliebigen Umlaufs in Lokalbest
		}

		sfUmlegen(minimal.get(randomMinimal), localBest.getUmlaufplan().get(gross)); // Umlegen minimal und gross
		
		return localBest; // lokal beste Loesung zurueckgeben
	}
		
	/** sfUmlegen: Methode zum Umlegen von zwei Fahrzeugumlaeufen
	 * 
	 * @param klein - kleinerer Umlauf
	 * @param gross - groeßerer Umlauf
	 */
	public void sfUmlegen(Fahrzeugumlauf klein, Fahrzeugumlauf gross){
		int kleinSize = klein.size();
		int grossSize = gross.size();

		Fahrzeugumlauf anfangGross = new Fahrzeugumlauf(gross.getId());
		anfangGross.addFahrten(gross.getFahrten());
		Fahrzeugumlauf anfangKlein = new Fahrzeugumlauf(klein.getId());
		anfangKlein.addFahrten(klein.getFahrten());
		int anzahlSF = 0;
		for (int i = 0; i < localBest.getUmlaufplan().size(); i++) {
			for (int j = 0; j < localBest.getUmlaufplan().get(i).size(); j++) {
				if(localBest.getUmlaufplan().get(i).getFahrten().get(j) instanceof Servicejourney){
					anzahlSF++;
				}
			}
		}
		ArrayList<Servicejourney> sfVonKlein = new ArrayList<Servicejourney>(); // Liste aller SF vom kleineren Umlauf
		for (int i = 1; i < klein.size()-1; i = i + 2) {
			sfVonKlein.add((Servicejourney)klein.getAtIndex(i)); 
		}
		if(sfVonKlein.size() == 1){
			System.out.println();
		}
		for (int k = 0; k < sfVonKlein.size(); k++) { // fuer jede SF aus dem kleineren Umlauf
			Servicejourney kleinSf = sfVonKlein.get(k);
			
			for (int i = 3; i < gross.size()-3; i = i + 2) { // fuer jede SF aus dem groeßeren Umlauf
				int index = 0; 
				for (int j = 0; j < klein.size(); j++) {
					if(kleinSf.equals(klein.getFahrten().get(j))){
						break;
					}
					index ++; // index der gewaehlten SF im kleineren Umlauf
				}
				Fahrzeugumlauf neuGross = new Fahrzeugumlauf(gross.getId());
				Fahrzeugumlauf neuKlein = new Fahrzeugumlauf(klein.getId());
				Servicejourney temp = (Servicejourney) gross.getAtIndex(i); 
				
				if(kleinSf.getSfArrTime().getTime() <= temp.getSfDepTime().getTime()){ // passen die Servicefahrten zeitlich?
					Deadruntime nachSf = deadruntimes.get(kleinSf.getToStopId() + temp.getFromStopId()); // ID der Nach-Leerfahrt
					if (kleinSf.getSfArrTime().getTime()+nachSf.getRuntime() <= temp.getSfDepTime().getTime()) { // passt die SF + die Leerfahrt danach zeitlich?
						temp = (Servicejourney) gross.getAtIndex(i-2);
						Deadruntime vorSf = deadruntimes.get(temp.getToStopId() + kleinSf.getFromStopId()); // ID der Vor-Leerfahrt
						if (temp.getSfArrTime().getTime() + vorSf.getRuntime() <= kleinSf.getSfDepTime().getTime()) { // passt die Leerfahrt davor + die SF zeitlich
							neuGross.addFahrten(gross.getFahrtenVonBis(0, i-2)); // Anfang gross bis einschließlich i-2
							neuGross.addFahrt(vorSf); // Vor-Leerfahrt
							neuGross.addFahrt(kleinSf); // SF aus klein
							neuGross.addFahrt(nachSf); // Nach-Leerfahrt
							neuGross.addFahrten(gross.getFahrtenVonBis(i, gross.size()-1)); // i bis Ende von gross
							if(gross.size() - neuGross.size() != -2){
								System.out.println();
							}
							if(klein.size() > 3){ // neuKlein wird nur gebaut wenn klein mehr als eine SF hat
								if(index >= 3 && index <= klein.size()-3){ // falls eine mittlere SF geloescht wird
									neuKlein.addFahrten(klein.getFahrtenVonBis(0, (index)-2)); // Anfang bis einschließlich index - 2 aus klein
									neuKlein.addFahrt(deadruntimes.get(klein.getAtIndex((index)-2).getToStopId() + klein.getAtIndex((index)+2).getFromStopId())); // Leerfahrt
									neuKlein.addFahrten(klein.getFahrtenVonBis((index)+2, klein.size()-1)); // index + 2 bis Ende klein
									if(!localBest.isFeasible(neuKlein)){ 
										break; // break wenn neuKlein nicht feasible
									}
									if(((kleinSize+grossSize)) - (neuKlein.size()+neuGross.size()) != 0){
										System.out.println();
									}
								}
								else if(index == 1){ // falls die erste SF geloescht wird
									neuKlein.addFahrt(deadruntimes.get("00001" + klein.getAtIndex(3).getFromStopId())); // erste Leerfahrt von Depot zum naechsten SF
									neuKlein.addFahrten(klein.getFahrtenVonBis(3, klein.size()-1)); // bis Ende klein
									if(!localBest.isFeasible(neuKlein)){
										break; // break wenn neuKlein nicht feasible
									}
									if(((kleinSize+grossSize)) - (neuKlein.size()+neuGross.size()) != 0){
										System.out.println();
									}
								}
								else if(index == klein.size()-2){ // falls die letzte SF geloescht wird
									neuKlein.addFahrten(klein.getFahrtenVonBis(0, (index)-2)); // Anfang klein bis index - 2
									neuKlein.addFahrt(deadruntimes.get(klein.getAtIndex(klein.size()-4).getToStopId() + "00001")); // Leerfahrt
									if(!localBest.isFeasible(neuKlein)){
										break; // break wenn neuKlein nicht feasible
									}
									if(((kleinSize+grossSize)) - (neuKlein.size()+neuGross.size()) != 0){
										System.out.println();
									}
								}
								else{
									if(((kleinSize+grossSize)) - (neuKlein.size()+neuGross.size()) != 1){
										System.out.println();
									}
									break;
								} 
									
							}
							if(localBest.isFeasible(neuGross)){ // wenn neuGross feasible ist
								for (int j = 0; j < localBest.getUmlaufplan().size(); j++) { // suche gross in Lokalbest
									if(localBest.getUmlaufplan().get(j).getId().equals(gross.getId())){
										localBest.getUmlaufplan().remove(j); // entferne gross aus Lokalbest
										localBest.getUmlaufplan().add(neuGross); // fuege neuGross in Lokalbest hinzu
										
									}
									
								}
								

								gross.getFahrten().clear(); 
								gross.addFahrten(neuGross.getFahrten()); // aktualisiere gross durch neuGross
								gross.setLaden(neuGross.getLaden()); // aktualisiere Ladenliste von gross durch Ladenliste von neuGross
								
								if(klein.size() > 3){ // wenn neuGross feasible ist und klein mehr als eine SF hat
									for (int j = 0; j < localBest.getUmlaufplan().size(); j++) {
										if(localBest.getUmlaufplan().get(j).getId().equals(klein.getId())){ //suche nach klein in Lokalbest
											localBest.getUmlaufplan().remove(j); // entferne klein
											localBest.getUmlaufplan().add(neuKlein); // fuege neuKlein hinzu
											
										}
									}
									if(!localBest.isFeasible(neuKlein)){
										System.out.println("NeuKlein Not Feasible sfUmlegen!");
									}
								}
								else{ // wenn neuGross feasible ist und klein nur eine SF hat
									for (int j = 0; j < localBest.getUmlaufplan().size(); j++) {
										if(localBest.getUmlaufplan().get(j).getId().equals(klein.getId())){
											localBest.getUmlaufplan().remove(j); // entferne klein aus Lokalbest
										}
									}
									

								}
								
								
								if (klein.size() > 3) {
									klein.getFahrten().clear();
									klein.addFahrten(neuKlein.getFahrten());
									klein.setLaden(neuKlein.getLaden());
								}
								
								if(!localBest.isFeasible(neuGross)){
									System.out.println("Neu Gross Not Feasible sfUmlegen!");
								}
								break;
							}
							else{
								break;
							}
						}
					}
				}
					
			
			
			}
			int anzahlSFNach = 0;
			for (int i1 = 0; i1 < localBest.getUmlaufplan().size(); i1++) {
				for (int j = 0; j < localBest.getUmlaufplan().get(i1).size(); j++) {
					if(localBest.getUmlaufplan().get(i1).getFahrten().get(j) instanceof Servicejourney){
						anzahlSFNach++;
					}
				}
			}
			if(anzahlSFNach != 433){
				System.out.println();
			}
			
			localBest.berechneFrequenzen();
			
		}

	}

	/** ZweiOpt: Methode gibt zurück, ob eine Verbesserung zwischen 2 unterschiedlichen Fahrzeugumläufen möglich ist (durch Kantentausch)
	 * 
	 * @param random1: ID des ersten Fahrzeugumlaufs 
	 * @param random2: ID des zweiten Fahrzeugumlaufs 
	 * @return 
	 */
	public ZweiOptVerbesserung zweiOpt(int random1, int random2){ 
		
		ZweiOptVerbesserung result = null;
		
		Fahrzeugumlauf eins = localBest.getUmlaufplan().get(random1); // 
		Fahrzeugumlauf zwei = localBest.getUmlaufplan().get(random2); // 
		
		double currentCostValue = eins.getKostenMitLadestationen() + zwei.getKostenMitLadestationen(); //aktuelle Gesamtkosten von Fahrzeugumlauf eins und zwei
		double initialCostValue = currentCostValue;
		
		Fahrzeugumlauf betterEins = null;
		Fahrzeugumlauf betterZwei = null;
		
		for (int i = -1; i < eins.size()-2; i = i + 2) { // es werden nur Servicefahrten aus dem ersten Umlauf betrachtet, daher i + 2
			if(i == -1){ // falls i = -1 (Depotknoten im ersten Umlauf)
				for (int j = 3; j < zwei.size(); j = j + 2) { //die erste LF von j darf nicht geloescht werden
					if(validEdges.get(zwei.getAtIndex(j-2).getId()+eins.getAtIndex(i+2).getId()) == 1){ 
						//falls zeitlich von (j-2) zu (i+2) möglich ist -> verbinden
						String deadruntimeId = zwei.getAtIndex(j-2).getToStopId() + eins.getAtIndex(i+2).getFromStopId(); 
						Fahrzeugumlauf einsNeu = new Fahrzeugumlauf(eins.getId());
						einsNeu.addFahrten(zwei.getFahrtenVonBis(0, j-2)); // Anfang Zwei bis einschließlich SF j-2
						einsNeu.addFahrt(deadruntimes.get(deadruntimeId)); // Leerfahrt j-2 bis i+2
						einsNeu.addFahrten(eins.getFahrtenVonBis(i+2, eins.size() - 1)); // SF i+2 bis Ende Eins
						if(localBest.isFeasible(einsNeu)){ // falls einsNeu feasible ist
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId()); // initialisere zweiNeu
							deadruntimeId = "00001" + zwei.getAtIndex(j).getFromStopId();
							zweiNeu.addFahrt(deadruntimes.get(deadruntimeId)); // LF von Depot nach SF j
							zweiNeu.addFahrten(zwei.getFahrtenVonBis(j, zwei.size() - 1)); // bis Ende zwei
							if(localBest.isFeasible(zweiNeu)){ // wenn zweiNeu feasible ist
								// neue Umlaeufe speichern, falls besser
								double newCostValue = einsNeu.getKostenMitLadestationen() + zweiNeu.getKostenMitLadestationen(); //neue Kosten durch einsNeu und zweiNeu
								if(newCostValue < currentCostValue){ // wenn gespart wird
									currentCostValue = newCostValue; // aktualisiere currentCost
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
						if(localBest.isFeasible(einsNeu)){
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
							deadruntimeId = "00001" + eins.getAtIndex(i+2).getFromStopId(); // neue Depotkante muss hinzugefuegt werden
							zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
							zweiNeu.addFahrten(eins.getFahrtenVonBis(i+2, eins.size()-1));
							if(localBest.isFeasible(zweiNeu)){
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
							if(localBest.isFeasible(einsNeu)){
								Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(zwei.getId());
								zweiNeu.addFahrten(zwei.getFahrtenVonBis(0, j-2));
								deadruntimeId = zwei.getAtIndex(j-2).getToStopId() + eins.getAtIndex(i+2).getFromStopId();
								zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
								zweiNeu.addFahrten(eins.getFahrtenVonBis(i+2, eins.size()-1));
								if(localBest.isFeasible(zweiNeu)){
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
						if(localBest.isFeasible(einsNeu)){
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(eins.getId());
							deadruntimeId = "00001" + eins.getAtIndex(j).getFromStopId();
							zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
							zweiNeu.addFahrten(eins.getFahrtenVonBis(j, eins.size() - 1));
							if(localBest.isFeasible(zweiNeu)){
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
						if(localBest.isFeasible(einsNeu)){
							Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(eins.getId());
							deadruntimeId = "00001" + zwei.getAtIndex(i+2).getFromStopId(); // neue Depotkante muss hinzugefuegt werden
							zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
							zweiNeu.addFahrten(zwei.getFahrtenVonBis(i+2, zwei.size()-1));
							if(localBest.isFeasible(zweiNeu)){
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
							if(localBest.isFeasible(einsNeu)){
								Fahrzeugumlauf zweiNeu = new Fahrzeugumlauf(eins.getId());
								zweiNeu.addFahrten(eins.getFahrtenVonBis(0, j-2));
								deadruntimeId = eins.getAtIndex(j-2).getToStopId() + zwei.getAtIndex(i+2).getFromStopId();
								zweiNeu.addFahrt(deadruntimes.get(deadruntimeId));
								zweiNeu.addFahrten(zwei.getFahrtenVonBis(i+2, zwei.size()-1));
								if(localBest.isFeasible(zweiNeu)){
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
			if((eins.size()+zwei.size()) - (betterEins.size()+betterZwei.size()) != 0){
				System.out.println();
			}
			savings = initialCostValue - currentCostValue;
			result = new ZweiOptVerbesserung(savings, betterEins, betterZwei, random1, random2);
		}
		
		return result;
	}
}
