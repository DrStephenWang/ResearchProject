package edu.fudan.nlp.parser;

import java.io.IOException;

import edu.fudan.nlp.parser.dep.YamadaParser;
import edu.fudan.nlp.tag.POS;
/**
 * Yamada依存句法分析使用示例
 * @author xpqiu
 *
 */
public class yamada {

	private static YamadaParser parser;

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		parser = new YamadaParser("model/dep-fast.gz");

		testWithPOS();
		testWithoutPOS();

	}
	/**
	 * 只输入句子，不带词性
	 */
	private static void testWithoutPOS() {
		String word = "中国进出口银行与中国银行加强合作";
		POS tag = new POS("model/pos.ctb.gz");
		String[][] s = tag.tag2Array(word);
		int[] heads = new int[s[0].length];
		try {
			parser.getBestParse(s[0], s[1], heads);
		} catch (Exception e) {			
			e.printStackTrace();
		}

		for(int i = 0; i < heads.length; i++)
			System.out.printf("%s\t%s\t%d\n", s[0][i], s[1][i], heads[i]);
	}
	/**
	 * 输入句子，并标注了词性
	 */
	private static void testWithPOS() {
		String[] words = new String[]{"中国", "进出口", "银行", "与", "中国", "银行", "加强", "合作"};
		String[] pos = new String[]{"NR", "NN", "NN", "CC", "NR", "NN", "VV", "NN"};
		int[] heads = new int[words.length];

		try {
			parser.getBestParse(words, pos, heads);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(int i = 0; i < heads.length; i++)
			System.out.printf("%s\t%s\t%d\n", words[i], pos[i], heads[i]);
	}

}