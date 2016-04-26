package UML;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.io.XMLWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
	 
public class write2 {

	
	
	     public static void main(String[] args) throws  IOException, Exception {
		// TODO Auto-generated method stub
 
		
	
	       
	        	
	        	SAXReader reader= new SAXReader();
	        	 int i=0;
	   			Document dom=reader.read("UPPAAL.xml");
	   			Element root=dom.getRootElement(); 
				List<Element> list=root.element("template").elements("location");
				Element template1=list.get(i);
				while(i<2)
				{
				 template1=list.get(i);
				 String xx = String.valueOf(22);
				 String yy=String.valueOf(22);
	        	template1.attribute("y").setText(xx);
	        	template1.attribute("x").setText(yy);
	        	i++;System.out.println(template1.attribute("y").getValue());
	           }
				
				XMLWriter writer =new XMLWriter(new FileOutputStream("UPPAAL.xml"),OutputFormat.createPrettyPrint());
				  writer.write(dom);
				  writer.close();
				
	           }
	}

