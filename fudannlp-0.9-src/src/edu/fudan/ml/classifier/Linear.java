package edu.fudan.ml.classifier;
import java.io.Serializable;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.IMaxSolver;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class Linear implements Classifier, Serializable {
	private static final long serialVersionUID = 289199706361022589L;
	protected Pipe pipes;
	protected SparseVector weight;
	protected MaxSolver msolver;
	public Generator generator;
	protected Alphabet labelAlphabet;
	public Linear() {
	}
	public Linear(SparseVector sv, MaxSolver msolver, Generator generator,
			Pipe pipes, Alphabet labelAlphabet) {
		this.pipes = pipes;
		this.weight = sv;
		this.generator = generator;
		this.generator.setStopIncrement(true);
		this.msolver = msolver;
		this.labelAlphabet = labelAlphabet;
		if (labelAlphabet != null)
			labelAlphabet.setStopIncrement(true);
	}
	public Results classify(Instance instance) {
		Results pred = msolver.getBest(instance, 1);
		return pred;
	}
	public Results classify(Instance instance, int n) {
		return msolver.getBest(instance, n);
	}
	public String getLabel(Instance instance) {
		Results pred = (Results) msolver.getBest(instance, 1);
		return labelAlphabet.lookupString((Integer) pred.predList[0]);
	}
	public Alphabet getLabelAlphabet() {
		return labelAlphabet;
	}
	public IMaxSolver getSolver() {
		return msolver;
	}
	public SparseVector getWeight() {
		return weight;
	}
	public void compact()	{
		int[] ids = weight.indices();
		for(int i = 0; i < ids.length; i++)	{
			double v = weight.elementAt(ids[i]);
			if (v == 0)	{
				weight.remove(ids[i]);
			}
		}
		weight.trim();
	}
}
