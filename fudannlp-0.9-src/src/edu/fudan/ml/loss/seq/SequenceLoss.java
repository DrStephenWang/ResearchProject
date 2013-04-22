package edu.fudan.ml.loss.seq;
import java.util.List;
public class SequenceLoss implements ILoss {
	public static enum Type	{
		POINT, EDGE
	}
	Type type;
	public SequenceLoss(Type type)	{
		this.type = type;
	}
	public double calc(Object o1, Object o2) {
		double errCount = 0;
		if (o1 instanceof int[] && o2 instanceof int[]) {
			int[] pred = (int[]) o1;
			int[] gold = (int[]) o2;
			if (type == Type.POINT)	{
				for (int i = 0; i < pred.length; i++) {
					if (pred[i] != gold[i])
						errCount++;
				}
			}else if (type == Type.EDGE)	{
				for (int i = 1; i < pred.length; i++) {
					if (pred[i - 1] != gold[i - 1] || pred[i] != gold[i])
						errCount++;
				}
			}
		}
		return errCount;
	}
	@Override
	public double calc(int[] l1, int[] l2) {
		return 0;
	}
	@Override
	public double calc(String[] s1, String[] s2) {
		return 0;
	}
	@Override
	public double calc(List l1, List l2) {
		return 0;
	}
}
