import java.io.IOException;
import java.util.*;

public class DigTest {

	public static void main(String[] args) throws IOException {
		
		// you can set your own root and test other websites
		DNSResolver resolver = new DNSResolver(); // default to "a.root-servers.net"
		resolver.myDig("google.co.jp.");

		//System.out.println();
		
		// Code to run 10 tests on a website while calculating the percentiles
		/*
		List<Long> times = new ArrayList<>();
		long totalTime = 0;
		for(int i = 0; i < 10; i++) {
			resolver.myDig("Linkedin.com.");
			times.add(resolver.getQueryTime());
			totalTime += resolver.getQueryTime();
			System.out.println();
		}
		long percentile25 = Percentile(times, 25);
		long average = totalTime / 10;
		long percentile75 = Percentile(times, 75);
		System.out.println("25th Percentile: " + percentile25);
		System.out.println("Average: " + average);
		System.out.println("75th Percentile: " + percentile75);
		*/
	}
	
	// Gets given percentile of list of times
	public static long Percentile(List<Long> times, double percentile)
    {
        Collections.sort(times);
        int index = (int)Math.ceil(((double)percentile / (double)100) * (double)(times.size()));
        return times.get(index - 1);
    }
}
