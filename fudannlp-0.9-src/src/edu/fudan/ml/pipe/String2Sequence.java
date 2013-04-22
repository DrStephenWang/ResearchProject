package edu.fudan.ml.pipe;
import java.util.Arrays;
import java.util.List;
import edu.fudan.ml.types.Instance;
import edu.fudan.nlp.chinese.Chars;
public class String2Sequence extends Pipe{
	private static final long serialVersionUID = 5699154494725645936L;
	boolean hasLabel = true;
	public static String delimer = "\\s+";
	public String2Sequence(boolean b){
		hasLabel = b;
	}
	@Override
	public void addThruPipe(Instance inst) {
		String str = (String) inst.getData();
		String seq = genSequence(str);
		String[] toks = seq.split("\n");
		List data = Arrays.asList(toks);
		inst.setData(data);
	}
	public static String genSequence(String sent){
		char[] tag = Chars.getTag(sent);
		StringBuilder sb = new StringBuilder();
		for(int j=0; j<sent.length(); j++) {
			char c = sent.charAt(j);	
			sb.append(c);
			if(j==sent.length()-1)
				sb.append('\n');
			else if(tag[j]=='h'||tag[j]=='p'){
				sb.append('\n');
			}else if(tag[j]=='e' || tag[j]=='d'){
				if(tag[j+1]=='e'|| tag[j+1]=='d'){
					continue;
				}else{
					sb.append("\n");
					if(tag[j+1]=='s'){
						j++;
					}
				}
			}else if(tag[j]=='p'){
				sb.append("\n");
			}					
		}
		sb.append('\n');
		return sb.toString();
	}
	public static String genSequenceWithLabel(String sent){
		StringBuilder sb = new StringBuilder();
		String[] wordArray = sent.split(delimer);
		for(int i=0; i<wordArray.length; i++) {
			String word = wordArray[i];
			char[] tag = Chars.getTag(word);
			for(int j=0; j<word.length(); j++) {
				char c = word.charAt(j);	
				sb.append(c);
				if(tag[j]=='e' || tag[j]=='d'){
					if(j==word.length()-1){
						sb.append("\tS");
					}else if(tag[j+1]=='e'|| tag[j+1]=='d'){
						continue;
					}else{
						sb.append("\tB");
					}						
				}else if(tag[j]=='p'){
					sb.append("\tS");
				}else{
					sb.append('\t');
					if(j == 0) {
						if(word.length() == 1)
							sb.append('S');
						else
							sb.append('B');
					} else if(j == word.length()-1) {
						sb.append('E');
					} else {
						if(j==1) {
							sb.append("B1");
						} else if(j==2) {
							sb.append("B2");
						} else {
							sb.append('M');
						}
					}
				}
				sb.append('\n');
			}
		}
		sb.append('\n');
		return sb.toString();
	}
	public static String genSequenceWithLabelSimple(String sent){
		StringBuilder sb = new StringBuilder();
		String[] wordArray = sent.split(delimer);
		for(int i=0; i<wordArray.length; i++) {
			String word = wordArray[i];
			for(int j=0; j<word.length(); j++) {
				char c = word.charAt(j);	
				sb.append(c);
				sb.append('\t');
				if(j == 0) {
					if(word.length() == 1)
						sb.append('S');
					else
						sb.append('B');
				} else if(j == word.length()-1) {
					sb.append('E');
				} else {
//						sb.append("B1");
//						sb.append("B2");
					sb.append('M');
				}
				sb.append('\n');
			}
		}
		sb.append('\n');
		return sb.toString();
	}
	public static void main(String[] args){
		genSequenceWithLabelSimple("\tB");
	}
}
