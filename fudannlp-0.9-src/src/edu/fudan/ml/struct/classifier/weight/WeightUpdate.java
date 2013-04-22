package edu.fudan.ml.struct.classifier.weight;
import java.io.Serializable;
import java.util.List;
import sun.awt.image.IntegerInterleavedRaster;
import edu.fudan.ml.feature.generator.templet.TemplateGroup;
import edu.fudan.ml.feature.generator.templet.Templet;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntIntIterator;
public class WeightUpdate implements Serializable, UpdateStrategy{
	private static final long serialVersionUID = 4198902147638417425L;
	TemplateGroup templets;
	int numTemplets;
	int numLabels;
	private boolean useLoss=false;
	public WeightUpdate(TemplateGroup templets, int numLabels, boolean useLoss2) {
		this.templets = templets;
		this.numTemplets = templets.size();
		this.numLabels = numLabels;
		this.useLoss = useLoss2;
	}
	public int update(Instance inst, double[] weights, Object predictLabel, double c) {
		return update(inst,weights,predictLabel,null, c);
	}
	public int update(Instance inst, double[] weights, Object predictLabel, Object goldenLabel, double c) {
		int[][] data = (int[][])inst.getData();
		int[] target;
		if(goldenLabel==null)
			target= (int[]) inst.getTarget();
		else
			target = (int[]) goldenLabel;
		int[] predict = (int[]) predictLabel;
		int ne=0;
		int tS=0, pS=0;
		double diffW = 0;
		int loss = 0;
		int L = data.length;
		TIntIntHashMap diffF = new  TIntIntHashMap();
		for(int o=-templets.maxOrder-1, l=0; l<L; o++, l++) {
			tS = tS*numLabels%templets.numStates+target[l];
			pS = pS*numLabels%templets.numStates+predict[l];
			if(predict[l] != target[l]) ne++;
			if(o>=0 && (predict[o] != target[o])) ne--;
			if(ne>0) {
				loss++;
				for(int t=0; t<numTemplets; t++) {
					if(data[l][t] == -1) continue;
					int tI = data[l][t]+templets.offset[t][tS];
					int pI = data[l][t]+templets.offset[t][pS];
					if(tI != pI) { 
						diffF.adjustOrPutValue(tI, 1, 1);
						diffF.adjustOrPutValue(pI, -1, -1);						
						diffW += weights[tI] - weights[pI];
					}
				}
			}
		}
		double diff = 0;
		TIntIntIterator it = diffF.iterator();
		for (int i = diffF.size(); i-- > 0;) {
			it.advance();
			diff += it.value() * it.value();
		}
		it = null;
		double alpha;
		double delta;
		if(useLoss){
			delta = loss;
		}else
			delta = 1;
		if(diffW <= delta) {
			tS = 0; pS = 0;
			ne = 0;
			alpha = (delta - diffW) / diff;
			alpha = Math.min(c, alpha); 
			it = diffF.iterator();
			for (int i = diffF.size(); i-- > 0;) {
				it.advance();
				weights[it.key()] += it.value()*alpha;
			}
			return 1;
		} else {
			return 0;
		}
	}
}
