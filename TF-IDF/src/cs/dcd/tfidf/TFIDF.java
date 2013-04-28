package cs.dcd.tfidf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

public class TFIDF {

	private static HashMap<String, HashMap<String, Double>> allTheTf = new HashMap<String, HashMap<String, Double>>();
	private static HashMap<String, HashMap<String, Integer>> allTheNormalTF = new HashMap<String, HashMap<String, Integer>>();
	
	public TFIDF()
	{
		
	}
	
    public static Map<String, HashMap<String, Integer>> NormalTFOfAll(Map<Integer, String> fileMap) throws IOException
    {
    	for (Map.Entry<Integer, String> entry : fileMap.entrySet())
		{
			int fileKey = entry.getKey();
			HashMap<String, Integer> dict = new HashMap<String, Integer>();
			
			String inputFilePath = entry.getValue();
			
			BufferedReader fileInput = new BufferedReader(new FileReader(inputFilePath));
			String str;
			
			String[] filePath = inputFilePath.split("\\\\");//fileName[fileName.length-1]为文件名
			
			List<String> cutWordResult = new ArrayList<String>();
			
			while ((str = fileInput.readLine()) != null)
			{	
				StringReader reader = new StringReader(str);
				IKSegmenter ik = new IKSegmenter(reader, true);// 当为true时，分词器进行最大词长切分
				Lexeme lexeme = null;
				while ((lexeme = ik.next()) != null){
					cutWordResult.add(lexeme.getLexemeText());
				}
			}
			
			fileInput.close();
			
			dict = TFIDF.normalTF(cutWordResult);
            allTheNormalTF.put(filePath[filePath.length-1], dict);
		}

        return allTheNormalTF;
    }
    
    public static HashMap<String, Integer> normalTF(List<String> cutWordResult)
    {
        HashMap<String, Integer> tfNormal = new HashMap<String, Integer>();//没有正规化
        int wordNum = cutWordResult.size();
        int wordtf = 0;
        for (int i = 0; i < wordNum; i++) {
            wordtf = 0;
            if (cutWordResult.get(i) != " ") {
                for (int j = 0; j < wordNum; j++) {
                    if (i != j) {
                        if (cutWordResult.get(i).equals(cutWordResult.get(j)))
                        {
                            cutWordResult.set(j, " ");
                            wordtf++;
                        }
                    }
                }
                tfNormal.put(cutWordResult.get(i), ++wordtf);
                cutWordResult.set(i, " ");
            }
        }
        
        return tfNormal;
    }
	
    public static Map<String, HashMap<String, Double>> tfOfAll(Map<Integer, String> fileMap) throws IOException
    {
    	for (Map.Entry<Integer, String> entry : fileMap.entrySet())
		{
			int fileKey = entry.getKey();
			HashMap<String, Double> dict = new HashMap<String, Double>();
			
			String inputFilePath = entry.getValue();
			
			BufferedReader fileInput = new BufferedReader(new FileReader(inputFilePath));
			String str;
			
			String[] filePath = inputFilePath.split("\\\\");//fileName[fileName.length-1]为文件名
			
			List<String> cutWordResult = new ArrayList<String>();
			
			while ((str = fileInput.readLine()) != null)
			{	
				StringReader reader = new StringReader(str);
				IKSegmenter ik = new IKSegmenter(reader, true);// 当为true时，分词器进行最大词长切分
				Lexeme lexeme = null;
				while ((lexeme = ik.next()) != null){
					cutWordResult.add(lexeme.getLexemeText());
				}
			}
			
			fileInput.close();
			
			dict = TFIDF.tf(cutWordResult);
			allTheTf.put(filePath[filePath.length-1], dict);
		}

    	return allTheTf;
    }
    
    public static HashMap<String, Double> tf(List<String> cutWordResult)
    {
        HashMap<String, Double> tf = new HashMap<String, Double>();//正规化
        int wordNum = cutWordResult.size();
        int wordtf = 0;
        for (int i = 0; i < wordNum; i++) {
            wordtf = 0;
            for (int j = 0; j < wordNum; j++) {
                if (cutWordResult.get(i) != " " && i != j) {
                    if (cutWordResult.get(i).equals(cutWordResult.get(j))) {
                        cutWordResult.set(j, " ");
                        wordtf++;
                    }
                }
            }
            if (cutWordResult.get(i) != " ") {
                tf.put(cutWordResult.get(i), (new Double((++wordtf)) / wordNum));
                cutWordResult.set(i, " ");
            }
        }
        return tf;
    }   
    
    public static Map<String, Double> idf(Map<Integer, String> fileMap) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        //公式IDF＝log((1+|D|)/|Dt|)，其中|D|表示文档总数，|Dt|表示包含关键词t的文档数量。
        Map<String, Double> idf = new HashMap<String, Double>();
        List<String> located = new ArrayList<String>();

        float Dt = 1;
        float D = allTheNormalTF.size();//文档总数
        
        List<String> key = new ArrayList<String>();//存储各个文档名的List
        
        for (Map.Entry<Integer, String> entry : fileMap.entrySet())
		{
			int fileKey = entry.getKey();

			String inputFilePath = entry.getValue();
					
			String[] filePath = inputFilePath.split("\\\\");//fileName[fileName.length-1]为文件名
			
			key.add(filePath[filePath.length-1]);
		}
                
        Map<String, HashMap<String, Integer>> tfInIdf = allTheNormalTF;//存储各个文档tf的Map

        for (int i = 0; i < D; i++) {
            HashMap<String, Integer> temp = tfInIdf.get(key.get(i));
            for (String word : temp.keySet()) {
                Dt = 1;
                if (!(located.contains(word))) {
                    for (int k = 0; k < D; k++) {
                        if (k != i) {
                            HashMap<String, Integer> temp2 = tfInIdf.get(key.get(k));
                            if (temp2.keySet().contains(word)) {
                                located.add(word);
                                Dt = Dt + 1;
                                continue;
                            }
                        }
                    }
                    idf.put(word, Log.log((1 + D) / Dt, 10));
                }
            }
        }
        return idf;
    }
    
    public static Map<String, HashMap<String, Double>> tfidf(Map<Integer, String> fileMap) throws IOException {

        Map<String, Double> idf = TFIDF.idf(fileMap);
        Map<String, HashMap<String, Double>> tf = TFIDF.tfOfAll(fileMap);

        for (String file : tf.keySet()) {
            Map<String, Double> singelFile = tf.get(file);
            for (String word : singelFile.keySet()) {
                singelFile.put(word, (idf.get(word)) * singelFile.get(word));
            }
        }
        return tf;
    }
    
}
