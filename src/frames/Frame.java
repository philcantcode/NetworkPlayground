package frames;

import java.util.ArrayList;

import config.Settings;
import experiments.Experiment;
import substructures.AccessPoint;
import substructures.HostDevice;
import substructures.IP;
import substructures.NetFlow;
import substructures.URL;
import universal.Utils;
import utils.Packet;
import utils.Parser;

/* 1: https://github.com/the-tcpdump-group/libpcap/blob/master/pcap/dlt.h */
public class Frame
{
	public int frameID = -1;
	public static int frameCount = 0;			// Number of packets captured / read in
	private static int unknownErrorCount = 0;			// Unknown error count
	private static int incompleteErrorCount = 0;		// While parsing a frame type the end of the hex was reached before all fields 
	private static int crcErrorCount = 0; 		// Errors in calculating the CRC value
	private static int invalidUrlCount = 0;
	
	public Parser parse = null;
	public long dateTime = -1;

	public ArrayList<Packet> protoStack = new ArrayList<Packet>();
	
	private static ArrayList<Frame> regularList = new ArrayList<Frame>(Settings.loadedFileSizeBits / Settings.AVERAGE_PACKET_SIZE_BITS);
	private static ArrayList<Frame> unknownErrorList = new ArrayList<Frame>(10_000);
	private static ArrayList<Frame> incompleteErrorList = new ArrayList<Frame>(10_000);
	private static ArrayList<Frame> crcErrorList = new ArrayList<Frame>(10_000);
	private static ArrayList<Frame> invalidUrlList = new ArrayList<Frame>(10_000);
		
	public Frame(int dlt, String data, int frameIndex, long dateTime) 
	{			
		Frame.frameCount++;
		this.dateTime = dateTime;
				
		if (frameIndex == -1)
		{
			this.frameID = Frame.frameCount;
		}
		else
		{
			this.frameID = frameIndex;
		}
				
		parse = new Parser(data);
		
		if (Settings.PRINT)
		{
			Utils.printDivider("Frame " + this.frameID);
			parse.printHex();
		}
	
		dltSwitch(dlt);
		
		if (Settings.STORE_FRAMES)
		{
			if (parse.unknownError == true)
			{
				unknownErrorCount++;
				unknownErrorList.add(this);
			}
			else if (parse.crcError == true)
			{
				crcErrorCount++;
				crcErrorList.add(this);
			}
			else if (parse.incompleteError == true)
			{
				incompleteErrorCount++;
				incompleteErrorList.add(this);
			}
			else if (parse.invalidUrlError == true)
			{
				invalidUrlCount++;
				invalidUrlList.add(this);
			}
			else
			{
				regularList.add(this);
			}
		}		
	
		Experiment.framePipeline(this);
	}
	
	// See [1] for DLT numbers
	public void dltSwitch(int dlt)
	{
		if (Settings.PRINT)
			System.out.println("[+] DLT: " + dlt);
		
		switch(dlt)
		{
			case 1:
				protoStack.add(new IEEE_802_3_ethernet_header(this));
				break;
			case 127:
				protoStack.add(new IEEE_802_11_radiotap_header(this));
				protoStack.add(new IEEE_802_11_mgmt_beacon(this));
				break;
		}
	}
	
	public void etherTypeSwitch(int etherType)
	{		
		switch (etherType)
		{
			case 2048:
				protoStack.add(new IPv4(this));
				break;
			case 2054:
				protoStack.add(new ARP(this));
				break;
			case 34525:
				protoStack.add(new IPv6(this));
				break;
		}
	}
	
	public void ipProtoSwitch(int proto)
	{		
		switch (proto)
		{
			case 1:
				protoStack.add(new ICMP(this));
				break;
			case 6:
				protoStack.add(new TCP(this));
				break;
			case 17:
				protoStack.add(new UDP(this));
				break;
			case 58:
				protoStack.add(new ICMPv6(this));
				break;
		}
	}
	
	public void portSwitch(int port)
	{
		switch (port)
		{
			case 53:
				protoStack.add(new DNS(this));
				break;
		}
	}
	
	public static void printCaptureSummary()
	{	
		if (Settings.PRINT_SUMMARY)
		{
			NetFlow.print();
			AccessPoint.print();
			URL.print();
			HostDevice.print();
			IP.print();
			
			Utils.printDivider("Capture Summary");
			System.out.println("    | Parsing Errors: " + Frame.unknownErrorCount);
			System.out.println("    | Incomplete Packet Errors: " + Frame.incompleteErrorCount);
			System.out.println("    | Invalid URL Errors: " + Frame.invalidUrlCount);
			System.out.println("    | CRC Errors: " + Frame.crcErrorCount);
			System.out.println("    | Packet Count: " + Frame.frameCount);
			System.out.println("    | Access Points: " + AccessPoint.list.keySet().size());
			System.out.println("    | Netflows: " + NetFlow.numFlows());
			System.out.println("    | IPs: " + IP.ipHostList.size());
			System.out.println("    | MACs: " + HostDevice.hostDeviceList.size());
			System.out.println("    | URLs: " + URL.urlList.size());
		}
	}
	
	public Packet get(int i)
	{
		return protoStack.get(i);
	}
}
