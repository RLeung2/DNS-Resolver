import java.io.IOException;
import java.net.UnknownHostException;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.xbill.DNS.*;

public class DNSResolver {
	
	public long queryStartTime;
	public long queryEndTime;
	public long queryTime; // used to find averages in Part B
	public String root;
	
	public DNSResolver() {
		this.root = "a.root-servers.net"; // default to this root DNS Resolver
	}
	
	public DNSResolver(String root) {
		this.root = root;
	}
	
	public long getQueryTime() {
		return queryTime;
	}

	public void myDig(String website) throws IOException {
		// String must end with period to be a valid name
		if(!website.endsWith(".")) {
			queryStartTime = System.currentTimeMillis();
			myDig(website.concat("."), root, Type.NS); // start recursive call
		}
		else {
			queryStartTime = System.currentTimeMillis();
			myDig(website, root, Type.NS); // start recursive call
		}
		
	}
	
	public void myDig(String website, String resolver, int type) throws IOException {
		String newWebsite = website; // website address
		Name name = Name.fromString(website); // create a Name to use in Record object
		
		int dclass = DClass.IN; // IN = internet
		int newType = Type.X25; // something that the program won't handle so recursion can end in case of error
		String newResolver = resolver;
		
		Record rec = Record.newRecord(name, type, dclass); // used to pass through Message object
		Message query = Message.newQuery(rec); // used to send to resolver
		
		SimpleResolver simpleResolver = new SimpleResolver(resolver); // create DNS resolver object (starts at root)
		Message response = simpleResolver.send(query); // query the resolver and get response
		
		// parse the response message
		String[] lines = response.toString().split("\\r?\\n");
		String[] currentLine = null;
		
		// Check Answers line for type:
		if(!lines[6].equals("")) { 
			currentLine = lines[6].toString().split("\\s+");
			newType = Type.value(currentLine[3]); // new type of the response
		}
		// If no answers, check Authority line for type: 
		else if(!lines[8].equals("")) {
			currentLine = lines[8].toString().split("\\s+");
			newType = Type.value(currentLine[3]); // new type of the response
		}	
		
		if(newType == Type.NS || newType == Type.SOA) {
			newResolver = currentLine[4]; // new resolver to query
			myDig(website, newResolver, Type.A); // recursion: use same website address, new resolver, Type A
		}
		
		else if(newType == Type.CNAME) {
			newWebsite = currentLine[4]; // if CNAME, run root resolver on new address given
			newResolver = root; // root server
			myDig(newWebsite, newResolver, Type.NS); // recursion: new website address, root resolver, Type NS
		}
		
		else if(newType == Type.A) {			
			// End time when A response is received to calculate query time
			queryEndTime = System.currentTimeMillis();
			
			// write to output.txt file
			File output = new File("mydig_output.txt");
			FileWriter fw = new FileWriter(output);
			
			// Print starting from the QUESTIONS:
			int i;
			for(i = 2; i < 6; i++) {
				System.out.println(lines[i]);
				fw.write(lines[i]);
				fw.write("\r\n");
			}
			
			// while there is another answer
			while(!lines[i].equals("")) {
				System.out.println(lines[i]);
				fw.write(lines[i]);
				fw.write("\r\n");
				i++;
			}
			
			System.out.println();
			
			// print query time
			queryTime = queryEndTime - queryStartTime;
			System.out.println("Query time: " + queryTime);
			fw.write("\r\n");
			fw.write("Query time: " + queryTime);
			
			// print date
			Date date = new Date();
			System.out.println("When: " + date);
			fw.write("\r\n");
			fw.write("When: " + date);
			
			// locate and print message size
			int messageSizeIndex = response.toString().indexOf(";; Message size");
			String sizeSub = response.toString().substring(messageSizeIndex);
			
			System.out.println(sizeSub);
			
			fw.write("\r\n");
			fw.write(sizeSub);
			
			fw.close();
		}
		
		else
			return;
	}

}
