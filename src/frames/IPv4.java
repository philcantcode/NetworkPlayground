package frames;

import substructures.IP;
import utils.Field.DTYPE;
import utils.Packet;

/* 1: https://en.wikipedia.org/wiki/List_of_IP_protocol_numbers */
public class IPv4 extends Packet
{		
	public static final String PROTOTYPE_IP4 = "0800";
	
	public IPv4(Frame frame) 
	{	
		super(frame, PROTOCOL.IPV4, ENDIANNESS.BIG);
		
		initCRC(CRC.CRC16_CHECKSUM_INCLUDED);
		
		parseBits("version", 4, DTYPE.HEX);
		parseBits("internetHeaderLength", 4, DTYPE.HEX);
		parseBytes("typeOfService", 1, DTYPE.HEX);
		parseBytes("totalLength", 2, DTYPE.HEX);

		// the entire packet size in bytes, including header and data
		setPacketSize(field("totalLength").num() * 8);
		setHeaderSize(field("internetHeaderLength").num() * 32);
		
		parseBytes("identification", 2, DTYPE.HEX);
		parseBits("unassigned", 1, DTYPE.HEX);
		parseBits("dontFrag", 1, DTYPE.HEX);
		parseBits("moreFrag", 1, DTYPE.HEX);
		parseBits("fragOffset", 13, DTYPE.HEX);
		
		parseBytes("ttl", 1, DTYPE.HEX);
		parseBytes("protocol", 1, DTYPE.NUM);
		parseBytes("checksum", 2, DTYPE.HEX);
		
		parseBytes("source", 4, DTYPE.IP);
		parseBytes("destination", 4, DTYPE.IP);
		
		calcCRC(null);
		
		//TODO: Handle IPV4 optional fields
		
		IP.validateIpAddress(field("source").ip());
		IP.validateIpAddress(field("destination").ip());
		
		printHeader();
		frame.ipProtoSwitch(field("protocol").num());
	}
}
