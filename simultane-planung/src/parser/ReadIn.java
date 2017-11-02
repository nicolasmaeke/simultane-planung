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
 * Created by nilsolsen on 09.03.17.
 */
public class ReadIn {

    public File f;
    public String output;
    public String input;
    Date start;
    Date ende;
    public double durchschnittsdistance;
    public double durchschnittsruntime;
    public double maxloading;

    // 1.1 Streckennetz und Ladeinfrastruktur

    // 1.1.1 Ladesysteme
    public Vector<Integer> ChargingID;
    public Vector<String> ChargingBezeichnung;

    // 1.1.2 Stoppoints
    public Vector<Integer> StopPointID;
    public Vector<String> StopPointCode;
    public Vector<String> StopPointName;
    public Vector<Double> StopPointVehCapForCharging;
    public Vector<Integer> StopPointChargingSystem; // Ladesysteme j von Stoppoint i: (i-1)*4 + j
    public Vector<Integer> StopPointFastChargingCapability;

    // 1.1.3 Linien
    public Vector<Integer> LineID;
    public Vector<String> LineCODE;
    public Vector<String> LineNAME;

    // 1.1.4 Fahrzeugtypen
    public Vector<Integer> VehTypeID;
    public Vector<String> VehTypeCode;
    public Vector<String> VehTypeName;
    public Vector<Integer> VehTypeVehCharakteristic;
    public Vector<String> VehTypeVehClass;
    public Vector<Double> VehTypeCurbWeightKg;
    public Vector<Double> VehTypeVehCost;
    public Vector<Double> VehTypeKmCost;
    public Vector<Double> VehTypeHourCost;
    public Vector<Double> VehTypeCapacity;
    public Vector<Double> VehTypeBatteryCapacity;
    public Vector<Double> VehTypeConServiceKm;
    public Vector<Double> VehTypeConDeadKm;
    public Vector<Double> VehTypeRechargingCost;
    public Vector<Double> VehTypeSlowRechargingTime;
    public Vector<Double> VehTypeFastRechargingTime;
    public Vector<Integer> VehTypeChargingSystem; // new Vector<, welches die Ladesysteme enthält, mit denen ein Fahrzeugtyp geladen werden kann

    // 1.1.5 Fahrzeugtypgruppen
    public Vector<Integer> VehTypeGroupID;
    public Vector<String> VehTypeGroupCODE;
    public Vector<String> VehTypeGroupNAME;

    // 1.1.6 Fahrzeugtyp zu Fahrzeugtypgruppe
    public Vector<Integer> VehTypToVehTypeGroup; // A(i) = Fahrzeugtypgruppe von Fahrzeugtyp i

    // 1.1.7 Fahrzeugkapazität (für Laden) zu Haltestelle
    public Vector<Integer> CapVehTypeID; // (i,j): Fahrzeugtyp i kann an Haltestelle j geladen werden
    public Vector<Integer> CapStoppointID; // (i,j): Fahrzeugtyp i kann an Haltestelle j geladen werden
    public Vector<Double> CapIDMin; // 0
    public Vector<Double> CapIDMax; // 1000 (unbegrenzt)

    // 1.1.8 Servicefahrten
    public Vector<Integer> SFID;
    public Vector<Integer> SFLineID;
    public Vector<Integer> SFFromStopID;
    public Vector<Integer> SFToStopID;
    public Vector<Date> SFDepTime;
    public Vector<Date> SFArrTime;
    public Vector<Integer> SFVehTypeGroupID;
    public Vector<Double> SFDistance;

    // 1.1.9 Verbindungen
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


    public void Problem() {

        // depotnummer = dn;
        ChargingID = new Vector<Integer>(0);
        ChargingBezeichnung = new Vector<String>(0);

        // 1.1.2 Stoppoints
        StopPointID = new Vector<Integer>(0);
        StopPointCode = new Vector<String>(0);
        StopPointName = new Vector<String>(0);
        StopPointVehCapForCharging = new Vector<Double>(0);
        StopPointChargingSystem = new Vector<Integer>(0); // Ladesysteme j von Stoppoint i: i*4 + j
        StopPointFastChargingCapability = new Vector<Integer>(0);

        // 1.1.3 Linien
        LineID = new Vector<Integer>(0);
        LineCODE = new Vector<String>(0);
        LineNAME = new Vector<String>(0);

        // 1.1.4 Fahrzeugtypen
        VehTypeID = new Vector<Integer>(0);
        VehTypeCode = new Vector<String>(0);
        VehTypeName = new Vector<String>(0);
        VehTypeVehCharakteristic = new Vector<Integer>(0);
        VehTypeVehClass = new Vector<String>(0);
        VehTypeCurbWeightKg = new Vector<Double>(0);
        VehTypeVehCost = new Vector<Double>(0);
        VehTypeKmCost = new Vector<Double>(0);
        VehTypeHourCost = new Vector<Double>(0);
        VehTypeCapacity = new Vector<Double>(0);
        VehTypeBatteryCapacity = new Vector<Double>(0);
        VehTypeConServiceKm = new Vector<Double>(0);
        VehTypeConDeadKm = new Vector<Double>(0);
        VehTypeRechargingCost = new Vector<Double>(0);
        VehTypeSlowRechargingTime = new Vector<Double>(0);
        VehTypeFastRechargingTime = new Vector<Double>(0);
        VehTypeChargingSystem = new Vector<Integer>(0); // new Vector<, welches die Ladesysteme enthält, mit denen ein Fahrzeugtyp geladen werden kann

        // 1.1.5 Fahrzeugtypgruppen
        VehTypeGroupID = new Vector<Integer>(0);
        VehTypeGroupCODE = new Vector<String>(0);
        VehTypeGroupNAME = new Vector<String>(0);

        // 1.1.6 Fahrzeugtyp zu Fahrzeugtypgruppe
        VehTypToVehTypeGroup = new Vector<Integer>(0); // A(i) = Fahrzeugtypgruppe von Fahrzeugtyp i

        // 1.1.7 Fahrzeugkapazität (für Laden) zu Haltestelle
        CapVehTypeID = new Vector<Integer>(0); // (i,j): Fahrzeugtyp i kann an Haltestelle j geladen werden
        CapStoppointID = new Vector<Integer>(0); // (i,j): Fahrzeugtyp i kann an Haltestelle j geladen werden
        CapIDMin = new Vector<Double>(0); // 0
        CapIDMax = new Vector<Double>(0); // 1000 (unbegrenzt)

        // 1.1.8 Servicefahrten
        SFID = new Vector<Integer>(0);
        SFLineID = new Vector<Integer>(0);
        SFFromStopID = new Vector<Integer>(0);
        SFToStopID = new Vector<Integer>(0);
        SFDepTime = new Vector<Date>(0);
        SFArrTime = new Vector<Date>(0);
        SFVehTypeGroupID = new Vector<Integer>(0);
        SFDistance = new Vector<Double>(0);

        // 1.1.9 Verbindungen
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

        this.ReadIn(); // Datei auslesen
    }

    public void ReadIn() {

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

                if (BlockBegin.equals("$CHARGINGSYSTEM")) // 1. Relation: Ladesysteme
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {

                        ChargingID.add(Integer.parseInt(temp.split(";")[0]));
                        ChargingBezeichnung.add(temp.split(";")[1]);

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    } // end while

                    continue;
                } // end if

                if (BlockBegin.equals("$STOPPOINT")) // 2. Relation: Stoppoint
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {
                        StopPointID.add(Integer.parseInt(temp.split(";")[0])); // ID
                        StopPointCode.add(temp.split(";")[1]); // Code
                        StopPointName.add(temp.split(";")[2]); // Name
                        // StopPointVehCapForCharging.add(Double.parseDouble(temp.split(";")[3])); // Fahrzeugkapazität zum Laden
                        if (Ladestation.elementAt(StopPointID.size() - 1) == 1) {
                            StopPointVehCapForCharging.add(1000.0);
                        } else {
                            StopPointVehCapForCharging.add(0.0);
                        }

                        for (int k = 0; k < 4; k++) {
                            StopPointChargingSystem.add(1);
                        }

                        StopPointFastChargingCapability.add(1);


                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    }
                    continue;
                }


                if (BlockBegin.equals("$LINE")) // 3. Relation: Lines
                {
                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen


                    while (temp != null && !ersteszeichen.equals("*")) {

                        LineID.add(Integer.parseInt(temp.split(";")[0]));// ID
                        LineCODE.add(temp.split(";")[1]); // Code
                        LineNAME.add(temp.split(";")[2]);// Name

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    }
                    continue;
                }

                if (BlockBegin.equals("$VEHICLETYPE")) // 4. Relation: Fahrzeugtypen
                {
                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {

                        VehTypeID.add(Integer.parseInt(temp.split(";")[0])); // ID
                        VehTypeCode.add(temp.split(";")[1]); // Code
                        VehTypeName.add(temp.split(";")[2]); // Name
                        VehTypeVehCharakteristic.add(Integer.parseInt(temp.split(";")[3])); // Characteristik
                        VehTypeVehClass.add(temp.split(";")[4]); // Fahrzeugklasse

                        if (!temp.split(";")[5].equals("-")) {
                            VehTypeCurbWeightKg.add(Double.parseDouble(temp.split(";")[5]));
                        } else {
                            VehTypeCurbWeightKg.add(0.0);
                        }

                        VehTypeVehCost.add(Double.parseDouble(temp.split(";")[6])); // Fahrzeugkosten
                        VehTypeKmCost.add(Double.parseDouble(temp.split(";")[7])); // Km-Kosten
                        VehTypeHourCost.add(Double.parseDouble(temp.split(";")[8])); // H-Kosten
                        VehTypeCapacity.add(Double.parseDouble(temp.split(";")[9])); // Kapazität
                        //VehTypeBatteryCapacity.add(Double.parseDouble(temp.split(";")[10].replace(",", "."))); // Batteriekapazität
                        VehTypeBatteryCapacity.add(120.0);
                        //VehTypeConServiceKm.add(Double.parseDouble(temp.split(";")[11].replace(",", "."))); // Verbrauch auf SF-Km
                        //VehTypeConDeadKm.add(Double.parseDouble(temp.split(";")[12].replace(",", "."))); // Verbrauch auf LF-Km
                        VehTypeConServiceKm.add(4.0); // Verbrauch auf SF-Km
                        VehTypeConDeadKm.add(3.0); // Verbrauch auf LF-Km
                        VehTypeRechargingCost.add(Double.parseDouble(temp.split(";")[13])); // Ladekosten
                        //VehTypeRechargingCost.add(0.0); // Ladekosten

                        if (temp.split(";")[14].equals("-")) {
                            VehTypeSlowRechargingTime.add(0.0);
                        } else {
                            VehTypeSlowRechargingTime.add(Double.parseDouble(temp.split(";")[14]));
                        }

                        if (temp.split(";")[15].equals("-")) {
                            VehTypeFastRechargingTime.add(0.0);
                        } else {
                            VehTypeFastRechargingTime.add(Double.parseDouble(temp.split(";")[15]));
                        }

                        help = temp.split(";")[16]; // schreibe in help alle verfuegbaren Ladesysteme
                        Vector<Integer> helv2 = new Vector<Integer>(0);

                        for (int k = 0; k < help.split(",").length; k++) {
                            helv2.add(Integer.parseInt(help.split(",")[k]));
                        }

                        Vector<Integer> temphelp = new Vector<Integer>(0);

                        for (int k = 0; k < 4; k++) {
                            temphelp.add(0);
                        }

                        for (int k = 0; k < helv2.size(); k++) // gehe diese durch
                        {

                            if (helv2.elementAt(k) == 0) temphelp.set(0, 1);
                            if (helv2.elementAt(k) == 1) temphelp.set(1, 1);
                            if (helv2.elementAt(k) == 2) temphelp.set(2, 1);
                            if (helv2.elementAt(k) == 3) temphelp.set(3, 1);

                        }

                        for (int k = 0; k < 4; k++) {
                            VehTypeChargingSystem.add(temphelp.elementAt(k)); // fuege alle Ladesysteme zusammen in ein Array
                        }


                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen


                    }
                    continue;
                }


                if (BlockBegin.equals("$VEHICLETYPEGROUP")) // 5. Relation: Fahrzeugtypgruppe
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {

                        VehTypeGroupID.add(Integer.parseInt(temp.split(";")[0])); // ID
                        VehTypeGroupCODE.add(temp.split(";")[1]); // Code
                        VehTypeGroupNAME.add(temp.split(";")[2]); // Name


                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen


                    }
                    continue;
                }

                if (BlockBegin.equals("$VEHTYPETOVEHTYPEGROUP")) // 6. Relation: Fahrzeugtyp zu Fahrzeugtypgruppe
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {

                        VehTypToVehTypeGroup.add(Integer.parseInt(temp.split(";")[0]));// ID


                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen


                    }
                    continue;
                }

                if (BlockBegin.equals("$VEHTYPECAPTOSTOPPOINT")) // 7. Relation: Fahrzeugtyp zu Ladestation
                {

                    // fuege Ladeeigenschaften den Fahrzeugtypen hinzu

                    for (int k = 0; k < StopPointID.size(); k++) // gehe alle Haltestellen durch
                    {
                        if (StopPointVehCapForCharging.elementAt(k) > 0) // wenn dort geladen werden kann
                        {
                            for (int l = 0; l < VehTypeCode.size(); l++) // trage alle Fahrzeugtypen ein
                            {
                                CapVehTypeID.add(VehTypeID.elementAt(l));
                                CapStoppointID.add(StopPointID.elementAt(k));
                                CapIDMin.add(0.0);
                                CapIDMax.add(1000.0);
                            }
                        }
                    }

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {

                        //CapVehTypeID.add(Integer.parseInt(temp.split(";")[0]));
                        //CapStoppointID.add(Integer.parseInt(temp.split(";")[1]));
                        //CapIDMin.add(Double.parseDouble(temp.split(";")[2]));
                        //CapIDMax.add(Double.parseDouble(temp.split(";")[3]));


                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    }
                    continue;

                }

                if (BlockBegin.equals("$SERVICEJOURNEY")) // 8. Relation: Servicefahrten
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

                        SFVehTypeGroupID.add(Integer.parseInt(temp.split(";")[8]));
                        SFDistance.add(Double.parseDouble(temp.split(";")[11]) / 1000);

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    }
                    continue;
                }

                if (BlockBegin.equals("$DEADRUNTIME")) // 9. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    while (temp != null && !ersteszeichen.equals("*")) {

                        FromStopID.add(Integer.parseInt(temp.split(";")[0])); // ID
                        ToStopID.add(Integer.parseInt(temp.split(";")[1])); // ID
                        Distance.add(Double.parseDouble(temp.split(";")[4]) / 1000);
                        Runtime.add(Integer.parseInt(temp.split(";")[5]) / 60);

                        temp = reader.readLine();

                    }
                    continue;
                }


            } // end while

        } catch (IOException e) {
            System.out.println(e);
        }

    }
}