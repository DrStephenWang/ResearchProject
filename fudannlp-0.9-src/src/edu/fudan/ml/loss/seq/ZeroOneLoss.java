package edu.fudan.ml.loss.seq;
import java.util.List;
public class ZeroOneLoss implements ILoss {
	public double calc(List l1, List l2) {
		boolean eq = true;
		for(int i=0; i<l1.size(); i++) {
			if (!l1.get(i).equals(l2.get(i))){
				eq = false;
				break;
			}
		}
		return eq?0:1;
	}
	public double calc(int[] l1, int[] l2) {
		boolean eq = true;
		for(int i=0; i<l1.length; i++) {
			if (l1[i] != l2[i]){
				eq = false;
				break;
			}
		}
		return eq?0:1;
	}
	@Override
	public double calc(String[] s1, String[] s2) {
		return 0;
	}
}
