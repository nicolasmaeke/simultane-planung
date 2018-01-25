package helper;

import model.Fahrzeugumlauf;

public class SFumlegen {

	private Fahrzeugumlauf eins;
	private int indexAltZwei;
	private int indexAltEins;
	private double costs;
	private Fahrzeugumlauf zwei;

	public SFumlegen(double savings, Fahrzeugumlauf eins, Fahrzeugumlauf zwei, int indexAltEins, int indexAltZwei){
		this.setEins(eins);
		this.setZwei(zwei);
		this.setCosts(savings);
		this.setIndexAltEins(indexAltEins);
		this.setIndexAltZwei(indexAltZwei);
	}

	public int getIndexAltZwei() {
		return indexAltZwei;
	}

	public void setIndexAltZwei(int indexAltZwei) {
		this.indexAltZwei = indexAltZwei;
	}

	public Fahrzeugumlauf getEins() {
		return eins;
	}

	public void setEins(Fahrzeugumlauf eins) {
		this.eins = eins;
	}

	public int getIndexAltEins() {
		return indexAltEins;
	}

	public void setIndexAltEins(int indexAltEins) {
		this.indexAltEins = indexAltEins;
	}

	public double getCosts() {
		return costs;
	}

	public void setCosts(double costs) {
		this.costs = costs;
	}

	public Fahrzeugumlauf getZwei() {
		return zwei;
	}

	public void setZwei(Fahrzeugumlauf zwei) {
		this.zwei = zwei;
	}
}
