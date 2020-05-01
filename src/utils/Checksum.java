package utils;

import universal.Types;
import universal.Utils;

public class Checksum 
{	
	public Checksum()
	{
		assertTestChecksum();
	}
	
	/* Calculates the CRC for 16 bit values, pass in the hex including the checksum value, will return 0 if matches */
	public static String CRC16(String hex) 
	{
		byte[] buf = Types.hexToBytes(hex);
		int length = buf.length;
	    int i = 0;

	    long sum = 0;
	    long data;

	    while (length > 1) 
	    {
	    	data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
	    	sum += data;

	    	if ((sum & 0xFFFF0000) > 0) 
	    	{
	    		sum = sum & 0xFFFF;
	    		sum += 1;
	    	}

	    	i += 2;
	    	length -= 2;
	    }

	    if (length > 0) 
	    {
	    	sum += (buf[i] << 8 & 0xFF00);

	    	if ((sum & 0xFFFF0000) > 0) 
	    	{
	    		sum = sum & 0xFFFF;
	    		sum += 1;
	    	}
	    }

	    sum = ~sum;
	    sum = sum & 0xFFFF;
	    	    
	    return String.format("%X", sum);
	}
	
	public static boolean crc32WithChecsumIncluded(String hex)
	{
		if (CRC16(hex).equals("0"))
			return true;
		else
			return false;
	}
	
	public static void assertTestChecksum()
	{
		byte[] buf = {(byte) 0xe3, 0x4f, 0x23, (byte) 0x96, 0x44, 0x27, (byte) 0x99, (byte) 0xf3};
		
		if (!CRC16(Types.bytesToHex(buf)).equals("1AFF"))
		{
			Utils.exit("Checksum.java", "assertTestChecksum() failed.");
		}		
	}
}
