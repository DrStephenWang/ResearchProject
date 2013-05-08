package cd.dcd.datacleaning;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static final double TDIDF_MIN_VALUE = 0.0015;
	public static final double TDIDF_MAX_VALUE = 0.0025;
	
	public static void main(String[] args) throws IOException
    {
    	String inputPath = ReadFiles.readDirectoryPath("Input Target Path: ");
    	String outputPath = ReadFiles.readDirectoryPath("Output Target Path: ");
    	Map<Integer, String> fileMap = ReadFiles.readFileMap(inputPath);
    	
    	Map<String, HashMap<String, Integer>> normal = TFIDF.NormalTFOfAll(fileMap);
    	PrintWriter fileTFOutput = new PrintWriter(new FileWriter(outputPath + "\\TF.txt"));
    	for (String filename : normal.keySet())
        {
    		fileTFOutput.println("fileName " + filename);
    		fileTFOutput.println("TF " + normal.get(filename).toString());
        }
    	fileTFOutput.close();
        
    	Map<String, HashMap<String, Double>> notNormal = TFIDF.tfOfAll(fileMap);
    	PrintWriter fileTFNormalOutput = new PrintWriter(new FileWriter(outputPath + "\\TFNormal.txt"));
    	for (String filename : notNormal.keySet())
    	{
    		fileTFNormalOutput.println("fileName " + filename);
    		fileTFNormalOutput.println("TF " + notNormal.get(filename).toString());
        }
    	fileTFNormalOutput.close();
  	
    	Map<String, Double> idf = TFIDF.idf(fileMap);
    	PrintWriter fileIDFOutput = new PrintWriter(new FileWriter(outputPath + "\\IDF.txt"));
    	for (String word : idf.keySet())
    	{
    		fileIDFOutput.println("keyword :" + word + " idf: " + idf.get(word));
        }
    	fileIDFOutput.close();

    	Map<String, HashMap<String, Double>> tfidf = TFIDF.tfidf(fileMap);
    	PrintWriter TFIDFOutput = new PrintWriter(new FileWriter(outputPath + "\\TFIDF.txt"));
    	for (String filename : tfidf.keySet())
    	{
    		TFIDFOutput.println("fileName " + filename);
    		TFIDFOutput.println("TFIDF " + tfidf.get(filename).toString());
    		
    		PrintWriter fileTFIDFOutput = new PrintWriter(new FileWriter(outputPath + "\\" + filename));
    		for (String itemString : tfidf.get(filename).keySet())
    		{
    			if (tfidf.get(filename).get(itemString) > TDIDF_MIN_VALUE && tfidf.get(filename).get(itemString) < TDIDF_MAX_VALUE)
    			{
    				fileTFIDFOutput.println(itemString);
    			}
    		}
    		fileTFIDFOutput.close();
        }
    	TFIDFOutput.close();
    	
    	System.out.println("Done");
    	
    }
	
}
