package cs.dcd.apriori;

import java.io.IOException;
import java.util.Map;

import cs.dcd.apriori.ReadFiles;

public class Main {

	public static void main(String[] args) throws IOException
	{
		String inputPath = ReadFiles.readDirectoryPath("Input Target Path: ");
    	String outputPath = ReadFiles.readDirectoryPath("Output Target Path: ");
    	
    	Map<Integer, String> fileMap = ReadFiles.readFileMap(inputPath);
    	
    	Apriori ap = new Apriori();
    	ap.init(fileMap, outputPath);
    	ap.aprioriMining();
    	
    	System.out.println("Done!");
    	
	}
	
}
