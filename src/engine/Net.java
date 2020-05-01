package engine;

import config.Settings;
import universal.Utils;
import utils.OS;

public class Net
{
	public String iface = null;
	
	/* MacOS Commands */
	private static final String AIRPORT_PATH = "/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport ";
	private static final String AIRPORT_ON = "networksetup -setairportpower en0 on";
	private static final String AIRPORT_OFF = "networksetup -setairportpower en0 off";
	
	public ChannelHopper hopper = new ChannelHopper(this);
	
	public Net(String iface) 
	{
		this.iface = iface;
	}
	
	public void changeChannel(int channel)
	{
		String oldChannel = getChannel();
		disassociate();
		
		if (OS.isMacOS)
		{
			String out = OS.runTermCMD(OS.workingDirectory + "/src/system/mac/ChannelHopper " + iface + " " + channel);
			
			if (Settings.PRINT)
				System.out.println("Channel Switched: " + out + " from " + oldChannel);
		}
		else
		{
			Utils.exit("Net.java", "No OS support for changeChannel");
		}
	}
	
	public void enableMonitorModeManually(int ch)
	{
		if (OS.isMacOS)
		{
			System.out.println(OS.runTermCMD(AIRPORT_PATH + iface + " sniff " + ch + " &"));
		}
		else
		{
			Utils.exit("Net.java", "No OS support for enterMonMode");
		}
	}
	
	/** Off = 0, On = 1, Both = 2 */
	public static void toggleWiFi(int status)
	{
		if (OS.isMacOS)
		{
			if (status == 0)
			{
				OS.runTermCMD(AIRPORT_OFF);
			}
			else if (status == 1)
			{
				OS.runTermCMD(AIRPORT_ON);
			}
			else if (status == 2)	
			{
				OS.runTermCMD(AIRPORT_OFF);
				OS.runTermCMD(AIRPORT_ON);
			}
		}
		else
		{
			Utils.exit("Net.java", "No OS support for toggleWiFi");
		}
	}
	
	public String getChannel()
	{ 
		if (OS.isMacOS)
		{
			return OS.runTermCMD(AIRPORT_PATH + iface + " --channel");
		}
		else
		{
			Utils.exit("Net.java", "No OS support for getChannel");
		}
		
		return null;
	}
	
	/** Disassociate from the network */
	public void disassociate()
	{ 
		if (OS.isMacOS)
		{
			OS.runTermCMD(AIRPORT_PATH + iface + " -z");
		}
		else
		{
			Utils.exit("Net.java", "No OS support for disassociate");
		}
	}
	


}
