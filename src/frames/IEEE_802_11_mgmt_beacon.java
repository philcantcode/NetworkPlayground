package frames;

import substructures.AccessPoint;
import universal.Utils;
import utils.Field.DTYPE;
import utils.Packet;

/* While the bit ordering within each individual data field is big-endian, 
 * the fields themselves are transmitted in reverse order, within the byte-boundaries. 
 * 
 * 1: https://www.edn.com/Home/PrintView?contentItemId=4375340
 * 2: https://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=7786995
 * 3: http://www.studioreti.it/slide/802-11-Frame_E_C.pdf
 * 4: http://80211notes.blogspot.com/2013/09/understanding-address-fields-in-80211.html
 * 5: https://btwside.com/net-802-11
 * */
public class IEEE_802_11_mgmt_beacon extends Packet
{	
	public String fcs = "";
	
	public IEEE_802_11_mgmt_beacon(Frame frame) 
	{	
		super(frame, PROTOCOL.IEEE_802_11_MANAGEMENT_BEACON, ENDIANNESS.MIXED_BYTE_BOUNDARIES);
		
		frame.parse.initialiseCRC();
		
		/* Frame Control field (2 bytes)
		 * B0 - B1 : REVERSED ENDIANNESS */ 
		parseBits("subType", 4, DTYPE.NUM);
		parseBits("type", 2, DTYPE.NUM);
		parseBits("version", 2, DTYPE.NUM);
		
		// B1 - B2 : REVERSED ENDIANNESS
		parseBits("order", 1, DTYPE.HEX);
		parseBits("protectedFrame", 1, DTYPE.HEX);
		parseBits("moreData", 1, DTYPE.HEX);
		parseBits("powerManagement", 1, DTYPE.HEX);
		parseBits("retry", 1, DTYPE.HEX);
		parseBits("moreFrag", 1, DTYPE.HEX);
		parseBits("fromDS", 1, DTYPE.HEX);
		parseBits("toDS", 1, DTYPE.HEX);
		
		// B2 - B26
		parseBytes("duration", 2, DTYPE.HEX);
		
		String addr1 = parseBytes("addr1", 6, DTYPE.HEX).hex();
		String addr2 = parseBytes("addr2", 6, DTYPE.HEX).hex();
		String addr3 = parseBytes("addr3", 6, DTYPE.HEX).hex();
		String addr4 = "";
		
		parseBytes("seqControl", 2, DTYPE.HEX);
		
		if (field("toDS").hex().equals("1") && field("fromDS").hex().equals("1"))
			addr4 = parseBytes("addr4", 6, DTYPE.HEX).hex();
		
		sortMacAddresses(addr1, addr2, addr3, addr4);
				
		parseBytes("timestamp", 8, DTYPE.HEX);
		parseBytes("beaconInterval", 2, DTYPE.HEX);
		parseBytes("capabilityInfo", 2, DTYPE.HEX);
		
		// Tags in frame body section
		String firstTag = parseBytes("tag1", 1, DTYPE.HEX).hex();
		
		if (firstTag.equals("00"))
		{
			int ssidLen = parseBytes("ssidLen", 1, DTYPE.NUM).num();
			
			parseBytes("ssid", ssidLen, DTYPE.ASCII);
		}
		
		fcs = Utils.flipBytes(frame.parse.lastBytes(4));
		frame.parse.checkCRC32(fcs);
		
		if (frame.parse.unknownError == false)
			AccessPoint.validateAccessPoint(field("bssid").mac(), field("ssid").ascii());
			
		printHeader();
	}
	
	/* Sort out Mac addresses into their correct order */
	public void sortMacAddresses(String addr1, String addr2, String addr3, String addr4)
	{
		if (field("toDS").hex().equals("0") && field("toDS").hex().equals("0"))
		{
			addField("destination", addr1, DTYPE.MAC);
			addField("source", addr2, DTYPE.MAC);
			addField("bssid", addr3, DTYPE.MAC);
		}
		else if (field("toDS").hex().equals("1") && field("toDS").hex().equals("0"))
		{
			addField("bssid", addr1, DTYPE.MAC);
			addField("source", addr2, DTYPE.MAC);
			addField("destination", addr3, DTYPE.MAC);
		}
		else if (field("toDS").hex().equals("0") && field("toDS").hex().equals("1"))
		{
			addField("destination", addr1, DTYPE.MAC);
			addField("bssid", addr2, DTYPE.MAC);
			addField("source", addr3, DTYPE.MAC);
		}
		else if (field("toDS").hex().equals("1") && field("toDS").hex().equals("1"))
		{
			addField("receiver", addr1, DTYPE.MAC);
			addField("transmitter", addr2, DTYPE.MAC);
			addField("destination", addr3, DTYPE.MAC);
			addField("source", addr4, DTYPE.MAC);
		}
	}

}
