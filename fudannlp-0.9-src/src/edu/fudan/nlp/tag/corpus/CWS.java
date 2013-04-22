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
import edu.fudan.nlp.chinese.Chars;
public class CWS {
	private static boolean labeled=false;;
	public static void main(String[] args) throws Exception {
		String input1 =args[0];
		String output1;
		if(args.length==1){
			output1 = input1+".word";
		}else{
			output1 = args[1];
		}
		String input2 ="D:/xpqiu/项目/自选/CLP2010/CWS/data";
		String output2 = "D:/xpqiu/项目/自选/CLP2010/CWS/processed/";
		String2Sequence.delimer = "[\\s"+String.valueOf((char)12288)+"]+"; //全角空格
		File f = new File(input1);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				processLabeledData(files[i].toString(),output1+files[i].getName());
			}
		}else{
			processLabeledData(input1,output1);
		}
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
		FileInputStream is = new FileInputStream(input);
		BufferedReader r = new BufferedReader(
				new InputStreamReader(is, "utf8"));
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(output), "utf8");
		while(true) {
			String sent = r.readLine();
			if(null == sent) break;
			String s = String2Sequence.genSequenceWithLabel(sent);
			w.write(s);
		}
		r.close();
		w.close();
	}
}
