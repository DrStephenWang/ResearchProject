package cs.dcd.casessegmentation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws IOException
    {
    	String inputPath = ReadFiles.readDirectoryPath("Input Target Path: ");
    	String outputPath = ReadFiles.readDirectoryPath("Output Target Path: ");
    	Map<Integer, String> fileMap = ReadFiles.readFileMap(inputPath);
    	
    	for (Map.Entry<Integer, String> entry : fileMap.entrySet())
		{
			int fileKey = entry.getKey();
			String inputFilePath = entry.getValue();
			
			BufferedReader fileInput = new BufferedReader(new FileReader(inputFilePath));
			String str;
			
			int fileFlag = 0;
			int fileNum = 0;
			String fileName = "";
			
			PrintWriter fileOutput = null;
			
			str = fileInput.readLine();
			while (str != null)
			{					
				
				if (str != null && fileFlag == 0 && (str.contains("一、") || str.contains("二、") || str.contains("三、") || str.contains("四、") || str.contains("五、") || str.contains("六、") || str.contains("七、") || str.contains("八、") || str.contains("九、") || str.contains("十、")))
				{
					fileNum++;
					
					if (fileNum < 10)
					{
						fileName = "00000" + fileNum;
					}
					else if (fileNum < 100)
					{
						fileName = "0000" + fileNum;
					}
					else if (fileNum < 1000)
					{
						fileName = "000" + fileNum;
					}
					else if (fileNum < 10000)
					{
						fileName = "00" + fileNum;
					}
					else if (fileNum < 100000)
					{
						fileName = "0" + fileNum;
					}
					else
					{
						
					}
					
					fileOutput = new PrintWriter(new FileWriter(outputPath + "\\" + fileName + ".txt"));
					fileFlag = 1;
				}
				
				if (str != null && fileFlag == 1)
				{
					fileOutput.println(str);
					str = fileInput.readLine();
				}
				
				
				if (str != null && fileFlag == 0)
				{
					str = fileInput.readLine();
				}
				
				if (str != null && fileFlag == 1 && (str.contains("【编者评注】") || str.contains("[编者评注]") || str.contains("一、") || str.contains("二、") || str.contains("三、") || str.contains("四、") || str.contains("五、") || str.contains("六、") || str.contains("七、") || str.contains("八、") || str.contains("九、") || str.contains("十、")))
				{
					fileOutput.close();
					fileFlag = 0;
				}
				
			}
			
			fileInput.close();
		}
    	
    	System.out.println("Done");
    	
    }
	
}
