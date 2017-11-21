package helper;

import java.util.HashMap;

import model.Deadruntime;
import model.Servicejourney;

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
	public static long zeitpuffer(String i, String j, HashMap<String, Deadruntime> deadruntimes, HashMap<String, Servicejourney> servicejourneys){
		long result = 0;
		Servicejourney eins = servicejourneys.get(i);
		Servicejourney zwei = servicejourneys.get(j);
		String deadrunId = ""+eins.getToStopId()+zwei.getFromStopId();
		result = (zwei.getSfDepTime().getTime() - eins.getSfArrTime().getTime()) - deadruntimes.get(deadrunId).getRuntime();
		return result;
	}

}
