package helper;

import model.Fahrzeugumlauf;

public class ZweiOptVerbesserung {
	
	double costs = 0;
	Fahrzeugumlauf eins = null;
	Fahrzeugumlauf zwei = null;
	int indexAltEins = 0;
	int indexAltZwei = 0;
	
	public ZweiOptVerbesserung(double savings, Fahrzeugumlauf eins, Fahrzeugumlauf zwei, int indexAltEins, int indexAltZwei){
		this.eins = eins;
		this.zwei = zwei;
		this.costs = savings;
	}

	public double getCosts() {
		return costs;
	}

	public void setCosts(double savings) {
		this.costs = savings;
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
