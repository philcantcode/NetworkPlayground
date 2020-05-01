package experiments;

import java.util.ArrayList;

import engine.PcapParser;
import frames.Frame;
import substructures.Flow;
import substructures.NetFlow;
import substructures.NetFlow.DIRECTION;
import universal.CSVLoader;
import universal.CSVWriter;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import utils.OS;

public class FlowProfile extends Experiment
{

	private static final String OUTPUT_FOLDER = "/Users/Phil/Dropbox/Datasets/FlowProfile/";
	private static final String OUTPUT_FILE_NAME = "test.csv";
	private static CSVWriter csvWriter = null;
	
	public FlowProfile() 
	{
		
	}
	
	public void init()
	{
		PcapParser.parsePCAP("/Users/Phil/Dropbox/Datasets/testbed-13junLocalFilter.pcap");
		csvWriter = new CSVWriter(OUTPUT_FOLDER + OUTPUT_FILE_NAME);
		tagFlows();
		
		for (String key : NetFlow.netFlowList.keySet())
		{
			for (String subKey : NetFlow.netFlowList.get(key).keySet())
			{
				NetFlow nf = NetFlow.netFlowList.get(key).get(subKey);
				ArrayList<String> data = new ArrayList<String>();
				
				for (int i = 0; i < nf.flows.size(); i++)
				{
					int uniqueSrcPorts = nf.countUniquePorts(DIRECTION.SOURCE);
					int uniqueDstPorts = nf.countUniquePorts(DIRECTION.DESTINATION);
					
					System.out.println(uniqueSrcPorts);
					System.out.println(uniqueDstPorts);
					System.out.println();
				}
			}
		}
	}
	
	private void tagFlows()
	{
		int numFlows = 0;
		int numMal = 0;
		int numNorm = 0;
		
		for (String key : NetFlow.netFlowList.keySet())
		{
			for (String subKey : NetFlow.netFlowList.get(key).keySet())
			{				
				for (int i = 0; i < NetFlow.netFlowList.get(key).get(subKey).flows.size(); i++)
				{
					int tag = this.temporalFlowTag(NetFlow.netFlowList.get(key).get(subKey).flows.get(i).hash, NetFlow.netFlowList.get(key).get(subKey).flows.get(i).dateTime);
					
					if (tag == 1)
					{
						NetFlow.netFlowList.get(key).get(subKey).flows.get(i).isMalicious = true;
						numMal++;
					}
					else
					{
						NetFlow.netFlowList.get(key).get(subKey).flows.get(i).isMalicious = false;
						numNorm++;
					}
					
					numFlows++;
				}
			}
		}
		
		CodeLogger.log("Finished Parsing Flows", DEPTH.PARENT);
		CodeLogger.log("Num Flows: " + numFlows, DEPTH.CHILD);
		CodeLogger.log("Num Norm: " + numNorm, DEPTH.CHILD);
		CodeLogger.log("Num Mal: " + numMal, DEPTH.CHILD);
	}

	@Override
	public void frameHook(Frame f) 
	{
		
	}

	@Override
	public void flowHook(Flow f) 
	{
		
	}

	@Override
	public void summary() 
	{
		
	}

}
