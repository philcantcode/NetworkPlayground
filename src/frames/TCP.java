package frames;

import substructures.Flow;
import substructures.NetFlow;
import substructures.URL;
import universal.Types;
import utils.Field.DTYPE;
import utils.Packet;

public class TCP extends Packet
{
	public String netFlowHash = null;
	
	public TCP(Frame frame) 
	{
		super(frame, PROTOCOL.TCP, ENDIANNESS.BIG);
		
		initCRC(CRC.CRC16_CHECKSUM_INCLUDED);
																// Bit Offset
		parseBytes("srcPort", 2, DTYPE.NUM); 					// B00 - B16
		parseBytes("dstPort", 2, DTYPE.NUM); 					// B16 - B32
		
		parseBytes("seqNum", 4, DTYPE.HEX);						// B32 - B64
		parseBytes("ackNum", 4, DTYPE.HEX);						// B64 - B96
		
		parseBits("dataOffset", 4, DTYPE.NUM);					// B96 - B100
		parseBits("reserved", 3, DTYPE.HEX);					// B100 - B103
		parseBits("flagNS", 1, DTYPE.HEX);						// B103 - B104
		parseBits("flagCWR", 1, DTYPE.HEX);						// B104 - B105
		parseBits("flagECE", 1, DTYPE.HEX);						// B105 - B106
		parseBits("flagURG", 1, DTYPE.HEX);						// B106 - B107
		parseBits("flagACK", 1, DTYPE.HEX);						// B107 - B108
		parseBits("flagPSH", 1, DTYPE.HEX);						// B108 - B109
		parseBits("flagRST", 1, DTYPE.HEX);						// B109 - B110
		parseBits("flagSYN", 1, DTYPE.HEX);						// B110 - B111
		parseBits("flagFIN", 1, DTYPE.HEX);						// B111 - B112
		
		// Specifies the size of the TCP header in 32-bit words
		setHeaderSize(field("dataOffset").num() * 32); 
		
		parseBytes("windowSize", 2, DTYPE.HEX);					// B112 - B128
		parseBytes("checksum", 2, DTYPE.HEX);   				// B128 - B144
		parseBytes("urgPointer", 2, DTYPE.HEX); // 160			// B144 - B160
		parseBits("optional", headerSize() - 160, DTYPE.HEX);   // B160 - Remaining header determined by dataOffset
		
		// Calculate TCP data length and check for netflow
		if (peek().protoType == PROTOCOL.IPV4)
		{			
			setDataSize(peek().packetSize() - (peek().headerSize() + headerSize()));
			setPacketSize(headerSize() + dataSize());
			parseBits("data", dataSize(), DTYPE.HEX);
			
			Flow flow = new Flow(peekField("source").ip(), peekField("destination").ip(), field("srcPort").num(), field("dstPort").num(), peekField("protocol").num(), this.frame.frameID, this.frame.dateTime);
			this.netFlowHash = flow.hash;
			NetFlow.validateNetFlow(flow);
		}
		else if (peek().protoType == PROTOCOL.IPV6)
		{
			setPacketSize(peek().dataSize());
			setDataSize(packetSize() - headerSize());
			parseBits("data", dataSize(), DTYPE.HEX);
			
			Flow flow = new Flow(peekField("source").ip(), peekField("destination").ip(), field("srcPort").num(), field("dstPort").num(), peekField("nextHeader").num(), this.frame.frameID, this.frame.dateTime);
			this.netFlowHash = flow.hash;
			NetFlow.validateNetFlow(flow);
		}
		
		// If HTTP, check for urls
		if (field("srcPort").num() == 80 || field("srcPort").num() == 443 || field("dstPort").num() == 80 || field("dstPort").num() == 443)
		{
			if (sizeBits("data") > 0)
			{						
				if (URL.validateURL(field("data").ascii()) == false)
				{
					this.frame.parse.invalidUrlError = true;
				}
			}
		}
		
		printHeader();
		
		frame.portSwitch(field("srcPort").num());
		frame.portSwitch(field("dstPort").num());
	}
	
	//TODO: FIX BROKEN
	public String genChecksum()
	{
		StringBuilder pseudoHeader = new StringBuilder();
		
		// From IPv4 - Pseudo Header
		pseudoHeader.append(peekField("source").bin());
		pseudoHeader.append(peekField("destination").bin());
		pseudoHeader.append("00000000"); // reserved block
		pseudoHeader.append(peekField("protocol").bin());
		pseudoHeader.append(Types.hexToBin(Types.numToHex((int) packetSize() / 8)));
		
		// From TCP Header
		pseudoHeader.append(field("srcPort").bin());
		pseudoHeader.append(field("dstPort").bin());
		pseudoHeader.append(field("seqNum").bin());
		pseudoHeader.append(field("ackNum").bin());
		pseudoHeader.append(field("dataOffset").bin());
		pseudoHeader.append(field("reserved").bin());
		pseudoHeader.append(field("flagNS").bin());
		pseudoHeader.append(field("flagCWR").bin());
		pseudoHeader.append(field("flagECE").bin());
		pseudoHeader.append(field("flagURG").bin());
		pseudoHeader.append(field("flagACK").bin());
		pseudoHeader.append(field("flagPSH").bin());
		pseudoHeader.append(field("flagRST").bin());
		pseudoHeader.append(field("flagSYN").bin());
		pseudoHeader.append(field("flagFIN").bin());
		pseudoHeader.append(field("windowSize").bin());
		pseudoHeader.append("0000000000000000"); // checksum zeroed out
		pseudoHeader.append(field("urgPointer").bin());
		pseudoHeader.append(field("optional").bin());
		pseudoHeader.append(field("data").bin());
				
		// Pad to 16 bit boundary 
		for (int i = 0; i < (pseudoHeader.length() % 16); i++)
		{
			pseudoHeader.append("0");
		}
						
		return Types.binToHex(pseudoHeader.toString());
	}
}
