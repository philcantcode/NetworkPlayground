package engine;

import engine.NetHandler.STATUS;
import frames.ARP;
import frames.IEEE_802_3_ethernet_header;
import frames.IPv4;

public class Attacks extends Thread
{
	public static String enablePacketForwardingOSX = "sysctl -w net.inet.ip.forwarding=1";
	public static String disablePacketForwardingOSX = "sysctl -w net.inet.ip.forwarding=0";
	public static String checkPacketForwardingOSX = "sysctl -w net.inet.ip.forwarding";
	
	public Attacks() 
	{

	}
	
	public static void spoofARP()
	{
		FrameBuilder fb = new FrameBuilder();
		fb.buildEthernet("01:01:01:01:01:01", "02:02:02:02:02:02", ARP.HWTYPE_ARP);
		fb.buildARP(IEEE_802_3_ethernet_header.HWTYPE_ETHERNET, IPv4.PROTOTYPE_IP4, "06", "04", ARP.OP_REQUEST, "01:01:01:01:01:01", "1.1.1.1", "02:02:02:02:02:02", "2.2.2.2");
				
		System.out.println("COMPILE: " + fb.compile());
		
		
		NetHandler nh = new NetHandler("en0", STATUS.SEND_PACKET_ETHER, -1);
		nh.sendPacket = fb.compile() + "\\";
		nh.start();
	}

}
