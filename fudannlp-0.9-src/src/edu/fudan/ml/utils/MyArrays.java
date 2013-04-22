package edu.fudan.ml.utils;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntIntHashMap;
public class MyArrays {
	public static int addBest(double[] scores, int[] pos, double score, int p) {
		int n = scores.length;
		int i;
		for(i=0;i<n;i++){
			if(score>scores[i])
				break;
		}
		if(i>=n) return -1;
		for(int k=n-2;k>=i;k--){
			scores[k+1] = scores[k];
			pos[k+1] = pos[k];
		}
		scores[i] = score;
		pos[i] = p;
		return i;
	}
	public static double[][] histogram(double[] count, int nbin){
		double maxCount = Double.NEGATIVE_INFINITY;
		double minCount = Double.MAX_VALUE;
		for (int i = 0 ; i < count.length ; i++)
		{
			if (maxCount < count[i])
			{
				maxCount = count[i];
			}
			if(minCount>count[i]){
				minCount = count[i];
			}
		}
		double[][] hist = new double[2][nbin];
		double interv = (maxCount - minCount)/nbin;
		for (int i = 0 ; i < count.length ; i++)
		{
			int idx = (int) Math.floor((count[i]-minCount)/interv);
			if(idx==nbin)
				idx--;
			hist[0][idx]++;
		}
		for(int i = 0;i<nbin;i++){
			hist[1][i] = minCount+i*interv;
		}
		return hist;
	}
}
