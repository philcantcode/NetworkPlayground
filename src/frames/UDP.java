package frames;

import utils.Field.DTYPE;
import utils.Packet;

public class UDP extends Packet
{	
	public UDP(Frame frame) 
	{		
		super(frame, PROTOCOL.UDP, ENDIANNESS.BIG);
		
		parseBytes("srcPort", 2, DTYPE.NUM);
		parseBytes("dstPort", 2, DTYPE.NUM);
		parseBytes("length", 2, DTYPE.NUM);
		parseBytes("checksum", 2, DTYPE.HEX);
		
		printHeader();
		
		frame.portSwitch(field("srcPort").num());
		frame.portSwitch(field("dstPort").num());
	}
}
