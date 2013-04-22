package edu.fudan.ml.multi.classifier;
import java.io.Serializable;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.IMaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
public class Linear implements Serializable {
	private static final long serialVersionUID = -1599891626397888670L;
	public Pipe pipes;
	SparseVector[] weight;
	private IMaxSolver msolver;
	private Generator gen;
	private Alphabet labelAlphabet;
	public Linear(SparseVector[] weights, IMaxSolver msolver) {	
		this.weight = weights;
		this.msolver = msolver;
		msolver.isUseTarget(false);
	}
	public Linear(SparseVector[] weights, IMaxSolver msolver, 
			Generator gen, Pipe pipes, Alphabet labelAlphabet) {
		this.weight = weights;
		this.pipes = pipes;
		this.gen = gen;
		gen.setStopIncrement(true);
		this.msolver = msolver;
		msolver.isUseTarget(false);
		this.labelAlphabet = labelAlphabet;
		labelAlphabet.setStopIncrement(true);
	}
	public int classify(Instance instance) {
		Results pred = msolver.getBest(instance, 1);		
		return pred.predList[0];
	}
	public int[] classify(InstanceSet instance) {
		int[] pred= new int[instance.size()];
		for(int i=0;i<instance.size();i++){
			pred[i]=  classify(instance.getInstance(i));			
		}
		return pred;
	}
	public String getLabel(Instance instance){
		Results pred = (Results) msolver.getBest(instance, 1);
		return (String) labelAlphabet.lookupString(pred.predList[0]);
	}
}
