package edu.fudan.nlp.parser.dep;
import java.io.*;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.AlphabetFactory;
import edu.fudan.ml.types.Instance;
import edu.fudan.nlp.parser.Util;
public class Sentence extends Instance {
	int[] forms = null;
	int[] postags = null;
	int[] heads = null;
	int length = 0;
	Alphabet wordAlphabet = AlphabetFactory.buildAlphabet("word");
	Alphabet postagAlphabet = AlphabetFactory.buildAlphabet("postag");
	public Sentence(String[] forms) {
		this.forms = new int[forms.length];
		for (int i = 0; i < forms.length; i++) {
			this.forms[i] = wordAlphabet.lookupIndex(forms[i]);
		}
		length = forms.length;
	}
	public Sentence(String[] forms, String[] postags) {
		if (forms.length != postags.length)
			throw new IllegalArgumentException(
					"length of words and postags are not equal!");
		this.forms = new int[forms.length];
		this.postags = new int[postags.length];
		for (int i = 0; i < forms.length; i++) {
			this.forms[i] = wordAlphabet.lookupIndex(forms[i]);
			this.postags[i] = postagAlphabet.lookupIndex(postags[i]);
		}
		length = forms.length;
	}
	public Sentence(String[] forms, String[] postags, int[] heads) {
		this(forms, postags);
		this.heads = heads;
	}
	public int length() {
		return length;
	}
	public void clearDependency() {
		for (int i = 0; i < heads.length; i++) {
			heads[i] = -1;
		}
	}
	public String getWord(int n) {
		if (n > length || n < 0)
			throw new IllegalArgumentException(
					"index should be less than length or great than 0!");
		return wordAlphabet.lookupString(forms[n]);
	}
	public String getTag(int n) {
		if (n > length || n < 0)
			throw new IllegalArgumentException(
					"index should be less than length or great than 0!");
		return postagAlphabet.lookupString(postags[n]);
	}
	public void writeInstance(BufferedWriter outputWriter) throws Exception {
		for (int i = 0; i < forms.length; i++) {
			outputWriter.write(wordAlphabet.lookupString(forms[i]) + '\t');
		}
		outputWriter.newLine();
		for (int i = 0; i < postags.length; i++) {
			outputWriter.write(postagAlphabet.lookupString(postags[i]) + '\t');
		}
		outputWriter.newLine();
		String[] strHeads = new String[heads.length];
		for (int i = 0; i < heads.length; i++)
			strHeads[i] = new Integer(heads[i] + 1).toString();
		outputWriter.write(Util.join(strHeads, '\t') + "\n");
		outputWriter.write("\n");
		outputWriter.flush();
	}
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			buf.append(wordAlphabet.lookupString(forms[i]));
			buf.append("/");
			buf.append(postagAlphabet.lookupString(postags[i]));
			buf.append(" ");
		}
		return buf.toString().trim();
	}
}
