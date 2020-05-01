package utils;

import java.util.zip.CRC32;

import config.Settings;
import universal.Types;
import universal.Utils;

public class Parser 
{
	public StringBuilder bin = new StringBuilder();
	
	public boolean unknownError = false;
	public boolean crcError = false;
	public boolean incompleteError = false;
	public boolean invalidUrlError = false;
	
	private boolean frameBuilder = false;
	
	public int offset = 0;
	public int binLen = 0;
	public int byteLen = 0;
	private int crcIndex = 0;
	
	public Parser(String data) 
	{
		if (data == null) // For Building frames manually
		{
			frameBuilder = true;
		}
		else
		{
			this.bin.append(data);
			binLen = bin.length();			
		}
	}
	
	public void printHex()
	{
		System.out.println("Raw: " + Types.binToHex(bin.toString()));
		System.out.println(Utils.repeat((offset / 4) + 5, " ") + "^");
	}
	
	public void printBin()
	{
		System.out.println(bin);
	}
	
	public void initialiseCRC()
	{
		crcIndex = offset;
	}
	
	/** Performs the Cyclic redundancy check.
	 *  Prerequisite: must call crcInit() at the start of the frame
	 *  Input: The CRC from the frame
	 *  Process: Substrings the binary string using the CRC index - the 32 bit CRC value
	 *  Returns: True if matches, else false   */
	public boolean checkCRC32(String crc)
	{
		CRC32 crc32 = new CRC32();
		String substr = "";
		
		try {
			substr = bin.substring(crcIndex, binLen - 32);
		} catch (StringIndexOutOfBoundsException e) {
			this.crcError = true;
			System.out.println(e);
			return false;
		}
		
		crc32.update(Types.hexToBytes(Types.binToHex(substr)));
		String calculatedCRC = String.format("%X", crc32.getValue());
				
		if (crc.equals(calculatedCRC))
		{
			return true;
		}
		else
		{
			this.crcError = true;
			return false;
		}
	}
	
	public String lastBits(int n)
	{
		String substr = bin.substring((binLen - n), binLen);
		
		return Types.binToHex(substr);
	}
	
	public String lastBytes(int n)
	{
		return lastBits(n * 8);
	}
	
	/** Returns the next # of bits as hex */
	public Field nextBits(int n)
	{
		int numBits = n;
		int newOffset = offset + numBits;
		
		Field field = null;
		
		if (newOffset <= binLen) // Offset within bounds during frame chopping
		{
			String substr = bin.substring(offset, newOffset);
			offset = newOffset;
			
			field = new Field(substr, n);
		}
		else // End of bin string reached before all fields in frame spec
		{
			field = new Field("0000", 4);
			this.incompleteError = true;
			field.incompleteError = true;			
		}
		
		if (Settings.RELEASE_BINARY_AFTER_PARSE)
		{
			if (this.offset == this.binLen + 1)
			{
				this.bin = null;
			}
		}
		
		return field;
	}
	
	public String range(int start, int end)
	{	
		return bin.substring(start, end);
	}
	
	/** Returns the next 8 bits as hex */
	public Field nextBytes(int n)
	{
		return nextBits(n * 8);
	}
}
