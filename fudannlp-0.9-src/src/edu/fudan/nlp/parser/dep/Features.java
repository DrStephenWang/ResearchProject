package edu.fudan.nlp.parser.dep;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
public class Features extends Generator implements Serializable {
	public Features() {
		super();
	}
	public Features(Alphabet alphabet) {
		this.alphabet = alphabet;
	}
	@Override
	public FeatureVector getVector(Instance inst, Object label) {
		Object data = inst.getData();
		FeatureVector fv = new FeatureVector(alphabet);
		if (!(data instanceof FeatureVector)) {
			Iterator<String> ite = ((List<String>) data).iterator();
			while (ite.hasNext()) {
				fv.addFeature((String)ite.next()+"#"+label);
			}
		}else	{
			int[] ids = ((FeatureVector)data).indices();
			for(int i = 0; i < ids.length; i++)	{
				fv.addFeature(String.valueOf(ids[i])+"#"+label);
			}
		}
		return fv;
	}
	public Alphabet getAlphabet() {
		return alphabet;
	}
}
