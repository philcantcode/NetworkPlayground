package engine;

import java.util.ArrayList;

public class ChannelHopper extends Thread
{
	private static final int[] LOW_CHANNELS = {1, 6, 11};
	private static final int[] GHZ_24_CHANNELS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
	private static final int[] UNII1_CHANNELS = {36, 40, 44, 48};
	private static final int[] GHZ_5_CHANNELS = {36, 40, 44, 48, 52, 56, 60, 64, 100, 104, 108, 112, 116, 120, 124, 128, 132, 136, 140};
	
	private ArrayList<Integer> channels = new ArrayList<Integer>();
	private static int hopTime = 500;
	private static boolean stopHopping = false;
	private Net net = null;
	
	public enum CHANNELS 
	{
		LOW_CHANNELS, GHZ_24_CHANNELS, UNII1_CHANNELS, GHZ_5_CHANNELS
	}
	
	public ChannelHopper(Net net) 
	{
		this.net = net;
		ChannelHopper.stopHopping = false;
	}
	
	public void addChannel(int channel)
	{
		if (!channels.contains(channel))
			channels.add(channel);
	}
	
	public void addChannelSet(CHANNELS channels)
	{
		if (channels == CHANNELS.LOW_CHANNELS)
		{
			for (int c : LOW_CHANNELS)
			{
				addChannel(c);
			}
		}
		else if (channels == CHANNELS.GHZ_24_CHANNELS)
		{
			for (int c : GHZ_24_CHANNELS)
			{
				addChannel(c);
			}
		}
		else if (channels == CHANNELS.UNII1_CHANNELS)
		{
			for (int c : UNII1_CHANNELS)
			{
				addChannel(c);
			}
		}
		else if (channels == CHANNELS.GHZ_5_CHANNELS)
		{
			for (int c : GHZ_5_CHANNELS)
			{
				addChannel(c);
			}
		}
	}
	
	@Override
	public void run() 
	{
		while (stopHopping == false)
		{	
			for (int c : this.channels)
			{
				try {
					Thread.sleep(hopTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				this.net.changeChannel(c);
				
				if (stopHopping == true)
					break;
			}
		}		
	}
	
	public static void stopHop()
	{
		ChannelHopper.stopHopping = true;
	}

}
