package start;

import parser.ProjectReadWrite;

public class Main {

	public static void main(String[] args) {

		ProjectReadWrite test = new ProjectReadWrite("/Users/XuanSon/Desktop/Uni/Master/WS1718/Java/project/simultan/ReadAndWrite/data/sample_toy_6_SF_9_stoppoints.txt");
		
		System.out.println(test.durchschnittsdistancedeadrun);
		System.out.println(test.durchschnittsruntimedeadrun);
		
		// System.out.println("List of stop points: " + test.listStopPoints);
		System.out.println(test.listStopPoints.size());
		System.out.println((test.listFromToDeadRun.size()));
		
		// System.out.println("List of Distance: " + test.listDistanceDeadRun);
		
		
		
	}

}
