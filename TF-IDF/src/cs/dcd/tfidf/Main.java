package cs.dcd.tfidf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cs.dcd.tfidf.ReadFiles;

public class Main {
	
    public static void main(String[] args) throws IOException
    {
    	String inputPath = ReadFiles.readDirectoryPath("Input Target Path: ");
    	Map<Integer, String> fileMap = ReadFiles.readFileMap(inputPath);
    	
    	Map<String, HashMap<String, Integer>> normal = TFIDF.NormalTFOfAll(fileMap);
        for (String filename : normal.keySet())
        {
            System.out.println("fileName " + filename);
            System.out.println("TF " + normal.get(filename).toString());
        }    	
    	
        System.out.println("-----------------------------------------");
        
    	Map<String, HashMap<String, Double>> notNarmal = TFIDF.tfOfAll(fileMap);
    	for (String filename : notNarmal.keySet())
    	{
            System.out.println("fileName " + filename);
            System.out.println("TF " + notNarmal.get(filename).toString());
        }

        System.out.println("-----------------------------------------");
  	
    	Map<String, Double> idf = TFIDF.idf(fileMap);
    	for (String word : idf.keySet())
    	{
            System.out.println("keyword :" + word + " idf: " + idf.get(word));
        }

        System.out.println("-----------------------------------------");

    	Map<String, HashMap<String, Double>> tfidf = TFIDF.tfidf(fileMap);
    	for (String filename : tfidf.keySet())
    	{
            System.out.println("fileName " + filename);
            System.out.println(tfidf.get(filename));
        }
    	
    }

}
