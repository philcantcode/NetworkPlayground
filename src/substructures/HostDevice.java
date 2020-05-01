package substructures;

import java.util.HashMap;

import universal.Utils;

public class HostDevice 
{
	public static HashMap<String, HostDevice> hostDeviceList = new HashMap<String, HostDevice>();
	
	public String macAddress = "";
	public int count = 0;
	
	private HostDevice(String macAddress) 
	{
		this.macAddress = macAddress;
		
		hostDeviceList.put(macAddress, this);
	}
	
	public static final void validateHostDevice(String macAddress)
	{
		if (hostDeviceList.containsKey(macAddress))
		{
			hostDeviceList.get(macAddress).count++;
		}
		else
		{
			new HostDevice(macAddress);
		}
	}
	
	public static void print()
	{
		Utils.printDivider("Host Device List");
		for (String s : HostDevice.hostDeviceList.keySet())
		{
			System.out.println("    " + HostDevice.hostDeviceList.get(s).macAddress + ": " + HostDevice.hostDeviceList.get(s).count);
		}
	}

}
