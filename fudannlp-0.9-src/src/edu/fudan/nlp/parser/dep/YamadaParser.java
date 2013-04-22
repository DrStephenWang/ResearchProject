package edu.fudan.nlp.parser.dep;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import edu.fudan.ml.classifier.Linear;
import edu.fudan.ml.results.Results;
import edu.fudan.ml.solver.LinearMax;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.AlphabetFactory;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
public class YamadaParser {
	Alphabet postagAlphabet;
	Alphabet wordAlphabet;
	Alphabet featureAlphabet;
	Linear[] models;
	public YamadaParser(String modelfile) throws IOException,
			ClassNotFoundException {
		loadModel(modelfile);
	}
	public double getBestParse(String[] words, String[] postags, int[] heads)
			throws Exception {
		Sentence instance = new Sentence(words, postags, heads);
		return getBestParse(instance);
	}
	public double getBestParse(Sentence instance) throws Exception {
		instance.clearDependency();
		double score = 0;
		ParsingState state = new ParsingState(instance);
		Alphabet postagAlphabet = AlphabetFactory.buildAlphabet("postag");
		while (!state.isFinalState()) {
			int[] lr = state.getFocusIndices();
			FeatureVector features = state.getFeatures();
			Instance inst = new Instance(features);
			int lpos = postagAlphabet.lookupIndex(instance.getTag(lr[0]));
			double[][] estimates = estimateActions(models[lpos], inst);
			if ((int) estimates[0][0] == 1)
				state.next(ParsingState.Action.LEFT);
			else if ((int) estimates[0][0] == 2)
				state.next(ParsingState.Action.RIGHT);
			else if ((int) estimates[0][1] == 1)
				state.next(ParsingState.Action.LEFT, estimates[1][1]);
			else
				state.next(ParsingState.Action.RIGHT, estimates[1][1]);
			if (estimates[0][0] != 0)
				score += Math.log10(estimates[1][0]);
			else
				score += Math.log10(estimates[1][1]);
		}
		state.saveRelation();
		return Math.exp(score);
	}
	private double[][] estimateActions(Linear model, Instance inst) {
		Alphabet actionList = model.getLabelAlphabet();
		int numOfClasses = actionList.size();
		double[][] result = new double[2][numOfClasses];
		Results ret = model.classify(inst, numOfClasses);
		int[] guess = ret.predList;
		double[] scores = ret.predScore;
		double total = 0;
		for (int i = 0; i < guess.length; i++) {
			result[0][i] = Integer.parseInt(actionList.lookupString(guess[i]));
			result[1][i] = Math.exp(scores[i]);
			total += result[1][i];
		}
		for (int i = 0; i < guess.length; i++) {
			result[1][i] = result[1][i] / total;
		}
		return result;
	}
	public void loadModel(String modelfile) throws IOException,
			ClassNotFoundException {
		ObjectInputStream instream = new ObjectInputStream(new GZIPInputStream(
				new FileInputStream(modelfile)));
		postagAlphabet = AlphabetFactory.setAlphabet("postag",
				(Alphabet) instream.readObject());
		wordAlphabet = AlphabetFactory.setAlphabet("word",
				(Alphabet) instream.readObject());
		featureAlphabet = AlphabetFactory.setAlphabet("feature",
				(Alphabet) instream.readObject());
		models = (Linear[]) instream.readObject();
		for (int i = 0; i < models.length; i++) {
			LinearMax solver = (LinearMax) models[i].getSolver();
			solver.setWeight(models[i].getWeight());
		}
		instream.close();
	}
}
