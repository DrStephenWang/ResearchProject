package edu.fudan.nlp.tag;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import edu.fudan.ml.data.SequenceReader;
import edu.fudan.ml.feature.generator.templet.CharRangeTemplet;
import edu.fudan.ml.feature.generator.templet.TemplateGroup;
import edu.fudan.ml.loss.seq.ILoss;
import edu.fudan.ml.loss.seq.HammingLoss;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.pipe.Sequence2FeatureSequence;
import edu.fudan.ml.pipe.SeriesPipes;
import edu.fudan.ml.pipe.SplitDataAndTarget;
import edu.fudan.ml.pipe.StringTokenizer;
import edu.fudan.ml.pipe.Target2Label;
import edu.fudan.ml.struct.classifier.Linear;
import edu.fudan.ml.struct.classifier.PATrainer;
import edu.fudan.ml.struct.classifier.weight.LabelwiseWeightUpdate;
import edu.fudan.ml.struct.classifier.weight.UpdateStrategy;
import edu.fudan.ml.struct.classifier.weight.WeightUpdate;
import edu.fudan.ml.struct.solver.IMaxSolver;
import edu.fudan.ml.struct.solver.Labelwise;
import edu.fudan.ml.struct.solver.Viterbi;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.nlp.tag.Format.SimpleFormatter;
public class Tagger   {
	Linear cl;
	String train;
	String testfile = null;
	String output = null;
	String templateFile;
	private String model;
	private int iterNum;
	private double c1;
	private double c2;
	private boolean labelwise=false;
	private boolean useLoss;
	private String delimiter;
	private boolean interim;
	public static void main(String[] args) throws Exception {
		Options opt = new Options();
		opt.addOption("h", false, "Print help for this application");
		opt.addOption("iter", true, "iterative num, default 40");
		opt.addOption("c1", true, "parameters 1, default 1");
		opt.addOption("c2", true, "parameters 2, default 0.1");
		opt.addOption("delimiter", true, "delimiter for string tokenizer");
		opt.addOption("train", false, "switch to training mode(Default: test model");
		opt.addOption("labelwise", false, "switch to labelwise mode(Default: viterbi model");
		opt.addOption("margin", false, "use hamming loss as margin threshold");
		opt.addOption("interim", false, "save interim model file");
		BasicParser parser = new BasicParser();
		CommandLine cl;
		try {
			cl = parser.parse(opt, args);
		} catch (Exception e) {
			System.err.println("Parameters format error");
			return;
		}
		if (args.length == 0 || cl.hasOption('h') ) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp("Tagger:\n" +
					"tagger [option] -train templet_file train_file model_file [test_file];\n" +
					"tagger [option] model_file test_file output_file\n", opt);
			return;
		}
		Tagger tagger = new Tagger();
		tagger.iterNum =Integer.parseInt(cl.getOptionValue("iter","40"));
		tagger.c1 =Double.parseDouble(cl.getOptionValue("c1","1"));
		tagger.c2 =Double.parseDouble(cl.getOptionValue("c2","0.1"));
		tagger.labelwise =cl.hasOption("labelwise");
		tagger.useLoss = cl.hasOption("margin");
		tagger.delimiter = cl.getOptionValue("delimiter","\t");
		tagger.interim = cl.hasOption("interim");
		String[] arg = cl.getArgs();
		if (cl.hasOption("train")&&arg.length==3){
			tagger.templateFile = arg[0];
			tagger.train = arg[1];
			tagger.model = arg[2];
			tagger.train();
		}else if(cl.hasOption("train")&&arg.length==4){
			tagger.templateFile = arg[0];
			tagger.train = arg[1];
			tagger.model = arg[2];
			tagger.testfile = arg[3];
			tagger.train();
		}else if(arg.length==3){
			tagger.model = arg[0];
			tagger.testfile = arg[1];
			tagger.output = arg[2];
			tagger.test();
		}else if(arg.length==2){
			tagger.model = arg[0];
			tagger.testfile = arg[1];
			tagger.test();
		}else{
			System.err.println("paramenters format error!");
			System.err.println("Print option \"-h\" for help.");
			return;
		}
	}
	public void train() throws Exception{
		TemplateGroup templates = new TemplateGroup();
		templates.load(templateFile);
		//		d.loadWithWeigth("D:/xpqiu/项目/自选/CLP2010/CWS/av-b-lut.txt", "AV");
		templates.add(new CharRangeTemplet(templates.gid++,new int[]{0}));
		templates.add(new CharRangeTemplet(templates.gid++,new int[]{-1,0}));
		templates.add(new CharRangeTemplet(templates.gid++,new int[]{-1,0,1}));
		Alphabet labels = new Alphabet();
		Pipe prePipe = new SeriesPipes(new Pipe[] {
				new StringTokenizer(delimiter),
				new SplitDataAndTarget(), new Target2Label(labels)});
		Alphabet features = new Alphabet();
		Pipe featurePipe = new Sequence2FeatureSequence(templates, features, labels);
		Pipe pipe = new SeriesPipes(new Pipe[]{prePipe,featurePipe});
		InstanceSet trainSet = new InstanceSet(pipe);
		trainSet.loadThruStagePipes(new SequenceReader(train, "utf8"));
		trainSet.setLabelAlphabet(labels);
		System.out.println("Training Number: "+trainSet.size());	
		System.out.println("Label Number: "+labels.size());			// 标签个数
		System.out.println("Feature Number: "+features.size());		// 特征个数
		InstanceSet testSet = null;
		if(testfile!=null){
			features.setStopIncrement(true);
			labels.setStopIncrement(true);
			Pipe tpipe;
			if(false){
				SeriesPipes tprePipe = new SeriesPipes(new Pipe[] {
						new StringTokenizer()});
				tpipe = new SeriesPipes(new Pipe[]{tprePipe,featurePipe});
			}else{
				tpipe = pipe;
			}
			testSet = new InstanceSet(tpipe);
			testSet.loadThruStagePipes(new SequenceReader(testfile , "utf8"));
			System.out.println("Test Number: "+testSet.size());			// 样本个数
		}
		UpdateStrategy update;
		IMaxSolver inference;
		if(labelwise){
			update = new LabelwiseWeightUpdate(templates, labels.size());
			inference = new Labelwise(features, labels.size(), templates);
		}else{
			update = new WeightUpdate(templates, labels.size(),useLoss);
			inference = new Viterbi(features, labels.size(), templates);
		}
		ILoss loss = new HammingLoss();
		PATrainer trainer = new PATrainer(inference, loss, iterNum,features,c1);
		trainer.update = update;
		trainer.interim = interim;
		cl = trainer.train(trainSet,testSet);
		cl.setPipe(featurePipe);
		cl.saveModel(model);
	}
	private void test() throws Exception {
		cl = Linear.readModel(model);
		Pipe prePipe = new SeriesPipes(new Pipe[] {
				new StringTokenizer(delimiter),
				new SplitDataAndTarget(), new Target2Label(cl.getLabelAlphabet())});
		Pipe pipe = new SeriesPipes(new Pipe[]{prePipe,cl.getPipe()});
		InstanceSet testSet = new InstanceSet(pipe);
		testSet.loadThruStagePipes(new SequenceReader(testfile , "utf8"));
		System.out.println("Test Number: "+testSet.size());			// 样本个数
		String[][] labelsSet = cl.predict(testSet);
		boolean acc = true;;
		if(acc ){
			double error = 0;
			int len = 0;
			ILoss loss = new HammingLoss();
			for(int i=0;i<testSet.size();i++){
				Instance inst = testSet.getInstance(i);
				len += labelsSet[i].length;			
				error += loss.calc(labelsSet[i] ,cl.getLabelAlphabet().lookupString((int[])inst.getTarget()));
			}
			System.out.println("Test Accuracy:\t" + (1-error/len));
		}
		if(output!=null){
			BufferedWriter prn = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(output), "utf8"));
			String s = SimpleFormatter.format(testSet, labelsSet);
			prn.write(s.trim());
			prn.close();
		}
		System.out.println("Done");
	}
}
