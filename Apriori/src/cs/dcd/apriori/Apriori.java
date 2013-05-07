package cs.dcd.apriori;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class Apriori {
	
	private static final double MIN_SUPPORT = 0.6;//最小支持度
	private static final double MIN_CONFIDENCE = 0.2;//最小置信度
	private static final int FILE_NUMBER = 100;//最小置信度
	
	private TreeSet transSet[] = new TreeSet[FILE_NUMBER];
	
	private IdentityHashMap ruleMap = new IdentityHashMap();//注意使用IdentityHashMap，否则由于关联规则产生存在键值相同的会出现覆盖
	
	private int itemCounts = 0;//候选1项目集大小,即字母的个数
	private TreeSet frequencySet[] = new TreeSet[40];//频繁项集数组，[0]:代表1频繁集...，TreeSet（）使用元素的自然顺序对元素进行排序
	private TreeSet maxFrequency = new TreeSet();//最大频繁集
	private TreeSet candidate = new TreeSet();
	private TreeSet candidateSet[] = new TreeSet[40];//候选集数组[0]:代表1候选集
	private int frequencyIndex;
	
	public Apriori()
	{
		
	}
	
	public void init(Map<Integer, String> fileMap) throws IOException, FileNotFoundException
	{
		maxFrequency = new TreeSet();
		
		transactionInit(fileMap);
		
	}
	
	public void transactionInit(Map<Integer, String> fileMap) throws IOException, FileNotFoundException
	{
		for (Map.Entry<Integer, String> entry : fileMap.entrySet())
		{
			int fileKey = entry.getKey();
			transSet[fileKey] = new TreeSet();
			
			String inputFilePath = entry.getValue();
			
			BufferedReader fileInput = new BufferedReader(new FileReader(inputFilePath));
			String str;
			
			String[] filePath = inputFilePath.split("\\\\");//fileName[fileName.length-1]为文件名
			
			while ((str = fileInput.readLine()) != null)
			{	
				str.replaceAll("", "\r");
				transSet[fileKey].add(str);
			}
			
			System.out.println(transSet[fileKey].toString());
			
			fileInput.close();
		}

	}
	
	
	
	
	
}
