package cd.dcd.datacleaning;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

public class CreateFeatureMap {

	public CreateFeatureMap()
	{
		
	}
	
	public static TreeSet<String> CreateFeatureMapFromDict(String dictFilePath) throws IOException
	{
		TreeSet<String> featureMap = new TreeSet<>();
		BufferedReader fileInput = new BufferedReader(new FileReader(dictFilePath));		
		String str;
		
		String[] filePath = dictFilePath.split("\\\\");//fileName[fileName.length-1]为文件名
		
		while ((str = fileInput.readLine()) != null)
		{	
			str.replaceAll("", "\r");
			featureMap.add(str); 
		}
		
		return featureMap;
	}
	
}
