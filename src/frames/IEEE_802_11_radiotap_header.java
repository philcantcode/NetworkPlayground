/* Header Info: https://www.radiotap.org */
package frames;

import utils.Field.DTYPE;
import utils.Packet;

/* https://www.radiotap.org */
public class IEEE_802_11_radiotap_header extends Packet
{	
	public IEEE_802_11_radiotap_header(Frame frame) 
	{	
		super(frame, PROTOCOL.IEEE_802_11_RADIOTAP_HEADER, ENDIANNESS.LITTLE);
		
		parseBytes("version", 1, DTYPE.HEX);
		parseBytes("padding", 1, DTYPE.HEX);
		parseBytes("length", 2, DTYPE.NUM).flip();
		parseBytes("present", 4, DTYPE.HEX).flip();
		
		//TODO: Unpack the last 17 bytes calculated from the legnth field
		int dataSize = field("length").num() - 8;		
		parseBytes("otherData", dataSize, DTYPE.HEX);
		
		printHeader();
	}
}
