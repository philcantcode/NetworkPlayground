package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

import universal.Utils;

public class OS 
{
	public static Boolean isRoot = null;
	public static String workingDirectory = null;
	public static String configDirectory = null;
	public static String dataDirectory = null;
	public static Boolean isBigEndian = null;
	public static Boolean isLittleEndian = null;
	public static String osVersion = "";
	public static Boolean isMacOS = false;
	public static Boolean isWinOS = false;
	public static Boolean isNixOS = false;
	public static Boolean isSolOS = false;
	public static ArrayList<String> NICNames = new ArrayList<String>();
	
	public OS()
	{
		configWorkingDirectories(); 
		getEndianness();
		checkOS();
		getUserID();
		getNicNames();
	}
	
	private static void configWorkingDirectories()
	{
		OS.workingDirectory = System.getProperty("user.dir");
		OS.configDirectory = OS.workingDirectory + "/src/config/";
		OS.dataDirectory = OS.workingDirectory + "/src/data/";		
	}
	
	public static String getEndianness()
	{
		if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) 
		{
			OS.isBigEndian = true;
			OS.isLittleEndian = false;
			
			return "Big";
		}
		else 
		{
			OS.isBigEndian = false;
			OS.isLittleEndian = true;
			
			return "Little";
		}
	}
	
	/** Returns mac, win, lin or sol */
	public static String checkOS()
	{
		String os = System.getProperty("os.name").toLowerCase();
		String osVer = "";
		
		if (os.contains("mac"))
		{
			osVer = "mac";
			isMacOS = true;
		}
		else if (os.contains("win"))
		{
			osVer = "win";
			isWinOS = true;
		}
		else if (os.contains("nix") || os.contains("nux") || os.contains("aix"))
		{
			isNixOS = true;
			osVer = "nix";
		}
		else if (os.contains("sunos"))
		{
			isSolOS = true;
			osVer = "sol";
		}
		else
		{
			Utils.exit("OS.java", "Could not determine OS");
		}
		
		OS.osVersion = osVer;
		
		return OS.osVersion;
	}
	
	public static int getUserID()
	{
		int userID = Integer.valueOf(runTermCMD("id -u"));
		
		if (userID == 0)
			OS.isRoot = true;
		
		return userID;
	}
	
	public static String runTermCMD(String cmd)
	{
		String line = "";
		
		try {
	        Process p = Runtime.getRuntime().exec(cmd);	       
	        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        
	        while ((line = reader.readLine()) != null) 
	        {
	        	return line;
	        }
		}
		catch(Exception e)
		{
			line = "-1";
		}
		
		return line;
	}
	
	public static ArrayList<String> getNicNames()
	{
		NICNames.clear();
		
		try 
		{
			for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces()))
			{
				NICNames.add(nic.getDisplayName());
			}
		} 
		catch (SocketException e) 
		{
			e.printStackTrace();
		}
		
		return NICNames;
	}
}
