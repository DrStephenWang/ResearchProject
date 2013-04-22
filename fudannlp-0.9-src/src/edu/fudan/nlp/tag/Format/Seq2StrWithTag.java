package edu.fudan.nlp.tag.Format;
import java.util.List;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class Seq2StrWithTag {
	public static String format(InstanceSet testSet,String[][] labelsSet){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<testSet.size(); i++) {
			Instance inst = testSet.getInstance(i);
			String[] labels = labelsSet[i];
			List<String> data = (List<String>) inst.getSource();
			sb.append(format(inst, labels));
		}
		return sb.toString();
	}
	public static String format(Instance inst, String[] labels){
		List<String> data = (List<String>) inst.getSource();
		StringBuilder sb = new StringBuilder();
		for(int j=0; j<data.size(); j++) {
			String label = labels[j];
			int tagidx = label.indexOf("-");
			String tag = label.substring(tagidx+1);
			label = label.substring(0,tagidx);
			int ind = data.get(j).indexOf('\t');
			ind = ind>0?ind:data.get(j).length();
			String w = data.get(j).substring(0,ind);
			sb.append(w);			
			if(label.equals("E") || label.equals("S")) {
			sb.append("/"+tag+" ");
			}
		}
		return sb.toString();
	}
}
