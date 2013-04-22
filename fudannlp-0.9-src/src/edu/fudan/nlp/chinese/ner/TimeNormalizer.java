package edu.fudan.nlp.chinese.ner;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import edu.fudan.ml.classifier.Classifier;
public class TimeNormalizer implements Classifier, Serializable {
	private static final long serialVersionUID = 463541045644656392L;
	private String timeBase;
	private String oldTimeBase;
	private static Pattern patterns = null;
	private String target;
	private TimeUnit[] timeToken = new TimeUnit[0];
	public TimeNormalizer() {
	}
	public TimeNormalizer(String path){
		if(patterns == null){
			try {
				patterns = readModel(path);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.print("Read model error!");
			}
		}
	}
	public void parse(String target,String timeBase){
		this.target = target;
		this.timeBase = timeBase;
		this.oldTimeBase = timeBase;
		preHandling();
		timeToken = TimeEx(this.target,timeBase);
	}
	public void parse(String target){
		this.target = target;
		this.timeBase = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
		this.oldTimeBase = timeBase;
		preHandling();
		timeToken = TimeEx(this.target,timeBase);
	}
	public String getTimeBase(){
		return timeBase;
	}
	public String getOldTimeBase(){
		return oldTimeBase;
	}
	public void setTimeBase(String s){
		timeBase = s;
	}
	public void resetTimeBase(){
		timeBase = oldTimeBase;
	}
	public TimeUnit[] getTimeUnit(){
		return timeToken;
	}
	private void preHandling(){
		target = stringPreHandlingModule.delKeyword(target, "\\s+"); //清理空白符
		target = stringPreHandlingModule.delKeyword(target, "[的]+"); //清理语气助词
		target = stringPreHandlingModule.numberTranslator(target);
	}
	private TimeUnit[] TimeEx(String tar,String timebase)
	{
		Matcher match;
		int startline=-1,endline=-1;
		String [] temp = new String[99];
		int rpointer=0;
		TimeUnit[] Time_Result = null;
		match=patterns.matcher(tar);	
		boolean startmark=true;
		while(match.find())
		{
			startline=match.start();
			if (endline==startline) 
			{
				rpointer--;
				temp[rpointer]=temp[rpointer]+match.group();
			}
			else
			{
				if(!startmark)
				{
					rpointer--;
					rpointer++;	
				}	
				startmark=false;
				temp[rpointer]=match.group();
			}
			endline=match.end();
			rpointer++;
		}
		if(rpointer>0)
		{
			rpointer--;
			rpointer++;
		}
		Time_Result=new TimeUnit[rpointer];
		//	System.out.println("Basic Data is " + timebase); 
		for(int j=0;j<rpointer;j++)
		{
			Time_Result[j]=new TimeUnit(temp[j],this);
		}
		return Time_Result;
	}
	public void text2binModel(String inText,String outBin) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream (
				new GZIPOutputStream (new FileOutputStream(outBin))));
		String rules = loadtxt(inText);
		Pattern p = Pattern.compile(rules);
		out.writeObject(p);
		out.close();
	}
	private Pattern readModel(InputStream is) throws Exception{
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream 
				(new GZIPInputStream (is)));
		return readModel(in);
	}
	private Pattern readModel(String file) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream 
				(new GZIPInputStream (new FileInputStream(file))));
		return readModel(in);
	}
	private Pattern readModel(ObjectInputStream in) throws Exception {
		Pattern p = (Pattern) in.readObject();
		return p=Pattern.compile(p.pattern());
	}
	private String loadtxt(String path){
		String rules = "";
		try {		
			InputStreamReader  read = new InputStreamReader (new FileInputStream(path),"utf-8");
			BufferedReader bin = new BufferedReader(read);
			String _ruleunit = bin.readLine();
			while (_ruleunit!=null)
			{
				if (!_ruleunit.startsWith("-"))
				{
					if(rules.equals(""))
						rules=rules+"("+_ruleunit+")";
					else
						rules=rules+"|("+_ruleunit+")";
				}	
				_ruleunit=bin.readLine();
			}
		}catch(Exception e){
			System.out.println("正则表达式文件未找到！");
		}
		return rules;
	}
}
