package graph;

import java.util.LinkedHashMap;

import universal.Utils;

public class Node
{
	public Object label = null;
	private int nodeCount = 0;
	
	public LinkedHashMap<Object, Integer> ingressNodes = new LinkedHashMap<Object, Integer>();
	public LinkedHashMap<Object, Integer> egressNodes = new LinkedHashMap<Object, Integer>();
	
	protected Node(Object label) 
	{
		this.label = label;
		
		this.nodeCount++;
	}
	
	public int getNodeCount()
	{
		return this.nodeCount;
	}
	
	public void increaseNodeCount()
	{
		this.nodeCount++;
	}
	
	public void addIngress(Object label)
	{
		if (ingressNodes.containsKey(label))
			ingressNodes.put(label, (ingressNodes.get(label) + 1));
		else
			ingressNodes.put(label, 1);
	}
	
	public void addEgress(Object label)
	{
		if (egressNodes.containsKey(label))
			egressNodes.put(label, (egressNodes.get(label) + 1));
		else
			egressNodes.put(label, 1);
	}
	
	public int getTotalIngress()
	{
		int num = 0;
		
		for (Object k : ingressNodes.keySet())
		{
			num += ingressNodes.get(k);
		}
		
		return num;
	}
	
	public int getTotalEgress()
	{
		int num = 0;
		
		for (Object k : egressNodes.keySet())
		{
			num += egressNodes.get(k);
		}
		
		return num;
	}
	
	public int getEgress(String src)
	{
		int num = 0;
		
		for (Object k : egressNodes.keySet())
		{
			if (k.equals(src))
				num += egressNodes.get(k);
		}
		
		return num;
	}
	
	public int getInress(String src)
	{
		int num = 0;
		
		for (Object k : ingressNodes.keySet())
		{
			if (k.equals(src))
				num += ingressNodes.get(k);
		}
		
		return num;
	}
	
	public float percOfTotalEgress(Object sourceNode)
	{
		double totalForSrc = 0;
		double total = 0;
		
		for (Object k : egressNodes.keySet())
		{
			if (k.equals(sourceNode))
			{
				total += egressNodes.get(k);
				totalForSrc += egressNodes.get(k);
			}
			else
			{
				total += egressNodes.get(k);
			}
		}
	
		if (total == 0.0)
			return (float) 0.0;
		else
			return Float.valueOf(Utils.DECIMAL_FORMAT.format((totalForSrc / total) * 100.0));
	}
}
