package substructures;

import java.util.HashMap;

import universal.Utils;

public class IP 
{
	public static HashMap<String, IP> ipHostList = new HashMap<String, IP>();
	
	public String ipAddress = "";
	public int count = 0;
	
	private IP(String ipAddress) 
	{
		this.ipAddress = ipAddress;
		
		ipHostList.put(ipAddress, this);
	}
	
	public static final void validateIpAddress(String ipAddress)
	{
		if (ipHostList.containsKey(ipAddress))
		{
			ipHostList.get(ipAddress).count++;
		}
		else
		{
			new IP(ipAddress);
		}
	}
	
	public static void print()
	{
		Utils.printDivider("IP Device List");
		for (String s : IP.ipHostList.keySet())
		{
			System.out.println("    " + IP.ipHostList.get(s).ipAddress + ": " + IP.ipHostList.get(s).count);
		}
	}

}
