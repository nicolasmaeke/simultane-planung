package parser;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import output.Schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author nicolasmaeke
 * Parser Klassen, die nur die Instanzen der Daten einliest,
 * die fuer die Problemstellung unseres Projektes notwendig sind.
 *
 */
public class ProjectReadIn {

    public File f;
    public String output;
    public String input;
    Date start;
    Date ende;
    public double durchschnittsdistance;
    public double durchschnittsruntime;
    public double maxloading;

    // 1.1 Variablen fuer Streckennetz und Ladeinfrastruktur erstellen
    
    // 1.1.1 Stoppoints
    public Vector<Integer> StopPointID; // ID, CODE und NAME sind identisch
    public Vector<String> StopPointCode;
    public Vector<String> StopPointName;
    
    // 1.1.2 Linien
    public Vector<Integer> LineID; // ID, CODE und NAME sind identisch
    public Vector<String> LineCODE;
    public Vector<String> LineNAME;
    
    // 1.1.3 Servicefahrten
    public Vector<Integer> SFID;
    public Vector<Integer> SFLineID;
    public Vector<Integer> SFFromStopID;
    public Vector<Integer> SFToStopID;
    public Vector<Date> SFDepTime;
    public Vector<Date> SFArrTime;
    public Vector<Double> SFDistance;
    
    // 1.1.4 Verbindungen (Deadruntime)
    public Vector<Integer> FromStopID;
    public Vector<Integer> ToStopID;
    public Vector<Double> Distance;
    public Vector<Integer> Runtime;
    
    // Zeitformat
    public DateFormat zformat;

    // Ladefunktion
    public String funktionstyp;

    // Lösung
    public Schedule s;
    public int depotnummer;
    public double factor_oben;
    public double factor_unten;
    public Vector<Integer> Ladestation;

    // Ladestationen
    public double LSpro = 0.1;
    public Map<Integer, Integer> nearestchargingstation;
    public Table<Integer, Integer, Double> distance;
    public Table<Integer, Integer, Double> sfdistance;
    
    /**
     * Wenn die Methode aufgerufen wird, instanziiert sie zunaechst die Variablen
     * und ruft zum Schluss die Methode ProjectReadIn auf
     */
    public void Problem() {
    	
    	// 1.1.1 Stoppoints
        StopPointID = new Vector<Integer>(0);
        StopPointCode = new Vector<String>(0);
        StopPointName = new Vector<String>(0);

        // 1.1.2 Linien
        LineID = new Vector<Integer>(0);
        LineCODE = new Vector<String>(0);
        LineNAME = new Vector<String>(0);

        // 1.1.3 Servicefahrten
        SFID = new Vector<Integer>(0);
        SFLineID = new Vector<Integer>(0);
        SFFromStopID = new Vector<Integer>(0);
        SFToStopID = new Vector<Integer>(0);
        SFDepTime = new Vector<Date>(0);
        SFArrTime = new Vector<Date>(0);
        SFDistance = new Vector<Double>(0);

        // 1.1.4 Verbindungen
        FromStopID = new Vector<Integer>(0);
        ToStopID = new Vector<Integer>(0);
        Distance = new Vector<Double>(0);
        Runtime = new Vector<Integer>(0);

        nearestchargingstation = new HashMap<Integer, Integer>();
        distance = HashBasedTable.create();
        sfdistance = HashBasedTable.create();

        // Zeitformat
        zformat = new SimpleDateFormat("HH:mm:ss");

        // Lösung
        s = new Schedule();

        this.ProjectReadIn(); // Datei auslesen
    }

    /**
     * Methode liest die Daten-Datei zeilenweise aus 
     * und speichert die Instanzen in den zuvor erstellten und instanziierten Variablen
     */
	private void ProjectReadIn() {
		
		BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (IOException e) {
            System.out.println(e);
        }

        // 1.2 Zeilen auslesen und in Variablen schreiben

        try {
            String temp = "";
            String BlockBegin;
            String ersteszeichen;
            String help;

            while (temp != null) // lese Datei zeilenweise aus
            {
                temp = reader.readLine(); // nächste Zeile
                BlockBegin = temp.split(":")[0]; // erster Teil der Zeile bis zum ":"

                if (BlockBegin.equals("$STOPPOINT")) // 1. Relation: Stoppoint
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {
                        StopPointID.add(Integer.parseInt(temp.split(";")[0])); // ID
                        StopPointCode.add(temp.split(";")[1]); // Code
                        StopPointName.add(temp.split(";")[2]); // Name

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    } // end while
                    continue;
                } // end if


                if (BlockBegin.equals("$LINE")) // 2. Relation: Lines
                {
                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen


                    while (temp != null && !ersteszeichen.equals("*")) {

                        LineID.add(Integer.parseInt(temp.split(";")[0]));// ID
                        LineCODE.add(temp.split(";")[1]); // Code
                        LineNAME.add(temp.split(";")[2]);// Name

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    } // end while
                    continue;
                } // end if


                if (BlockBegin.equals("$SERVICEJOURNEY")) // 3. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {


                        SFID.add(Integer.parseInt(temp.split(";")[0])); // ID
                        SFLineID.add(Integer.parseInt(temp.split(";")[1])); // LineID
                        SFFromStopID.add(Integer.parseInt(temp.split(";")[2])); // Starthaltestelle
                        SFToStopID.add(Integer.parseInt(temp.split(";")[3])); // Endhaltestelle

                        help = temp.split(";")[4];
                        Date zeit = null;

                        try {
                            zeit = zformat.parse(help.split(":")[1] + ":" + help.split(":")[2] + ":" + help.split(":")[3]);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        SFDepTime.add(zeit);

                        help = temp.split(";")[5];
                        zeit = null;

                        try {
                            zeit = zformat.parse(help.split(":")[1] + ":" + help.split(":")[2] + ":" + help.split(":")[3]);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        SFArrTime.add(zeit);

                        SFDistance.add(Double.parseDouble(temp.split(";")[11]) / 1000);

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    } // end while
                    continue;
                } // end if

                if (BlockBegin.equals("$DEADRUNTIME")) // 4. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {

                        FromStopID.add(Integer.parseInt(temp.split(";")[0])); // ID
                        ToStopID.add(Integer.parseInt(temp.split(";")[1])); // ID
                        Distance.add(Double.parseDouble(temp.split(";")[4]) / 1000);
                        Runtime.add(Integer.parseInt(temp.split(";")[5]) / 60);

                        temp = reader.readLine();

                    } // end while
                    continue;
                } // end if


            } // end outer while

        } catch (IOException e) {
            System.out.println(e);
        }
	}
}

