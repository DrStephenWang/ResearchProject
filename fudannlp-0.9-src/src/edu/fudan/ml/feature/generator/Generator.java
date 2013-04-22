package edu.fudan.ml.feature.generator;
import java.io.Serializable;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.SparseVector;
public abstract class Generator implements Serializable {
	private static final long serialVersionUID = -6933148218344973548L;
	public Alphabet alphabet=null;
	public Generator(){
		alphabet = new Alphabet();
	}
	public void setStopIncrement(boolean stopIncrement){
		alphabet.setStopIncrement(stopIncrement);
	}
	public Alphabet getAlphabet() {
		return this.alphabet;
	}
	public void setAlphabet(Alphabet alphabet)	{
		this.alphabet = alphabet;
	}
	public int[] get(Instance inst){
		return get(inst,inst.getTarget());
	}
	public  int[] get(Instance inst,Object label){
		return null;
	}
	public SparseVector getVector(Instance inst){
		return getVector(inst,inst.getTarget());
	}
	public  abstract SparseVector getVector(Instance inst, Object object);
}
