package edu.fudan.ml.struct.classifier;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import edu.fudan.ml.classifier.Classifier;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.struct.solver.IMaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
public class Linear implements Classifier, Serializable {
	private static final long serialVersionUID = 7811611261579845079L;
	private double[] weights;
	private IMaxSolver msolver;
	private Alphabet labelAlphabet;
	private Pipe pipe;
	public Alphabet getLabelAlphabet() {
		return labelAlphabet;
	}
	public void setLabelAlphabet(Alphabet labelAlphabet) {
		this.labelAlphabet = labelAlphabet;
	}
	public Linear() {
	}
	public Linear(double[] sv, IMaxSolver msolver, Alphabet labelAlphabet,Pipe pipe) {
		this.msolver = msolver;
		this.setWeights(sv);		
		this.labelAlphabet = labelAlphabet;
		this.setPipe(pipe);
	}
	public Object classify(Instance instance) {
		List pred = (List) msolver.getBest(instance, 2);
		return pred.get(0);
	}
	public String[] predict(Instance instance) {
		List pred = (List) msolver.getBest(instance, 2);
		int[] label =  (int[]) pred.get(0);
		return labelAlphabet.lookupString(label);
	}
	public String[][] predict(InstanceSet instSet) {
		String[][] res = new String[instSet.size()][];
		for(int i=0; i<instSet.size(); i++) {
			Instance inst = instSet.getInstance(i);
			res[i]=predict(inst);
		}
		return res;
	}
	public double[] getWeight() {
		return getWeights();
	}
	public void setPipe(Pipe pipe) {
		this.pipe = pipe;
	}
	public Pipe getPipe() {
		return pipe;
	}
	public void saveModel(String file) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream (
				new GZIPOutputStream (new FileOutputStream(file))));
		out.writeObject(this);
		out.close();
	}
	public static Linear readModel(InputStream is) throws IOException{
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream 
				(new GZIPInputStream (is)));
		return readModel(in);
	}
	public static Linear readModel(String file) throws IOException {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream 
				(new GZIPInputStream (new FileInputStream(file))));
		return readModel(in);
	}
	public static Linear readModel(ObjectInputStream in){
		try {
			Linear cl = (Linear) in.readObject();
			return cl;
		}catch(Exception e) {
			e.printStackTrace();
			System.err.print("Read model error!");
			return null;
		}
	}
	public void setWeights(double[] weights) {
		this.weights = weights;
		if(msolver==null){
			System.err.println("Solver is null!");
			return;
		}
		msolver.setWeight(weights);
	}
	public double[] getWeights() {
		return weights;
	}
}
