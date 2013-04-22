package edu.fudan.ml.struct.classifier;
import java.util.List;
import edu.fudan.ml.loss.seq.ILoss;
import edu.fudan.ml.struct.classifier.weight.UpdateStrategy;
import edu.fudan.ml.struct.solver.IMaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class SemiPATrainer {
	private double[] weights;
	private IMaxSolver msolver;
	public UpdateStrategy strategy;
	private ILoss loss;
	private int maxIter = Integer.MAX_VALUE;
	private double eps = 1e-5;
	public int count;
	double c;
	private double c2;
	public SemiPATrainer(IMaxSolver msolver, ILoss loss,	int maxIter, Alphabet features, double c, double c2){
		this.msolver =msolver;
		this.loss = loss;
		this.maxIter = maxIter;
		this.c=c;
		this.c2 =c2;
		weights = new double[features.size()];
		this.msolver.setWeight(weights);
	}
	public Linear train(InstanceSet trainingList,InstanceSet testList, InstanceSet unlabelSet) {
		int numSamples = trainingList.size();
		count = 0;
		for (int ii = 0; ii < trainingList.size(); ii++) {
			Instance inst = trainingList.getInstance(ii);
			count += ((int[]) inst.getTarget()).length;
		}
		System.out.println("Training Size: "+trainingList.size());	// 样本总数
		//		System.out.println("Label Number: " +trainingList.getLabelAlphabet().size());		// label个数
		//		System.out.println("Feature Number: "+ trainingList.getPipes().size());	// 特征总数
		System.out.println("Chars Number: " +count);				
		int iter = 0;
		double oldErrorRate = Double.MAX_VALUE;
		long beginTime, endTime;
		long beginTimeIter, endTimeIter;
		beginTime = System.currentTimeMillis();
		double pE = 0;
		while (iter++ < maxIter) {
			double err = 0;
			double errorAll = 0;
			beginTimeIter = System.currentTimeMillis();
			for (int ii = 0; ii < trainingList.size(); ii++) {
				Instance inst = trainingList.getInstance(ii);
				List pred = (List) msolver.getBest(inst, 2);
				double l = loss.calc((int[]) pred.get(0),(int[]) inst.getTarget());
				if (l>0) {
					errorAll += 1.0;
					err += l;
					strategy.update(inst,weights, pred.get(0), c);
				} else {
					if(pred.size()>1)
						strategy.update(inst, weights, pred.get(1), c);
				}
				if((ii+1)%100==0) System.out.print('.');
			}
			double errRate = err/count;
			if(Math.abs(errRate-oldErrorRate)<eps)
				break;
			oldErrorRate = errRate;
			System.out.print("\niter:");
			System.out.print(iter);
			endTimeIter = System.currentTimeMillis();
			System.out.println("\ttime:"+(endTimeIter-beginTimeIter)/1000.0+"s");
			System.out.print("Train:");
			System.out.print("\tTag acc:");
			System.out.print(1-errRate);
			System.out.print("\tSentence acc:");
			System.out.print(1-errorAll/numSamples);
			System.out.println();
			if(testList!=null){
				test(testList);
			}
		}
		endTime = System.currentTimeMillis();
		System.out.println("done!");
		System.out.println("time escape:"+(endTime-beginTime)/1000.0+"s");
		Linear p = new Linear(weights,msolver,trainingList.getLabelAlphabet(),trainingList.getPipes());
		return p;
	}
	public void test(InstanceSet testSet) {
		double err = 0;
		double errorAll = 0;
		int total = 0;
		for(int i=0; i<testSet.size(); i++) {
			Instance inst = testSet.getInstance(i);
			total+=((int[]) inst.getTarget()).length;
			List pred = (List) msolver.getBest(inst, 1);
			double l = loss.calc((int[]) pred.get(0),(int[]) inst.getTarget());
			if (l>0) {
				errorAll += 1.0;
				err += l;
			}
		}
		System.out.print("Test:\t");
		System.out.print(total-err);
		System.out.print('/');
		System.out.print(total);
		System.out.print("\tTag acc:");
		System.out.print(1-err/total);
		System.out.print("\tSentence acc:");
		System.out.println(1-errorAll/testSet.size());
		System.out.println();
	}
}
