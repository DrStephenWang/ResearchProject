package edu.fudan.ml.multi.classifier;
import java.util.ArrayList;
import java.util.HashSet;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.ILoss;
import edu.fudan.ml.multi.solver.MultiLinearMax;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.results.Evaluation;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.IMaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
import edu.fudan.ml.types.Tree;
import edu.fudan.nlp.tc.Mean;
public class PATrainer {
	private SparseVector[] weights;
	private Linear classifier;
	private MultiLinearMax msolver;
	private Generator featureGen;
	private ILoss loss;
	private int maxIter = Integer.MAX_VALUE;
	private double eps = 1e-10;
	private Tree tree;
	private double c;
	public PATrainer(IMaxSolver msolver,Generator featureGen,ILoss loss,
			int maxIter,double c,Tree tr){
		this.msolver = (MultiLinearMax) msolver;
		this.featureGen =featureGen;
		this.loss = loss;
		this.maxIter = maxIter;
		tree = tr;
		this.c = c;
	}
	public Linear getClassifier() {
		return classifier;
	}
	public Linear train(InstanceSet trainingList, Evaluation eval) {
		System.out.println("Sample Size: "+trainingList.size());
		int numClass = trainingList.getLabelAlphabet().size();
		weights = Mean.mean(trainingList, tree);
		msolver.setWeight(weights);
		int loops = 0;
		double oldErrorRate = Double.MAX_VALUE;
		int numSamples = trainingList.size();
		int frac = numSamples/10;
		System.out.println("Begin Training...");
		long beginTime = System.currentTimeMillis();
		while (loops++ < maxIter) {
			System.out.print("Loop: "+loops);
			double totalerror = 0.0;
			trainingList.shuffle();
			long beginTimeInner = System.currentTimeMillis();
			int progress = frac;
			for (int ii = 0; ii < numSamples; ii++) {
				Instance inst = trainingList.getInstance(ii);
				Integer maxE;
				Integer maxC = (Integer) inst.getTarget();
				HashSet<Integer> t = new HashSet<Integer>();
				t.add(maxC);
				Results pred = (Results) msolver.getBest(inst, 1);
				maxE = pred.predList[0];
				int error;
				if(tree==null){
					error = ((Integer) pred.predList[0]==maxC)?0:1;
				}else{
					error = tree.dist(maxE,maxC);
				}
				double loss = error - (pred.oracleScore[0] - ((Double) pred.predScore[0]));
				if (loss>0) {
					totalerror +=1;
					double phi = featureGen.getVector(inst).l2Norm2();
					double alpha = Math.min(c, loss/(phi*error));
					if(tree!=null){
						ArrayList<Integer> anc = tree.getPath(maxC);
						for(int j=0;j<anc.size();j++){
							weights[anc.get(j)].plus(featureGen.getVector(inst),alpha);
						}
						anc = tree.getPath(maxE);
						for(int j=0;j<anc.size();j++){
							weights[anc.get(j)].plus(featureGen.getVector(inst),-alpha);
						}
					}else{
						weights[maxC].plus(featureGen.getVector(inst), alpha);
						weights[maxE].plus(featureGen.getVector(inst), -alpha);
					}
				}
				if(ii%progress==0) {
					System.out.print('.');
					progress +=frac;
				}
			}
			double acc = 1- totalerror/numSamples;
			System.out.print("\t Accuracy:"+acc);
			System.out.println("\t Time(s):"+(System.currentTimeMillis()-beginTimeInner)/1000);
			if(eval!=null){
				System.out.println("Test:");
				Linear classifier = new Linear(weights, msolver);
				eval.eval(classifier);
				msolver.isUseTarget(true);
			}
			if(acc ==1 && Math.abs(oldErrorRate-acc)/oldErrorRate<eps)
				break;
		}
		System.out.println("Training End");
		System.out.println("Training Time(s):"+(System.currentTimeMillis()-beginTime)/1000);
		Pipe dataPipe = trainingList.getPipes();
		classifier = new Linear(weights, msolver, featureGen, dataPipe,trainingList.getLabelAlphabet());
		return classifier;
	}
}
