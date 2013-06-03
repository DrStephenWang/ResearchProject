package cs.dcd.datacleaning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ReadFiles {
	
	private static Map<Integer, String> fileMap = new HashMap<Integer, String>();
	
	public ReadFiles()
	{
		
	}
	
	public static String readDirectoryPath(String tips) throws IOException
	{
		System.out.print(tips);
		InputStreamReader reader = new InputStreamReader(System.in);
		
		return new BufferedReader(reader).readLine();
	}
	
	public static Map<Integer, String> readFileMap(String filePath) throws IOException, FileNotFoundException
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
						readFileMap(filePath + "/" + fileList[i]);
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
	
}
