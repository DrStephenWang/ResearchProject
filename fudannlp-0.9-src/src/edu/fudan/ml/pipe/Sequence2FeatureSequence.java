package edu.fudan.ml.pipe;
import java.util.Arrays;
import java.util.List;
import edu.fudan.ml.feature.generator.templet.Templet;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class Sequence2FeatureSequence extends Pipe{
	private static final long serialVersionUID = -6481304918657094682L;
	List<Templet> templets;
	public Alphabet features;
	Alphabet labels;
	public Sequence2FeatureSequence(List<Templet> templets,
			Alphabet features, Alphabet labels) {
		this.templets = templets;
		this.features = features;
		this.labels = labels;
	}
	public void addThruPipe(Instance instance) throws Exception {
		List data = (List) instance.getData();
		int[][] newData = new int[data.size()][templets.size()];
		for (int i=0; i<data.size(); i++) {
			Arrays.fill(newData[i], -1);
			for(int j=0; j<templets.size(); j++) {
				templets.get(j).generateAt(instance, i, newData[i], this.features, labels.size());
			}
		}
		instance.setData(newData);
	}
}
