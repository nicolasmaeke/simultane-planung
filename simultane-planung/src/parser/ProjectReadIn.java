package parser;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import model.Deadruntime;
import model.Line;
import model.Servicejourney;
import model.Stoppoint;
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
    public Vector<Stoppoint> stoppoints;
    
    // 1.1.2 Linien
    public Vector<Line> lines;

    
    // 1.1.3 Servicefahrten
    public Vector<Servicejourney> servicejeourneys;
    
    
    // 1.1.4 Verbindungen (Deadruntime)
    public Vector<Deadruntime> deadruntimes;
    
    
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

        nearestchargingstation = new HashMap<Integer, Integer>();
        distance = HashBasedTable.create();
        sfdistance = HashBasedTable.create();

        // Zeitformat
        zformat = new SimpleDateFormat("HH:mm:ss");

        // Lösung
        s = new Schedule();

        //this.ProjectReadIn(); // Datei auslesen
    }

    /**
     * Methode liest die Daten-Datei zeilenweise aus 
     * und speichert die Instanzen in den zuvor erstellten und instanziierten Variablen
     */
	public ProjectReadIn(String path) {
		
		this.f = new File(path);
		
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

            while (temp != null) // lese Datei zeilenweise aus
            {
                temp = reader.readLine(); // nächste Zeile
                BlockBegin = temp.split(":")[0]; // erster Teil der Zeile bis zum ":"

                if (BlockBegin.equals("$STOPPOINT")) // 1. Relation: Stoppoint
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    stoppoints = new Vector<Stoppoint>();
                    
                    while (temp != null && !ersteszeichen.equals("*")) {
                    
                    	stoppoints.add(new Stoppoint(Integer.parseInt(temp.split(";")[0])));
                    
                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    } // end while
                    continue;
                } // end if


                if (BlockBegin.equals("$LINE")) // 2. Relation: Lines
                {
                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    lines = new Vector<Line>();
                    
                    while (temp != null && !ersteszeichen.equals("*")) {
                    	
                    	lines.add(new Line(Integer.parseInt(temp.split(";")[0])));

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    } // end while
                    continue;
                } // end if


                if (BlockBegin.equals("$SERVICEJOURNEY")) // 3. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    servicejeourneys = new Vector<Servicejourney>();

                    while (temp != null && !ersteszeichen.equals("*")) {

                        int sfId= Integer.parseInt(temp.split(";")[0]); // ID
                        int sfLineID = Integer.parseInt(temp.split(";")[1]); // LineID
                        int sfFromStopID = Integer.parseInt(temp.split(";")[2]); // Starthaltestelle
                        int sfToStopID = Integer.parseInt(temp.split(";")[3]); // Endhaltestelle
                        String sfDepTime = temp.split(";")[4]; // Abfahrtszeit
                        String sfArrTime = temp.split(";")[5]; // Ankunftszeit
                        double sfDistance = Double.parseDouble(temp.split(";")[11]) / 1000; // Distanz wird in Kilometer umgerechnet
                        
                        servicejeourneys.add(new Servicejourney(sfId, sfLineID, sfFromStopID, sfToStopID, sfDepTime, sfArrTime, sfDistance));

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    } // end while
                    continue;
                } // end if

                if (BlockBegin.equals("$DEADRUNTIME")) // 4. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    deadruntimes = new Vector<Deadruntime>();

                    while (temp != null && !ersteszeichen.equals("*")) {

                        int fromStopID = Integer.parseInt(temp.split(";")[0]); // ID
                        int toStopID = Integer.parseInt(temp.split(";")[1]); // ID
                        double distance = Double.parseDouble(temp.split(";")[4]) / 1000;
                        int runtime = Integer.parseInt(temp.split(";")[5]) / 60;

                        deadruntimes.add(new Deadruntime(fromStopID, toStopID, distance, runtime));
                        
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

