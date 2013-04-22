package edu.fudan.ml.feature.generator.templet;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
public class TemplateGroup extends ArrayList<Templet> {
	private static final long serialVersionUID = 115444082750769279L;
	public int gid;
	public int[] base;
	public int maxOrder;
	public int numStates;
	int[] orders;
	public int[][] offset;
	public TemplateGroup() {
		super();
		gid = 0;
	}
	public void load(String file) {
		try {
			InputStreamReader  read = new InputStreamReader (new FileInputStream(file),"utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str;
			while((str=lbin.readLine())!=null){
				if(str.charAt(0)=='#')
					continue;
				add(new BaseTemplet(gid++, str));
			}
			lbin.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	public void calc(int numLabels){
		int numTemplets = size();
		this.orders = new int[numTemplets];
		for(int j=0; j<numTemplets; j++) {
			Templet t = get(j);
			this.orders[j] = t.getOrder();
			if (orders[j] > maxOrder)
				maxOrder = orders[j];
		}
		base = new int[maxOrder+2];
		base[0]=1;
		for(int i=1; i<base.length; i++) {
			base[i]=base[i-1]*numLabels;
		}
		this.numStates = base[maxOrder+1];
		offset = new int[numTemplets][numStates];
		for(int t=0; t<numTemplets; t++) {
			Templet tpl = this.get(t);
			int[] vars = tpl.getVars();
			int[] bits = new int[maxOrder+1];
			int v;
			for(int s=0; s<numStates; s++) {
				int d = s;
				for(int i=0; i<maxOrder+1; i++) {
					bits[i] = d%numLabels;
					d = d/numLabels;
				}
				v = 0;						
				for(int i=0; i<vars.length; i++) {
					v = v*numLabels + bits[-vars[i]];
				}
				offset[t][s] = v;
			}
		}
	}
}
