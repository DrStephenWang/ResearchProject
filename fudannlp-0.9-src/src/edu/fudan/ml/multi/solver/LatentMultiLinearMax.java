package edu.fudan.ml.multi.solver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.IMaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
import edu.fudan.ml.types.Tree;
public class LatentMultiLinearMax implements IMaxSolver,Serializable {
	private static final long serialVersionUID = 1729099580633672184L;
	private Alphabet alphabet;
	private Tree tree;
	int numThread;
	ExecutorService pool;
	private int numLatent;
	private SparseVector[] weights;
	private Generator featureGen;
	private int numClass;
	private int numLatentClass;
	Set<Integer> leafs =null;
	private boolean isUseTarget = true;
	public LatentMultiLinearMax(Generator featureGen, Alphabet alphabet, Tree tree,int n,int numLatent) {
		this.featureGen = featureGen;
		this.alphabet = alphabet;
		numThread = n;
		this.tree = tree;
		this.numLatent = numLatent;
		pool = Executors.newFixedThreadPool(numThread);
		numClass = alphabet.size();
		numLatentClass = numClass*numLatent;
		if(tree==null){
			leafs = alphabet.toSet();
		}else
			leafs= tree.getLeafs();
	}
	public Results getBest(Instance inst, int n) {
		Integer target = null;
		if(isUseTarget)
			target= (Integer) inst.getTarget();
		SparseVector fv = featureGen.getVector(inst);
		double[] sw = new double[numLatentClass];
		Multiplesolve[] c= new Multiplesolve [numLatentClass];
		Future<Double>[] f = new Future[numLatentClass];
		for (int i = 0; i < numLatentClass; i++) {
			c[i] = new Multiplesolve(fv,i);
			f[i] = pool.submit(c[i]);
		}
		for (int i = 0; i < numLatentClass; i++){ 			
			try {
				sw[i] = (Double) f[i].get();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		Results res = new Results(n);
		if(target!=null){
			res.buildOracle();
		}
		int[] maxlatent;
		if(tree==null){
			maxlatent = new int[1];
		}else{
			maxlatent = new int[tree.getDepth()];
		}
		int[] o = null;
		int[] l = null;
		Iterator<Integer> it = leafs.iterator();
		while(it.hasNext()){
			double score;
			Integer i = it.next();			
			if(tree!=null){
				score=0.0;
				ArrayList<Integer> anc = tree.getPath(i);
				for(int j=0;j<anc.size();j++){
					double max = Double.NEGATIVE_INFINITY;
					int start = anc.get(j)*numLatent;
					for(int k=start;k<start+numLatent;k++){
						if(sw[k]>max){
							max = sw[k];
							maxlatent[j] = k;
						}
					}
					score += max;
				}			
			}else{
				int start = i*numLatent;
				double max = Double.NEGATIVE_INFINITY;
				for(int k=start;k<start+numLatent;k++){
					if(sw[k]>max){
						max = sw[k];
						maxlatent[0] = k;
					}
				}
				score = max;
			}
			if(target!=null&&target.equals(i)){
				int idx = res.addOracle(score,i);
				if(idx==0)
					o = Arrays.copyOf(maxlatent, maxlatent.length);
			}else{
				int idx = res.addPred(score,i);
				if(idx==0)
					l = Arrays.copyOf(maxlatent, maxlatent.length);
			}
		}
		res.other = new Object[]{o,l};
		return res;
	}
	class Multiplesolve implements Callable<Double> {
		SparseVector fv;
		int idx;
		public  Multiplesolve(SparseVector fv,int i) {
			this.fv = fv;
			idx = i;
		}
		public Double call() {
			double score = fv.dotProduct(weights[idx]);
			return score;
		}
	}
	public void setWeight(SparseVector[] weights) {
		this.weights = weights;
	}
	@Override
	public void isUseTarget(boolean b) {
		isUseTarget = b;
	}
}
