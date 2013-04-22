package edu.fudan.ml.classifier;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.ILoss;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public class PATrainer extends AverageTrainer {
	public PATrainer(MaxSolver msolver, Generator featureGen, ILoss loss,
			int maxIter, double eps) {
		super(msolver, featureGen, loss, maxIter, eps);
	}
	public PATrainer(Linear perceptron, ILoss loss, int maxIter, double eps) {
		super(perceptron, loss, maxIter, eps);
	}
	protected double update(Instance inst) {
		Results pred = msolver.getBest(inst, 1);
		double error = loss.calc(pred.predList[0], (Integer) inst.getTarget());
		if (error > 0) {
			SparseVector goldVector = generator.getVector(inst);
			SparseVector guessVector = generator.getVector(inst,
					pred.predList[0]);
			goldVector.minus(guessVector);
			double diff = error - tmpWeight.dotProduct(goldVector);
			diff = diff / goldVector.l2Norm2();
			if (!(Double.isNaN(diff) || Double.isInfinite(diff))) {
				goldVector.scalarMultiply(diff);
				tmpWeight.plus(goldVector);
			}
		}
		return error;
	}
}
