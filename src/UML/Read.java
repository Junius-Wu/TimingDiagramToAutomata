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

		if (umlLifelines.isEmpty()) // �����߷ǿ�
		{

			return true;
		}
		return false;

	}

	public void load(Element root) throws Exception {
		ArrayList<Element> TimeLinelist = new ArrayList<Element>();		//������
		ArrayList<Element> timeLinexrefsList = new ArrayList<Element>();
		ArrayList<Element> connnectorList = new ArrayList<Element>();	//��Ϣ

		try {																//��ȡ���������� ����timelinelist��
			TimeLinelist.addAll(root.element("Model")
					.element("packagedElement").element("packagedElement")
					.element("ownedBehavior").elements("lifeline"));
		} catch (NullPointerException e) {
			System.out.println("������Ϊ�� �˳���");
			System.exit(0);
		}

		timeLinexrefsList.addAll(root.element("Extension").element("elements")
				.elements("element"));
		connnectorList.addAll(root.element("Extension").element("connectors") //��ȡ������Ϣ �ŵ�connnectorList��
				.elements("connector"));
		
		Iterator<Element> xrefsIterator = timeLinexrefsList.iterator(); //3���α�
		Iterator<Element> lifelineIterator = TimeLinelist.iterator();
		Iterator<Element> connectorIterator = connnectorList.iterator();
		while (xrefsIterator.hasNext()) {					//ȥ��UML�е����з������ߵ�Ԫ�� ���������������״̬����					
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
		while (lifelineIterator.hasNext()) { 			//����������
			Element lineTemp = lifelineIterator.next();// ��ȡ�������ߵ�ID ���� ���д������״̬��Ϣ
			Element xrefsTemp = xrefsIterator.next();	//ɸѡ��õ���������Ԫ��
			TimingLifeline temp = new TimingLifeline();
			temp.setLifelineID(lineTemp.attribute("id").getValue());
			temp.setName(lineTemp.attribute("name").getValue());
			temp.setXrefs(getStringInformation.getxrefs(xrefsTemp
					.element("extendedProperties").attribute("runstate")
					.getValue()));
			umlLifelines.add(temp);   					//���������
		}
		System.out.println("UML�����߶�ȡ��ɣ�һ��: " + umlLifelines.size() + "��");

		while (connectorIterator.hasNext()) {		//������Ϣ
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
					.attribute("name").getValue());//������Ϣ��ʼ�Զ�����Ŀ���Զ���������
			
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
			umlConnectors.add(connectorTemp);//�����Ϣ
		}
		System.out.println("��Ϣ��Ϣ��ȡ��� ��һ��  " + umlConnectors.size() + "��");
	}

	public ArrayList<TimingLifeline> getTimingLine() {
		return umlLifelines;
	}

	public ArrayList<connector> getConnector() {
		return umlConnectors;
	}
}
