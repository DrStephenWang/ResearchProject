package edu.fudan.ml.struct.classifier.weight;
import java.io.Serializable;
import java.util.List;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import edu.fudan.ml.feature.generator.templet.Templet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntIntIterator;
public class LabelwiseWeightUpdate implements Serializable, UpdateStrategy{
	private static final long serialVersionUID = 3869857577612720364L;
	List<Templet> templets;
	int numTemplets;
	int numLabels;
	int[] orders;
	int maxOrder;
	int[] base;
	int numStates;
	int[][] offset;
	private double delta=1.0;
	public LabelwiseWeightUpdate(List<Templet> templets, int numLabels) {
		this.templets = templets;
		this.numTemplets = templets.size();
		this.numLabels = numLabels;
		this.orders = new int[numTemplets];
		for(int j=0; j<numTemplets; j++) {
			Templet t = (Templet) templets.get(j);
			this.orders[j] = t.getOrder();
			if (orders[j] > maxOrder)
				maxOrder = orders[j];
		}
		base = new int[maxOrder+2];
		base[0]=1;
		for(int i=1; i<base.length; i++) {
			base[i]=base[i-1]*numLabels;
		}
		this.numStates = (int) Math.pow(numLabels, maxOrder+1);
		offset = new int[numTemplets][numStates];
		for(int t=0; t<numTemplets; t++) {
			Templet tpl = templets.get(t);
			int[] vars = tpl.getVars();
			int[] bits = new int[maxOrder+1];
			int v;
			for(int s=0; s<numStates; s++) {
				int d = s;
				for(int i=0; i<maxOrder+1; i++) {
					bits[i] = d%numLabels;
					d = d/numLabels;
				}
				v = 0;									
				for(int i=0; i<vars.length; i++) {
					v = v*numLabels + bits[-vars[i]];
				}
				offset[t][s] = v;
			}
		}
	}
	public int update(Instance inst, double[] weights, Object predictLabel, double c) {
		return update(inst,weights,predictLabel,null, c);
	}
	public int update(Instance inst, double[] weights, Object predictLabel, Object goldenLabel, double c1) {
		int[][] data = (int[][])inst.getData();
		int[] target;
		if(goldenLabel==null)
			target= (int[]) inst.getTarget();
		else
			target = (int[]) goldenLabel;
		int[] predict = (int[]) predictLabel;
		int ne=0;
		int tS=0, pS=0;
		int L = data.length;
		double[] diff = new double[L];
		double[] diffW = new double[L];
		TIntIntHashMap[] diffF = new TIntIntHashMap[L]; 
		for(int i=0;i<L;i++){
			diffF[i] = new TIntIntHashMap();
		}
		int[][][] state = (int[][][]) inst.getTempData();
		TIntArrayList idx = new TIntArrayList();
		for(int o=-maxOrder-1, l=0; l<L; o++, l++) {
			tS = tS*numLabels%numStates+target[l];
			pS = pS*numLabels%numStates+predict[l];
			int lpS = state[l][predict[l]][0];
			int rpS = state[l][predict[l]][1];
			if(predict[l] != target[l]) ne++;
			if(o>=0 && (predict[o] != target[o])) ne--;
			if(ne>0) {
				idx.add(l);
				for(int t=0; t<numTemplets; t++) {
					if(data[l][t] == -1) continue;
					int tI = data[l][t]+offset[t][tS];
					int pI = data[l][t]+offset[t][pS];
					if(tI != pI) {
						diffF[l].adjustOrPutValue(tI, 1, 1);
						diffF[l].adjustOrPutValue(pI, -1, -1);
						diffW[l] += weights[tI]*2 - weights[pI];
					}
					if(templets.get(t).getOrder()>0){
						int lpI = data[l][t]+offset[t][lpS];
						diffW[l] -=  - weights[lpI];
						diffF[l].adjustOrPutValue(lpI, -1, -1);							
						if(l<L-1){
							int rpI = data[l+1][t]+offset[t][rpS];
							diffW[l+1] -=  - weights[lpI];
							diffF[l+1].adjustOrPutValue(rpI, -1, -1);	
						}
					}
				}
			}
		}
		int det = idx.size();
		double[] x = null;
		boolean useMatrix = false;
		if(!useMatrix ){
			for(int l=0;l<L;l++){
				TIntIntIterator it = diffF[l].iterator();
				for (int i = diffF[l].size(); i-- > 0;) {
					it.advance();
					diff[l] += it.value() * it.value();
				}
			}
		}else{
			Matrix m1 = new Matrix(det, det);
			Matrix B = new Matrix(det,1);
			for(int r=0; r<det; r++) {
				int realRow = idx.get(r);
				B.set(r, 0, 1-diffW[realRow]);
				for(int c=r; c<det; c++) {
					int realColumn = idx.get(c);
					double dot = SparseVector.dotProduct(diffF[realRow], diffF[realColumn]);
					m1.set(r, c, dot);
					m1.set(c, r, dot);
				}
			}
			SingularValueDecomposition de = new SingularValueDecomposition(m1);
			Matrix S = de.getS();
			Matrix U = de.getU();
			Matrix V = de.getV();
			int R = de.rank();
			S = S.getMatrix(0, R-1, 0, R-1);
			U = U.getMatrix(0, det-1, 0, R-1);
			V = V.getMatrix(0, det-1, 0, R-1);
			Matrix xx = V.times(S.inverse()).times(U.transpose()).times(B);
			x = xx.getColumnPackedCopy();
		}
		tS = 0; pS = 0;
		ne = 0;
		for(int r=0;r<det;r++){
			int l = idx.get(r);
			double loss = diffW[l]>delta?0:delta-diffW[l];
			if(diff[l]==0||loss==0)
				continue;
			double alpha;
			if(useMatrix){
				alpha = x[r];
			}else{
				alpha = loss/diff[l];
			}
			if(Double.isNaN(alpha))
				continue;
			alpha = Math.min(c1, alpha);
			TIntIntIterator it = diffF[l].iterator();
			for (int i = diffF[l].size(); i-- > 0;) {
				it.advance();
				weights[it.key()] += it.value()*alpha;
			}
		}
		return 0;
	}
}
