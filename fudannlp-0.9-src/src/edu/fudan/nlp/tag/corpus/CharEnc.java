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
public class CharEnc {
	private static boolean labeled=false;;
	public static void main(String[] args) throws Exception {
		String input1 ="D:/Datasets/sighan2006/processed";
		File f = new File(input1);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				processLabeledData(files[i].toString(),"utf8","gbk");
			}
		}
		System.out.println("Done");
	}
	public static void processLabeledData(String input,String enc1, String enc2) throws Exception{
		FileInputStream is = new FileInputStream(input);
		BufferedReader r = new BufferedReader(
				new InputStreamReader(is, enc1));
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(input+"."+enc2), enc2);
		while(true) {
			String sent = r.readLine();
			if(null == sent) break;			
			w.write(sent);
			w.write('\n');
		}
		r.close();
		w.close();
	}
}
