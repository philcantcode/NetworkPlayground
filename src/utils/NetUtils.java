package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import universal.Utils;

public class NetUtils 
{
	public static final SimpleDateFormat UNB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public NetUtils() 
	{
		/* UNB ISCX 2012 Dataset is in Canadian timezone */
		UNB_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Canada/Atlantic"));
	}
	
	public static int protocolToNum(String protocol)
	{		
		try 
		{
			return Integer.valueOf(protocol);
		}
		catch (NumberFormatException e)
		{
			switch (protocol)
			{
				case "icmp_ip":
					return 1;
				case "tcp_ip":
					return 6;
				case "udp_ip":
					return 17;
				default:
					CodeLogger.log("No protocolToNum() case: " + protocol, DEPTH.ROOT);
					System.exit(0);
			}
		}
		
		return -1;
	}

	public static long UNBTimestampToUnix(String time)
	{
		try 
		{
			return UNB_DATE_FORMAT.parse(time).getTime();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			Utils.exit("Utils", "UNB Timestamp Error");
		}
		
		return -1;
	}
	
	public static String unixToUNBTime(long time)
	{
		return UNB_DATE_FORMAT.format(time);
	}

}
