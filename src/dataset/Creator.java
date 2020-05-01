package dataset;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import frames.Frame;
import universal.CodeClock;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import universal.Utils;
import utils.OS;
import utils.Packet.PROTOCOL;

public class Creator 
{
	private static final String MISSING = "?";
	private ArrayList<double[]> data = new ArrayList<double[]>();
	
	public Creator() 
	{
		
	}
	
	public void processFrame(Frame frame)
	{
		for (int i = 0; i < frame.protoStack.size(); i++)
		{
			if (frame.protoStack.get(i).protoType == PROTOCOL.IPV4)
			{
				
			}
			else if (frame.protoStack.get(i).protoType == PROTOCOL.TCP)
			{
				
			}
			else if (frame.protoStack.get(i).protoType == PROTOCOL.UDP)
			{
				
			}
		}
	}
	
	public static void UNB_ISCX_MALICIOUS_FLOWS_EXTRACTOR()
	{
		XMLoader.parse("/Users/Phil/Google Drive/Datasets/Jun13Testbed/TestbedSunJun13Flows.xml", "TestbedSunJun13Flows");
		String file = "/Users/Phil/Google Drive/Datasets/Jun13Testbed/Malicious.csv";
		FileWriter fileWriter = null;
		CodeClock clock = new CodeClock();
		
		try 
		{
			Utils.fileWiper(file);
			fileWriter = new FileWriter(file, true);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    
	    CodeLogger.log("Starting UNB ISCX Data Parser", DEPTH.PARENT);
	    clock.restart();
		
		for (int i = 0; i < XMLoader.data.size(); i++)
		{
			if (XMLoader.data.get(i).get("Tag").equals("Attack"))
			{
				StringBuilder sb = new StringBuilder();
				
				 sb.append("," + XMLoader.data.get(i).get("source"));
				 sb.append("," + XMLoader.data.get(i).get("destination"));
				 sb.append("," + XMLoader.data.get(i).get("sourcePort"));
				 sb.append("," + XMLoader.data.get(i).get("destinationPort"));
				 sb.append("," + XMLoader.data.get(i).get("protocolName"));
				 sb.append("," + XMLoader.data.get(i).get("startDateTime"));
				 sb.append("," + XMLoader.data.get(i).get("stopDateTime"));
				 
				 printWriter.println(sb.toString().substring(1));
			}
			
			if (i % 10000 == 0)
				 CodeLogger.log("Parsed " + i + " of " + XMLoader.data.size(), DEPTH.CHILD);
		}
		
		CodeLogger.log("Finished parsing " +  XMLoader.data.size() + " rows", DEPTH.CHILD);
		clock.end();
		
		printWriter.close();
		XMLoader.clear(); 
	}

}
