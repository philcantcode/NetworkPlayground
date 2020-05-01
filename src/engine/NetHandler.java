/* javap -s -p CaptureInterface 
 * javah -jni engine.PacketCapture
 * gcc -shared -o wirelessMon.so wirelessMon.c -I /Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/include/ -I /Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/include/darwin/ -lpcap
 * http://www.devdungeon.com/content/using-libpcap-c#packet-type
 * */
package engine;

import java.util.HashMap;

import config.Settings;
import frames.Frame;
import universal.Utils;
import utils.OS;

public class NetHandler extends Thread
{
	private int status = 3;
	public String dev = ""; 
	
	public String sendPacket = "";
	public native void init(String dev); // Native method for passing between Java and C
	private static HashMap<STATUS, Integer> statusTypes = new HashMap<STATUS, Integer>();
	
	public NetHandler(String dev, STATUS type, int capLimit)
	{	
		for (int i = 0; i < STATUS.values().length; i++)
			statusTypes.put(STATUS.values()[i], i);
		
		this.dev = dev;
		Settings.captureLimit = capLimit;
		
		setStatus(type);
		
		if (OS.isMacOS)
			System.load(OS.workingDirectory + "/src/natives/mac.so");
		else
			Utils.exit("NetHandler.java", "No OS support for NetHandler constructor");
		
		System.out.println("NetHandler Loaded");
	}
	
	public static enum STATUS 
	{
		STOP, 
		ETHER_PROMISC_OFF, ETHER_PROMISC_ON, 
		MONMODE_WIFI_BEACONS, MONMODE_ALL, 
		SEND_PACKET_MONMODE, SEND_PACKET_ETHER
	}
	
	public void setStatus(STATUS status)
	{
		this.status = statusTypes.get(status);
	}
	
	@Override
	public void run() 
	{
		System.out.println("Capturing on: " + dev);
		init(dev); // Start capture in C
		ChannelHopper.stopHop();
		
		Frame.printCaptureSummary();
	}
	
	/** This method is called by libpcap whenever a packet is received */
	public int deliverHex(String[] data)
	{	
		new Frame(Integer.valueOf(data[0]), data[1], -1, -1);
		
		if (Frame.frameCount >= Settings.captureLimit && Settings.captureLimit != -1)
			setStatus(STATUS.STOP);
						
		return status;
	}
	
	public int notify(String message)
	{
		System.out.println("Notify: " + message);
		
		return status;
	}
	
	/* To send a packet, put the hex of the packet in this.sendPacket and set the status to the appropriate value */
	public String sendPacket()
	{
		return this.sendPacket;
	}
}
