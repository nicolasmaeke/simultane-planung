package parser;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import helper.feasibilityHelper;
import model.Deadruntime;
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
    
    // 1.1.2 Servicefahrten
    public Vector<Servicejourney> servicejourneys;
    
    // 1.1.3 Verbindungen (Deadruntime)
    public HashMap<String, Deadruntime> deadruntimes;
    
    // Zeitformat
    public DateFormat zformat;

    // Ladefunktion
    public String funktionstyp;

    // Lösung
    public Schedule s;
    public double factor_oben;
    public double factor_unten;
    

    // Ladestationen
    public double LSpro = 0.1;
    public Map<Integer, Integer> nearestchargingstation;
    public Table<Integer, Integer, Double> distance;
    public Table<Integer, Integer, Double> sfdistance;
    
    // sind Verbindungen zwischen zwei Servicefahrten moeglich
    public HashMap<String, Boolean> validEdges;
    
    /**
     * 
     */
    public void Problem() {

        nearestchargingstation = new HashMap<Integer, Integer>();
        distance = HashBasedTable.create();
        sfdistance = HashBasedTable.create();

        // Zeitformat
        zformat = new SimpleDateFormat("HH:mm:ss");

        // Lösung
        s = new Schedule();
    }

    /**
     * Konstruktor: liest die Daten-Datei zeilenweise aus 
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
                    
                    	stoppoints.add(new Stoppoint(temp.split(";")[0]));
                    
                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    } // end while
                    continue;
                } // end if


                if (BlockBegin.equals("$SERVICEJOURNEY")) // 2. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    servicejourneys = new Vector<Servicejourney>();

                    while (temp != null && !ersteszeichen.equals("*")) {

                        String sfId= (temp.split(";")[0]); // ID
                        String sfFromStopID = (temp.split(";")[2]); // Starthaltestelle
                        String sfToStopID = (temp.split(";")[3]); // Endhaltestelle
                        String sfDepTime = temp.split(";")[4]; // Abfahrtszeit
                        String sfArrTime = temp.split(";")[5]; // Ankunftszeit
                        double sfDistance = Double.parseDouble(temp.split(";")[11]) / 1000; // Distanz wird in Kilometer umgerechnet
                        
                        servicejourneys.add(new Servicejourney(sfId, sfFromStopID, sfToStopID, sfDepTime, sfArrTime, sfDistance));

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    } // end while
                    continue;
                } // end if

                if (BlockBegin.equals("$DEADRUNTIME")) // 3. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    deadruntimes = new HashMap<String, Deadruntime>();

                    while (temp != null && !ersteszeichen.equals("*")) {

                        String fromStopID = (temp.split(";")[0]); // ID
                        String toStopID =(temp.split(";")[1]); // ID
                        double distance = Double.parseDouble(temp.split(";")[4]) / 1000; // Distanz wird in Kilometer umgerechnet
                        int runtime = Integer.parseInt(temp.split(";")[5]) / 60; // Runtime wird in Minuten umgerechnet

                        Deadruntime neu = new Deadruntime(fromStopID, toStopID, distance, runtime);
                        deadruntimes.put(neu.getId(), neu);
                        
                        temp = reader.readLine();

                    } // end while
                    continue;
                } // end if
            } // end outer while
        } catch (IOException e) {
            System.out.println(e);
        }
        
        /**
         * es wird eine Matrix mit moeglichen Verbindungen zwischen zwei Servicefahrten erstellt
         */
        validEdges = new HashMap<String, Boolean>();
        for (int i = 0; i < servicejourneys.size(); i++) {
			for (int j = 0; j < servicejourneys.size(); j++) {
				if(i==j){
					validEdges.put(servicejourneys.get(i).getId() + servicejourneys.get(j).getId(), false);
				}
				else{
					if(feasibilityHelper.zeitpuffer(servicejourneys.get(i), servicejourneys.get(j), deadruntimes) >= 0){
						validEdges.put(servicejourneys.get(i).getId() + servicejourneys.get(j).getId(), true);
					}
					else{
						validEdges.put(servicejourneys.get(i).getId() + servicejourneys.get(j).getId(), false);
					}
				}
			}
		}   
	}
}

