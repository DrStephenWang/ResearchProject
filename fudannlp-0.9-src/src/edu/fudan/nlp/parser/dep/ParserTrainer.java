package edu.fudan.nlp.parser.dep;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import edu.fudan.ml.classifier.AverageTrainer;
import edu.fudan.ml.classifier.FastAverageTrainer;
import edu.fudan.ml.classifier.Linear;
import edu.fudan.ml.classifier.PerceptronTrainer;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.loss.ZeroOneLoss;
import edu.fudan.ml.solver.LinearMax;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.AlphabetFactory;
import edu.fudan.ml.types.FeatureVector;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
public class ParserTrainer {
	String modelfile;
	Charset charset;
	File fp;
	public ParserTrainer(String data) {
		this(data, "UTF-8");
	}
	public ParserTrainer(String dataPath, String charset) {
		this.modelfile = dataPath;
		this.charset = Charset.forName(charset);
	}
	private void buildInstanceList(String file) throws IOException {
		System.out.print("generating training instances ...");
		CoNLLReader reader = new CoNLLReader(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fp), charset));
		int count = 0;
		while (reader.hasNext()) {
			Sentence instance = (Sentence) reader.next();
			ParsingState state = new ParsingState(instance);
			while (!state.isFinalState()) {
				int[] lr = state.getFocusIndices();
				FeatureVector features = state.getFeatures();
				ParsingState.Action action = getAction(lr[0], lr[1],
						instance.heads);
				state.next(action);
				if (action == ParsingState.Action.LEFT)
					instance.heads[lr[1]] = -1;
				if (action == ParsingState.Action.RIGHT)
					instance.heads[lr[0]] = -1;
				writer.write(String.valueOf(instance.postags[lr[0]]));
				writer.write(" ");
				switch (action) {
				case LEFT:
					writer.write("1");
					break;
				case RIGHT:
					writer.write("2");
					break;
				default:
					writer.write("0");
				}
				writer.write(" ");
				int[] idx = features.indices();
				for (int i = 0; i < idx.length; i++) {
					writer.write(String.valueOf(idx[i]));
					writer.write(" ");
				}
				writer.newLine();
			}
			writer.write('\n');
			writer.flush();
			count++;
		}
		writer.close();
		System.out.println(" ... finished");
		System.out.printf("%d instances have benn loaded.\n\n", count);
	}
	public void train(String dataFile, int maxite, double eps)
			throws IOException {
		fp = File.createTempFile("train-features", null, new File("./"));
		buildInstanceList(dataFile);
		Alphabet postagAlphabet = AlphabetFactory.buildAlphabet("postag");
		Features generator = new Features(new Alphabet());
		Linear[] models = new Linear[postagAlphabet.size()];
		for (int i = 0; i < postagAlphabet.size(); i++) {
			String pos = postagAlphabet.lookupString(i);
			InstanceSet instset = readInstanceSet(i);
			Alphabet alphabet = instset.getLabelAlphabet();
			System.out.printf("Training with data: %s\n", pos);
			System.out.printf("number of labels: %d\n", alphabet.size());
			LinearMax solver = new LinearMax(generator, alphabet, alphabet
					.size());
			ZeroOneLoss loss = new ZeroOneLoss();
			PerceptronTrainer trainer = new AverageTrainer(solver, generator,
					loss, maxite, eps);
			Linear model = trainer.train(instset);
			models[i] = model;
			instset = null;
			solver = null;
			loss = null;
			trainer = null;
		}
		saveModel(models);
		fp.deleteOnExit();
	}
	private InstanceSet readInstanceSet(int pos) throws IOException {
		InstanceSet instset = new InstanceSet();
		instset.setLabelAlphabet(new Alphabet());
		Alphabet labelAlphabet = instset.getLabelAlphabet();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(fp), charset));
		String line = null;
		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.matches("^$"))
				continue;
			if (line.startsWith(pos + " ")) {
				List<String> tokens = Arrays.asList(line.split("\\s+"));
				Instance inst = new Instance(tokens.subList(2, tokens.size()));
				inst.setTarget(labelAlphabet.lookupIndex(tokens.get(1)));
				instset.add(inst);
			}
		}
		in.close();
		return instset;
	}
	private void saveModel(Linear[] models) throws IOException {
		Generator generator = models[0].generator;
		Alphabet alphabet = new Alphabet();
		Alphabet featureAlphabet = AlphabetFactory.buildAlphabet("feature");
		Alphabet nalphabet = new Alphabet();
		for(int i = 0; i < models.length; i++)	{
			SparseVector sv = models[i].getWeight();
			int[] ids = sv.indices();
			for(int j = 0; j < ids.length; j++)	{
				String s = generator.alphabet.lookupString(ids[j]);
				alphabet.put(ids[j], s);
				int ftid = Integer.parseInt(s.substring(0, s.indexOf("#")));
				String ft = featureAlphabet.lookupString(ftid);
				nalphabet.put(ftid, ft);
			}
		}
		generator.setAlphabet(alphabet);
		alphabet.setStopIncrement(true);
		nalphabet.setStopIncrement(true);
		ObjectOutputStream outstream = new ObjectOutputStream(new GZIPOutputStream(
				new FileOutputStream(modelfile)));
		alphabet = AlphabetFactory.buildAlphabet("postag");
		alphabet.setStopIncrement(true);
		outstream.writeObject(alphabet);
		alphabet = AlphabetFactory.buildAlphabet("word");
		outstream.writeObject(alphabet);
		outstream.writeObject(nalphabet);
		outstream.writeObject(models);
		outstream.close();
	}
	private ParsingState.Action getAction(int l, int r, int[] heads) {
		if (heads[l] == r && modifierNumOf(l, heads) == 0)
			return ParsingState.Action.RIGHT;
		else if (heads[r] == l && modifierNumOf(r, heads) == 0)
			return ParsingState.Action.LEFT;
		else
			return ParsingState.Action.SHIFT;
	}
	private int modifierNumOf(int h, int[] heads) {
		int n = 0;
		for (int i = 0; i < heads.length; i++)
			if (heads[i] == h)
				n++;
		return n;
	}
	public static void main(String[] args) throws Exception {
		String datafile = args[0];
		String modelfile = args[1];
		int maxite = Integer.parseInt(args[2]);
		double eps = 10e-5;
		ParserTrainer trainer = new ParserTrainer(modelfile);
		trainer.train(datafile,	maxite, eps);
	}
}
