package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Klasse liest Datei mit Rohdaten ein 
 * und erganezt fehlende Wertemit Durchschnittswerten.
 *
 */
public class ProjectReadWrite {
	public File f;
    public BufferedWriter output;
    public PrintWriter writer;
    public int durchschnittsdistancedeadrun;
    public int durchschnittsruntimedeadrun;
    public int totalruntime;
    public int totaldistance;
    public int durchschnittsdistanceservicejourney;
    public int totaldistanceservicejourney;
    
    public Vector<String> listStopPoints = new Vector<String>();
    
    public Vector<String> listServiceJourney = new Vector<String>();
    public Vector<String> listFromToServiceJourney = new Vector<String>();
    public Vector<Integer> listDistanceServiceJourney = new Vector<Integer>();
    
    
    public Vector<String> listDeadRun = new Vector<String>();
    public Vector<String> listFromToDeadRun = new Vector<String>();
    public Vector<Integer> listDistanceDeadRun = new Vector<Integer>();
    public Vector<Integer> listRunTimeDeadRun = new Vector<Integer>();


    // factorial function
    
    int factorial(int n) {
        if (n == 1)
              return 1;
        else
              return n * factorial(n - 1);
        }
    
    // Methode liest die Daten-Datei zeilenweise aus und fügt Zeilen hinzu, wo Daten fehlen

	public ProjectReadWrite(String path) {
		
		this.f = new File(path);
		
		BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (IOException e) {
            System.out.println(e);
        }
        

		try {
			writer = new PrintWriter(new FileOutputStream("/Users/XuanSon/Desktop/Uni/Master/WS1718/Java/project/simultan/ReadAndWrite/data/newFile.txt", false));
		} catch (FileNotFoundException e1) {
			System.out.println(e1);
		}

        
        // Zeilen auslesen

        try {
            String temp = reader.readLine();
            String BlockBegin;
            String ersteszeichen;

            while (temp != null) // lese Datei zeilenweise aus
            {
                writer.println(temp); // schreibe neue Datei zeilenweise 
                writer.flush();
                temp = reader.readLine(); // nächste Zeile
                
                BlockBegin = temp.split(":")[0]; // erster Teil der Zeile bis zum ":"

                if (BlockBegin.equals("$STOPPOINT")) // 1. Relation: Stoppoint 
                {
                    writer.println(temp); 
                    writer.flush();
                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen                 
                    
                    while (temp != null && !ersteszeichen.equals("*")) { // solange Ziffern von ID < 5, füge 0 vorne hinzu
                    	
                        String spId = (temp.split(";")[0]) ;
                    	
                    	do {
                        	spId = "0" + spId;
                        	
                        } while (spId.length() < 5);
          
                    	listStopPoints.add(spId);
                    	
                    	temp = temp.replace(temp.split(";")[0], spId); // ersetze neue ID mit 5-Ziffern
                        
                        writer.println(temp);
                        writer.flush();
                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                        
                    } // end while
                    continue;
                } // end if


                if (BlockBegin.equals("$SERVICEJOURNEY")) // 2. Relation: Servicefahrten
                {
                    writer.println(temp);
                    writer.flush();
                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen                   

                    while (temp != null && !ersteszeichen.equals("*")) {

                        // String sfId = (temp.split(";")[0]); // ID
                        String sfFromStopID = (temp.split(";")[2]); // Starthaltestelle
                        String sfToStopID = (temp.split(";")[3]); // Endhaltestelle
                        // String sfDepTime = temp.split(";")[4]; // Abfahrtszeit
                        // String sfArrTime = temp.split(";")[5]; // Ankunftszeit
                        int sfDistance = Integer.parseInt(temp.split(";")[11]); // Distanz wird in Kilometer umgerechnet
                        
                        do {
                        	sfFromStopID = "0" + sfFromStopID;
                        	
                        } while (sfFromStopID.length() < 5); // solange Ziffern von FromStopID < 5, füge 0 vorne hinzu
                        
                        do {
                        	sfToStopID = "0" + sfToStopID;
                        	
                        } while (sfToStopID.length() < 5);  // solange Ziffern von ToStopID < 5, füge 0 vorne hinzu
                        
                        listFromToServiceJourney.add(sfFromStopID + sfToStopID);
                        listDistanceServiceJourney.add(sfDistance);
                        temp = temp.replace(temp.split(";")[2], sfFromStopID); // aktuellisiere neue FromStopID
                        temp = temp.replace(temp.split(";")[3], sfToStopID); // aktuallisiere neue TopStopID
                        
                        writer.println(temp); // schreie Zeile mit neuen IDs
                        writer.flush();
                        temp = reader.readLine(); // lese nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen  
   
                    } // end while
                    
                    for(int i = 0; i < listDistanceServiceJourney.size(); i++) {
                    	totaldistanceservicejourney += listDistanceServiceJourney.get(i);
                    } 
                    durchschnittsdistanceservicejourney = totaldistanceservicejourney / listDistanceServiceJourney.size();
                    
                    double y = factorial(listStopPoints.size()-2);
                    double x = factorial(listStopPoints.size());
                    double max = (x /y);
                    
                    do {
 
                    for (String i : listStopPoints){
                		for (String j : listStopPoints){
                			if (!listFromToServiceJourney.contains(i+j) && (!i.equals(j))) {
                				String fromStopID = i;
                				String toStopID = j;
                            
                				listFromToServiceJourney.add(fromStopID+toStopID);	
                				
                                writer.println("0;" + "0;" + fromStopID + ";" + toStopID + ";0;0;0;0;0;0;0;" + durchschnittsdistanceservicejourney);
                                writer.flush();
                			} 
                		}
                		
                	}  
                  } 
                    while (listFromToServiceJourney.size() < max);
                    
                    continue;
                } // end if

                if (BlockBegin.equals("$DEADRUNTIME")) // 3. Relation: Servicefahrten
                {
                	writer.println(temp);
                    writer.flush();
                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
           

                    while (temp != null && !ersteszeichen.equals("*")) {

                        String fromStopID = (temp.split(";")[0]); // ID
                        String toStopID = (temp.split(";")[1]); // ID
                        String distance = (temp.split(";")[4]);
                        int runtime = Integer.parseInt(temp.split(";")[5]);
                        
                        do {
                        	fromStopID = "0" + fromStopID;
                        	
                        } while (fromStopID.length() < 5);
                        
                        
                        do {
                        	toStopID = "0" + toStopID;

                        } while (toStopID.length() < 5);
                        
                        temp = temp.replace(temp.split(";")[0], fromStopID);
                        temp = temp.replace(temp.split(";")[1], toStopID);
                        
                        listDeadRun.add(fromStopID);
                        listRunTimeDeadRun.add(runtime);
                        listDistanceDeadRun.add(Integer.valueOf(distance));
                        listFromToDeadRun.add(fromStopID+toStopID);
                        
                        writer.println(temp);
                        writer.flush();
                        temp = reader.readLine();

                    } // end while
                    
                    for(int i = 0; i < listRunTimeDeadRun.size(); i++) {
                    	totalruntime += listRunTimeDeadRun.get(i); 
                    }
                    durchschnittsruntimedeadrun = totalruntime / listRunTimeDeadRun.size();

                    
                    for(int i = 0; i < listDistanceDeadRun.size(); i++) {
                    	totaldistance += listDistanceDeadRun.get(i);
                    } 
                    durchschnittsdistancedeadrun = totaldistance / listDistanceDeadRun.size();                     
                    
                    double y = factorial(listStopPoints.size()-2);
                    double x = factorial(listStopPoints.size());
                    double max = (x /y);
                    
                    do {
                    for (String i : listStopPoints){
                    		for (String j : listStopPoints){
                    			if (!listFromToDeadRun.contains(i+j) && (!i.equals(j))) {
                    				String fromStopID = i;
                    				String toStopID = j;
                    				int distance = durchschnittsdistancedeadrun;
                    				int runtime = durchschnittsruntimedeadrun;                   				

                    				listFromToDeadRun.add(fromStopID+toStopID);	
                    				listDistanceDeadRun.add(distance);
                    				listRunTimeDeadRun.add(runtime);
                    				
                    				writer.println(fromStopID + ";" + toStopID + ";000:00:00:00;001:12:00:00;" + durchschnittsdistancedeadrun + ";" + durchschnittsruntimedeadrun + ";;;;;");
                                    writer.flush();
                    			} 
                    		}
                    	  	
                    	}  
                    }
                    while (listFromToDeadRun.size() < max); 
            
                    continue;
                 // end if
                	
               }} // end outer while
        } catch (IOException e) {
            System.out.println(e);
        }

	}
	
}
