package edu.fudan.ml.struct.solver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.fudan.ml.feature.generator.templet.TemplateGroup;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.utils.MyArrays;
public class Viterbi implements IMaxSolver,Serializable {
	private static final long serialVersionUID = 6023318778006156804L;
	int numLabels;
	int numTemplets;
	double[] weights;
	private TemplateGroup templets;
	private int numStates;
	public Viterbi(Alphabet features, int numLabels, TemplateGroup templets) {
		this.numLabels = numLabels;
		this.templets = templets;
		this.templets.calc(numLabels);
		this.numTemplets = templets.size();
		numStates = templets.numStates;
	}
	public void setWeight(double[] weights) {
		this.weights = weights;
	}
	public List<int[]> getBest(Instance instance, int nbest) {
		int[][] data;
		int[] target;
		Node[][] lattice;
		data = (int[][]) instance.getData();
		lattice = new Node[data.length][templets.numStates];
		for(int ip=0; ip<data.length; ip++)
			for(int s=0; s<templets.numStates; s++)
				lattice[ip][s] = new Node(nbest);
		for(int ip=0; ip<data.length; ip++) {
			for(int s=0; s<numStates; s++) {
				for(int t=0; t<numTemplets; t++) {
					if (data[ip][t] == -1) continue;
					lattice[ip][s].weight += weights[data[ip][t]+ templets.offset[t][s]];
				}
			}
		}
		for(int s=0; s<numLabels; s++) {
			lattice[0][s].best[0] = lattice[0][s].weight;
		}
		double[] best = new double[nbest];
		int[] prev = new int[nbest];
		for(int ip=1; ip<data.length; ip++) {
			for(int s=0; s<numStates; s+=numLabels) {				
				Arrays.fill(best , Double.NEGATIVE_INFINITY);	
				for(int k=0; k<numLabels; k++) {
					int sp = (k*templets.numStates+s)/numLabels;
					for(int ibest=0; ibest<nbest; ibest++) {
						double b = lattice[ip-1][sp].best[ibest];
						MyArrays.addBest(best,prev,b,sp*nbest+ibest);
					}
				}
				for(int r=s; r<s+numLabels; r++) {
					for(int n=0;n<nbest;n++){
						lattice[ip][r].best[n] = best[n]+lattice[ip][r].weight;
						lattice[ip][r].prev[n] = prev[n];
					}
				}
			}
		}
		List<int[]> res = getPath(lattice, nbest);
		return res;
	}
	private List<int[]> getPath(Node[][] lattice, int nbest) {
		double best;
		Node lastNode = new Node(nbest);
		int last = lattice.length-1;
		for(int s=0; s<templets.numStates; s++) {
			for(int ibest=0; ibest<nbest; ibest++) {
				best = lattice[last][s].best[ibest];
				lastNode.addBest(best, s*nbest+ibest);
			}
		}
		List<int[]> res = new ArrayList<int[]>(nbest);
		for(int k=0; k<nbest; k++) {
			int[] phi = new int[lattice.length];
			int p = last;
			int s = lastNode.prev[k];
			for(int d=s/nbest, i=0; i<templets.maxOrder && p>=0; i++, p--) {
				phi[p] = d%numLabels;
				d = d/numLabels;
			}
			while(p>=0) {
				phi[p] = s/nbest/templets.base[templets.maxOrder];
				s = lattice[p+templets.maxOrder][s/nbest].prev[s%nbest];
				--p;
			}
			res.add(phi);
		}
		return res;
	}
	public final class Node implements Serializable {
		private static final long serialVersionUID = -2394261703451272214L;
		int n;
		double weight = 0.0;
		double[] best;
		int[] prev;
		public Node(int n){
			this.n = n;
			best = new double[n];
			prev = new int[n];
		}
		public int addBest(double score, int p) {
			int i;
			for(i=0;i<n;i++){
				if(score>best[i])
					break;
			}
			if(i>=n) return -1;
			for(int k=n-2;k>=i;k--){
				best[k+1] = best[k];
				prev[k+1] = prev[k];
			}
			best[i] = score;
			prev[i] = p;
			return i;
		}
		public String toString() {
			return String.format("%f %f %d", weight, best[0], prev[0]);
		}
	}
}
