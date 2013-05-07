package cs.dcd.pinyinsort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
  
/**
 * 汉字转换拼音
 * @author Administrator
 * @since Dec 23, 2009, 10:14:32 AM
 */
@SuppressWarnings("unchecked")
public class HanZiToPinYin {   
	
	/**
	 * 将传递的汉字list转换成拼音List
	 * @param list
	 */
	public List getPinyinList(List list){
		List pinyinList = new ArrayList();
		for(Iterator i=list.iterator(); i.hasNext();) {
			String str = (String)i.next();
			try {
				String pinyin = getPinYin(str);
				pinyinList.add(pinyin);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		}
		return pinyinList;
	}
	
	
    /**
     * 将中文转换成拼音
     * @param 拼音-汉字
     * @return
     */
    private String getPinYin(String zhongwen)   
            throws BadHanyuPinyinOutputFormatCombination {   
  
        String zhongWenPinYin = "";   
        char[] chars = zhongwen.toCharArray();   
  
        for (int i = 0; i < chars.length; i++) {   
            String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i],   
                    getDefaultOutputFormat());   
            // 当转换不是中文字符时,返回null   
            if (pinYin != null) {   
            	zhongWenPinYin += pinYin[0];   
            } else {   
                zhongWenPinYin += chars[i];   
            }   
        }   
  
        return zhongWenPinYin + "-" + zhongwen;   
    }   
  
    /**  
     * 输出格式  
     *   
     * @return  
     */  
    private HanyuPinyinOutputFormat getDefaultOutputFormat() {   
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();   
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写   
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 没有音调数字   
        format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);// u显示   
  
        return format;   
    }   
  
//    /**  
//     * Capitalize 首字母大写  
//     *   
//     * @param s  
//     * @return  
//     */  
//    public static String capitalize(String s) {   
//        char ch[];   
//        ch = s.toCharArray();   
//        if (ch[0] >= 'a' && ch[0] <= 'z') {   
//            ch[0] = (char) (ch[0] - 32);   
//        }   
//        String newString = new String(ch);   
//        return newString;   
//    }   
    
//    public static void main(String[] args) {
//    	try {
//			System.out.println(getPinYin("中行惹民123，孙新"));
//		} catch (BadHanyuPinyinOutputFormatCombination e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
  
}  
