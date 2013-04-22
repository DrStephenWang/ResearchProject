package edu.fudan.ml.struct.solver;
import edu.fudan.ml.types.Instance;
public interface IMaxSolver{
	public Object getBest(Instance inst, int n);
	public void setWeight(double[] weights);
}
