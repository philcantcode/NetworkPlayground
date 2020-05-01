package experiments;

import java.util.ArrayList;

import engine.PcapParser;
import frames.Frame;
import frames.TCP;
import substructures.Flow;
import universal.CSVLoader;
import universal.CSVWriter;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import utils.Packet.PROTOCOL;

public class OneHotFactory extends Experiment
{	
	private static int numCols = 0;
	private static int numRows = 0;
	private static boolean saveHeaders = false;
	private static boolean saveData = false;
	private static final int FLUSH_TO_FILE_INCREMENT = 10000;
	private static final boolean RELEASE_AFTER_FLUSH = true;
	private static final String OUTPUT_FOLDER = "/Users/Phil/Google Drive/Datasets/OneHotFactory/";
	private static String OUTPUT_FILE_NAME = "onehot.csv";
	
	private static ArrayList<String> headers = new ArrayList<String>();
	private static ArrayList<ArrayList<Integer>> data = new ArrayList<ArrayList<Integer>>(FLUSH_TO_FILE_INCREMENT);
	private ArrayList<Integer> row = new ArrayList<Integer>();
	private static int countMal = 0;
	private static CSVWriter csvWriter = null;
	
	public OneHotFactory() 
	{
		
	}
	
	@Override
	public void frameHook(Frame frame)
	{
		String hash = "";
		
		if (frame.protoStack.get(0).protoType == PROTOCOL.IEEE_802_3_ETHERNET_HEADER)
		{
			if (frame.protoStack.get(1).protoType == PROTOCOL.IPV4)
			{
				addFeature("IPv4", 1);
				//addFeature(frame.get(1).field("source").ip() + " (IPv4)", 1);
				//addFeature(frame.get(1).field("destination").ip() + " (IPv4)", 1);
				addFeature("Type_of_Service_IPv4", frame.get(1).field("typeOfService").num());
				addFeature("Packet_Size_bytes_IPv4", frame.get(1).packetSize());
				addFeature("Header_Size_bytes_IPv4", frame.get(1).headerSize());
				addFeature("Identification_IPv4", frame.get(1).field("identification").num());
				addFeature("Unassigned_IPv4", frame.get(1).field("unassigned").num());
				addFeature("Dont_Frag_Flag_IPv4", frame.get(1).field("dontFrag").num());
				addFeature("More_Frag_Flag_IPv4", frame.get(1).field("moreFrag").num());
				addFeature("Time_To_Live_IPv4", frame.get(1).field("ttl").num());
				
			}
			else if (frame.protoStack.get(1).protoType == PROTOCOL.IPV6)
			{
				addFeature("IPv6", 1);
				//addFeature(frame.get(1).field("source").ip() + " (IPv6)", 1);
				//addFeature(frame.get(1).field("destination").ip() + " (IPv6)", 1);
				addFeature("Traffic_Class_IPv6", frame.get(1).field("trafficClass").num());
				addFeature("Packet_Size_bytes_IPv6", frame.get(1).packetSize());
				addFeature("Header_Size_bytes_IPv6", frame.get(1).headerSize());
				addFeature("Data_Size_bytes_IPv6", frame.get(1).dataSize());
				addFeature("Next_Header_IPv6", frame.get(1).field("nextHeader").num());
				addFeature("Hop_Limit_IPv6", frame.get(1).field("hopLimit").num());
			}
			else if (frame.protoStack.get(1).protoType == PROTOCOL.ARP)
			{
				addFeature("ARP", 1);
				addFeature(frame.get(1).field("senderProtoAddress").ip() + "_ARP", 1);
				addFeature(frame.get(1).field("senderMAC").mac() + "_ARP", 1);
				addFeature(frame.get(1).field("targetProtoAddress").ip() + "_ARP", 1);
				addFeature(frame.get(1).field("targetMAC").mac() + "_ARP", 1);
			}
			
			if (frame.protoStack.get(2).protoType == PROTOCOL.TCP)
			{
				addFeature("TCP", 1);
				addFeature("Port_Src_TCP", frame.get(2).field("srcPort").num());
				addFeature("Port_Dst_TCP", frame.get(2).field("dstPort").num());
				//addFeature("Port " + frame.get(2).field("srcPort").num() + " (TCP)", 1);
				//addFeature("Port " + frame.get(2).field("dstPort").num() + " (TCP)", 1);
				addFeature("Header_Size_TCP", frame.get(2).headerSize());
				addFeature("Data_Size_TCP", frame.get(2).dataSize());
				addFeature("Packet_Size_TCP", frame.get(2).packetSize());
				addFeature("Reserved_TCP", frame.get(2).field("reserved").num());
				addFeature("Flag_NS_TCP", frame.get(2).field("flagNS").num());
				addFeature("Flag_CWR_TCP", frame.get(2).field("flagCWR").num());
				addFeature("Flag_ECE_TCP", frame.get(2).field("flagECE").num());
				addFeature("Flag_UGR_TCP", frame.get(2).field("flagURG").num());
				addFeature("Flag_ACK_TCP", frame.get(2).field("flagACK").num());
				addFeature("Flag_PSH_TCP", frame.get(2).field("flagPSH").num());
				addFeature("Flag_RST_TCP", frame.get(2).field("flagRST").num());
				addFeature("Flag_SYN_TCP", frame.get(2).field("flagSYN").num());
				addFeature("Flag_FIN_TCP", frame.get(2).field("flagFIN").num());
				addFeature("Window_Size_TCP", frame.get(2).field("windowSize").num());
				
				hash = ((TCP) frame.get(2)).netFlowHash;
			}
			else if (frame.protoStack.get(2).protoType == PROTOCOL.UDP)
			{
				addFeature("UDP", 1);
				addFeature("Port_Src_UDP", frame.get(2).field("srcPort").num());
				addFeature("Port_Dst_UDP", frame.get(2).field("dstPort").num());
				//addFeature("Port " + frame.get(2).field("srcPort").num() + " (UDP)", 1);
				//addFeature("Port " + frame.get(2).field("dstPort").num() + " (UDP)", 1);
				addFeature("Length_UDP", frame.get(2).field("length").num());
			}
		}
		
		int tag = temporalFlowTag(hash, frame.dateTime);
		OneHotFactory.countMal += tag;
		addFeature("Class", tag);
		
		compileRow();
		
		if (OneHotFactory.saveData && data.size() >= FLUSH_TO_FILE_INCREMENT)
		{
			csvWriter.flush(data);
			data.clear();
		}
	}
	
	private void addFeature(String feature, int value)
	{
		int index = featureIndex(feature);
			
		if (OneHotFactory.saveHeaders == true)
		{
			if (index == -1)
				index = addHeader(feature);
		}
		
		if (OneHotFactory.saveData == true)
		{		
			this.row.set(index, value);
		}
	}
	
	// Add the feature row to the dataset
	private void compileRow()
	{
		if (OneHotFactory.saveData == true)
			data.add(row);
		
		row = new ArrayList<Integer>();
		
		while (row.size() < numCols)
		{
			row.add(0);
		}
		
		OneHotFactory.numRows++;
	}
	
	private int featureIndex(String feature)
	{		
		if (headers.contains(feature))
			return headers.indexOf(feature);
		else
			return -1;
	}
	
	private int addHeader(String feature)
	{
		numCols++;
		headers.add(feature);
		resize();
				
		return featureIndex(feature);
	}
	
	public void resize()
	{
		for (int i = 0; i < data.size(); i++)
		{
			data.get(i).add(0);
		}
	}

	@Override
	public void flowHook(Flow f) 
	{
		
	}

	@Override
	public void init() 
	{
		String pcapFile = "/Users/Phil/Google Drive/Datasets/2017ThurInfil/Thursday-WorkingHours.pcap";
		csvWriter = new CSVWriter(OUTPUT_FOLDER + OUTPUT_FILE_NAME);
		csvWriter.clearFile();
		
		CodeLogger.log("Stage 1: Now loading the headers", DEPTH.PARENT);
		OneHotFactory.saveHeaders = true;
		OneHotFactory.saveData = false;
		PcapParser.parsePCAP(pcapFile);
		csvWriter.addRow(headers);
		csvWriter.flush();
				
		CodeLogger.log("Stage 2: Now loading the data", DEPTH.PARENT);
		OneHotFactory.saveHeaders = false;
		OneHotFactory.saveData = true;
		PcapParser.parsePCAP(pcapFile);
		csvWriter.flush(data);
	}
	
	@Override
	public void summary() 
	{
		
	}

}
