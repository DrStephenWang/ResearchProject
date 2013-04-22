package edu.fudan.ml.multi.classifier;
import java.util.*;
import edu.fudan.ml.cluster.Kmeans;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.ILoss;
import edu.fudan.ml.multi.solver.LatentMultiLinearMax;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.results.Evaluation;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.IMaxSolver;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
import edu.fudan.ml.types.Tree;
import edu.fudan.nlp.tc.Mean;
public class LatentPATrainer {
	private SparseVector[] weights;
	private LatentMultiLinearMax msolver;
	private Generator featureGen;
	private ILoss loss;
	private int maxIter = Integer.MAX_VALUE;
	private double eps = 1e-10;
	private Tree tree;
	private double c;
	int numLatent = 2;
	public LatentPATrainer(IMaxSolver msolver,Generator featureGen,ILoss loss,
			int maxIter,double c,Tree tr,int numLatent){
		this.msolver =(LatentMultiLinearMax) msolver;
		this.featureGen =featureGen;
		this.loss = loss;
		this.maxIter = maxIter;
		tree = tr;
		this.c = c;
		this.numLatent = numLatent;
	}
	public Linear train(InstanceSet trainingList, Evaluation eval) {
		int numClass = trainingList.getLabelAlphabet().size();	
		int numSamples = trainingList.size();
		if(tree==null){
			return null;
		}
		int numNodeswithLatent = tree.size*numLatent;
		weights = new SparseVector[numNodeswithLatent];
		ArrayList<Instance>[] nodes = new ArrayList[tree.size];
		for(int i=0;i<nodes.length;i++){
			nodes[i] = new ArrayList<Instance>();
		}
		for(int i=0;i<trainingList.size();i++){
			ArrayList<Integer> anc = tree.getPath((Integer) trainingList.getInstance(i).getTarget());
			for(int j=0;j<anc.size();j++){
				nodes[anc.get(j)].add(trainingList.getInstance(i));
			}			
		}
		boolean way = true;
		if(way){
			SparseVector[] weights1 = Mean.mean(trainingList, tree);
			weights = new SparseVector[weights1.length*numLatent];
			for(int i=0;i<weights1.length;i++)
				for(int j=0;j<numLatent;j++)
					weights[i*numLatent+j] = weights1[i].clone();
		}else{
			for(int i=0;i<tree.size;i++){
				Kmeans km = new Kmeans(numLatent);
				km.cluster(nodes[tree.getNode(i)]);
				for(int j=0;j<numLatent;j++){
					weights[2*tree.getNode(i)+j] = km.centroids[j];
				}
			}
		}
		msolver.setWeight(weights);
		int loops = 0;
		double oldErrorRate = Double.MAX_VALUE;
		int frac = numSamples/10;
		System.out.println("Begin Training...");
		long beginTime = System.currentTimeMillis();
		while (loops++ < maxIter) {
			System.out.print("Loop:"+loops);
			double totalerror = 0.0;
			long beginTimeInner = System.currentTimeMillis();
			int progress = frac;
			for (int ii = 0; ii < numSamples; ii++) {
				Instance inst = trainingList.getInstance(ii);
				Integer maxEY;
				Integer maxCY;
				maxCY= (Integer) inst.getTarget();
				Results res = msolver.getBest(inst, 1);
				if(res==null){
					System.err.println("Error: " + ii);
					continue;
				}
				int error;
				maxEY = res.predList[0];
				error = tree.dist(maxEY,maxCY);
				double margin = res.oracleScore[0]-res.predScore[0];
				double loss = error - margin;
				Object[] maxlatent = (Object[]) res.other;
				int[] o = (int[]) maxlatent[0];
				int[] l = (int[]) maxlatent[1];
				if (loss>0) {
					totalerror +=1;
					double phi = featureGen.getVector(inst).l2Norm2();
					double alpha = Math.min(c, loss/(phi*error));
					ArrayList<Integer> anc = tree.getPath(maxCY);
					for(int j=0;j<anc.size();j++){
						weights[o[j]].plus(featureGen.getVector(inst),alpha);
					}
					anc = tree.getPath(maxEY);
					for(int j=0;j<anc.size();j++){
						weights[l[j]].plus(featureGen.getVector(inst),-alpha);
					}
				}
				if(ii%progress==0) {
					System.out.print('.');
					progress +=frac;
				}
			}
			double acc = 1- totalerror/numSamples;
			System.out.print("\t Train Accuracy:"+acc);
			System.out.println("\t Train Time(s):"+(System.currentTimeMillis()-beginTimeInner)/1000);
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
		Linear classifier = new Linear(weights, msolver, featureGen, dataPipe,trainingList.getLabelAlphabet());
		return classifier;
	}
}
