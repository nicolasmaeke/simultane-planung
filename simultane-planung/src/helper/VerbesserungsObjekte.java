package helper;

import model.Fahrzeugumlauf;

public interface VerbesserungsObjekte {
	
	static double savings = 0;
	Fahrzeugumlauf eins = null;
	Fahrzeugumlauf zwei = null;
	int indexAltEins = 0;
	int indexAltZwei = 0;

	public double getSavings();

	public void setSavings(double savings);

	public Fahrzeugumlauf getEins();

	public void setEins(Fahrzeugumlauf eins);

	public Fahrzeugumlauf getZwei();

	public void setZwei(Fahrzeugumlauf zwei);

	public int getIndexAltEins();

	public void setIndexAltEins(int indexAltEins);

	public int getIndexAltZwei();

	public void setIndexAltZwei(int indexAltZwei);

}
