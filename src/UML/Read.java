package UML;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;

public class Read {
	ArrayList<TimingLifeline> umlLifelines = new ArrayList<TimingLifeline>();
	ArrayList<connector> umlConnectors = new ArrayList<connector>();

	public boolean hasNoLifeline() {

		if (umlLifelines.isEmpty()) // 生命线非空
		{

			return true;
		}
		return false;

	}

	public void load(Element root) throws Exception {
		ArrayList<Element> TimeLinelist = new ArrayList<Element>();		//生命线
		ArrayList<Element> timeLinexrefsList = new ArrayList<Element>();
		ArrayList<Element> connnectorList = new ArrayList<Element>();	//消息

		try {																//读取所有生命线 方到timelinelist中
			TimeLinelist.addAll(root.element("Model")
					.element("packagedElement").element("packagedElement")
					.element("ownedBehavior").elements("lifeline"));
		} catch (NullPointerException e) {
			System.out.println("生命线为空 退出！");
			System.exit(0);
		}

		timeLinexrefsList.addAll(root.element("Extension").element("elements")
				.elements("element"));
		connnectorList.addAll(root.element("Extension").element("connectors") //读取所有消息 放到connnectorList中
				.elements("connector"));
		
		Iterator<Element> xrefsIterator = timeLinexrefsList.iterator(); //3个游标
		Iterator<Element> lifelineIterator = TimeLinelist.iterator();
		Iterator<Element> connectorIterator = connnectorList.iterator();
		while (xrefsIterator.hasNext()) {					//去掉UML中的所有非生命线的元素 用来对生命线添加状态参数					
			Element xrefsTemp = xrefsIterator.next();
			// System.out.println(xrefsTemp.attribute("type").getValue());
			if (!xrefsTemp.attribute("type").getValue().equals("uml:TimeLine"))  
			{
				//System.out.println(xrefsIterator.getClass()+"  "+xrefsIterator..);
				xrefsIterator.remove();
					
					
			}
			// timeLinexrefsList.remove(xrefsTemp);
		}

		xrefsIterator = timeLinexrefsList.iterator();				
		while (lifelineIterator.hasNext()) { 			//遍历生命线
			Element lineTemp = lifelineIterator.next();// 获取了生命线的ID 名称 还有待处理的状态信息
			Element xrefsTemp = xrefsIterator.next();	//筛选后得到的生命线元素
			TimingLifeline temp = new TimingLifeline();
			temp.setLifelineID(lineTemp.attribute("id").getValue());
			temp.setName(lineTemp.attribute("name").getValue());
			temp.setXrefs(getStringInformation.getxrefs(xrefsTemp
					.element("extendedProperties").attribute("runstate")
					.getValue()));
			umlLifelines.add(temp);   					//添加生命线
		}
		System.out.println("UML生命线读取完成，一共: " + umlLifelines.size() + "条");

		while (connectorIterator.hasNext()) {		//遍历消息
			Element connectorElement = connectorIterator.next();
			connector connectorTemp = new connector();
			connectorTemp.setConnectorId(connectorElement.attribute("idref")
					.getValue());
			//connectorTemp.setInner(connectorElement.element("where").attribute("inner").getValue());
			connectorTemp.setSourceId(connectorElement.element("source")
					.attribute("idref").getValue());

			connectorTemp.setTragetId(connectorElement.element("target")
					.attribute("idref").getValue());
			
			connectorTemp.setSourceName(connectorElement.element("source").element("model")
					.attribute("name").getValue());

			connectorTemp.setTragetName(connectorElement.element("target").element("model")
					.attribute("name").getValue());//设置消息初始自动机到目标自动机的名称
			
			connectorTemp.setName(connectorElement.element("properties")
					.attribute("name").getValue());
			connectorTemp.setSendTime(getStringInformation
					.getSourceTime(connectorElement.element("style")
							.attribute("value").getValue()));
			// System.out.println(connectorTemp.getSendTime());
			//
			connectorTemp.setReceiveTime(getStringInformation
					.getReceiveTime(connectorElement.element("style")
							.attribute("value").getValue()));
			// System.out.println("YYYYY"+connectorTemp.getReceiveTime());
			umlConnectors.add(connectorTemp);//添加消息
		}
		System.out.println("消息信息读取完成 ，一共  " + umlConnectors.size() + "条");
	}

	public ArrayList<TimingLifeline> getTimingLine() {
		return umlLifelines;
	}

	public ArrayList<connector> getConnector() {
		return umlConnectors;
	}
}
