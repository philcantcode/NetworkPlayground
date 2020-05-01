package frames;

import substructures.HostDevice;
import utils.Field.DTYPE;
import utils.Packet;

public class IEEE_802_3_ethernet_header extends Packet
{	
	public static final String HWTYPE_ETHERNET = "0001";
	
	public IEEE_802_3_ethernet_header(Frame frame) 
	{			
		super(frame, PROTOCOL.IEEE_802_3_ETHERNET_HEADER, ENDIANNESS.BIG);
		
		parseBytes("destination", 6, DTYPE.MAC);
		parseBytes("source", 6, DTYPE.MAC);
		parseBytes("etherType", 2, DTYPE.NUM);
		
		HostDevice.validateHostDevice(field("source").mac());
		HostDevice.validateHostDevice(field("destination").mac());
		
		this.printHeader();
		frame.etherTypeSwitch(field("etherType").num());
	}
}
