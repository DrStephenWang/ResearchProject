package edu.fudan.ml.struct.solver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.fudan.ml.feature.generator.templet.TemplateGroup;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.utils.MyArrays;
public class Labelwise implements IMaxSolver,Serializable {
	private static final long serialVersionUID = 504088074088644518L;
	int numLabels;
	int numTemplets;
	double[] weights;
	private TemplateGroup templets;
	public Labelwise(Alphabet features, int numLabels, TemplateGroup templets) {
		this.numLabels = numLabels;
		this.templets = templets;
		this.templets.calc(numLabels);
		this.numTemplets = templets.size();
	}
	public void setWeight(double[] weights) {
		this.weights = weights;
	}
	public List<int[]> getBest(Instance instance, int nbest) {
		int[][] data;
		int[] target;
		data = (int[][]) instance.getData();
		int length = data.length;
		double[][] lattice = new double[data.length][numLabels];
		int[][][] maxState = new int[length][numLabels][2];
		for(int ip=0; ip<data.length; ip++) {
			double[] score0 = new double[numLabels];
			double[] maxscore1 = new double[numLabels];
			for(int s=0; s<templets.numStates; s++) {
				int label = s%numLabels;
				int label1 = s/numLabels%numLabels;
				for(int t=0; t<numTemplets; t++) {
					if (data[ip][t] == -1) continue;
					int ord = templets.get(t).getOrder();
					if(ord==0){
						score0[label] += weights[data[ip][t]+ templets.offset[t][s]];
					}else {
						double sc = weights[data[ip][t]+ templets.offset[t][s]];
						if(sc>maxscore1[label]){
							maxscore1[label] = sc;
							maxState[ip][label][0] = s;
						}
						if(ip<length-ord-1){
							if(data[ip+1][t] == -1) continue;
							sc = weights[data[ip+1][t]+ templets.offset[t][s]];
							if(sc>maxscore1[label1]){
								maxscore1[label1] = sc;
								maxState[ip+1][label][1] = s;
							}
						}
					}
				}
			}
			for(int i=0;i<numLabels;i++){
				lattice[ip][i] = score0[i]+ maxscore1[i];
			}
		}
		instance.setTempData(maxState);
		List<int[]> res = getPath(lattice, nbest);
		return res;
	}
	private List<int[]> getPath(double[][] lattice, int nbest) {
		List<int[]> res = new ArrayList<int[]>(nbest);
		double[][] score = new double[lattice.length][nbest];
		int[][] list = new int[lattice.length][nbest];
		for(int i=0;i<lattice.length;i++){
			for(int j=0;j<numLabels;j++){
				MyArrays.addBest(score[i], list[i], lattice[i][j], j);
			}
		}
		for(int k=0; k<nbest; k++) {
			int[] phi = new int[lattice.length];
			for(int i=0;i<lattice.length;i++){
				phi[i] = list[i][k];
			}
			res.add(phi);
		}
		return res;
	}
}
