package edu.fudan.ml.pipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.fudan.ml.types.Instance;
public class SplitDataAndTarget extends Pipe {
	private static final long serialVersionUID = 331639154658696010L;
	public void addThruPipe(Instance instance) {
		List<String[]> seq = (List<String[]>) instance.getData();
		List<String[]> data = new ArrayList<String[]>();
		List<String> target = new ArrayList<String>();
		for(int i=0; i<seq.size(); i++) {
			String[] arr = seq.get(i);
			if(arr.length<2){
				System.err.println("The number of column must be 2 at least. skip");
				System.err.println(arr[0]);
				continue;
			}
			data.add(Arrays.copyOfRange(arr, 0, arr.length-1));
			target.add(arr[arr.length-1]);
		}
		instance.setData(data);
		instance.setTarget(target);
	}
}
