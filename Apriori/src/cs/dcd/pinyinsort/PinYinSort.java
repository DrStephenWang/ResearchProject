package cs.dcd.pinyinsort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class PinYinSort {
	
	/**
	 * 对list进行排序
	 * @return
	 */
	public static List getSortList(List list){
		Collections.sort(list);
		return list;
	}
	
	/**
	 * @return 排好顺序的汉字字符串
	 */
	
	public static List getSortStirng(List list) {
		HanZiToPinYin hanzi2Pinyin = new HanZiToPinYin();
		//遍历list，取出以字母数字汉字开头的项分别存放
		//以数字、小写字母开头
		List elist = new ArrayList();
		//以大写字母开头
		List ulist = new ArrayList();
		//以汉字开头
		List plist = new ArrayList();
		for(Iterator i=list.iterator();i.hasNext();) {
			String str = (String)i.next();
			if(str.matches("^[0-9a-z].*")) {
				elist.add(str);
			}else if(str.matches("^[A-Z].*")){
				ulist.add(str);
			}else {
				plist.add(str);
			}
		}
		getSortList(elist);
		getSortList(ulist);
		//将汉字list转换成拼音list
		List pinyinList = hanzi2Pinyin.getPinyinList(plist);
		//排序
		getSortList(pinyinList);
		for(Object str : ulist){
			elist.add(str);
		}
		for(Object str : pinyinList) {
			String[] hanzi = ((String) str).split("-");
			elist.add(hanzi[1]);
		}
		return elist;
	}
	
	public static void main(String[] args) {
		List list = new ArrayList();
		list.add("2");
		list.add("1");
		list.add("8");
		list.add("5");
		list.add("a");
		list.add("h");
		list.add("v");
		list.add("b");
		list.add("A");
		list.add("D");
		list.add("B");
		list.add("1A");
		list.add("7W");
		list.add("2Z");
		list.add("1飞");
		list.add("6飞");
		list.add("2飞");
		list.add("a啊");
		list.add("d啊");
		list.add("b啊");
		list.add("我");
		list.add("好");
		list.add("暗示");
		list.add("飞");
		list.add("访问");
		list.add("大方");
		list.add("七位");
		list.add("啊");
		list.add("的");
		list.add("狗头");
		List list1 = getSortStirng(list);
		for(Iterator i=list1.iterator();i.hasNext();) {
			System.out.println(i.next());
		}
		
	}
}

