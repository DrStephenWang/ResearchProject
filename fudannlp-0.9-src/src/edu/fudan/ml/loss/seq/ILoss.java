package edu.fudan.ml.loss.seq;
import java.util.List;
public interface ILoss {
	public double calc(List l1, List l2);
	double calc(int[] l1, int[] l2);
	double calc(String[] s1, String[] s2);
}
