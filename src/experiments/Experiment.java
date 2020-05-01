package experiments;

import java.util.ArrayList;
import java.util.HashMap;

import frames.Frame;
import substructures.Flow;
import universal.CSVLoader;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import universal.Utils;
import utils.NetUtils;

public abstract class Experiment
{	
	public HashMap<String, ArrayList<Flow>> malFlows = new HashMap<String, ArrayList<Flow>>();
	public ArrayList<String> malSources = new ArrayList<String>();
	
	public abstract void init();
	public abstract void summary();
	public abstract void frameHook(Frame f);
	public abstract void flowHook(Flow f);
	
	/* All initialised experiments stored here */
	private static ArrayList<Experiment> experiments = new ArrayList<Experiment>();
	
	public Experiment()
	{
		//String caller = StringUtils.substringBetween(Thread.currentThread().getStackTrace()[2].toString(), "(", ")").split("\\.")[0];
		CodeLogger.log("New Experiment Created", DEPTH.PARENT);
		experiments.add(this);
		summary();
	}
	
	/* Takes any data in the CSVLoader() class and interprets them as
	 * malicious flows
	 * IMPLICIT ASSUMPTION: There are flows in the CSVLoader data sturcture */
	public void initMalicious(CSVLoader loader)
	{
		for (int i = 0; i < loader.data.size(); i++)
		{
			Flow flow = new Flow();
			flow.source = loader.data.get(i).get(0);
			flow.destination = loader.data.get(i).get(1);
			flow.sourcePort = Integer.valueOf(loader.data.get(i).get(2));
			flow.destinationPort = Integer.valueOf(loader.data.get(i).get(3));
			flow.protocol = NetUtils.protocolToNum(loader.data.get(i).get(4));
			flow.startTime = NetUtils.UNBTimestampToUnix(loader.data.get(i).get(5));
			flow.endTime = NetUtils.UNBTimestampToUnix(loader.data.get(i).get(6));
			flow.hash();
			
			if (this.malFlows.containsKey(flow.hash))
			{
				this.malFlows.get(flow.hash).add(flow);
			}
			else
			{
				this.malFlows.put(flow.hash, new ArrayList<Flow>());
				this.malFlows.get(flow.hash).add(flow);
			}
			
			if (!malSources.contains(flow.source))
				malSources.add(flow.source);				
		}
		
		CodeLogger.log("Finished Loading " + Utils.countElementsInHashArray(this.malFlows) + " Malicious Flows", DEPTH.CHILD);
	}
	
	/* Temporally correlates the input flow has with known malicious flows */
	protected short temporalFlowTag(String hash, long time)
    {
		// TODO: icmp_ip needs a hash
        if (this.malFlows.containsKey(hash))
        {
        	for (int i = 0; i < this.malFlows.get(hash).size(); i++)
        	{
	            if (time >= this.malFlows.get(hash).get(i).startTime && time <= this.malFlows.get(hash).get(i).endTime)
	            {
	                return 1;
	            }
        	}
        }
         
        return 0;
    }
	
	/* The hook called every time a new flow is loaded */
	public static void flowPipeline(Flow f)
	{
		for (int i = 0; i < experiments.size(); i++)
		{
			experiments.get(i).flowHook(f);
		}
	}
	
	/* The hook called every time a new frame is loaded */
	public static void framePipeline(Frame f)
	{
		for (int i = 0; i < experiments.size(); i++)
		{
			experiments.get(i).frameHook(f);
		}
	}
}
