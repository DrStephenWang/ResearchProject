package edu.fudan.ml.types;
import gnu.trove.TIntObjectHashMap;
import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectIntIterator;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
public final class Alphabet implements Serializable, Iterable<Integer> {
	private static final long serialVersionUID = 5886220722038702893L;
	private TIntObjectHashMap<String> data;
	private TObjectIntHashMap<String> index;
	int size = 0;
	boolean frozen = false;
	public Set<Integer> toSet() {
		Set<Integer> set = new HashSet<Integer>();
		for (TObjectIntIterator<String> it = index.iterator(); it.hasNext();) {
			it.advance();
			set.add(it.value());
		}
		return set;
	}
	public boolean isStopIncrement() {
		return frozen;
	}
	public void setStopIncrement(boolean stopIncrement) {
		this.frozen = stopIncrement;
	}
	public Alphabet() {
		data = new TIntObjectHashMap<String>();
		index = new TObjectIntHashMap<String>();
	}
	public int lookupIndex(String s) {
		return lookupIndex(s, 1);
	}
	public int lookupIndex(String s, int step) {
		if (index.containsKey(s))
			return index.get(s);
		if (frozen)
			return -1;
		else{
			data.put(size, s);
			index.put(s, size);
			int oldsize = size;
			size += step;
			return oldsize;
		}
	}
	public String lookupString(int index) {
		return (String) data.get(index);
	}
	public String[] lookupString(int[] label) {
		String[] tag = new String[label.length];
		for (int i = 0; i < label.length; i++) {
			tag[i] = lookupString(label[i]);
		}
		return tag;
	}
	public int nonzoresize() {
		return this.data.size();
	}
	public int size() {
		return this.size;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < index.size(); i++) {
			sb.append(data.get(i));
			sb.append('@');
			sb.append(i);
			sb.append('\n');
		}
		return sb.toString();
	}
	public boolean hasKey(String s) {
		return index.containsKey(s);
	}
	public boolean hasIndex(int i) {
		return data.containsKey(i);
	}
	public void remove(int id) {
		if (index.containsValue(id)) {
			String s = data.get(id);
			data.put(id, null);
		}
	}
	public void trim() {
		index.compact();
		data.compact();
	}
	public String put(int id, String s) {
		String os = null;
		if (data.containsKey(id)) {
			os = data.get(id);
			if (!os.equals(s))
				throw new IllegalArgumentException(String.format(
						"conflict: (%d, %s)", id, s));
		} else {
			if (index.containsKey(s))
				os = data.get(id);
			data.put(id, s);
			index.put(s, id);
		}
		return os;
	}
	public Iterator<Integer> iterator() {
		return new IndexIterator();
	}
	private class IndexIterator implements Iterator<Integer> {
		TObjectIntIterator ite = null;
		public IndexIterator() {
			ite = index.iterator();
		}
		public boolean hasNext() {
			boolean hn = ite.hasNext();
			if (hn)
				ite.advance();
			return hn;
		}
		public Integer next() {
			return ite.value();
		}
		public void remove() {
			ite.remove();
		}
	}
}
