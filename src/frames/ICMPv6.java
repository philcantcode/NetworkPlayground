package frames;

import utils.Field.DTYPE;
import utils.Packet;

/* 1: https://en.wikipedia.org/wiki/Internet_Control_Message_Protocol */
public class ICMPv6 extends Packet
{
	public ICMPv6(Frame frame) 
	{
		super(frame, PROTOCOL.ICMPV6, ENDIANNESS.BIG);
		
		parseBytes("type", 1, DTYPE.NUM);
		parseBytes("code", 1, DTYPE.NUM);
		parseBytes("checksum", 2, DTYPE.HEX);
		parseBytes("restOfHeader", 4, DTYPE.HEX);
		
		printHeader();
	}
}
