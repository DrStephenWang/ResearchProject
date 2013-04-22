package edu.fudan.ml.solver;
import java.io.Serializable;
import edu.fudan.ml.types.SparseVector;
public abstract class MaxSolver implements IMaxSolver	{
	protected SparseVector weight;
	public void setWeight(SparseVector weight)	{
		this.weight = weight;
	}
}
