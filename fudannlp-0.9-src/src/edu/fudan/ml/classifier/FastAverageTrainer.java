package edu.fudan.ml.classifier;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.ILoss;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class FastAverageTrainer extends AverageTrainer {
	public FastAverageTrainer(Linear perceptron, ILoss loss, int maxIter,
			double eps) {
		super(perceptron, loss, maxIter, eps);
	}
	public FastAverageTrainer(MaxSolver msolver, Generator generator, ILoss loss,
			int maxIter, double eps)	{
		super(msolver, generator, loss, maxIter, eps);
	}
	protected double doTraining(InstanceSet instset)	{
		double allerr = 0;
		instset.shuffle();
		int _count = instset.size();
		if (_count > 100)
			_count /= 100;
		for (int ii = 0; ii < instset.size();) {
			Instance inst = instset.getInstance(ii++);
			double error = update(inst);
			allerr += error;
			if (ii % _count == 0)
				System.out.print(".");
		}
		double errate = (allerr / instset.size());
		weight.plus(tmpWeight);
		return errate;
	}
}
