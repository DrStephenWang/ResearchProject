package edu.fudan.ml.solver;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.types.Instance;
public interface IMaxSolver{
	public Results getBest(Instance inst, int n);
	public void isUseTarget(boolean b);
}
