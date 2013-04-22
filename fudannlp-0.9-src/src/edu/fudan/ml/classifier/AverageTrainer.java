package edu.fudan.ml.classifier;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.ILoss;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
public class AverageTrainer extends PerceptronTrainer {
	public AverageTrainer(MaxSolver msolver, Generator featureGen, ILoss loss,
			int maxIter, double eps) {
		super(msolver, featureGen, loss, maxIter, eps);
	}
	public AverageTrainer(Linear perceptron, ILoss loss, int maxIter, double eps) {
		super(perceptron, loss, maxIter, eps);
	}
	protected double doTraining(InstanceSet instset)	{
		double allerr = 0;
		instset.shuffle();
		int _count = instset.size();
		if (_count > 100)
			_count /= 100;
		SparseVector vector = new SparseVector();
		for (int ii = 0; ii < instset.size();) {
			Instance inst = instset.getInstance(ii++);
			double error = update(inst);
			allerr += error;
			vector.plus(tmpWeight);
			if (ii % _count == 0)
				System.out.print(".");
		}
		double errate = (allerr / instset.size());
		vector.scalarDivide(instset.size());
		weight.plus(vector);
		return errate;
	}
	protected double update(Instance inst)	{
		Results pred = msolver.getBest(inst, 1);
		double error = loss.calc(pred.predList[0], (Integer) inst
				.getTarget());
		if (error > 0) {
			SparseVector fvi = generator.getVector(inst);
			tmpWeight.plus(fvi);
			fvi = generator.getVector(inst, pred.predList[0]);
			tmpWeight.minus(fvi);
		}
		return error;
	}
	public Linear train(InstanceSet trainingList) {
		System.out.println("总样本数：" + trainingList.size());
		int loops = 0;
		double oldErrate = Double.MAX_VALUE;
		msolver.setWeight(tmpWeight);
		System.out.println("开始训练");
		long beginTime = System.currentTimeMillis();
		for (; loops < maxIter; loops++) {
			System.out.printf("迭代：%d ", loops);
			long beginTimeInner = System.currentTimeMillis();
			double errate = doTraining(trainingList);
			long endTimeInner = System.currentTimeMillis();
			System.out.println();
			System.out.print(String.format("\t 累计错误率：%.8f", errate));
			System.out.print(String.format("\t 非零参数个数: %d", tmpWeight.size()));
			System.out.println(String.format("\t 时间(s): %.2f",
					(endTimeInner - beginTimeInner) / 1000.0));
			if (errate == 0
					|| Math.abs(oldErrate - errate) / oldErrate < eps)
				break;
			oldErrate = errate;
		}
		weight.scalarDivide(loops);
		msolver.setWeight(weight);
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("结束训练\n总时间(s): %.2f\n",
				(endTime - beginTime) / 1000.0));
		classifier = new Linear(weight, msolver, generator, trainingList.getPipes(),
				trainingList.getLabelAlphabet());
		classifier.compact();
		return classifier;
	}
}
