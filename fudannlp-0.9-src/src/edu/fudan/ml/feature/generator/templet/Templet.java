package edu.fudan.ml.feature.generator.templet;
import java.io.Serializable;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public interface Templet extends Serializable{
	public int[] getVars();
	public int getOrder();
	public void generateAt( Instance instance,
							int pos, 
							int[] fv,
							Alphabet features,
							int numLabels ) throws Exception;
}
