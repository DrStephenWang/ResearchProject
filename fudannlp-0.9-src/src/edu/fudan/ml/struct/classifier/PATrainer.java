package edu.fudan.ml.struct.classifier;
import java.io.IOException;
import java.util.List;
import edu.fudan.ml.loss.seq.ILoss;
import edu.fudan.ml.struct.classifier.weight.UpdateStrategy;
import edu.fudan.ml.struct.solver.IMaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class PATrainer {
	private double[] weights;
	private IMaxSolver msolver;
	public UpdateStrategy update;
	private ILoss loss;
	private int maxIter = Integer.MAX_VALUE;
	private double eps = 1e-5;
	public int count;
	double c;
	boolean simpleOutput = false;
	boolean usePerceptron = true;
	public boolean interim = false;
	public PATrainer(IMaxSolver msolver, ILoss loss2,	int maxIter, Alphabet features, double c){
		this.msolver = msolver;
		this.loss = loss2;
		this.maxIter = maxIter;
		this.c=c;
		weights = new double[features.size()];
		this.msolver.setWeight(weights);
	}
	public Linear train(InstanceSet trainingList,InstanceSet testList) {
		int numSamples = trainingList.size();
		count = 0;
		for (int ii = 0; ii < trainingList.size(); ii++) {
			Instance inst = trainingList.getInstance(ii);
			count += ((int[]) inst.getTarget()).length;
		}
		System.out.println("Chars Number: " +count);				
		double oldErrorRate = Double.MAX_VALUE;
		long beginTime, endTime;
		long beginTimeIter, endTimeIter;
		beginTime = System.currentTimeMillis();
		double pE = 0;
		int iter = 0;
		int frac = numSamples/10;
		while (iter++ < maxIter) {
			if(!simpleOutput){
				System.out.print("iter:");
				System.out.print(iter+"\t");
			}
			double err = 0;
			double errorAll = 0;
			beginTimeIter = System.currentTimeMillis();
			int progress = frac;
			for (int ii = 0; ii < numSamples; ii++) {
				Instance inst = trainingList.getInstance(ii);
				List pred = (List) msolver.getBest(inst, 2);
				double l = loss.calc((int[])pred.get(0),(int[])inst.getTarget());
				if (l>0) {
					errorAll += 1.0;
					err += l;
					update.update(inst,weights, pred.get(0), c);
				} else {
					if(pred.size()>1)
						update.update(inst, weights, pred.get(1), c);
				}
				if(!simpleOutput&&ii%progress==0) {
					System.out.print('.');
					progress +=frac;
				}
			}
			double errRate = err/count;
			endTimeIter = System.currentTimeMillis();
			if(!simpleOutput){
				System.out.println("\ttime:"+(endTimeIter-beginTimeIter)/1000.0+"s");
				System.out.print("Train:");
				System.out.print("\tTag acc:");
			}
			System.out.print(1-errRate);
			if(!simpleOutput){
				System.out.print("\tSentence acc:");
				System.out.print(1-errorAll/numSamples);
				System.out.println();
			}
			if(testList!=null){
				test(testList);
			}
			if(Math.abs(errRate-oldErrorRate)<eps){
				System.out.println("Convergence!");
				break;
			}
			oldErrorRate = errRate;
			if(interim){
				Linear p = new Linear(weights,msolver,trainingList.getLabelAlphabet(),trainingList.getPipes());
				try {
					p.saveModel("tmp.model");
				} catch (IOException e) {
					System.err.println("write model error!");
				}
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
		if(!simpleOutput){
			System.out.print("Test:\t");
			System.out.print(total-err);
			System.out.print('/');
			System.out.print(total);
			System.out.print("\tTag acc:");
		}else{
			System.out.print('\t');
		}
		System.out.print(1-err/total);
		if(!simpleOutput){
			System.out.print("\tSentence acc:");
			System.out.println(1-errorAll/testSet.size());
		}
		System.out.println();
	}
}
