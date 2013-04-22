import edu.fudan.nlp.resources.WordCount;


public class GenDic {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WordCount fm = new WordCount();
		String fileName = "data//人民日报 199801/199801.txt";
		fm.read(fileName);
		fm.save("../data/words.cn.dic", false);
		System.out.println("Done");

	}

}
