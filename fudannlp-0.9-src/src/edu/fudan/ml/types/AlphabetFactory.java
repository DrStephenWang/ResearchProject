package edu.fudan.ml.types;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
public class AlphabetFactory implements Serializable {
	final static Map<String, Alphabet> map = new HashMap<String, Alphabet>();
	public static Alphabet buildAlphabet(String name)	{
		if (map.containsKey(name))
			return map.get(name);
		Alphabet alphabet = new Alphabet();
		map.put(name, alphabet);
		return map.get(name);
	}
	public static Alphabet setAlphabet(String name, Alphabet alphabet) {
		map.put(name, alphabet);
		return map.get(name);
	}
}
