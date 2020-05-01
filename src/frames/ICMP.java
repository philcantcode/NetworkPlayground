package frames;

import utils.Field.DTYPE;
import utils.Packet;

/* 1: https://en.wikipedia.org/wiki/Internet_Control_Message_Protocol */
public class ICMP extends Packet
{
	public ICMP(Frame frame) 
	{
		super(frame, PROTOCOL.ICMP, ENDIANNESS.BIG);
		
		parseBytes("type", 1, DTYPE.NUM);
		parseBytes("code", 1, DTYPE.NUM);
		parseBytes("checksum", 2, DTYPE.HEX);
		parseBytes("restOfHeader", 4, DTYPE.HEX);
		
		printHeader();
	}
}
