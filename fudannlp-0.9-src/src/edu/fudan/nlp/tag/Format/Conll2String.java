package edu.fudan.nlp.tag.Format;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import edu.fudan.ml.data.SequenceReader;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.pipe.SeriesPipes;
import edu.fudan.ml.pipe.SplitDataAndTarget;
import edu.fudan.ml.pipe.StringTokenizer;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class Conll2String {
	public static void main(String[] args) throws Exception {
		convert("D:/Datasets/sighan2005/output/as.conll","D:/Datasets/sighan2005/output/as-pa");
	}
	public static void convert(String input, String output) throws Exception{
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(output), "utf8");
		Alphabet labels = new Alphabet();
		Pipe prePipe = new SeriesPipes(new Pipe[] {
				new StringTokenizer(),
				new SplitDataAndTarget()});
		InstanceSet set = new InstanceSet(prePipe);
		set.loadThruStagePipes(new SequenceReader(input, "utf8"));
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<set.size(); i++) {
			Instance inst = set.getInstance(i);
			List t = (List) inst.getTarget();
			String[] lab = (String[]) t.toArray(new String[t.size()]);
			List<String> data = (List<String>) inst.getSource();
			sb.append(Seq2String.format(inst, lab));
			sb.append("\n");
		}
		w.write(sb.toString());
		System.out.print(sb);
	}
}
