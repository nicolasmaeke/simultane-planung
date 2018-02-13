package helper;

import model.Fahrzeugumlauf;

public class ZweiOptVerbesserung implements VerbesserungsObjekte {
	
	double savings = 0;
	Fahrzeugumlauf eins = null;
	Fahrzeugumlauf zwei = null;
	int indexAltEins = 0;
	int indexAltZwei = 0;
	
	/**
	 * Konstruktor 
	 */
	public ZweiOptVerbesserung(double savings, Fahrzeugumlauf eins, Fahrzeugumlauf zwei, int indexAltEins, int indexAltZwei){
		this.eins = eins;
		this.zwei = zwei;
		this.savings = savings;
		this.indexAltEins = indexAltEins;
		this.indexAltZwei = indexAltZwei;
	}

	public double getSavings() {
		return savings;
	}

	public void setSavings(double savings) {
		this.savings = savings;
	}

	public Fahrzeugumlauf getEins() {
		return eins;
	}

	public void setEins(Fahrzeugumlauf eins) {
		this.eins = eins;
	}

	public Fahrzeugumlauf getZwei() {
		return zwei;
	}

	public void setZwei(Fahrzeugumlauf zwei) {
		this.zwei = zwei;
	}

	public int getIndexAltEins() {
		return indexAltEins;
	}

	public void setIndexAltEins(int indexAltEins) {
		this.indexAltEins = indexAltEins;
	}

	public int getIndexAltZwei() {
		return indexAltZwei;
	}

	public void setIndexAltZwei(int indexAltZwei) {
		this.indexAltZwei = indexAltZwei;
	}

}
