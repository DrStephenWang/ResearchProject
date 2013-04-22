package edu.fudan.ml.classifier;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.ILoss;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.MaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
public class PerceptronTrainer implements Trainer {
	protected SparseVector weight;
	protected Linear classifier;
	protected MaxSolver msolver;
	protected Generator generator;
	protected ILoss loss;
	protected int maxIter = Integer.MAX_VALUE;
	protected double eps = 1e-10;
	protected SparseVector tmpWeight = null;
	public PerceptronTrainer(MaxSolver msolver, Generator featureGen,
			ILoss loss, int maxIter, double eps) {
		this.msolver = msolver;
		this.generator = featureGen;
		this.generator.setStopIncrement(false);
		this.loss = loss;
		this.maxIter = maxIter;
		this.eps = eps;
		this.weight = new SparseVector();
		tmpWeight = weight.clone();
	}
	public PerceptronTrainer(Linear model, ILoss loss, int maxIter,
			double eps) {
		this.msolver = model.msolver;
		this.generator = model.generator;
		this.generator.setStopIncrement(false);
		this.loss = loss;
		this.maxIter = maxIter;
		this.eps = eps;
		this.weight = model.weight;
		tmpWeight = weight.clone();
	}
	public Linear getClassifier() {
		return classifier;
	}
	public Linear train(InstanceSet trainingList) {
		System.out.println("总样本数：" + trainingList.size());
		int loops = 0;
		double oldErrate = Double.MAX_VALUE;
		msolver.setWeight(weight);
		System.out.println("开始训练");
		long beginTime = System.currentTimeMillis();
		for (; loops < maxIter; loops++) {
			System.out.printf("迭代：%d ", loops);
			long beginTimeInner = System.currentTimeMillis();
			double errate = doOneTraining(trainingList);
			long endTimeInner = System.currentTimeMillis();
			System.out.println();
			System.out.print(String.format("\t 累计错误率：%.8f", errate));
			System.out.print(String.format("\t w非零个数: %d", weight.size()));
			System.out.println(String.format("\t 时间(s): %.2f",
					(endTimeInner - beginTimeInner) / 1000.0));
			if (errate == 0
					|| Math.abs(oldErrate - errate) / oldErrate < eps)
				break;
			oldErrate = errate;
		}
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("结束训练\n总时间(s): %.2f\n",
				(endTime - beginTime) / 1000.0));
		classifier = new Linear(weight, msolver, generator, trainingList.getPipes(),
				trainingList.getLabelAlphabet());
		classifier.compact();
		return classifier;
	}
	protected double doOneTraining(InstanceSet trainingList) {
		double allerr = 0;
		trainingList.shuffle();
		int _count = trainingList.size();
		if (_count > 100)
			_count /= 100;
		for (int ii = 0; ii < trainingList.size();) {
			Instance inst = trainingList.getInstance(ii++);
			double error = update(inst);
			allerr += error;
			if (ii % _count == 0)
				System.out.print(".");
		}
		return (allerr / trainingList.size());
	}
	protected double update(Instance inst) {
		Results pred = (Results) msolver.getBest(inst, 1);
		double error = loss.calc(pred.predList[0], (Integer) inst
				.getTarget());
		if (error > 0) {
			SparseVector fvi = generator.getVector(inst);
			weight.plus(fvi);
			fvi = generator.getVector(inst, pred.predList[0]);
			weight.minus(fvi);
		}
		return error;
	}
}
