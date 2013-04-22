package edu.fudan.ml.feature.generator.templet;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class BaseTemplet implements Templet, Serializable {
	private static final long serialVersionUID = 7543856094273600355L;
	Pattern parser = 
		Pattern.compile("(?:%(x|y)\\[(-?\\d+)(?:,(\\d+))?\\])");
	String templet;
	transient Matcher matcher;
	int[] vars;
	int order;
	int id;
	public BaseTemplet(int id, String templet) {
		this.id = id;
		this.templet = templet;
		this.matcher = parser.matcher(this.templet);
		List<String> l = new ArrayList<String>();
		while (matcher.find()) {
			if (matcher.group(1).equals("y")) {
				l.add(matcher.group(2));
			}
		}
		vars = new int[l.size()];
		for(int j=0; j<l.size(); j++) {
			vars[j] = Integer.parseInt(l.get(j));
		}
		Arrays.sort(vars);
		order = vars[vars.length-1]-vars[0];
		matcher.reset();
	}
	public int[] getVars() { return this.vars; }
	public int getOrder() { return this.order; }
	public void generateAt(Instance instance, int pos, 
			int[] fv, Alphabet features, int numLabels) throws Exception {
		List data = (List) instance.getData();
		int j=0, k=0;
		Matcher m = this.matcher;
		m.reset();
		StringBuffer sb = new StringBuffer();
		sb.append(id); sb.append(':');
		try{
			while (m.find()) {
				String rp = "";
				if (m.group(1).equals("x")) {
					j = Integer.parseInt(m.group(2));
					k = Integer.parseInt(m.group(3));
					if(pos+j<0 || pos+j>=data.size()) {
						if(pos+j<0) rp="//B_"+String.valueOf(-(pos+j)-1)+"//";
						if(pos+j>=data.size()) rp="//E_"+String.valueOf(pos+j-data.size())+"//";
					}else{
						if(k==0&&data.get(pos+j) instanceof String)
							rp = "//"+((String) data.get(pos+j))+"//";
						else{
							rp = "//"+((String[]) data.get(pos+j))[k]+"//";
						}
					}
				} else if (m.group(1).equals("y")) {
					j = Integer.parseInt(m.group(2));
					if(pos+j<0 || pos+j>=data.size()) return;
					rp = "";
				}
				if (-1 != rp.indexOf('$')) rp = rp.replaceAll("\\$", "\\\\\\$");
				m.appendReplacement(sb, rp);
			}
		}catch(Exception ex){
			System.err.println("pos, j, k, data.data:");
			System.err.print(pos+", ");
			System.err.print(j+", ");
			System.err.print(k+", ");
			System.err.println(data.size());
			System.err.println(instance.getSource());
			String[] datai = ((String[]) data.get(pos+j));
			for(int i=0;i<datai.length;i++){
				System.err.println(datai[i]);
			}
			throw(ex);
		}
		m.appendTail(sb);
		int index = features.lookupIndex(sb.toString(), (int)Math.pow(numLabels, order+1));
		fv[this.id] = index;
	}
	public String toString() {
		return this.templet;
	}
	private   void  writeObject(java.io.ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	} 
	private   void  readObject(java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
		this.matcher = parser.matcher(this.templet);
	} 
}
