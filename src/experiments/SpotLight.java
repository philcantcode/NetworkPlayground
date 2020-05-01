package experiments;

import java.util.ArrayList;

import engine.PcapParser;
import frames.Frame;
import graph.Graph;
import substructures.Flow;
import universal.CSVLoader;
import utils.OS;

// SpotLight: Detecting anomalies in streaming graphs : http://delivery.acm.org/10.1145/3230000/3220040/p1378-eswaran.pdf?ip=88.98.244.189&id=3220040&acc=OA&key=4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E4D4702B0C3E38B35%2E6DE71D4150C19379&__acm__=1567166150_7bc65c161f28a75daf70166bf1dd3a7a
public class SpotLight extends Experiment
{
	public static long startTime = -1;
	public static long currentTime = -1;
	public static final long SAMPLE_TIME_MS = 120 * 60 * 1000;
	public static int count = 0;
	
	public static ArrayList<Flow> flowBuffer = new ArrayList<Flow>();
	
	public SpotLight()
	{
		
	}
	
	public static void generateGraph()
	{		
		Graph graph = new Graph();
		
		for (int i = 0; i < flowBuffer.size(); i++)
		{
			graph.createExclusiveEdge(flowBuffer.get(i).source, flowBuffer.get(i).destination, flowBuffer.get(i).isMalicious);
		}
				
		graph.summary();		
	}
	
	@Override
	public void frameHook(Frame f) 
	{
		
	}

	@Override
	public void flowHook(Flow flow) 
	{
		flow.tag(temporalFlowTag(flow.hash, flow.dateTime));
		
		if (SpotLight.startTime == -1)
			SpotLight.startTime = flow.dateTime; 
				
		if ((long) (flow.dateTime - startTime) > SAMPLE_TIME_MS)
		{
			SpotLight.startTime = flow.dateTime;
			generateGraph();
			flowBuffer.clear();
			count = 0;
		}		
		else
		{
			SpotLight.currentTime = flow.dateTime;
			flowBuffer.add(flow);
			count++;
		}
		
	}

	@Override
	public void init() 
	{
		PcapParser.parsePCAP("/Users/Phil/Dropbox/Datasets/testbed-13junLocalFilter.pcap");
	}
	
	@Override
	public void summary() 
	{
		
	}

}
