package cs.dcd.analyzer;

import java.io.IOException;
import java.io.StringReader;

import org.wltea.analyzer.core.*;

public class IKAnalyzer {
	
	public static void main(String[] args) throws IOException {

		String str = "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";
		StringReader reader = new StringReader(str);
		IKSegmenter ik = new IKSegmenter(reader, true);// 当为true时，分词器进行最大词长切分
		Lexeme lexeme = null;
		while ((lexeme = ik.next()) != null){
			System.out.println(lexeme.getLexemeText());
		}
	}
	
}
