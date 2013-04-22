package edu.fudan.ml.feature.generator.templet;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class DictionaryTemplet implements Templet, Serializable {
	private Dictionary d;
	private int[] args;
	private int id;
	private String text;
	public DictionaryTemplet(Dictionary d, int id, int ... args) {
		this.d = d;
		this.id = id;
		this.args = args;
		Arrays.sort(args);
		StringBuffer sb = new StringBuffer();
		sb.append(id);
		sb.append(":dict");
		for(int i=0; i<args.length; i++) {
			sb.append(':');
			sb.append(args[i]);
		}
		sb.append(':');
		this.text = new String(sb);
	}
	@Override
	public void generateAt(Instance instance, int pos, int[] fv,
			Alphabet features, int numLabels) {
		List<String[]> data = (List<String[]> )instance.getData();
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<args.length; i++) {
			int idx = pos+args[i];
			if(idx>=0&&idx<data.size())
				sb.append((data.get(idx)[0]));
		}
		List<String> l = d.test(sb.toString());
		if(l.size()>0) {
			String label = text+l.get(0);
			fv[id] = features.lookupIndex(label, numLabels);;
		}
	}
	@Override
	public int getOrder() { return 0; }
	@Override
	public int[] getVars() { return new int[]{0}; }
}
