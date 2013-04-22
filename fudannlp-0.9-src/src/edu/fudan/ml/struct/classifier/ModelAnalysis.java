package edu.fudan.ml.struct.classifier;
import java.io.IOException;
import edu.fudan.ml.pipe.Sequence2FeatureSequence;
import edu.fudan.ml.types.Alphabet;
import gnu.trove.TDoubleArrayList;
import gnu.trove.TIntIntHashMap;
public class ModelAnalysis {
	private  Linear cl;
	private  Alphabet feature;
	private  double thresh = 0;
	private  double[] weis;
	private  Sequence2FeatureSequence p;
	private int old_weight_length;
	private int old_feature;
	public ModelAnalysis(Linear cl2) {
		cl = cl2;
		p = (Sequence2FeatureSequence) cl.getPipe();
		feature = p.features;
		weis = cl.getWeight();
	}
	public void removeZero() throws IOException {
		old_feature = feature.nonzoresize();
		old_weight_length = feature.size();
		int nonweigth = 0;
		int base=0;
		TIntIntHashMap non_zero_feature_array = new TIntIntHashMap();
		Alphabet newfeat = new Alphabet();
		TDoubleArrayList ww = new TDoubleArrayList();
		for(int i=0;i<=old_weight_length;i++){			
			if((feature.hasIndex(i))||i==old_weight_length){
				int interv = i-base;
				if(interv>0 && non_zero_feature_array.get(base)>0){
					String str = feature.lookupString(base);
					int idx = newfeat.lookupIndex(str, interv);
					for(int j=0;j<interv;j++){
						ww.insert(idx+j,weis[base+j]);
					}
				}
				base = i;
			}
			if(i<old_weight_length&&Math.abs(weis[i])>thresh ){
				non_zero_feature_array.adjustOrPutValue(base, 1, 1);
				nonweigth++;
			}
		}
		double[] weights = ww.toNativeArray();
		System.out.print("Total feature: ");
		System.out.println(String.valueOf(feature.nonzoresize()));
		System.out.print("Total weight: ");
		System.out.println(String.valueOf(feature.size()));
		System.out.print("non_zero_feature: ");
		System.out.println(String.valueOf(newfeat.nonzoresize()));
		System.out.print("non_zero_weight: ");
		System.out.println(String.valueOf(nonweigth));
		System.out.print("new weight_length: ");
		System.out.println(String.valueOf(weights.length));
		System.out.print("new weight_length: 11");
		System.out.println(String.valueOf(newfeat.size()));
		System.out.println("Remove Feature: "+ " : " + (old_feature-newfeat.nonzoresize()));
		cl.setWeights(weights);		
		newfeat.setStopIncrement(true);
		p.features = newfeat;
	}
	public static void main(String[] args) throws Exception {
		if(args.length!=1)
			return;
		String file = args[0];
		Linear cl = Linear.readModel(file);
		ModelAnalysis ma = new ModelAnalysis(cl);
		ma.removeZero();
		cl.saveModel(file);
		System.out.print("Done");
	}
}
