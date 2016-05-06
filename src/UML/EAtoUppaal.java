package UML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class EAtoUppaal {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Global global=new Global();
		
		//1.��������------------------------------------------------------------------------------------------------------
		HashSet<String> template_instantiations = new HashSet<String>();     //�洢template��name
		HashSet<String> global_declarations = new HashSet<String>();		//�洢channel��name
		ArrayList<UppaalTemPlate> temPlates = new ArrayList<UppaalTemPlate>();	//�洢����template
		ArrayList<TimingLifeline> Lifelines = new ArrayList<TimingLifeline>(); //�洢����lifeline
		ArrayList<connector> Connectors = new ArrayList<connector>();			//�洢����message
		
		UppaalLocation FL = new UppaalLocation();								//�洢��һ��location
		int id = 0;																//location��ID������
		//1.��������end---------------------------------------------------------------------------------------------------
		
		
		//2.��ȡuml.xml�е�����-------------------------------------------------------------------------------------------
		SAXReader reader = new SAXReader();// ��ȡ������

		Document dom = reader.read("0.xml");// ����XML��ȡ���������ĵ���dom����

		Element root = dom.getRootElement();// ��ȡ���ڵ�

		System.out.println("��ʼ��ȡEA�ļ�");
		Read uml = new Read();
		uml.load(root);
		if (uml.hasNoLifeline()) {
			System.exit(0);
			System.out.println("û���ҵ������ߣ��˳�");
		}

		Lifelines = uml.getTimingLine();
		Connectors = uml.getConnector();
		
		
		/*��message���Ż�����*/		
		HashMap <String,connector>  sourceIDsendTime_connector =new HashMap <String,connector>();    //���ڲ���FL�Ƿ�����Ϣ���ͷ�
		HashMap <String,connector>  tragertIDreceiveTime_connector =new HashMap <String,connector>();//���ڲ���FL�Ƿ�����Ϣ���ܷ�
		
		
		Iterator<connector> cIterator = Connectors.iterator();  //����������Ϣ ����sourceIDsendTime_connector ��tragertIDreceiveTime_connector
		while (cIterator.hasNext()) {
			connector temp = cIterator.next();
			sourceIDsendTime_connector.put("#sourceIdIs"+temp.getSourceId()+"#sendTimeIs"+temp.getSendTime(), temp);
			tragertIDreceiveTime_connector.put("#tragetIdIs"+temp.getTragetId()+"#receiveTimeIs"+temp.getReceiveTime(), temp);			
		}
		//2.��ȡuml.xml�е�����end----------------------------------------------------------------------------------------
		
		
		//3.��������������--------------------------------------------------------------------------------------------------
		Iterator<TimingLifeline> lineIterator = Lifelines.iterator();

		while (lineIterator.hasNext()) {
			//id = 0;
			// ArrayList<String> stateNameArrayList=new ArrayList<String>();
			TimingLifeline line = lineIterator.next();
			Iterator<lineInfo> lineState = line.getXrefs().iterator();
			template_instantiations.add(line.getName());// ��4��:��UPRAAL������һ��Templateȡ��ΪID
			UppaalTemPlate template = new UppaalTemPlate();
			template.setName(line.getName());
			System.out.println("���ڼ����Ϊ" + line.getName() + "��������");
			
			/*�Ե�ǰtemplate ��location�;�transition�Ż�����*/	
			HashMap <String,UppaalLocation> name_oldLocations = new HashMap <String,UppaalLocation>();//�����Ƿ��ǵ�ǰtemplate�ľ�״̬
			HashMap <String,UppaalTransition> nameSnameT_oldTransition = new HashMap<String,UppaalTransition>();//�����Ƿ��ǵ�ǰtemplate�ľ�״̬ת��
			
			//3.1������һ��lifeline��state���б���--------------------------------------------------------------------------			
			while (lineState.hasNext()) { // ��5��:��State or Condition
											// S��Ϊ��ת��6��,����ת��2����
				
				lineInfo State = lineState.next();
				UppaalLocation location = new UppaalLocation();
				UppaalTransition transition = new UppaalTransition();
				location.setLineEAID(line.getLifelineID());
				location.setName(State.getName()+":"+line.getName());
				System.out.println(location.getName());
				
				String print_transition_name= new String();  //���ڲ��� ��¼״̬ת�Ƶ�name
				//3.1.1��ʼ״̬
				if (State.value.equals("0")) // ��6��:��S�ǵ�һ��State or
												// Condition,ת��7��,����ת��9����
				{
					System.out.println("��ʼ״̬ " + State.name);

					location.setInit(true); // ��7��:��UPRAAL������һ��initial Location
											// B,ת��8����
					location.setInvariant(null); // ��8��:����B��InvariantΪ��,ת��28��
					location.setId(id++);
					 
					
					System.out.println("��location:"+location.getName()+"��ӵ�template��");
					template.locations.add(location);
					name_oldLocations.put(location.Name,location);
					
					FL = location;
				} else {
				//3.1.2�ǳ�ʼ״̬
					//3.1.2.1�ǳ�ʼ״̬  ����״̬
					if (!name_oldLocations.containsKey(location.Name)) // ��9��:��S��һ���µ�State
																			// or
																			// Conditionת��11��,
					{
						System.out.println("��״̬" + State.name);
						location.setId(id++); // ��11��:��UPPAAL������һ��Location
											// B������һ��Ǩ��FL��B,ת��12����
						location.setLineEAID(line.getLifelineID());
						// System.out.println();
						
						transition.setSourceId(FL.getId());
						transition.setTargetId(location.getId());
						transition.setNameS(FL.getName());
						transition.setNameT(location.getName());
						
						String c=new String();   //c��¼����Լ�������
						if (State.DConst == null)
							c="null";
						else
							c=DurationConstraint(State.DConst);	
						//3.1.2.1.1�ǳ�ʼ״̬  ����״̬  ����Լ�����ֲ�ͬ�����
						switch(c)
						{
						case "null":
															// ��19��:��B����Ϊnormal
															// Location,ת��20����
							location.setInvariant(null); 	// ��20��:����B��InvariantΪ��,ת��21����
							
							template.locations.add(location);
							name_oldLocations.put(location.Name,location);
							
							System.out.println("����Լ��Ϊnull");
							System.out.println("��location:"+location.getName()+"��ӵ�template��");
							break;
							
						case "0":
							location.setType(1);
							System.out.println("����λ�ã�" + State.getName()); // ��17��:��B����Ϊurgent
																			// Location
							location.setInvariant(null); // ��18��:����B��InvariantΪ��,ת��21��
							
							template.locations.add(location);
							name_oldLocations.put(location.Name,location);
							
							System.out.println("����Լ��Ϊ0");
							System.out.println("��location:"+location.getName()+"��ӵ�template��");
							break;
							
						default://t..t+n
							System.out.println("invariant=="+c);
							location.setInvariant("x<="+ c);
							
							int temp = Index(transition.Kind);
							transition.Kind[temp] = "assignment";
							transition.nameText[temp] = "x=0";
							
							template.locations.add(location);
							name_oldLocations.put(location.Name,location);
							
							System.out.println("����Լ��Ϊ: "+State.DConst);
							System.out.println("��location:"+location.getName()+"��ӵ�template��");
							break;
							
						}
						
					} else {
						//3.1.2.2�ǳ�ʼ״̬  �Ǿ�״̬
						location = name_oldLocations.get(location.Name);
						transition.setSourceId(FL.getId());
						transition.setTargetId(location.getId());
						transition.setNameS(FL.getName());
						transition.setNameT(location.getName());
					 
					}
					//3.1.2�õ���״̬��Ǩ���¼�Event
					if (State.getEvent() != null) // ��21��:��S��Event��TimeConstraint!=NULLת��22��,����ת��23����
					{
						int temp = Index(transition.Kind);
						transition.Kind[temp] = "guard";
						transition.nameText[temp] = State.getEvent(); // ��22��:����FL��B��Guard��ΪEvent&TimeConstraint,ת��23����

					}
					
					//3.1.3�ж�FL����һ��״̬���Ƿ���massage�ķ��ͷ�
					
					String SendKeyString = "#sourceIdIs"+ FL.getLineEAID()+ "#sendTimeIs" + State.getValue();
					if ( sourceIDsendTime_connector.containsKey(SendKeyString) ) // ��23��:���FL��һ����Ϣmessage�ķ�����,ת��24��,����ת��26����
					{
						String sender_name=sourceIDsendTime_connector.get(SendKeyString).getName();
						String EAid = sourceIDsendTime_connector.get(SendKeyString).getConnectorId();
						print_transition_name=sender_name;
						global_declarations.add(sender_name); 
						System.out.println("��Ϣ��Ϊ��"+sender_name);  	 // ��24��:��UPPAAL������һ��chan
																		// message,ת��25����
						 
						int temp = Index(transition.Kind);
                        //transition.setInner(sourceIDsendTime_connector.get(SendKeyString).getInner());
						transition.Kind[temp] = "synchronisation";
						transition.nameText[temp] = sender_name
								+ "!"; // ��25��:����FL��B��Sync��Ϊmessage!,ת��28����
						// System.out.println(transition.nameText[temp]);
						
						 transition.setTime(State.getValue());//Ϊ��ģ����֤���ӵķ���ʱ���¼
						 transition.setEAid(EAid);
						transition.setFromName(sourceIDsendTime_connector.get(SendKeyString).getSourceName());
						transition.setToName(sourceIDsendTime_connector.get(SendKeyString).getTragetName());					 
						
						
					} 
					//3.1.4�ж�FL����һ��״̬���Ƿ���massage�Ľ��ܷ�
					String ReceiveKeyString = "#tragetIdIs"+FL.getLineEAID()+ "#receiveTimeIs" + State.getValue();
					if ( tragertIDreceiveTime_connector.containsKey(ReceiveKeyString)) // ��26��:���FL��һ����Ϣmessage�Ľ�����,ת��27��,����
												// ת��28����
					{
						String receiver_name=tragertIDreceiveTime_connector.get(ReceiveKeyString).getName();
						String EAid = tragertIDreceiveTime_connector.get(ReceiveKeyString).getConnectorId();
						print_transition_name=receiver_name;
						global_declarations.add(receiver_name); // ��24��:��UPPAAL������һ��chan
						System.out.println("��Ϣ��Ϊ��"+receiver_name); // message,ת��25����
						
						int temp = Index(transition.Kind);
                        //transition.setInner(tragertIDreceiveTime_connector.get(ReceiveKeyString).getInner());
						transition.Kind[temp] = "synchronisation";
						transition.nameText[temp] = receiver_name
								+ "?"; // ��27��:����FL��B��Sync��Ϊmessage?,ת��28��
						 transition.setTime(State.getValue());//Ϊ��ģ����֤���ӵķ���ʱ���¼ -----
						 transition.setEAid(EAid);
						 transition.setFromName(tragertIDreceiveTime_connector.get(ReceiveKeyString).getSourceName());					 							
						 transition.setToName(tragertIDreceiveTime_connector.get(ReceiveKeyString).getTragetName());					 
					} 
					//������а����ظ���Ǩ�� ��ʱ�䲻ͬ��
//					template.transitions.add(transition);
//					
//					System.out.println("���"+FL.Name+"--"+print_transition_name+"("+State.getEvent()+")"+"->"+location.Name);
					
					
					//������а������ظ���Ǩ�� 
					//3.1.5 �ж��Ƿ����Ѿ����ڵ�Ǩ��
					String STKeyString="#nameSIs"+transition.nameS+"#nameTIs"+transition.nameT;
					
																								
					if(!nameSnameT_oldTransition.containsKey(STKeyString))//��key ˵��������nameS->nameT��Ǩ��
					{					
						template.transitions.add(transition);
						nameSnameT_oldTransition.put(STKeyString, transition);
						System.out.println("���"+FL.Name+"--"+print_transition_name+"("+State.getEvent()+")"+"->"+location.Name);
					}
					else
					{
						System.out.println("�Ѿ�����"+FL.Name+"--"+print_transition_name+"("+State.getEvent()+")"+"->"+location.Name);
					}
					
					//
					
					
					
					FL = location;
				}

			}
			//3.1������һ��lifeline��state���б���end-----------------------------------------------------------------------
			
			temPlates.add(template);
		 
		}
		//3.��������������end-----------------------------------------------------------------------------------------------
		
				//�ϲ�templates
				mergeTemplates(temPlates);
				//������Ϣ�Ŀ����
				outMessageConnectAutomatas(temPlates.get(0));
				
		//4.д�뵽UPPAAL.xml��----------------------------------------------------------------------------------------------
		Write.creatXML("UPPAAL.xml", global_declarations,
				template_instantiations, temPlates);
		//4.д�뵽UPPAAL.xml��end-------------------------------------------------------------------------------------------
		
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
		//�ҳ��ⲿmessage�ŵ�outTransition
		for(UppaalTransition transitionI : uppaalTemPlate.getTransitions()) {
			boolean out = false;
			int i = 0;
			String[] tempStrings = transitionI.getKind();
			while (tempStrings[i] != null) {//�ж��Ƿ����ⲿmessage
				if (tempStrings[i].equals("synchronisation")) 
					{out = true;break;}
				i++;
			}
			transitionI.setOutKindIndex(i);
			if (out) {//copyһ�ݵ�outTransition
				//UppaalTransition newTransition = (UppaalTransition)transitionI.clone();�����ÿ�¡
				outTransitions.add(transitionI);
			}
		}
		//����outTransition �����Զ���֮�������transition
		for(UppaalTransition transitionI : outTransitions) {
			String nameI = transitionI.getNameText()[transitionI.outKindIndex];
			int lengthI = nameI.length();
			for(UppaalTransition transitionJ : outTransitions) {
				String nameJ = transitionJ.getNameText()[transitionJ.outKindIndex];
				int lengthJ = nameJ.length();
				if (nameI.substring(lengthI-1, lengthI).equals("!") &&
						nameJ.substring(lengthJ-1, lengthJ).equals("?") &&
						transitionI.getEAid().equals(transitionJ.getEAid()) ) {
					//����ͬ����Ϣ��ͬ��̬ cofee! �� cofee?
					UppaalTransition addTransition = (UppaalTransition)transitionI.clone();
					
					addTransition.getNameText()[addTransition.outKindIndex] = addTransition.getNameText()[addTransition.outKindIndex];
					//cofee! -> cofee//.substring(0, lengthI-1);
					addTransition.setSourceId(transitionI.getSourceId());
					addTransition.setTargetId(transitionJ.getSourceId());
					uppaalTemPlate.getTransitions().add(addTransition);
					
					//ȥ�������ڵģ���Ϣ 
					uppaalTemPlate.getTransitions().remove(transitionI);
				}
			}
		}
	}
	public static UppaalLocation findState(ArrayList<UppaalLocation> locations,
			UppaalLocation location) {
		Iterator<UppaalLocation> LIterator = locations.iterator();
		while (LIterator.hasNext()) {
			UppaalLocation temp = LIterator.next();
			if (temp.getName().equals(location.getName())) {
				
				return temp;
			}
		}

		return null;
	}
	public static boolean ArraysContainsString(String [] s,String str)
	{
		for(int i=0;i<s.length;i++)
		{
			if(s[i]==null)
				break;
			if(s[i].equals(str))
				return true;
		}
		
		return false;
	}
	public static boolean isnewState(ArrayList<UppaalLocation> nameList,
			String name) {
		Iterator<UppaalLocation> string = nameList.iterator();
		while (string.hasNext()) {
			UppaalLocation temp = string.next();
			if (temp.getName().equals(name)) {
				// System.out.println("compare: "+name);
				return false;
			}
		}

		return true;
	}

	public static String DurationConstraint(String str) {

		if (str.equals("0")) {
			return "0";
		} else {
			String[] strings = str.split("[+]");
			// System.out.println(strings[1]);
			return strings[1];
		}

	}

	public static int Index(String[] str) {

		for (int i = 0; i < str.length; i++)
			if (str[i] == null)
				return i;
		return -1;
	}

	

	public static String SenderName(ArrayList<connector> Connectors, String ID,
			String value) {
		// String messageName=new String();
		Iterator<connector> cIterator = Connectors.iterator();
		while (cIterator.hasNext()) {
			connector temp = cIterator.next();
			System.out.println(temp.getSourceId());
			if (ID.equals(temp.getSourceId())
					&& temp.getSendTime().equals(value)) { // System.out.println("send  "+temp.getName());
				return temp.getName();

			}
		}

		return null;
	}

	
	
	public static String ReceiverName(ArrayList<connector> Connectors,
			String ID, String value) {
		// String messageName=new String();
		Iterator<connector> cIterator = Connectors.iterator();
		while (cIterator.hasNext()) {
			connector temp = cIterator.next();
			if (ID.equals(temp.getTragetId())
					&& temp.getReceiveTime().equals(value)) {
				return temp.getName();

			}
		}

		return null;
	}
}
