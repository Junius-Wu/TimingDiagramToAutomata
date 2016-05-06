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
		
		//1.声明变量------------------------------------------------------------------------------------------------------
		HashSet<String> template_instantiations = new HashSet<String>();     //存储template的name
		HashSet<String> global_declarations = new HashSet<String>();		//存储channel的name
		ArrayList<UppaalTemPlate> temPlates = new ArrayList<UppaalTemPlate>();	//存储所有template
		ArrayList<TimingLifeline> Lifelines = new ArrayList<TimingLifeline>(); //存储所有lifeline
		ArrayList<connector> Connectors = new ArrayList<connector>();			//存储所有message
		
		UppaalLocation FL = new UppaalLocation();								//存储上一个location
		int id = 0;																//location的ID计数器
		//1.声明变量end---------------------------------------------------------------------------------------------------
		
		
		//2.读取uml.xml中的数据-------------------------------------------------------------------------------------------
		SAXReader reader = new SAXReader();// 获取解析器

		Document dom = reader.read("0.xml");// 解析XML获取代表整个文档的dom对象

		Element root = dom.getRootElement();// 获取根节点

		System.out.println("开始读取EA文件");
		Read uml = new Read();
		uml.load(root);
		if (uml.hasNoLifeline()) {
			System.exit(0);
			System.out.println("没有找到生命线，退出");
		}

		Lifelines = uml.getTimingLine();
		Connectors = uml.getConnector();
		
		
		/*对message的优化查找*/		
		HashMap <String,connector>  sourceIDsendTime_connector =new HashMap <String,connector>();    //用于查找FL是否是消息发送方
		HashMap <String,connector>  tragertIDreceiveTime_connector =new HashMap <String,connector>();//用于查找FL是否是消息接受方
		
		
		Iterator<connector> cIterator = Connectors.iterator();  //遍历所有消息 构建sourceIDsendTime_connector 和tragertIDreceiveTime_connector
		while (cIterator.hasNext()) {
			connector temp = cIterator.next();
			sourceIDsendTime_connector.put("#sourceIdIs"+temp.getSourceId()+"#sendTimeIs"+temp.getSendTime(), temp);
			tragertIDreceiveTime_connector.put("#tragetIdIs"+temp.getTragetId()+"#receiveTimeIs"+temp.getReceiveTime(), temp);			
		}
		//2.读取uml.xml中的数据end----------------------------------------------------------------------------------------
		
		
		//3.遍历所有生命线--------------------------------------------------------------------------------------------------
		Iterator<TimingLifeline> lineIterator = Lifelines.iterator();

		while (lineIterator.hasNext()) {
			//id = 0;
			// ArrayList<String> stateNameArrayList=new ArrayList<String>();
			TimingLifeline line = lineIterator.next();
			Iterator<lineInfo> lineState = line.getXrefs().iterator();
			template_instantiations.add(line.getName());// 第4步:在UPRAAL中声明一个Template取名为ID
			UppaalTemPlate template = new UppaalTemPlate();
			template.setName(line.getName());
			System.out.println("现在检测名为" + line.getName() + "的生命线");
			
			/*对当前template 旧location和旧transition优化查找*/	
			HashMap <String,UppaalLocation> name_oldLocations = new HashMap <String,UppaalLocation>();//查找是否是当前template的旧状态
			HashMap <String,UppaalTransition> nameSnameT_oldTransition = new HashMap<String,UppaalTransition>();//查找是否是当前template的旧状态转移
			
			//3.1对其中一个lifeline的state进行遍历--------------------------------------------------------------------------			
			while (lineState.hasNext()) { // 第5步:若State or Condition
											// S不为空转第6步,否则转第2步。
				
				lineInfo State = lineState.next();
				UppaalLocation location = new UppaalLocation();
				UppaalTransition transition = new UppaalTransition();
				location.setLineEAID(line.getLifelineID());
				location.setName(State.getName()+":"+line.getName());
				System.out.println(location.getName());
				
				String print_transition_name= new String();  //用于测试 记录状态转移的name
				//3.1.1初始状态
				if (State.value.equals("0")) // 第6步:若S是第一个State or
												// Condition,转第7步,否则转第9步。
				{
					System.out.println("初始状态 " + State.name);

					location.setInit(true); // 第7步:在UPRAAL中声明一个initial Location
											// B,转第8步。
					location.setInvariant(null); // 第8步:设置B的Invariant为空,转第28步
					location.setId(id++);
					 
					
					System.out.println("将location:"+location.getName()+"添加到template中");
					template.locations.add(location);
					name_oldLocations.put(location.Name,location);
					
					FL = location;
				} else {
				//3.1.2非初始状态
					//3.1.2.1非初始状态  是新状态
					if (!name_oldLocations.containsKey(location.Name)) // 第9步:若S是一个新的State
																			// or
																			// Condition转第11步,
					{
						System.out.println("新状态" + State.name);
						location.setId(id++); // 第11步:在UPPAAL中声明一个Location
											// B并声明一个迁移FL→B,转第12步。
						location.setLineEAID(line.getLifelineID());
						// System.out.println();
						
						transition.setSourceId(FL.getId());
						transition.setTargetId(location.getId());
						transition.setNameS(FL.getName());
						transition.setNameT(location.getName());
						
						String c=new String();   //c记录持续约束的情况
						if (State.DConst == null)
							c="null";
						else
							c=DurationConstraint(State.DConst);	
						//3.1.2.1.1非初始状态  是新状态  持续约束三种不同的情况
						switch(c)
						{
						case "null":
															// 第19步:将B设置为normal
															// Location,转第20步。
							location.setInvariant(null); 	// 第20步:设置B的Invariant为空,转第21步。
							
							template.locations.add(location);
							name_oldLocations.put(location.Name,location);
							
							System.out.println("持续约束为null");
							System.out.println("将location:"+location.getName()+"添加到template中");
							break;
							
						case "0":
							location.setType(1);
							System.out.println("紧迫位置：" + State.getName()); // 第17步:将B设置为urgent
																			// Location
							location.setInvariant(null); // 第18步:设置B的Invariant为空,转第21步
							
							template.locations.add(location);
							name_oldLocations.put(location.Name,location);
							
							System.out.println("持续约束为0");
							System.out.println("将location:"+location.getName()+"添加到template中");
							break;
							
						default://t..t+n
							System.out.println("invariant=="+c);
							location.setInvariant("x<="+ c);
							
							int temp = Index(transition.Kind);
							transition.Kind[temp] = "assignment";
							transition.nameText[temp] = "x=0";
							
							template.locations.add(location);
							name_oldLocations.put(location.Name,location);
							
							System.out.println("持续约束为: "+State.DConst);
							System.out.println("将location:"+location.getName()+"添加到template中");
							break;
							
						}
						
					} else {
						//3.1.2.2非初始状态  是旧状态
						location = name_oldLocations.get(location.Name);
						transition.setSourceId(FL.getId());
						transition.setTargetId(location.getId());
						transition.setNameS(FL.getName());
						transition.setNameT(location.getName());
					 
					}
					//3.1.2得到该状态的迁移事件Event
					if (State.getEvent() != null) // 第21步:若S的Event或TimeConstraint!=NULL转第22步,否则转第23步。
					{
						int temp = Index(transition.Kind);
						transition.Kind[temp] = "guard";
						transition.nameText[temp] = State.getEvent(); // 第22步:设置FL→B的Guard域为Event&TimeConstraint,转第23步。

					}
					
					//3.1.3判断FL（上一个状态）是否是massage的发送方
					
					String SendKeyString = "#sourceIdIs"+ FL.getLineEAID()+ "#sendTimeIs" + State.getValue();
					if ( sourceIDsendTime_connector.containsKey(SendKeyString) ) // 第23步:如果FL是一个消息message的发送者,转第24步,否则转第26步。
					{
						String sender_name=sourceIDsendTime_connector.get(SendKeyString).getName();
						String EAid = sourceIDsendTime_connector.get(SendKeyString).getConnectorId();
						print_transition_name=sender_name;
						global_declarations.add(sender_name); 
						System.out.println("消息名为："+sender_name);  	 // 第24步:在UPPAAL中声明一个chan
																		// message,转第25步。
						 
						int temp = Index(transition.Kind);
                        //transition.setInner(sourceIDsendTime_connector.get(SendKeyString).getInner());
						transition.Kind[temp] = "synchronisation";
						transition.nameText[temp] = sender_name
								+ "!"; // 第25步:设置FL→B的Sync域为message!,转第28步。
						// System.out.println(transition.nameText[temp]);
						
						 transition.setTime(State.getValue());//为了模型验证增加的发送时间记录
						 transition.setEAid(EAid);
						transition.setFromName(sourceIDsendTime_connector.get(SendKeyString).getSourceName());
						transition.setToName(sourceIDsendTime_connector.get(SendKeyString).getTragetName());					 
						
						
					} 
					//3.1.4判断FL（上一个状态）是否是massage的接受方
					String ReceiveKeyString = "#tragetIdIs"+FL.getLineEAID()+ "#receiveTimeIs" + State.getValue();
					if ( tragertIDreceiveTime_connector.containsKey(ReceiveKeyString)) // 第26步:如果FL是一个消息message的接收者,转第27步,否则
												// 转第28步。
					{
						String receiver_name=tragertIDreceiveTime_connector.get(ReceiveKeyString).getName();
						String EAid = tragertIDreceiveTime_connector.get(ReceiveKeyString).getConnectorId();
						print_transition_name=receiver_name;
						global_declarations.add(receiver_name); // 第24步:在UPPAAL中声明一个chan
						System.out.println("消息名为："+receiver_name); // message,转第25步。
						
						int temp = Index(transition.Kind);
                        //transition.setInner(tragertIDreceiveTime_connector.get(ReceiveKeyString).getInner());
						transition.Kind[temp] = "synchronisation";
						transition.nameText[temp] = receiver_name
								+ "?"; // 第27步:设置FL→B的Sync域为message?,转第28步
						 transition.setTime(State.getValue());//为了模型验证增加的发送时间记录 -----
						 transition.setEAid(EAid);
						 transition.setFromName(tragertIDreceiveTime_connector.get(ReceiveKeyString).getSourceName());					 							
						 transition.setToName(tragertIDreceiveTime_connector.get(ReceiveKeyString).getTragetName());					 
					} 
					//添加所有包括重复的迁移 （时间不同）
//					template.transitions.add(transition);
//					
//					System.out.println("添加"+FL.Name+"--"+print_transition_name+"("+State.getEvent()+")"+"->"+location.Name);
					
					
					//添加所有包括不重复的迁移 
					//3.1.5 判断是否是已经存在的迁移
					String STKeyString="#nameSIs"+transition.nameS+"#nameTIs"+transition.nameT;
					
																								
					if(!nameSnameT_oldTransition.containsKey(STKeyString))//无key 说明不存在nameS->nameT的迁移
					{					
						template.transitions.add(transition);
						nameSnameT_oldTransition.put(STKeyString, transition);
						System.out.println("添加"+FL.Name+"--"+print_transition_name+"("+State.getEvent()+")"+"->"+location.Name);
					}
					else
					{
						System.out.println("已经存在"+FL.Name+"--"+print_transition_name+"("+State.getEvent()+")"+"->"+location.Name);
					}
					
					//
					
					
					
					FL = location;
				}

			}
			//3.1对其中一个lifeline的state进行遍历end-----------------------------------------------------------------------
			
			temPlates.add(template);
		 
		}
		//3.遍历所有生命线end-----------------------------------------------------------------------------------------------
		
				//合并templates
				mergeTemplates(temPlates);
				//处理消息的夸对象
				outMessageConnectAutomatas(temPlates.get(0));
				
		//4.写入到UPPAAL.xml中----------------------------------------------------------------------------------------------
		Write.creatXML("UPPAAL.xml", global_declarations,
				template_instantiations, temPlates);
		//4.写入到UPPAAL.xml中end-------------------------------------------------------------------------------------------
		
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
				//UppaalTransition newTransition = (UppaalTransition)transitionI.clone();并不用克隆
				outTransitions.add(transitionI);
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
					
					addTransition.getNameText()[addTransition.outKindIndex] = addTransition.getNameText()[addTransition.outKindIndex];
					//cofee! -> cofee//.substring(0, lengthI-1);
					addTransition.setSourceId(transitionI.getSourceId());
					addTransition.setTargetId(transitionJ.getSourceId());
					uppaalTemPlate.getTransitions().add(addTransition);
					
					//去掉对象内的！消息 
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
