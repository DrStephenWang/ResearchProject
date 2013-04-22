package edu.fudan.ml.data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class svmFileReaderforwipo extends Reader {
	String content = null;
	String contentlabel = null;
	BufferedReader reader;
	BufferedReader reader2;
	public svmFileReaderforwipo(String file,String label) {
		try {
			File f = new File(file);
			FileInputStream in = new FileInputStream(f);
			File f2 = new File(label);
			FileInputStream in2 = new FileInputStream(f2);
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			reader2 = new BufferedReader(new InputStreamReader(in2, "UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	public boolean hasNext() {
		try {
			content = reader.readLine();
			contentlabel = reader2.readLine();
			if (content == null) {
				reader.close();
				reader2.close();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public Instance next() {
		String[] tokens = content.split("\\t+|\\s+");
		String[] tokenslabel = contentlabel.split("\\t+|\\s+");
		SparseVector sv = new SparseVector();
		for (int i = 0; i < tokens.length; i++) {
			String[] toks = tokens[i].split(":");
			if (toks.length > 1) {
				int idx = Integer.parseInt(toks[0]);
				double value = Double.parseDouble(toks[1]);
				sv.put(idx, value);
			}
		}
		return new Instance(sv,tokenslabel[tokenslabel.length-1]);
	}
}
