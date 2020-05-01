package substructures;

import java.util.HashMap;

import universal.Utils;

public class AccessPoint
{
	public static HashMap<String, AccessPoint> list = new HashMap<String, AccessPoint>();
	
	public String bssid = "";
	public String ssid = "";
	int count = 1;

	private AccessPoint(String bssid, String ssid)  
	{		
		this.bssid = bssid;
		this.ssid = ssid; 
		
		list.put(ssid, this);
	}
	
	public static void validateAccessPoint(String bssid, String ssid)
	{
		if (list.keySet().contains(ssid))
		{
			list.get(ssid).count++;
		}
		else
		{
			new AccessPoint(bssid, ssid);
		}
	}
	
	public static void print()
	{
		Utils.printDivider("Access Point List");
		for (String s : AccessPoint.list.keySet())
		{
			System.out.println("    " + AccessPoint.list.get(s).ssid + ": " + AccessPoint.list.get(s).count);
		}
	}

}
