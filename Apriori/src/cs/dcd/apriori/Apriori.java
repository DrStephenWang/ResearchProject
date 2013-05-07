package cs.dcd.apriori;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Apriori {
	
	private double minSupport = 0.5;//最小支持度
	private double minConfidence = 0.2;//最小置信度
	
	private static List<Set<String>> dataTrans;//以List<Set<String>>格式保存的事物数据库,利用Set的有序性	
	private int transSetCount;
	
	private String outputPath;
	
	public Apriori()
	{
		
	}
	
	public double getMinSupport()
	{
		return minSupport;
	}
	
	public void setMinSupport(double minSupport)
	{
		this.minSupport = minSupport;
	}
	
	public double getMinConfidence()
	{
		return minConfidence;
	}
	
	public void setMinConfidence(double minConfidence)
	{
		this.minConfidence = minConfidence;
	}
	
	public void init(Map<Integer, String> fileMap, String outputPath) throws IOException, FileNotFoundException
	{
		transSetCount = fileMap.size();
		dataTrans = transactionInit(fileMap);

		this.outputPath = outputPath;
	}
	
	public List<Set<String>> transactionInit(Map<Integer, String> fileMap) throws IOException, FileNotFoundException
	{
		List<Set<String>> records = new ArrayList<Set<String>>(); 
		
		for (Map.Entry<Integer, String> entry : fileMap.entrySet())
		{
			int fileKey = entry.getKey();
			Set<String> record = new HashSet<String>(); 
			
			String inputFilePath = entry.getValue();
			
			BufferedReader fileInput = new BufferedReader(new FileReader(inputFilePath));
			String str;
			
			String[] filePath = inputFilePath.split("\\\\");//fileName[fileName.length-1]为文件名
			
			while ((str = fileInput.readLine()) != null)
			{	
				str.replaceAll("", "\r");
				record.add(str); 
			}
			
			//System.out.println(record.toString());
			
			records.add(record);
			
			fileInput.close();
		}

		return records;
	}
	
	public void aprioriMining() throws IOException
	{
		Map<String, Integer> f1Set = generateCanditate1(dataTrans);
		
		Map<Set<String>, Integer> f1Map = new HashMap<Set<String>, Integer>();
		for(Map.Entry<String, Integer> f1Item : f1Set.entrySet()){
			Set<String> fs = new HashSet<String>();
			fs.add(f1Item.getKey());
			f1Map.put(fs, f1Item.getValue());
		}
		Map<Set<String>, Integer> result = f1Map;
		
		//System.out.println(f1Map);
		
		FileWriter tgFileWriter = new FileWriter(outputPath + "\\AnalysisResult" + ".txt");
		
		int k = 0;
		do
		{
			result = generateCanditate(result);
			printMap(result, tgFileWriter);
			//System.out.println(result);
		} while (result.size() != 0);
		
	}
	
	public Map<String, Integer> generateCanditate1(List<Set<String>> dataTrans)
	{	
		Map<String, Integer> result = new HashMap<String, Integer>();
		Map<String, Integer> itemCount = new HashMap<String, Integer>();
				
		for(Set<String> dataSet : dataTrans)
		{
			for(String str : dataSet)
			{
				if(itemCount.containsKey(str))
				{
					itemCount.put(str, itemCount.get(str) + 1);
				} 
				else
				{
					itemCount.put(str, 1);
				}
			}
		}
		
		for(Map.Entry<String, Integer> item : itemCount.entrySet())
		{
			if(item.getValue() >= minSupport)
			{
				result.put(item.getKey(), item.getValue());
			}
		}
		
		//System.out.println(result);
		
		return result;		
	}
	
	public Map<Set<String>, Integer> generateCanditate(Map<Set<String>, Integer> preMap)
	{
		Map<Set<String>, Integer> result = new HashMap<Set<String>, Integer>();
		//遍历两个k-1项集生成k项集
		List<Set<String>> preSetArray = new ArrayList<Set<String>>();
		for(Map.Entry<Set<String>, Integer> preMapItem : preMap.entrySet()){
			preSetArray.add(preMapItem.getKey());
		}
		int preSetLength = preSetArray.size();
		for (int i = 0; i < preSetLength - 1; i++) {
			for (int j = i + 1; j < preSetLength; j++) {
				String[] strA1 = preSetArray.get(i).toArray(new String[0]);
				String[] strA2 = preSetArray.get(j).toArray(new String[0]);
				if (isCanLink(strA1, strA2)) { // 判断两个k-1项集是否符合连接成k项集的条件　
					Set<String> set = new TreeSet<String>();
					for (String str : strA1) {
						set.add(str);
					}
					set.add((String) strA2[strA2.length - 1]); // 连接成k项集
					// 判断k项集是否需要剪切掉，如果不需要被cut掉，则加入到k项集列表中
					if (!isNeedCut(preMap, set)) {//由于单调性，必须保证k项集的所有k-1项子集都在preMap中出现，否则就该剪切该k项集
						result.put(set, 0);
					}
				}
			}
		}
		
		return assertFP(result);//遍历事物数据库，求支持度，确保为频繁项集		
	}
	
	private boolean isNeedCut(Map<Set<String>, Integer> preMap, Set<String> set) {
		// TODO Auto-generated method stub
		boolean flag = false;
		List<Set<String>> subSets = getSubSets(set);
		for(Set<String> subSet : subSets){
			if(!preMap.containsKey(subSet)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	private List<Set<String>> getSubSets(Set<String> set) {
		// TODO Auto-generated method stub
		String[] setArray = set.toArray(new String[0]);
		List<Set<String>> result = new ArrayList<Set<String>>();
		for(int i = 0; i < setArray.length; i++){
			Set<String> subSet = new HashSet<String>();
			for(int j = 0; j < setArray.length; j++){
				if(j != i) subSet.add(setArray[j]);
			}
			result.add(subSet);
		}
		return result;
	}
	
	private Map<Set<String>, Integer> assertFP(
			Map<Set<String>, Integer> allKItem) {
		// TODO Auto-generated method stub
		Map<Set<String>, Integer> result = new HashMap<Set<String>, Integer>();
		for(Set<String> kItem : allKItem.keySet()){
			for(Set<String> data : dataTrans){
				boolean flag = true;
				for(String str : kItem){
					if(!data.contains(str)){
						flag = false;
						break;
					}
				}
				if(flag) allKItem.put(kItem, allKItem.get(kItem) + 1);
			}
			if(allKItem.get(kItem) >= minSupport) {
				result.put(kItem, allKItem.get(kItem));
			}
		}
		return result;
	}
	
	private boolean isCanLink(String[] strA1, String[] strA2) {
		// TODO Auto-generated method stub
		boolean flag = true;
		if(strA1.length != strA2.length){
			return false;
		}else {
			for(int i = 0; i < strA1.length - 1; i++){
				if(!strA1[i].equals(strA2[i])){
					flag = false;
					break;
				}
			}
			if(strA1[strA1.length -1].equals(strA2[strA1.length -1])){
				flag = false;
			}
		}
		return flag;
	}
	
	private int printMap(Map<Set<String>, Integer> f1Map, FileWriter tgFileWriter) throws IOException {
		// TODO Auto-generated method stub
		for(Map.Entry<Set<String>, Integer> f1MapItem : f1Map.entrySet()){
			for(String p : f1MapItem.getKey()){
				tgFileWriter.append(p + " ");
			}
			tgFileWriter.append(": " + f1MapItem.getValue() + "\n");
		}
		tgFileWriter.flush();
		return f1Map.size();
	}
	

	
	
	
	
}
