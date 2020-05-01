package frames;

import substructures.IP;
import utils.Field.DTYPE;
import utils.Packet;

/* 1: https://en.wikipedia.org/wiki/IPv6_packet */
public class IPv6 extends Packet
{
	public IPv6(Frame frame) 
	{
		super(frame, PROTOCOL.IPV6, ENDIANNESS.BIG);
		
		parseBits("version", 4, DTYPE.HEX);
		parseBytes("trafficClass", 1, DTYPE.HEX);
		parseBits("flowLabel", 20, DTYPE.HEX);
		
		parseBytes("payloadLength", 2, DTYPE.NUM);
		parseBytes("nextHeader", 1, DTYPE.NUM);
		parseBytes("hopLimit", 1, DTYPE.HEX);
		
		parseBytes("source", 16, DTYPE.IP);
		parseBytes("destination", 16, DTYPE.IP);
		
		setDataSize(field("payloadLength").num() * 8);
		setHeaderSize(320);
		setPacketSize(headerSize() + dataSize());
		
		IP.validateIpAddress(field("source").ip());
		IP.validateIpAddress(field("destination").ip());
		
		printHeader();
		frame.ipProtoSwitch(field("nextHeader").num());
	}
}
