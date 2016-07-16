import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MedianDegree {

	 static void findMedianToPayments(String inputFilePath, String outputFilePath, Graph graph) throws IOException {
		FileInputStream fis = new FileInputStream(new File(inputFilePath));
		FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
		//reader for input file
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		//writer for output file
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		String line = null;
		while ((line = br.readLine()) != null) {
			//parse the input strings and split on comma
			String[] tokens = line.replaceAll("\\{|\\}|\"", "").split(",");
			//Ignore inputs which do not have the required input format actor,target,created time
			if(tokens.length!=3)
				continue;
			
			//To handle cases which differ in the order of actor,target,created time
			HashMap<String,String> inputMap=new HashMap<String,String>(); 		
			//further split the strings to obtain actor,target,created time
			for (int i = 0; i < tokens.length; i++){
				String[] token_list= tokens[i].split(":", 2);
				inputMap.put(token_list[0].trim(),token_list[1].trim());
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			try {
				if(inputMap.size()!=3)
					continue;
				//convert into seconds
				Long timeInSeconds = ((((Date) formatter.parse(inputMap.get("created_time"))).getTime()) / 1000);
				//add the Edge to the graph
				graph.addEdge(inputMap.get("actor"),inputMap.get("target"),timeInSeconds);
				bw.write(String.valueOf(graph.getMedian()));
				bw.newLine();
				bw.flush();

				// call the add edge and dont convert to string
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fis.close();
				fos.close();
				br.close();
				bw.close();
			}

		}

	}

	public static void main(String args[]) {
		Graph graph = new Graph();
		if(args.length==2){	
			try {
					// read data from the input file,calculate median after each input payment 
				   //and write it to an output file
					findMedianToPayments(args[0], args[1], graph);
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
		else{
			//prompt user if input file not found
			System.out.println("Required: <Input File Path> <Output File Path>");
			System.exit(-1);
		}
	}
}


