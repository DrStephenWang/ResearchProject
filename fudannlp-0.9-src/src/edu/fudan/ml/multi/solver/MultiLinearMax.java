package edu.fudan.ml.multi.solver;
import java.io.Serializable;
import java.util.ArrayList;
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
public class MultiLinearMax implements IMaxSolver,Serializable {
	private static final long serialVersionUID = 460812009958228912L;
	private Alphabet alphabet;
	private Tree tree;
	int numThread;
	ExecutorService pool;
	private SparseVector[] weights;
	private Generator featureGen;
	private int numClass;
	Set<Integer> leafs =null;
	private boolean isUseTarget = true;
	public MultiLinearMax(Generator featureGen, Alphabet alphabet, Tree tree,int n) {
		this.featureGen = featureGen;
		this.alphabet = alphabet;
		numThread = n;
		this.tree = tree;
		pool = Executors.newFixedThreadPool(numThread);
		numClass = alphabet.size();
		if(tree==null){
			leafs = alphabet.toSet();
		}else
			leafs= tree.getLeafs();
	}
	public Results getBest(Instance inst, int n) {
		Integer target =null;
		if(isUseTarget)
			target = (Integer) inst.getTarget();
		SparseVector fv = featureGen.getVector(inst);
		double[] sw = new double[alphabet.size()];
		Callable<Double>[] c= new Multiplesolve[numClass];
		Future<Double>[] f = new Future[numClass];
		for (int i = 0; i < numClass; i++) {
			c[i] = new Multiplesolve(fv,i);
			f[i] = pool.submit(c[i]);
		}
		for (int i = 0; i < numClass; i++){ 			
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
		Iterator<Integer> it = leafs.iterator();
		while(it.hasNext()){
			double score=0.0;
			Integer i = it.next();
			if(tree!=null){
				ArrayList<Integer> anc = tree.getPath(i);
				for(int j=0;j<anc.size();j++){
					score += sw[anc.get(j)];
				}
			}else{
				score = sw[i];
			}
			if(target!=null&&target.equals(i)){
				res.addOracle(score,i);
			}else{
				res.addPred(score,i);
			}
		}		
		return res;
	}
	class Multiplesolve implements Callable {
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
