package edu.fudan.nlp.tc;
import java.io.IOException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import edu.fudan.ml.data.svmFileReader;
import edu.fudan.ml.data.svmFileReaderforwipo;
import edu.fudan.ml.feature.generator.Generator;
import edu.fudan.ml.feature.generator.SimpleGenerator;
import edu.fudan.ml.loss.ZeroOneLoss;
import edu.fudan.ml.multi.classifier.Linear;
import edu.fudan.ml.multi.classifier.PATrainer;
import edu.fudan.ml.multi.solver.MultiLinearMax;
import edu.fudan.ml.pipe.Normalize;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.pipe.SeriesPipes;
import edu.fudan.ml.pipe.TF2IDF;
import edu.fudan.ml.pipe.TFIDF;
import edu.fudan.ml.pipe.Target2Label;
import edu.fudan.ml.results.Evaluation;
import edu.fudan.ml.solver.IMaxSolver;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.Tree;
public class Experiemnt {
	static InstanceSet train;
	static InstanceSet test;
	static Alphabet alphabet = new Alphabet();
	static String path =null;
	private static boolean hier;
	static Tree tree= null;
	public static void main(String[] args) throws Exception{		
		Options opt = new Options();
        opt.addOption("h", false, "Print help for this application");
        opt.addOption("p", true, "The path of datasets");
        opt.addOption("tree", false, "use tree");
        opt.addOption("thread", true, "thread num");
        opt.addOption("iter", true, "iterative num");
        BasicParser parser = new BasicParser();
        CommandLine cl = parser.parse(opt, args);
        if ( args.length==1&& cl.hasOption('h') ) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp("OptionsTip", opt);
            return;
        }
        path = cl.getOptionValue("p");
		hier = cl.hasOption("tree");
		int numThreads = Integer.parseInt(cl.getOptionValue("thread","2"));
		int iterNum =Integer.parseInt(cl.getOptionValue("iter","10"));
		long start = System.currentTimeMillis();		
		loadwipo();
		Tree t = null;
		if(hier){
			t=tree;
		}
		System.out.println("Train Number: "+train.size());
		System.out.println("Test Number: "+test.size());
		System.out.println("Class Number: "+alphabet.size());
		double c =1.0;
		Generator featureGen = new SimpleGenerator();
		ZeroOneLoss loss = new ZeroOneLoss();
		IMaxSolver msolver = new MultiLinearMax(featureGen, alphabet,t,numThreads);		
		PATrainer trainer = new PATrainer(msolver, featureGen, loss, iterNum, c, t);
		Evaluation eval = new Evaluation(test,tree);
		Linear classfiy = trainer.train(train,eval);
		long end=System.currentTimeMillis();
		System.out.println("Total Time: "+(end-start));
		System.out.println("End!");
		System.exit(0);
	}
	private static void loadwipo() throws Exception {
		if(path==null)
			path = "D:/Datasets/wipo";
		tree = new Tree();
		tree.loadFromFileWithEdge(path+"/e.txt",alphabet);
		Pipe pipe = new SeriesPipes(new Pipe[]{new Target2Label(alphabet),new Normalize()});
		train = new InstanceSet(pipe,alphabet);
		alphabet.setStopIncrement(true);
		test = new InstanceSet(pipe,alphabet);
		svmFileReaderforwipo reader = new svmFileReaderforwipo(path+"/x_t.txt" , path+"/y_tr.txt");
		train.loadThruPipes(reader);
		reader = new svmFileReaderforwipo(path+"/x_ts.txt" , path+"/y_ts.txt");
		test.loadThruPipes(reader);
	}
	private static void loadlshtc() throws Exception {
		if(path==null)
			path = "D:/Datasets/lshtc/dry-run_lshtc_dataset";
		tree = new Tree();
		tree.loadFromFileWithPath(path+"/cat_hier.txt",alphabet);
		Pipe pipe = new Target2Label(alphabet);
		train = new InstanceSet(pipe,alphabet);
		alphabet.setStopIncrement(true);
		test = new InstanceSet(pipe,alphabet);
		train.loadThruPipes(new svmFileReader(path+ "/Task1_TrainCrawlData_TestCrawlData/train.txt"));
		train.loadThruPipes(new svmFileReader(path+ "/Task1_TrainCrawlData_TestCrawlData/validation.txt"));
		test.loadThruPipes(new svmFileReader(path+ "/Task1_TrainCrawlData_TestCrawlData/test.txt"));
		TF2IDF pipeIDF = new TF2IDF(train,test);
		pipeIDF.process(train);
		pipeIDF.process(test);
		pipe = new SeriesPipes(new Pipe[]{new TFIDF(pipeIDF.idf,train.size()+test.size()),new Normalize()});
		pipe.process(train);
		pipe.process(test);
	}
}
