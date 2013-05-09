package cd.dcd.datacleaning;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Main {

	public static final int TF_MIN_VALUE = 0;
	public static final int TF_MAX_VALUE = 5;
	
	public static void main(String[] args) throws IOException
    {
    	String inputPath = ReadFiles.readDirectoryPath("Input Target Path: ");
    	String outputPath = ReadFiles.readDirectoryPath("Output Target Path: ");
    	Map<Integer, String> fileMap = ReadFiles.readFileMap(inputPath);
    	
    	TreeSet<String> medicineSet = CreateFeatureMap.CreateFeatureMapFromDict("D:/Eclipse-WorkSpace/DataCleaning/src/中药.dic");
    	TreeSet<String> prescriptionSet = CreateFeatureMap.CreateFeatureMapFromDict("D:/Eclipse-WorkSpace/DataCleaning/src/方剂.dic");
    	TreeSet<String> symptomAndDiseaseSet = CreateFeatureMap.CreateFeatureMapFromDict("D:/Eclipse-WorkSpace/DataCleaning/src/医案病名病症.dic");
    	
    	Map<String, HashMap<String, Integer>> normal = TFIDF.NormalTFOfAll(fileMap);
    	PrintWriter fileTFOutput = new PrintWriter(new FileWriter(outputPath + "\\TF.txt"));
    	for (String filename : normal.keySet())
        {
    		fileTFOutput.println("fileName " + filename);
    		fileTFOutput.println("TF " + normal.get(filename).toString());
    		
    		PrintWriter fileTFIDFOutput = new PrintWriter(new FileWriter(outputPath + "\\" + filename));
    		for (String itemString : normal.get(filename).keySet())
    		{
    			if (normal.get(filename).get(itemString) > TF_MIN_VALUE && normal.get(filename).get(itemString) < TF_MAX_VALUE)
    			{
    				if (medicineSet.contains(itemString) || prescriptionSet.contains(itemString) || symptomAndDiseaseSet.contains(itemString))
    				{
    					fileTFIDFOutput.println(itemString);
    				}			
    			}
    		}
    		fileTFIDFOutput.close();
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
        }
    	TFIDFOutput.close();
    	
    	System.out.println("Done");
    	
    }
	
}
