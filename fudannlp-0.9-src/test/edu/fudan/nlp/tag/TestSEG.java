package edu.fudan.nlp.tag;

import edu.fudan.nlp.tag.SEG;

/**
 * 分词使用示例
 * @author xpqiu
 *
 */
public class TestSEG {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		SEG tag = new SEG("./model/seg.pku.gz");
		String str = "加字是我给你设计一张含你所需要的字的图纸，字是需要你自己绣上去的哦，十字绣是一种自己动手的艺术，抱枕上需要加上新人的名字或结婚日期等有意义的文字吗？10个字以内只需要10元，你是王秋雨吗？";
		String s = tag.tag(str);
		System.out.println(s);
		testFile(tag,"../FudanNLP/example.txt");
		
	}
	/**
	 * 批量测试
	 * @param tag
	 * @param file 文件名
	 * @throws Exception
	 */
	public static void testFile(SEG tag, String file) throws Exception{
		
		long beginTime = System.currentTimeMillis();
		String str1 = tag.tagFile(file);		
		String s= tag.tag(str1);
		float totalTime = (System.currentTimeMillis() - beginTime)/ 1000.0f;
		System.out.println("总时间(秒):" + totalTime);
		System.out.println("速度(字/秒):" + str1.length()/totalTime);
		
	}

}
