package edu.fudan.ml.loss;
public class ZeroOneLoss implements ILoss {
	@Override
	public double calc(int i1, int i2) {
		return i1==i2?0:1;
	}
	@Override
	public double calc(String l1, String l2) {
		return l1.equals(l2)?0:1;
	}
}
