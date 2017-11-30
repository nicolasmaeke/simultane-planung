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
import java.util.Map.Entry;
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
    public HashMap<String, Stoppoint> stoppoints;
    
    // 1.1.2 Servicefahrten
    public HashMap<String, Servicejourney> servicejourneys;
   
    // 1.1.3 Verbindungen (Deadruntime)
    public HashMap<String, Deadruntime> deadruntimes;
    
    // Zeitformat
    public DateFormat zformat;

    
    // sind Verbindungen zwischen zwei Servicefahrten moeglich
    public HashMap<String, Integer> validEdges;
 

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
                    
                    stoppoints = new HashMap<String, Stoppoint>();
                    
                    while (temp != null && !ersteszeichen.equals("*")) {
                    
                    	Stoppoint neu = new Stoppoint(temp.split(";")[0]);
                    	stoppoints.put(neu.getId(), neu);
                    
                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    } // end while
                    continue;
                } // end if


                if (BlockBegin.equals("$SERVICEJOURNEY")) // 2. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    servicejourneys = new HashMap<String, Servicejourney>();

                    while (temp != null && !ersteszeichen.equals("*")) {

                        String sfId= (temp.split(";")[0]); // ID
                        String sfFromStopID = (temp.split(";")[2]); // Starthaltestelle
                        String sfToStopID = (temp.split(";")[3]); // Endhaltestelle
                        String sfDepTime = temp.split(";")[4]; // Abfahrtszeit
                        String sfArrTime = temp.split(";")[5]; // Ankunftszeit
                        int sfDistance = Integer.parseInt(temp.split(";")[11]); 
                        
                        Servicejourney neu = new Servicejourney(sfId, sfFromStopID, sfToStopID, sfDepTime, sfArrTime, sfDistance);
                        servicejourneys.put(neu.getId(), neu);

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
                        String toStopID = (temp.split(";")[1]); // ID
                        int distance = Integer.parseInt(temp.split(";")[4]); 
                        int runtime = Integer.parseInt(temp.split(";")[5]); 

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
        validEdges = new HashMap<String, Integer>();
        
        for (Entry i: servicejourneys.entrySet()){
			for (Entry j: servicejourneys.entrySet()) {
				if(i==j){
					validEdges.put(""+ i.getKey() + j.getKey(), 0);
				}
				else{
					if(feasibilityHelper.zeitpufferZwischenServicefahrten(""+i.getKey(), ""+j.getKey(), deadruntimes, servicejourneys) >= 0){
						validEdges.put(""+ i.getKey() + j.getKey(), 1);
					}
					else{
						validEdges.put(""+ i.getKey() + j.getKey(), 0);
					}
				}
			}
		}   
	}
}

