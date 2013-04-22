package edu.fudan.ml.feature.generator;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class SimpleGenerator extends Generator{
	private static final long serialVersionUID = -7054038511042392649L;
	public SparseVector getVector(Instance inst) {
		return (SparseVector) inst.getData();
	}
	public SparseVector getVector(Instance inst, Object object)	{
		return null;
	}
}
