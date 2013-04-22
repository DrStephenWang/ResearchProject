package edu.fudan.ml.results;
import java.util.Arrays;
import edu.fudan.ml.utils.MyArrays;
public class Results {
	int n;
	public double[] predScore;
	public int[] predList;
	public double[] oracleScore;
	public int[] oracleList;
	public Object other;
	public Results(int n){
		this.n= n;
		predScore = new double[n];
		Arrays.fill(predScore, Double.NEGATIVE_INFINITY);
		predList = new int[n];
		oracleScore =null;
		oracleList =null;
	}
	public void buildOracle(){
		oracleScore = new double[n];;
		Arrays.fill(oracleScore, Double.NEGATIVE_INFINITY);
		oracleList = new int[n];
	}
	public int addPred(double score,int i){
		return MyArrays.addBest(predScore, predList, score, i);
	}
	public int addOracle(double score,int i){
		return MyArrays.addBest(oracleScore, oracleList, score, i);
	}
}
