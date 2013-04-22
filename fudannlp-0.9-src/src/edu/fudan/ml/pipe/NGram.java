package edu.fudan.ml.pipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import edu.fudan.ml.types.Instance;
public class NGram extends Pipe {
	int[] gramSizes = null;
	public NGram(int[] sizes) {
		this.gramSizes = sizes;
	}
	@Override
	public void addThruPipe(Instance inst) {
		Object data = inst.getData();
		List<String> tokens = Collections.emptyList();
		if (data instanceof String)	{
			tokens = Arrays.asList(((String)data).split("\\s+"));
		}else if (data instanceof List)	{
			tokens = (List<String>) data;
		}
		ArrayList<String> list = new ArrayList<String>();
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < gramSizes.length; j++) {
			int len = gramSizes[j];
			if (len <= 0 || len > tokens.size())
				continue;
			for (int i = 0; i < tokens.size() - len+1; i++) {
				buf.delete(0, buf.length());
				int k = 0;
				for(; k < len-1; ++k)	{
					buf.append(tokens.get(i+k));
					buf.append(' ');
				}
				buf.append(tokens.get(i+k));
				list.add(buf.toString().intern());
			}
		}
		inst.setData(list);
	}
}
