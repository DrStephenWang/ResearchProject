package edu.fudan.ml.solver;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class LinearMax extends MaxSolver implements Serializable {
	private static final long serialVersionUID = -7602321210007971450L;
	protected Alphabet alphabet;
	protected int numThread;
	private Generator featureGen;
	private boolean isUseTarget=true;
	public LinearMax(Generator featureGen, Alphabet alphabet, int n) {
		this.featureGen = featureGen;
		this.alphabet = alphabet;
		numThread = n;
	}
	public LinearMax(Generator featureGen, Alphabet alphabet, SparseVector weight, int n)	{
		this(featureGen, alphabet, n);
		this.weight = weight;
	}
	public Results getBest(Instance inst, int n) {
		Integer target = null;
		if (weight == null)
			weight = new SparseVector();
		Results res = new Results(n);
		if(target!=null){
			res.buildOracle();
		}
		for (int i = 0; i < alphabet.size(); i++) {
			SparseVector fv = featureGen.getVector(inst, i);
			double score = weight.dotProduct(fv);
			if (target != null && target == i)
				res.addOracle(score, i);
			else
				res.addPred(score, i);
		}
		return res;
	}
	@Override
	public void isUseTarget(boolean b) {
		isUseTarget = b;
	}
}
