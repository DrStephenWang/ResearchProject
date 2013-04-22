package edu.fudan.ml.pipe;
import java.util.Arrays;
import java.util.List;
import edu.fudan.ml.types.Alphabet;
import edu.fudan.ml.types.Instance;
public class Target2Label extends Pipe {
	private static final long serialVersionUID = -4270981148181730985L;
	private Alphabet labelAlphabet;
	public Target2Label(Alphabet labelAlphabet) {
		this.labelAlphabet = labelAlphabet;
	}
	@Override
	public void addThruPipe(Instance instance) {
		Object t = instance.getTarget();
		if (t == null)
			return;
		if (t instanceof String) {
			instance.setTarget(labelAlphabet.lookupIndex((String) t));
		} else {
			List l = null;
			if (t instanceof Object[]) {
				l = Arrays.asList((Object[]) t);
			} else if (t instanceof List) {
				l = (List) t;
			}
			if (l != null) {
				int[] newTarget = new int[l.size()];
				for (int i = 0; i < l.size(); ++i)
					newTarget[i] = labelAlphabet.lookupIndex((String) l.get(i));
				instance.setTarget(newTarget);
			}
		}
	}
}
