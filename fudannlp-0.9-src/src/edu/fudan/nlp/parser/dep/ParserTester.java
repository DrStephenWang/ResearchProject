package edu.fudan.nlp.parser.dep;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
public class ParserTester {
	YamadaParser parser;
	boolean finaltest = false;
	public ParserTester(String modelfile) throws IOException, ClassNotFoundException {
		parser = new YamadaParser(modelfile);
	}
	public void test(String testFile, String resultFile, String charset)
			throws Exception {
		int error = 0;
		int total = 0;
		int errsent = 0;
		int totsent = 0;
		System.out.print("Beginning the test ... ");
		CoNLLReader reader = new CoNLLReader(testFile);
		BufferedWriter writer = null;
		if (finaltest)
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(resultFile), charset));
		long beginTime = System.currentTimeMillis();
		while (reader.hasNext()) {
			Sentence instance = (Sentence) reader.next();
			int[] golds = new int[instance.length()];
			System.arraycopy(instance.heads, 0, golds, 0, golds.length);
			parser.getBestParse(instance);
			int[] preds = instance.heads;
			int curerr = diff(golds, preds);
			if (curerr != 0) {
				errsent++;
				error += curerr;
			}
			totsent++;
			total += golds.length;
			golds = null;
			if (finaltest)
				instance.writeInstance(writer);
		}
		if (finaltest)
			writer.close();
		long endTime = System.currentTimeMillis();
		parser = null;
		double time = (endTime - beginTime) / 1000.0;
		System.out.println("finish! =]");
		System.out.printf("total time: %.2f(s)\n", time);
		System.out.printf("errate(words): %.8f\ttotal(words): %d\n", 1.0
				* error / total, total);
		System.out.printf("errate(sents): %.8f\ttotal(sents): %d\n", 1.0
				* errsent / totsent, totsent);
		System.out.printf("average speed: %.2f(s)(perword)\t%.2f(s)(persent)",
				total / time, totsent / time);
	}
	private int diff(int[] golds, int[] preds) {
		int ret = 0;
		int[] ref = golds;
		if (golds.length > preds.length)
			ref = preds;
		for (int i = 0; i < ref.length; i++)
			if (golds[i] != preds[i])
				ret++;
		return ret;
	}
	public static void main(String[] args) throws Exception {
		String modelfile = args[0];
		String testfile = args[1];
		String resultfile = args[2];
		ParserTester tester = new ParserTester(modelfile);
		tester.test(testfile, resultfile, "UTF-8");
	}
}
