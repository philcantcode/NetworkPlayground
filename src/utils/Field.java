package utils;

import universal.Types;
import universal.Utils;

public class Field 
{
	private String name = "";
	private final StringBuilder bin = new StringBuilder();
	private DTYPE type = null;
	public int sizeBits = -1;
	
	public boolean parseErr = false;
	public boolean incompleteError = false;
	
	public Field(String data, int size) 
	{
		this.sizeBits = size;		
		this.bin.append(data);
	}
	
	public void setFieldName(String name)
	{
		this.name = name;
	}
	
	public void setDataType(DTYPE type)
	{
		this.type = type;
	}
	
	public enum DTYPE
	{
		HEX, NUM, IP, MAC, ASCII, HIDDEN
	}
	
	public int num()
	{
		return Integer.parseInt(bin.toString(), 2);
	}
	
	public String bin()
	{
		return bin.toString();
	}
	
	public String ip()
	{
		return Types.hexToIP(Types.binToHex(bin.toString()));
	}
	
	public String mac()
	{
		return Types.hexToMac(Types.binToHex(bin.toString()));
	}
	
	public DTYPE type()
	{
		return type;
	}
	
	public String hex()
	{
		return Types.binToHex(bin.toString());
	}
	
	public String ascii()
	{
		return Types.binToAscii(bin.toString());
	}
	
	public void flip()
	{
		bin.setLength(0);
		bin.append(Utils.flipBytes(Types.binToHex(bin.toString())));
	}
	
	public String castedString()
	{
		if (type == DTYPE.IP)
			return ip();
		else if (type == DTYPE.MAC)
			return mac();
		else if (type == DTYPE.NUM)
			return String.valueOf(num());
		else if (type == DTYPE.ASCII)
			return ascii();
		else if (type == DTYPE.HIDDEN)
			return "###HIDDEN###";
		else
			return hex();
		
	}
	
	public void appendData(String data)
	{		
		this.bin.append(data);
	}

}
