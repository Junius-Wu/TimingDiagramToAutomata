package UML;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.transform.Templates;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Write {

	public static void creatXML(String Path,
			HashSet<String> global_declarations,
			HashSet<String> template_instantiations,
			ArrayList<UppaalTemPlate> temPlates) throws IOException {
		int x = 30, y = 30;
		System.out.println("开始生成XML文件");
		Document document = DocumentHelper.createDocument(); // 创建文档
		Element nta = document.addElement("nta");
		Element declaration = nta.addElement("declaration");
		String sdeclaration = global_declarations.toString().substring(1,
				global_declarations.toString().length() - 1);
		System.out.println(global_declarations.toString());
		declaration.setText("chan" + " " + sdeclaration + ";clock x;");//写入全局变量
		//合并templates
		mergeTemplates(temPlates);
		//处理消息的夸对象
		outMessageConnectAutomatas(temPlates.get(0));
		Iterator<UppaalTemPlate> templateIterator = temPlates.iterator();

		while (templateIterator.hasNext()) {//写入template
			UppaalTemPlate temlPlate = templateIterator.next();
			Element tem = nta.addElement("template");
			Element nameElement = tem.addElement("name");
			String xx = String.valueOf(x++);
			String yy = String.valueOf(y++);
			nameElement.addAttribute("x", xx);
			nameElement.addAttribute("y", yy);
			nameElement.setText(temlPlate.getName());
			tem.addElement("declaration");// .setText("// Place local declarations here.");
			int inittemp = -1;
			Iterator<UppaalLocation> locationIterator = temlPlate.locations
					.iterator();
			Iterator<UppaalTransition> transitonIterator = temlPlate.transitions
					.iterator();
			while (locationIterator.hasNext()) {//写入location
				UppaalLocation location = locationIterator.next();
				Element loc = tem.addElement("location");
				loc.addAttribute("id", "id" + location.getId());
				loc.addAttribute("x", xx);
				loc.addAttribute("y", yy);
				Element name2 = loc.addElement("name");
				name2.addAttribute("x", xx);
				name2.addAttribute("y", yy);
				name2.setText(location.getName());
				if (location.getInvariant() != null) {
					Element invariant_ele = loc.addElement("label");
					invariant_ele.addAttribute("kind", "invariant");
					invariant_ele.addAttribute("x", xx);
					invariant_ele.addAttribute("y", yy);
					invariant_ele.setText(location.getInvariant());
					System.out.println("getInvariant"+location.getInvariant());
				}

				if (location.getType() == 1) {
					loc.addElement("urgent");
				}

				if (location.getInit())
					inittemp = location.getId();

			}
			tem.addElement("init").addAttribute("ref", "id" + inittemp);
			while (transitonIterator.hasNext()) {//写入状态迁移
				UppaalTransition transition = transitonIterator.next();
				Element tran = tem.addElement("transition");
				// tran.addElement("")
				tran.addElement("source").addAttribute("ref",
						"id" + transition.getSourceId());
				tran.addElement("target").addAttribute("ref",
						"id" + transition.getTargetId());
				String[] tempStrings = transition.getKind();
				String[] tempStrings2 = transition.getNameText();
				//String tempInner = transition.getInner();
			  
				int i = 0;
				boolean out = false;
				while (tempStrings[i] != null) {
					if (tempStrings[i].equals("synchronisation")) {
						tran.addElement("label")
							.addAttribute("kind", tempStrings[i])
							.addAttribute("x", xx).addAttribute("y", yy)
							.addAttribute("time",transition.getTime())
							.addAttribute("from", transition.getFromName())
							.addAttribute("to", transition.getToName())
							.setText(tempStrings2[i]);
						
						tran.addAttribute("out", "true");
						out = true;
						
					i++;
					}else {
						tran.addElement("label")
						.addAttribute("kind", tempStrings[i])
						.addAttribute("x", xx).addAttribute("y", yy)
						.setText(tempStrings2[i]);
				i++;
					}
					
				}
				if(!out)
				{
					tran.addAttribute("out", "false");
				}

			}
			//只生成第一个整合的template
			break;
		}
		Element sysElement = nta.addElement("system");
		String instantiations = template_instantiations.toString().substring(1,
				template_instantiations.toString().length() - 1);
		sysElement.setText("system" + " " + instantiations + ";");

		String doString = document.asXML();
		String[] out = doString.split("[?]>");
		//String dTDString = "?><!DOCTYPE nta PUBLIC '-//Uppaal Team//DTD Flat System 1.1//EN' 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd'>";
		FileOutputStream outputStream = new FileOutputStream(Path);
		outputStream.write(doString.getBytes());
		
		//outputStream.write(out[0].getBytes());
		//outputStream.write(dTDString.getBytes());
		//outputStream.write(out[1].getBytes());
		outputStream.close();
		// System.out.println(doString);
		System.out.println("转换完成!");

	}

	private static void mergeTemplates(ArrayList<UppaalTemPlate> temPlates) {
		UppaalTemPlate template0 = temPlates.get(0);
		for(int i = 1;i < temPlates.size(); i++) {
			template0.getLocations().addAll(temPlates.get(i).getLocations());
			template0.getTransitions().addAll(temPlates.get(i).getTransitions());
		}
	}
	
	private static void outMessageConnectAutomatas(UppaalTemPlate uppaalTemPlate) {
		ArrayList<UppaalTransition> outTransitions = new ArrayList<UppaalTransition>();
		//找出外部message放到outTransition
		for(UppaalTransition transitionI : uppaalTemPlate.getTransitions()) {
			boolean out = false;
			int i = 0;
			String[] tempStrings = transitionI.getKind();
			while (tempStrings[i] != null) {//判断是否是外部message
				if (tempStrings[i].equals("synchronisation")) 
					{out = true;break;}
				i++;
			}
			transitionI.setOutKindIndex(i);
			if (out) {//copy一份到outTransition
				UppaalTransition newTransition = (UppaalTransition)transitionI.clone();
				outTransitions.add(newTransition);
			}
		}
		//遍历outTransition 新增自动机之间的连接transition
		for(UppaalTransition transitionI : outTransitions) {
			String nameI = transitionI.getNameText()[transitionI.outKindIndex];
			int lengthI = nameI.length();
			for(UppaalTransition transitionJ : outTransitions) {
				String nameJ = transitionJ.getNameText()[transitionJ.outKindIndex];
				int lengthJ = nameJ.length();
				if (nameI.substring(lengthI-1, lengthI).equals("!") &&
						nameJ.substring(lengthJ-1, lengthJ).equals("?") &&
						transitionI.getEAid().equals(transitionJ.getEAid()) ) {
					//是相同的消息不同形态 cofee! 和 cofee?
					UppaalTransition addTransition = (UppaalTransition)transitionI.clone();
					//cofee! -> cofee
					addTransition.getNameText()[addTransition.outKindIndex] = addTransition.getNameText()[addTransition.outKindIndex].substring(0, lengthI-1);
					addTransition.setSourceId(transitionI.getSourceId());
					addTransition.setTargetId(transitionJ.getSourceId());
					uppaalTemPlate.getTransitions().add(addTransition);
				}
			}
		}
		
	
	}

}
