package substructures;

import java.util.ArrayList;
import java.util.HashMap;

import config.Settings;
import experiments.Experiment;
import universal.Utils;
import utils.Field;

public class NetFlow
{
	public int numPackets = 1;
	
	public Field data = null;
	public ArrayList<Integer> frameIds = new ArrayList<Integer>();
	public ArrayList<Flow> flows = new ArrayList<Flow>();
	
	/* [ srcIP [ hash : NetFlow ] ] */
	public static HashMap<String, HashMap<String, NetFlow>> netFlowList = new HashMap<String, HashMap<String, NetFlow>>(100_000);
			
	private NetFlow(Flow flow) 
	{		
		this.frameIds.add(flow.frameID);
		
		if (Settings.STORE_FRAMES_IN_FLOW)
			this.flows.add(flow);
				
		// If the outer key does exist but the inner key doesn't
		if (netFlowList.containsKey(flow.source))
		{
			netFlowList.get(flow.source).put(flow.hash, this);
		}
		else // If the outer key doesn't exist, create it and add the inner map
		{
			HashMap<String, NetFlow> innerMap = new HashMap<String, NetFlow>();
			innerMap.put(flow.hash, this);
			
			netFlowList.put(flow.source, innerMap);
		}
	}
	
	public static void validateNetFlow(Flow flow)
	{	
		if (Settings.VALIDATE_NETFLOW)
		{	
			Experiment.flowPipeline(flow);
			
			// If the outer key (srcIP) exists
			if (netFlowList.containsKey(flow.source))
			{				
				if (netFlowList.get(flow.source).containsKey(flow.hash))
				{
					netFlowList.get(flow.source).get(flow.hash).frameIds.add(flow.frameID);
					netFlowList.get(flow.source).get(flow.hash).flows.add(flow);
					netFlowList.get(flow.source).get(flow.hash).numPackets++;
				}
				else
				{
					new NetFlow(flow);
				}
			}
			else
			{
				new NetFlow(flow);
			}
		}
		
		
	}
	
	public static int numFlows()
	{
		int count = 0;
		
		for (HashMap<String, NetFlow> innerMap : netFlowList.values())
		{
			for (NetFlow nf : innerMap.values())
			{
				count++;
			}
		}
		
		return count;
	}
	
	public int countUniquePorts(DIRECTION direction)
	{
		int count = 0;
		
		ArrayList<Integer> ports = new ArrayList<Integer>();
		
		for (int i = 0; i < flows.size(); i++)
		{
			if (direction == DIRECTION.SOURCE)
			{
				if (!ports.contains(flows.get(i).sourcePort))
				{
					ports.add(flows.get(i).sourcePort);
					count++;
				}
			}
			else if (direction == DIRECTION.DESTINATION)
			{
				if (!ports.contains(flows.get(i).destinationPort))
				{
					ports.add(flows.get(i).destinationPort);
					count++;
				}
			}
		}
		
		return count;
	}
	
	public enum DIRECTION
	{
		SOURCE,
		DESTINATION
	}
	
	public static void print()
	{
		Utils.printDivider("NetFlow List");
		
		for (HashMap<String, NetFlow> innerMap : netFlowList.values())
		{
			for (NetFlow nf : innerMap.values())
			{
				System.out.println("    " + nf.flows.get(0).source + " (" + nf.flows.get(0).sourcePort + ") > " + nf.flows.get(0).destination + " (" + nf.flows.get(0).destinationPort + ") [" + nf.flows.get(0).protocol + "]: " + nf.numPackets);
			}
		}
	}
	
	
}
