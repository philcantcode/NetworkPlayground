package substructures;

import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import universal.Utils;

public class Flow 
{
	public String source = null;
	public String destination = null;
	public int protocol = -1;
	public int sourcePort = -1;
	public int destinationPort = -1;
	
	public long dateTime = -1;
	public int frameID = -1;
	public String hash = null;
	
	public Boolean isMalicious = null;
	public long startTime = -1;
	public long endTime = -1;
	
	public Flow()
	{
		
	}
	
	public Flow(String srcIP, String dstIP, int srcPort, int dstPort, int proto, int frameID, long dateTime) 
	{
		this.source = srcIP;
		this.destination = dstIP;
		this.protocol = proto;
		this.sourcePort = srcPort;
		this.destinationPort = dstPort;
		this.dateTime = dateTime;
		this.frameID = frameID;
		
		hash();
	}
	
	public Flow(String srcIP, String dstIP, int srcPort, int dstPort, int proto) 
	{
		this.source = srcIP;
		this.destination = dstIP;
		this.protocol = proto;
		this.sourcePort = srcPort;
		this.destinationPort = dstPort;
		
		hash();
	}
	
	public void hash()
	{
		this.hash = Utils.sha256Hash(this.source + this.destination + this.sourcePort + this.destinationPort + this.protocol);
	}
	
	public void print()
	{
		CodeLogger.log(source + " > " + destination + " Class: " + isMalicious, DEPTH.ROOT);
	}
	
	public void tag (short tag)
	{
		if (tag == 0)
			this.isMalicious = false;
		else if (tag == 1)
			this.isMalicious = true;
	}
}
