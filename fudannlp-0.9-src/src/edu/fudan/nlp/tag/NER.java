package edu.fudan.nlp.tag;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import edu.fudan.ml.pipe.Pipe;
import edu.fudan.ml.pipe.String2Sequence;
import edu.fudan.ml.struct.classifier.Linear;
import edu.fudan.ml.types.Instance;
public class NER {
	Linear cl;
	public NER() {
		InputStream is=this.getClass().getResourceAsStream("/model/ner.gz");   
		try {
			cl = Linear.readModel(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public NER(String file){
		try {
			cl = Linear.readModel(file);
			//			tagger.saveModel(file+".gz");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	Pipe p = new String2Sequence(false);
	public HashMap<String,String> tag(String src){
		HashMap<String,String> map = new HashMap<String,String>();
		String[] sents = src.split("[\\s　]+");
		try {
			for(int i=0;i<sents.length;i++){
				Instance inst = new Instance(sents[i]);
				p.addThruPipe(inst);
				inst.setSource(inst.getData());
				cl.getPipe().addThruPipe(inst);
				String[] target = cl.predict(inst);
				analysis(inst,target, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	private HashMap<String,String> analysis(Instance inst, String[] target, HashMap<String, String> map) {
		List<String> data = (List<String>) inst.getSource();
		StringBuilder sb = new StringBuilder();
		String label=null;
		String oldtag = "";
		for(int i=0; i<data.size(); i++) {
			int idx = target[i].lastIndexOf("-");
			String tag = target[i].substring(idx+1);
			if(target[i].equals("O")||(idx!=-1&&!tag.equals(oldtag))){
				if(sb.length()>0){
					map.put(sb.toString(), oldtag);
					sb = new StringBuilder();
					label=null;
				}
			}
			oldtag = tag;
			if(target[i].equals("O"))
				continue;
			sb.append(data.get(i));
		}
		return map;
	}
	public static void main(String[] args) throws Exception{
		Options opt = new Options();
		opt.addOption("h", false, "Print help for this application");
		opt.addOption("f", false, "segment file. Default string mode.");
		opt.addOption("s", false, "segment string");
		BasicParser parser = new BasicParser();
		CommandLine cl = parser.parse(opt, args);
		if (args.length == 0 || cl.hasOption('h') ) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp("Tagger:\n" +
					"java edu.fudan.nlp.tag.NER -f model_file input_file output_file;\n" +
					"java edu.fudan.nlp.tag.NER -s model_file string_to_segement", opt);
			return;
		}		
		String[] arg = cl.getArgs();
		String modelFile;
		String input;
		String output = null;
		if(cl.hasOption("f")&&arg.length==3){
			modelFile = arg[0];
			input = arg[1];
			output = arg[2];
		}else if(arg.length==2){
			modelFile = arg[0];
			input = arg[1];
		}else{
			System.err.println("paramenters format error!");
			System.err.println("Print option \"-h\" for help.");
			return;
		}
		NER ner = new NER(modelFile);
		if(cl.hasOption("f")){
			String s = ner.tagFile(input);
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(output), "utf8");
			w.write(s);
			w.close();
		}else{
			HashMap<String, String> s = ner.tag(input);
			System.out.println(s);
		}
	}
	public String tagFile(String input) {
		StringBuilder res = new StringBuilder();
		try {
			InputStreamReader  read = new InputStreamReader (new FileInputStream(input),"utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str = lbin.readLine();
			while(str!=null){
				HashMap<String, String> s= tag(str);
				res.append(s);
				res.append("\n");
				str = lbin.readLine();
			}
			lbin.close();
			return res.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
