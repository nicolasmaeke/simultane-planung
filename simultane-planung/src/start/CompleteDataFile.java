package start;

import parser.ProjectReadWrite;

/**
 * Klasse startet die Vervollstaendigung der Rohdaten 
 * und ueberschreibt dabei die Datei.
 * 
 */
public class CompleteDataFile {

	public static void main(String[] args) {

		//Lese Daten ein (fuer den Pfad siehe data --> Rechtsklick auf die gewuenschte Datei --> Properties)
		ProjectReadWrite test = new ProjectReadWrite("/Users/XuanSon/Desktop/Uni/Master/WS1718/Java/project/simultan/ReadAndWrite/data/sample_toy_6_SF_9_stoppoints.txt");
		
		System.out.println(test.durchschnittsdistancedeadrun);
		System.out.println(test.durchschnittsruntimedeadrun);
		
		System.out.println(test.listStopPoints.size());
		System.out.println((test.listFromToDeadRun.size()));
		
	}

}
