package frames;

import substructures.HostDevice;
import substructures.IP;
import utils.Field.DTYPE;
import utils.Packet;

public class ARP extends Packet
{
	public static final String HWTYPE_ARP = "0806";
	public static final String OP_REQUEST = "0001";
	public static final String OP_REPLY = "0002";
	
	public ARP(Frame frame) 
	{
		super(frame, PROTOCOL.ARP, ENDIANNESS.LITTLE);
		
		parseBytes("hardwareType", 2, DTYPE.NUM);
		parseBytes("protocolType", 2, DTYPE.NUM);
		
		parseBytes("hardwareAddressLen", 1, DTYPE.NUM);
		parseBytes("protocolAddressLen", 1, DTYPE.NUM);
		
		parseBytes("operation", 2, DTYPE.NUM);
		
		parseBytes("senderMAC", field("hardwareAddressLen").num(), DTYPE.MAC);
		parseBytes("senderProtoAddress", field("protocolAddressLen").num(), DTYPE.IP);
		
		parseBytes("targetMAC", field("hardwareAddressLen").num(), DTYPE.MAC);
		parseBytes("targetProtoAddress", field("protocolAddressLen").num(), DTYPE.IP);
		
		HostDevice.validateHostDevice(field("senderMAC").mac());
		HostDevice.validateHostDevice(field("targetMAC").mac());
		IP.validateIpAddress(field("senderProtoAddress").ip());
		IP.validateIpAddress(field("targetProtoAddress").ip());
		
		printHeader();
	}
}
