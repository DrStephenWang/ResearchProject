package edu.fudan.nlp.tag.corpus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import edu.fudan.ml.pipe.String2Sequence;
import edu.fudan.ml.pipe.String2SequenceWithTag;
import edu.fudan.nlp.chinese.Chars;
public class CTB {
	private static boolean labeled=false;;
	public static void main(String[] args) throws Exception {
		String input1 ="D:/Datasets/ctb_v6/data/postagged";
		String output1 = "D:/Datasets/ctb_v6/processed/postagged.train";
		String input2 ="D:/xpqiu/项目/自选/CLP2010/CWS/data";
		String output2 = "D:/xpqiu/项目/自选/CLP2010/CWS/processed/";
		String2Sequence.delimer = "\\s+";
		processLabeledData(input1,output1);
		System.out.println("Done");
	}
	public static void processUnLabeledData(String input,String output) throws Exception{
		FileInputStream is = new FileInputStream(input);
		BufferedReader r = new BufferedReader(
				new InputStreamReader(is, "utf8"));
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(output), "utf8");
		while(true) {
			String sent = r.readLine();
			if(sent==null) break;
			String s = String2Sequence.genSequence(sent);
			w.write(s);
		}
		w.close();
		r.close();
	}
	public static void processLabeledData(String input,String output) throws Exception{
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(output), "utf8");
		File f2 = new File(input);
		if (f2.isDirectory()) {
			File[] files = f2.listFiles();
			for (int i = 0; i < files.length; i++) {
				FileInputStream is = new FileInputStream(files[i]);
				BufferedReader r = new BufferedReader(new InputStreamReader(is, "utf8"));
				while(true) {
					String sent = r.readLine();
					if(null == sent) break;
					sent = sent.trim();
					if(sent.startsWith("<")) continue;
					String s = String2SequenceWithTag.genSequenceWithLabelSimple(sent);
					w.write(s);
				}
				r.close();
			}				
			w.close();
		}
	}
}
