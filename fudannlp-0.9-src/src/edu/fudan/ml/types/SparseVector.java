package edu.fudan.ml.types;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import gnu.trove.TIntDoubleHashMap;
import gnu.trove.TIntDoubleIterator;
import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntIntIterator;
public class SparseVector implements Vector, Serializable {
	private static final long serialVersionUID = 1467092492463327579L;
	public TIntDoubleHashMap vector = null;
	public int len = 0;
	public SparseVector() {
		vector = new TIntDoubleHashMap(100, 0.75f);
	}
	public void put(int index, double value) {
		vector.put(index, value);
		if (index > len)
			len = index;
	}
	public void length() {
		len = 0;
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			if (it.key() > len)
				len = it.key();
		}
	}
	public void minus(SparseVector sv) {
		TIntDoubleIterator it = sv.vector.iterator();
		for (int i = sv.vector.size(); i-- > 0;) {
			it.advance();
			vector.adjustOrPutValue(it.key(), -it.value(), -it.value());
		}
	}
	public void add(int index, double value) {
		vector.adjustOrPutValue(index, value, value);
	}
	public void plus(SparseVector sv) {
		if (sv == null)
			return;
		TIntDoubleIterator it = sv.vector.iterator();
		for (int i = sv.vector.size(); i-- > 0;) {
			it.advance();
			vector.adjustOrPutValue(it.key(), it.value(), it.value());
		}
		it = null;
	}
	public void plus(SparseVector sv, double w) {
		TIntDoubleIterator it = sv.vector.iterator();
		for (int i = sv.vector.size(); i-- > 0;) {
			it.advance();
			double v = w * it.value();
			vector.adjustOrPutValue(it.key(), v, v);
		}
		it = null;
	}
	public void set(int index, double value) {
		if (value == 0.0)
			return;
		vector.put(index, value);
	}
	public double elementAt(int index) {
		return vector.get(index);
	}
	public int size() {
		return vector.size();
	}
	public int[] indices() {
		return vector.keys();
	}
	public double dotProduct(SparseVector sv) {
		SparseVector sv1;
		SparseVector sv2;
		if (vector.size() < sv.vector.size()) {
			sv1 = this;
			sv2 = sv;
		} else {
			sv1 = sv;
			sv2 = this;
		}
		double product = 0;
		TIntDoubleIterator it = sv1.vector.iterator();
		for (int i = sv1.vector.size(); i-- > 0;) {
			it.advance();
			product += it.value() * sv2.getValue(it.key());
		}
		return product;
	}
	public double dotProduct(SparseVector wii, int li, int n) {
		double product = 0;
		TIntDoubleIterator it = vector.iterator();
		int z = n * li;
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			product += it.value() * wii.getValue(it.key() + z);
		}
		it = null;
		return product;
	}
	public void scalarMultiply(double d) {
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			double val = it.value() * d;
			vector.put(it.key(), it.value() * d);
		}
		it = null;
	}
	public void scalarDivide(double d) {
		if (d == 0)
			return;
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			vector.put(it.key(), it.value() / d);
		}
		it = null;
	}
	private double getValue(int key) {
		return vector.get(key);
	}
	public double l1Norm() {
		double norm = 0;
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			norm += Math.abs(it.value());
		}
		it = null;
		return norm;
	}
	public double l2Norm2() {
		double norm = 0;
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			norm += it.value() * it.value();
		}
		it = null;
		return norm;
	}
	public double l2Norm() {
		double norm = 0;
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			norm += it.value() * it.value();
		}
		it = null;
		return Math.sqrt(norm);
	}
	public double infinityNorm() {
		double norm = 0;
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			if (Math.abs(it.value()) > norm)
				norm = Math.abs(it.value());
		}
		it = null;
		return norm;
	}
	public SparseVector clone() {
		SparseVector sv = new SparseVector();
		sv.vector = (TIntDoubleHashMap) this.vector.clone();
		sv.len = this.len;
		return sv;
	}
	public SparseVector replicate(ArrayList list, int dim) {
		SparseVector sv = new SparseVector();
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			for (int j = 0; j < list.size(); j++) {
				sv.vector.put(it.key() + dim * ((Integer) list.get(j)), it
						.value());
			}
		}
		return sv;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		TIntDoubleIterator it = vector.iterator();
		while (it.hasNext()) {
			it.advance();
			sb.append(it.key());
			sb.append(':');
			sb.append(it.value());
			sb.append(' ');
		}
		return sb.toString();
	}
	public void trim() {
		vector.compact();
		len = vector.size();
	}
	public double remove(int id)	{
		return vector.remove(id);
	}
	public void normalize() {
		double norm = l2Norm();
		if (norm > 0)
			scalarMultiply(1 / norm);
	}
	public double distanceSquared(SparseVector sv) {
		double dist = 0.0;
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			dist += Math.pow(it.value() - sv.vector.get(it.key()), 2);
		}
		it = sv.vector.iterator();
		for (int j = sv.vector.size(); j-- > 0;) {
			it.advance();
			if (!vector.containsKey(it.key())) {
				dist += Math.pow(it.value(), 2);
			}
		}
		return dist;
	}
	public void clear() {
		vector.clear();
		len = 0;
	}
	public void normalize2() {
		double sum = 0;
		TIntDoubleIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			double value = Math.exp(vector.get(it.key()));
			vector.put(it.key(), value);
			sum += value;
		}
		this.scalarDivide(sum);
	}
	public static int dotProduct(TIntIntHashMap v1, TIntIntHashMap v2) {
		TIntIntHashMap sv1;
		TIntIntHashMap sv2;
		if (v1.size() < v2.size()) {
			sv1 = v1;
			sv2 = v2;
		} else {
			sv1 = v2;
			sv2 = v1;
		}
		double product = 0;
		TIntIntIterator it = sv1.iterator();
		for (int i = sv1.size(); i-- > 0;) {
			it.advance();
			product += it.value() * sv2.get(it.key());
		}
		return (int) product;
	}
	public Iterator<Integer> iterator() {
		return null;
	}
}
