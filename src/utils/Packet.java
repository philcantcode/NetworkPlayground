package utils;

import java.util.LinkedHashMap;

import config.Settings;
import frames.Frame;
import universal.Types;
import universal.Utils;
import utils.Field.DTYPE;

public class Packet 
{
	public Frame frame = null;
	public PROTOCOL protoType = null;
	public ENDIANNESS endianess = null;
	private int headerBits = -1;
	private int packetBits = -1;
	private int dataSize = -1;
	public String info = "";
	
	private int checksumStart = -1;
	private int checksumEnd = -1;
	private CRC crcType = null;
	
	private boolean isFrameBuilder = false;
	
	public LinkedHashMap<String, Field> fields = new LinkedHashMap<String, Field>();
	
	public Packet(Frame frame, PROTOCOL name, ENDIANNESS endianess)
	{
		if (frame == null)
		{
			this.isFrameBuilder = true;
		}
		else
		{
			this.frame = frame;
			this.protoType = name;
			this.endianess = endianess;
			
			frame.protoStack.add(this);
		}
	}
	
	public Field parseBits(String name, int amount, DTYPE type)
	{
		Field field = null;
		
		if (isFrameBuilder == true)
		{
			field = new Field(Utils.repeat(amount, "0"), amount);
		}
		else
		{
			field = frame.parse.nextBits(amount);
			
			field.setFieldName(name);
			field.setDataType(type);
		
			fields.put(name, field);	
			
		}
		
		return fields.get(name);
	}
	
	public void initCRC(CRC type)
	{
		this.checksumStart = frame.parse.offset;
		this.crcType = type;
	}
	
	public boolean calcCRC(String hex)
	{
		this.checksumEnd = frame.parse.offset;
		String data = "";
		boolean checkCorrect = false;
		
		if (hex == null)
			data = Types.binToHex(frame.parse.range(this.checksumStart, this.checksumEnd));
		else
			data = hex;
		
		if (this.crcType == CRC.CRC16_CHECKSUM_INCLUDED)
		{
			checkCorrect = Checksum.crc32WithChecsumIncluded(data);
		}
		
		if (checkCorrect == true)
		{
			addField("CRC", "1", DTYPE.NUM);
			
			return true;
		}
		else
		{
			addField("CRC", "0", DTYPE.NUM);
			
			frame.parse.crcError = true;
			
			return false;
		}		
	}
	
	public enum CRC
	{
		CRC16_CHECKSUM_INCLUDED,
		CRC32_CHECKSUM_OMITTED
	}
	
	public Field parseBytes(String name, int amount, DTYPE type)
	{
		return parseBits(name, amount * 8, type);
	}
	
	public Field addField(String name, String data, DTYPE type)
	{
		Field field = new Field(data, data.length() * 4);
		field.setFieldName(name);
		field.setDataType(type);
		
		fields.put(name, field);
		
		return fields.get(name);
	}
	
	public enum PROTOCOL
	{
		IEEE_802_3_ETHERNET_HEADER,
		IEEE_802_11_RADIOTAP_HEADER, IEEE_802_11_MANAGEMENT_BEACON,
		IPV4, IPV6, 
		TCP, UDP,
		ICMP, ICMPV6,
		ARP, DNS
	}
	
	public enum ENDIANNESS
	{
		BIG, LITTLE, MIXED_BYTE_BOUNDARIES, UNKNOWN
	}
	
	public void setInfo(String info) 
	{
		this.info = info;
	}
	
	public void setHeaderSize(int size)
	{
		this.headerBits = size;
	}
	
	public void setPacketSize(int size)
	{
		this.packetBits = size;
	}
	
	public void setDataSize(int size)
	{
		this.dataSize = size;
	}
	
	public int headerSize()
	{
		return this.headerBits;
	}
	
	public int packetSize()
	{
		return this.packetBits;
	}
	
	public int dataSize()
	{
		return this.dataSize;
	}
	
	public Field field(String field)
	{
		return fields.get(field);
	}
	
	public int sizeBits(String field)
	{
		return field(field).sizeBits;
	}
	
	public void printHeader()
	{
		if (Settings.PRINT)
		{
			System.out.println(this.protoType + " header");
			
			int count = 0;
			
			for (String s : fields.keySet())
			{
				Field f = fields.get(s);
				
				if (count == 0)
				{
					System.out.printf("    | [%s: %s]", s, f.castedString());
					count++;
				}
				else if (count == 1 || count == 2)
				{
					System.out.printf("[%s: %s]", s, f.castedString());
					count++;
				}
				else if (count == 3)
				{
					System.out.printf("[%s: %s]\n", s, f.castedString());
					count = 0;
				}
			}
			
			if (count != 0)
				System.out.println("");
		}
	}
	
	public Packet peek()
	{
		int index = -1;
		
		for (int i = 0; i < frame.protoStack.size(); i++)
		{
			if (frame.protoStack.get(i).protoType == this.protoType)
			{
				index = (i - 1);
			}
		}
		
		return frame.protoStack.get(index);
	}
	
	public Field peekField(String field)
	{	
		return peek().field(field);
	}
}
