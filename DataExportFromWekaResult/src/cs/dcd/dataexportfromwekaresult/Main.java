package cs.dcd.dataexportfromwekaresult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws IOException{
		String inputPath = ReadFiles.readDirectoryPath("Input Target Path: ");
    	String outputPath = ReadFiles.readDirectoryPath("Output Target Path: ");
    	Map<Integer, String> fileMap = ReadFiles.readFileMap(inputPath);
		
    	for (Map.Entry<Integer, String> entry : fileMap.entrySet())
		{
			int fileKey = entry.getKey();
			String inputFilePath = entry.getValue();
			
			BufferedReader fileInput = new BufferedReader(new FileReader(inputFilePath));
			
			String[] fileName = inputFilePath.split("\\\\");
			
			PrintWriter fileOutput = new PrintWriter(new FileWriter(outputPath + "\\" + fileName[fileName.length-1]));
			
			String str;
			int InstanceNumber = 0;
			
			while ((str = fileInput.readLine()) != null)
			{	
				String name1 = "";
				String name2 = "";
				long num1 = 0;
				long num2 = 0;
				double support = 0;
				double confidence = 0;
				double lift = 0;
				double conviction = 0;
				
				str.replaceAll("", "\r");
				
				if (str.startsWith("Instances:"))
				{
					for (int i=0; i<str.length(); i++)
					{
						if (Character.isDigit(str.charAt(i)))
						{
							InstanceNumber = InstanceNumber * 10 + (str.charAt(i) - '0');
						}
					}
				}
				else if (str.contains("==>"))
				{
					long nameFlag = 0;
					long numberFlag = 0;
					String numParametre1 = "";
					String numParametre2 = "";
					String parametre1 = "";
					String parametre2 = "";
					
					for (int i=0; i<str.length(); i++)
					{
						if (str.charAt(i) == '[' || str.charAt(i) == ']')
						{
							nameFlag++;
						}
						
						if ((nameFlag == 1) && ((str.charAt(i) >= '\u4e01' && str.charAt(i) <= '\u9fa5') || (str.charAt(i) >= '\uf900' && str.charAt(i) <='\ufa2d')))
						{
							name1 += str.charAt(i);
						}
						
						if ((nameFlag == 2) && (Character.isDigit(str.charAt(i))))
						{
							numParametre1 += str.charAt(i);
						}
						
						if ((nameFlag == 3) && ((str.charAt(i) >= '\u4e01' && str.charAt(i) <= '\u9fa5') || (str.charAt(i) >= '\uf900' && str.charAt(i) <='\ufa2d')))
						{
							name2 += str.charAt(i);
						}
						
						if ((nameFlag == 4) && (Character.isDigit(str.charAt(i))))
						{
							numParametre2 += str.charAt(i);
						}
						
						if (str.charAt(i) == '(' || str.charAt(i) == ')')
						{
							nameFlag++;
							numberFlag++;
						}
						
						if (numberFlag == 3 && ((Character.isDigit(str.charAt(i))) || (str.charAt(i) == '.')))
						{
							parametre1 += str.charAt(i);
						}
						
						if (numberFlag == 7 && ((Character.isDigit(str.charAt(i))) || (str.charAt(i) == '.')))
						{
							parametre2 += str.charAt(i);
						}									
					}
					
					num1 = Long.parseLong(numParametre1);
					num2 = Long.parseLong(numParametre2);
					
					support = 1.0 * num2 / InstanceNumber;
					confidence = 1.0 * num2 / num1;
					lift = Double.parseDouble(parametre1);
					conviction = Double.parseDouble(parametre2);
					
					NumberFormat sup = NumberFormat.getInstance();
					sup.setMaximumFractionDigits(4);
					
					NumberFormat other = NumberFormat.getInstance();
					other.setMaximumFractionDigits(2);
								
					fileOutput.println(name1 + " " + name2 + " " + sup.format(support) + " " + other.format(confidence) + " " + other.format(lift) + " " + other.format(conviction));
				}
				else
				{
					
				}
			}
			
			fileInput.close();
			fileOutput.close();
		}
        
        System.out.println("Done!");
        
    }  

}
