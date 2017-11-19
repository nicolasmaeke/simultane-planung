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
	public static long zeitpuffer(Servicejourney i, Servicejourney j, HashMap<String, Deadruntime> deadruntimes){
		long result = 0;
		String deadrunId = ""+i.getSfToStopId()+j.getSfFromStopId();
		//String deadrunId = "0889008880";
		//result = (j.getSfDepTime().getTime() - i.getSfArrTime().getTime()) - deadruntimes.get(deadrunId).getRuntime();
		return result;
	}

}
