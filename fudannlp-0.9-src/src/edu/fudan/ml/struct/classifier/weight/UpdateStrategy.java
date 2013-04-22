package edu.fudan.ml.struct.classifier.weight;
import edu.fudan.ml.types.Instance;
public interface UpdateStrategy {
	public int update(Instance inst, double[] weights, Object predictLabel, double c);
	public int update(Instance inst, double[] weights, Object predictLabel, Object goldenLabel, double c);
}
