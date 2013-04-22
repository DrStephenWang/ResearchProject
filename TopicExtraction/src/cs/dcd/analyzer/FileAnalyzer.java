package cs.dcd.analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.wltea.analyzer.core.*;

public class FileAnalyzer {
	
	public FileAnalyzer()
	{
		
	}
	
	public static String readDirectoryPath(String tips) throws IOException
	{
		System.out.print(tips);
		InputStreamReader reader = new InputStreamReader(System.in);
		
		return new BufferedReader(reader).readLine();
	}
	
	public static Map<Integer, String> readFile(String filePath, Map<Integer, String> fileMap) throws IOException, FileNotFoundException
	{
		if (fileMap == null)
		{
			fileMap = new HashMap<Integer, String>();
		}
		
		try {
			File file = new File(filePath);
			if (file.isFile())
			{
				System.out.println("FileName = "+ file.getName());
				System.out.println("FilePath = " + file.getPath());
				
				fileMap.put(fileMap.size(), file.getPath());
			}
			else if (file.isDirectory())
			{
				String[] fileList = file.list();
				for (int i = 0; i < fileList.length; i++)
				{
					File readingFile = new File(filePath + "/" + fileList[i]);
					if (readingFile.isFile())
					{	
						System.out.println("FileName = "+ readingFile.getName());
						System.out.println("FilePath = " + readingFile.getPath());
						
						fileMap.put(fileMap.size(), readingFile.getPath());
					}
					else if (readingFile.isDirectory())
					{
						readFile(filePath + "/" + fileList[i], fileMap);
					}
				}
			}
			else
			{
				System.out.println("Target Path Error!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ReadFile Exception: " + e.getMessage());
			System.out.println(e.getStackTrace());
		}
		
		return fileMap;
	}
	
	public static void analyzeFile(String outputPath, Map<Integer, String> fileMap) throws IOException, FileNotFoundException
	{
		for (Map.Entry<Integer, String> entry : fileMap.entrySet())
		{
			int fileKey = entry.getKey();
			String inputFilePath = entry.getValue();
			
			BufferedReader fileInput = new BufferedReader(new FileReader(inputFilePath));
			String str;
			
			String[] fileName = inputFilePath.split("\\\\");
			String outputFilePath = outputPath;
			if (outputPath.endsWith("\\"))
			{
				outputFilePath += fileName[fileName.length-1];
			}
			else
			{
				outputFilePath += "\\" + fileName[fileName.length-1];
			}
			
			PrintWriter fileOutput = new PrintWriter(new FileWriter(outputFilePath));
			
			while ((str = fileInput.readLine()) != null)
			{	
				StringReader reader = new StringReader(str);
				IKSegmenter ik = new IKSegmenter(reader, true);// 当为true时，分词器进行最大词长切分
				Lexeme lexeme = null;
				while ((lexeme = ik.next()) != null){
					fileOutput.println(lexeme.getLexemeText());
				}
			}
			
			fileInput.close();
			fileOutput.close();
		}
	}
	
	
	public static void main(String[] args) throws IOException, FileNotFoundException
	{	
		try {			
			String inputPath = readDirectoryPath("Input Target Path: ");
			Map<Integer, String> fileMap = readFile(inputPath, null);
			String outputPath = readDirectoryPath("Output Target Path: ");
			analyzeFile(outputPath, fileMap);	
			System.out.println("Done!");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Main Exception: " + e.getMessage());
			System.out.println(e.getStackTrace());
		}
	}
	
}
