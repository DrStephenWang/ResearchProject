package edu.fudan.nlp.chinese;
public class Chars {
	public static boolean isChar(String str){ 
		char[] c = str.toCharArray();
		for (int i = 0; i< c.length; i++) {
			if(!isChar(c[i]))
				return false;				
		}
		return true;
	}
	public static boolean isChar(char c){ 
		if((c>32&&c<127)||c == 12288||(c> 65280&& c< 65375)){
			return true; 
		}else{
			return false;
		}
	}
	public static char[] getTag(String str) {
		char[] tag = new char[str.length()];
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if((c>='a'&&c<='z')||(c>='A'&&c<='Z')||
					(c>='a'+65248&&c<='z'+65248)||(c>='a'+65248&&c<='z'+65248)){
				tag[i] = 'e';
			}else if(c=='-'||c=='—'||c=='/'){
				if(i>1&&tag[i-1]=='e'){
					tag[i] = 'e';
				}else if(i>1&&tag[i-1]=='d'){
					tag[i] = 'd';					
				}
				else if(i>1&&tag[i-1]=='d'&&i<str.length()-1&&isChar(tag[i+1])){
					tag[i] = 'e';
				}else
					tag[i] = 'h';
			}
			else if((c>='0'&&c<='9')||(c>='0'+65248&&c<='9'+65248)){
				tag[i] = 'd';
			}else if(c=='.'&&i>1&&tag[i-1]=='d'){
				tag[i]='d';
			}else if(c=='%'&&i>1&&tag[i-1]=='d'){
				tag[i]='d';
			}else if("()。!,\"'（）！，””<>《》".indexOf(c)!=-1){
				tag[i] = 'p';
			}else if(c==12288||c==32){
				tag[i] = 's';
			}else{			
				tag[i] = 'h';
			}
		}
		return tag;
	}
	public static String getType(String str){
		char[] tag = getTag(str);
		String s = String.valueOf(tag);
		if(s.contains("h")||s.contains("s"))
			return "O";
		else if(s.contains("d")){
			if(s.contains("e"))
				return "M";
			else
				return "D";
		}else if(s.contains("e")){
			return "E";
		}
		return "O";
	}
}
