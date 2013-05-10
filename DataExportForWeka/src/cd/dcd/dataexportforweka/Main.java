package cd.dcd.dataexportforweka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeSet;

public class Main {
	
	public static void main(String[] args) throws IOException
    {
		String inputPath = ReadFiles.readDirectoryPath("Input Target Path: ");
    	String outputPath = ReadFiles.readDirectoryPath("Output Target Path: ");
    	Map<Integer, String> fileMap = ReadFiles.readFileMap(inputPath);
    	
    	TreeSet<String> medicineSet = CreateFeatureMap.CreateFeatureMapFromDict("D:/Eclipse-WorkSpace/DataExportForWeka/src/中药.dic");
    	TreeSet<String> prescriptionSet = CreateFeatureMap.CreateFeatureMapFromDict("D:/Eclipse-WorkSpace/DataExportForWeka/src/方剂.dic");
    	TreeSet<String> symptomAndDiseaseSet = CreateFeatureMap.CreateFeatureMapFromDict("D:/Eclipse-WorkSpace/DataExportForWeka/src/医案病名病症.dic");
    	
    	PrintWriter dataSetOutput = new PrintWriter(new FileWriter(outputPath + "\\" + "DataSet.arff"));
    	
    	
    	
    	System.out.println("Done!");
    	
    }
	
}
