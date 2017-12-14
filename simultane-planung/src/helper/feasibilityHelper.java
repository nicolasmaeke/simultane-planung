package helper;

import java.util.HashMap;
import java.util.Vector;

import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Servicejourney;
import model.Stoppoint;

/**
 * 
 * Hilfsklasse beinhaltet Methoden, um zu ueberpruefen, ob die Loesung noch zulaessig ist
 *
 */
public class feasibilityHelper {

	/**
	 * Methode prueft, ob zwei Servicefahrten nacheinander in einem Fahrzeugumlauf
	 * erledigt werden können, ohne dass Verspaetungen entstehen 
	 * @param i
	 * @param j
	 * @param deadruntimes
	 * @return
	 */
	public static long zeitpufferZwischenServicefahrten(String i, String j, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Servicejourney> servicejourneys){
		long result = 0;
		Servicejourney eins = servicejourneys.get(i);
		Servicejourney zwei = servicejourneys.get(j);
		String deadrunId = ""+eins.getToStopId()+zwei.getFromStopId();
		result = (zwei.getSfDepTime().getTime() - eins.getSfArrTime().getTime()) - deadruntimes.get(deadrunId).getRuntime();
		return result;
	}
	
	public static boolean zeitpufferFuerLadezeit(String i, String j, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Servicejourney> servicejourneys, double restkapazitaet){
		double result = 0;
		double ladezeit = (80 - restkapazitaet) / ((7.5 / 60) / 1000);
		Servicejourney eins = servicejourneys.get(i);
		Servicejourney zwei = servicejourneys.get(j);
		String deadrunId = ""+eins.getToStopId()+zwei.getFromStopId();
		result = (zwei.getSfDepTime().getTime() - eins.getSfArrTime().getTime()) - deadruntimes.get(deadrunId).getRuntime() - ladezeit;
		return result >= 0;
	}

	public static boolean isUmlaufFeasible(Fahrzeugumlauf fahrzeugumlauf, HashMap<String, Stoppoint> stoppoints){
		boolean result = true;
		double verbrauch = 0;
		for (int i = 0; i < fahrzeugumlauf.getFahrten().size(); i++) {
			verbrauch = verbrauch + fahrzeugumlauf.getFahrten().get(i).getVerbrauch();
		}
		if(verbrauch > 80){
			result = false;
		}
		return result;
	}
}
