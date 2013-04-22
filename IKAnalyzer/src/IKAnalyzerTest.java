import java.io.IOException;
import java.io.StringReader;

import org.wltea.analyzer.core.*;

public class IKAnalyzerTest {

	public static void main(String[] args) throws IOException {

		String str = "IK Analyzer��һ����ϴʵ�ִʺ��ķ��ִʵ����ķִʿ�Դ���߰�����ʹ����ȫ�µ����������ϸ�����з��㷨��";
		StringReader reader = new StringReader(str);
		IKSegmenter ik = new IKSegmenter(reader, true);// ��Ϊtrueʱ���ִ����������ʳ��з�
		Lexeme lexeme = null;
		while ((lexeme = ik.next()) != null){
			System.out.println(lexeme.getLexemeText());
		}
	}
}

