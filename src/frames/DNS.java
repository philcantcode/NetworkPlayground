package frames;

import substructures.URL;
import universal.Types;
import utils.Field.DTYPE;
import utils.Packet;

public class DNS extends Packet
{

	public static final String PERIOD_SYMBOL = Types.hexToBin("2E");
	public static final int PERIOD_SYMBOL_LEN = PERIOD_SYMBOL.length();
	
	/* http://www.zytrax.com/books/dns/ch15/ */
	public DNS(Frame frame) 
	{
		super(frame, PROTOCOL.DNS, ENDIANNESS.UNKNOWN);
		
		setHeaderSize(256); // Static size
		
		parseBytes("identifier", 2, DTYPE.HEX);
			
		parseBits("queryResponse", 1, DTYPE.NUM);
		parseBits("operationCode", 4, DTYPE.NUM);
		parseBits("authAnswer", 1, DTYPE.NUM);
		parseBits("truncation", 1, DTYPE.NUM);
		parseBits("recursionDesired", 1, DTYPE.NUM);
		parseBits("recursionAvailable", 1, DTYPE.NUM);
		parseBits("reserved", 3, DTYPE.NUM);
		parseBits("responseCode", 4, DTYPE.NUM);
		
		parseBytes("questionCount", 2, DTYPE.NUM);
		parseBytes("answerRecordCount", 2, DTYPE.NUM);
		parseBytes("nameServerCount", 2, DTYPE.NUM);
		parseBytes("additionalRecordCount", 2, DTYPE.NUM);

		addField("dnsBodyOffset", Integer.toBinaryString(frame.parse.offset), DTYPE.NUM);
		
		parseQuestionFields(); 
		//parseAnswerFields(); // TODO: FIX ME
		
		printHeader();
	}
	
	public void parseQuestionFields()
	{
		// https://routley.io/tech/2017/12/28/hand-writing-dns-messages.html
		for (int i = 0; i < field("questionCount").num(); i++) // For each question
		{	
			int offset = frame.parse.offset;			
			String url = parseQnameFormat();
			
			addField("Q" + i + "-url:" + offset, url, DTYPE.ASCII);
			parseBytes("Q" + i + "-type:" + offset, 2, DTYPE.NUM);
			parseBytes("Q" + i + "-class:" + offset, 2, DTYPE.NUM);
			
			URL.validateURL(Types.binToAscii(url));
		}	
	}
	
	public void parseAnswerFields()
	{
		for (int i = 0; i < field("answerRecordCount").num(); i++) // For each question
		{
			if (frame.parse.nextBits(2).hex().equals("11")) // Offset format
			{
				int offset = frame.parse.nextBits(14).num();
			}
			else
			{
				String url = parseQnameFormat();
				addField("A" + i + "-url", url, DTYPE.ASCII);
			}
			
//			parseBytes("A" + i + "-type", 2, DTYPE.NUM);
//			parseBytes("A" + i + "-class", 2, DTYPE.NUM);
//			parseBytes("A" + i + "-ttl", 4, DTYPE.NUM);
//			
//			int rdLen = frame.parse.nextBytes(2).num();
//			
//			parseBytes("A" + i + "-data", rdLen, DTYPE.ASCII);
		}
	}
	
	// Returns the binary of the URL
	public String parseQnameFormat()
	{
		StringBuilder url = new StringBuilder();
		
		while (true)
		{
			int numBytes = frame.parse.nextBytes(1).num(); // Size of label (www.google.com = 3 labels)
				
			if (numBytes == 0)
			{
				break;
			}
			else
			{
				url.append(PERIOD_SYMBOL); // . symbol separating URL
				url.append(frame.parse.nextBytes(numBytes).bin());				
			}
			
			url.append(url.substring(PERIOD_SYMBOL_LEN));
		}
		
		return url.toString();
	}

}
