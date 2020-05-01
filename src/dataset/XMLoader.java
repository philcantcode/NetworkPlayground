package dataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import universal.CodeClock;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;

public class XMLoader 
{	
	public static ArrayList<LinkedHashMap<String, String>> data = new ArrayList<LinkedHashMap<String, String>>(500000);
		
	private XMLoader()
	{
		
	}
	
	public static void parse(String xmlFile, String elementID) 
	{		
		try {
			CodeLogger.log("Started Loading XML file", DEPTH.PARENT);
			CodeClock clock = new CodeClock();
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		    Document document = docBuilder.parse(new File(xmlFile));
	
		    NodeList nodeList = document.getElementsByTagName("*");
		    
		    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
		    
		    boolean start = false;
		    		    
		    for (int i = 0; i < nodeList.getLength(); i++) 
		    {
		        Node node = nodeList.item(i);
		        
		        if (node.getNodeType() == Node.ELEMENT_NODE) 
		        {
		            if (node.getNodeName().equals(elementID))
		            {
		            	if (start == true)
		            	{
		            		data.add(row);
		            	}
		            	
		            	row = new LinkedHashMap<String, String>();
		            	start = true;
		            }
		            else if (start == true)
		            {
		            	row.put(node.getNodeName(), node.getTextContent());
		            }
		        }
		        
		        if (i % 10000 == 0)
		        	CodeLogger.log("Loaded " + i + " of " + nodeList.getLength(), DEPTH.CHILD);
		    }
		    
		    CodeLogger.log("Finished Loading " + nodeList.getLength(), DEPTH.CHILD);
		    clock.end();
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void clear()
	{
		XMLoader.data.clear();
	}
}
