package edu.fudan.nlp.tag;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import edu.fudan.nlp.chinese.ChineseTrans;
import edu.fudan.nlp.tag.NER;

/**
 * 实体名识别使用示例
 * @author xpqiu
 *
 */
public class TestNER {	


	/**
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		NER tag = new NER("./model/key.gz");
		String str = "新浪体育讯　北京时间4月15日03:00(英国当地时间14日20:00)，2009/10赛季英格兰足球超级联赛第34轮一场焦点战在白鹿巷球场展开角逐，阿森纳客场1比2不敌托特纳姆热刺，丹尼-罗斯和拜尔先入两球，本特纳扳回一城。阿森纳仍落后切尔西6分(净胜球少15个)，夺冠几成泡影。热刺近 7轮联赛取得6胜，继续以1分之差紧逼曼城。";
		HashMap<String, String> s = tag.tag(str);
		System.out.println(s);
//		ss = tag.tag("孤灯一口交杯饮，风流独自慰平生，莫谈天性爱淡泊，天阴道远多珍重。");
		
		//		MyNER tag = new MyNER();
		
		testFile(tag,"../FudanNLP/example.txt");
		
	}
	public static void testFile(NER tag, String file) throws Exception{
		BufferedReader bin;
		StringBuilder res = new StringBuilder();
		String str1=null;
		try {
			InputStreamReader  read = new InputStreamReader (new FileInputStream(file),"utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str = lbin.readLine();
			while(str!=null){
				str = ChineseTrans.toSBC(str);
				res.append(str);				
				res.append("\n");
				str = lbin.readLine();
			}
			lbin.close();
			str1 = res.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		long beginTime = System.currentTimeMillis();
		HashMap<String, String> map = tag.tag(str1);
		float totalTime = (System.currentTimeMillis() - beginTime)/ 1000.0f;
		System.out.println("总时间(秒):" + totalTime);
		System.out.println("速度(字/秒):" + str1.length()/totalTime);
		System.out.println(map);
	}

}
