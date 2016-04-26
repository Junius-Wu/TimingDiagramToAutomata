package UML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class getStringInformation {

public static String getSourceTime(String string)
  {
	//System.out.println(string);
	String[] str=string.split(";");
	String[] str1=null;
	String temp="";
	for(int i=0;i<str.length;i++)
	{temp=str[i];
	//System.out.println(temp);	
	str1=temp.split("=");
	//System.out.println(str1[str1.length-1]);
	if(str1[0].contains("TS"))
	return str1[str1.length-1];}
	return null;
	
  }
public static String getReceiveTime(String string)
{
	String[] str=string.split(";");
	String[] str1=null;
	String temp="";
	
	temp=str[0];
	
	str1=temp.split("=");
	//System.out.println(str1[str1.length-1]);
	return str1[str1.length-1];
	
}

public static ArrayList<lineInfo> getxrefs(String value) {
		//	System.out.println(value);
	ArrayList<String>strA=new ArrayList<String>();
	ArrayList<lineInfo>infoList=new ArrayList<lineInfo>();
	
	String[] str=value.split(";");
		for(int i=0;i<str.length;i++)
	{
		if((!str[i].equals("Op=="))&&(!str[i].equals("@ENDVAR"))){
			strA.add(str[i]);
			//System.out.println(str[i]);
		}
	}
		
	Collections.reverse(strA);
 // System.out.println(strA);
	Iterator<String> tempIterator=strA.iterator();
	lineInfo[] info=new lineInfo[200];
	int p=0;
	info[p]=new lineInfo();
	while(tempIterator.hasNext()){
		
	String[] tString=tempIterator.next().split("=");
	
		switch (tString[0]) {
	case "Value":
		info[p].setValue(tString[1]);//System.out.println(info[p].getValue());	
		break;
	case "Variable":
		info[p].setName(tString[1]);//System.out.println("g===>"+info[p].getName());

	break;
	 
	case"@VAR":
		infoList.add(info[p]);p++;info[p]=new lineInfo();//System.out.println("p="+p);info.setDConst("-9999");
	    break;
	case "DConst":
		info[p].setDConst(tString[1]);//System.out.println(info[p].getDConst());
	    break;
	case "Event": if(tString.length==2){info[p].setEvent(tString[1]);break;}
                	else{info[p].setEvent(tString[1]+"=="+tString[3]);
		          break;}//System.out.println(info[p].getEvent());//  System.out.println(tString[1]);System.out.println(tString[2]);
	//System.out.println(tString[3]);
	default:  
		break;
	}	
		
	}
/*	Iterator<lineInfo> s=infoList.iterator();
	while(s.hasNext()){
		lineInfo tInfo=s.next();
		System.out.println(tInfo.getDConst());
		System.out.println(tInfo.getName());
		System.out.println(tInfo.getValue());
		System.out.println("--------------");
	}
	*/
	
//	System.out.println(infoList.size()+"s");
	return infoList;
}
}
