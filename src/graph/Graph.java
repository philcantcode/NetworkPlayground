package graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import universal.Utils;
import utils.StatMath;

public class Graph
{
	private LinkedHashMap<Object, Node> nodes = new LinkedHashMap<Object, Node>();
	private LinkedHashMap<Object, Edge> edges = new LinkedHashMap<Object, Edge>();
	private static ArrayList<Graph> graphs = new ArrayList<Graph>();
	public int graphID = -1;
	
	private GraphStats stats = new GraphStats();
	
	public Graph() 
	{
		Graph.graphs.add(this);
		graphID = graphs.size() - 1;
	}
	
	public void createExclusiveNode(Object label)
	{
		if (nodeExists(label) == false)
		{
			createNode(label);
		}
		else
		{
			nodes.get(label).increaseNodeCount();
		}
	}
	
	public void createNode(Object label)
	{
		nodes.put(label, new Node(label));
		stats.numNodes++;
	}
	
	public void createExclusiveEdge(Object source, Object destination)
	{
		createExclusiveEdge(source, destination, null);
	}
	
	public void createExclusiveEdge(Object source, Object destination, Boolean isMalicious)
	{
		String label = Edge.labelBuilder(source, destination);
		
		if (edges.containsKey(label))
		{
			edges.get(label).increaseEdgeCount(isMalicious);
			stats.numConnections++;
			
			if (isMalicious)
				stats.malEdges++;
			else
				stats.normEdges++;
		}
		else
		{
			createExclusiveNode(source);			
			createExclusiveNode(destination);
			
			edges.put(label, new Edge(nodes.get(source), nodes.get(destination), isMalicious));
			
			stats.numEdges++;
			stats.numConnections++;
		}		
		
		/* Add an ingress node to the destination & egress to the source */
		this.nodes.get(source).addEgress(destination);
		this.nodes.get(destination).addIngress(source);
	}
	
	private boolean nodeExists(Object node)
	{
		if (nodes.containsKey(node))
			return true;
		else
			return false;
	}
	
	public void visualise(int id)
	{
		CodeLogger.log("Visual", DEPTH.PARENT);
				
		for(Object node : graphs.get(id).nodes.keySet())
		{
			if (graphs.get(id).countIngress(node) == 0)
			{
				System.out.print("[root]: " + node + "\n");
				
				visualseNodeEgress(node, id, 4);
			}
		}
	}
	
	private void visualseNodeEgress(Object node, int id, int depth)
	{
		for (Object subNode : graphs.get(id).nodes.get(node).egressNodes.keySet())
		{
			System.out.print(Utils.repeat(depth, " ") + "[SubNode] " + subNode + "\n");
			visualseNodeEgress(subNode, id, depth + 4);
		}
	}
	
	public void summary()
	{	
		CodeLogger.log("Graph Summary", DEPTH.PARENT);
		CodeLogger.log("Nodes: " + stats.numNodes, DEPTH.CHILD);
		CodeLogger.log("Edges: " + stats.numEdges, DEPTH.CHILD);
		CodeLogger.log("Connections: " + stats.numConnections, DEPTH.CHILD);
		CodeLogger.log("Malicious Edges: " + stats.malEdges, DEPTH.CHILD);
		CodeLogger.log("Normal Edges: " + stats.normEdges, DEPTH.CHILD);
		
		egressAndIngress();
		graphStandardDeviation();
	}
	
	private void graphStandardDeviation()
	{
		double[] nodes = new double[graphs.size()];
		double[] edges = new double[graphs.size()];
		double[] conns = new double[graphs.size()];
		
		for (int i = 0; i < graphs.size(); i++)
		{
			nodes[i] = graphs.get(i).stats.numNodes;
			edges[i] = graphs.get(i).stats.numEdges;
			conns[i] = graphs.get(i).stats.numConnections;
		}
		
		double nodeSTD = StatMath.standardDeviation(nodes, false);
		double edgeSTD = StatMath.standardDeviation(edges, false);
		double connSTD = StatMath.standardDeviation(conns, false);
				
		CodeLogger.log("Node Count STD: " + nodeSTD, DEPTH.CHILD);
		CodeLogger.log("Edge Count STD: " + edgeSTD, DEPTH.CHILD);
		CodeLogger.log("Cons Count STD: " + connSTD, DEPTH.CHILD);
	}
	
	private void egressAndIngress()
	{
		ArrayList<Integer> egress = new ArrayList<Integer>();
		ArrayList<Integer> ingress = new ArrayList<Integer>();
		
		for (Object k : nodes.keySet())
		{
			egress.add(nodes.get(k).getTotalEgress());
			ingress.add(nodes.get(k).getTotalIngress());
		}
		
		CodeLogger.log("Ingress: " + StatMath.sum(ingress), DEPTH.CHILD);
		CodeLogger.log("Egress: " + StatMath.sum(egress), DEPTH.CHILD);
	}
	
	private void classify()
	{
		ArrayList<Object> malNodes = new ArrayList<Object>();
		ArrayList<Object> inocNodes = new ArrayList<Object>();
		
		if (true)
		{
			for (Object sourceNode : nodes.keySet())
			{
				CodeLogger.log(sourceNode, DEPTH.PARENT);
				
				for (Object egressNode : nodes.get(sourceNode).egressNodes.keySet())
				{
					CodeLogger.log(egressNode + " Egress: " + nodes.get(sourceNode).percOfTotalEgress(egressNode)
							+ ", Return Ingress: " + nodes.get(egressNode).percOfTotalEgress(sourceNode), DEPTH.CHILD);
					
					if (nodes.get(egressNode).percOfTotalEgress(sourceNode) > 90.0)
					{
						if (!malNodes.contains(sourceNode))
							malNodes.add(sourceNode);
					}
					else
					{
						if (!inocNodes.contains(sourceNode))
							inocNodes.add(sourceNode);
					}
				}
			}
		}
		
		CodeLogger.log("Num Mal Nodes: " + malNodes, DEPTH.CHILD);
		CodeLogger.log("Num Inoc Nodes: " + inocNodes, DEPTH.CHILD);
	}
	
	public int countIngress(Object node)
	{
		if (nodeExists(node))
		{
			return nodes.get(node).ingressNodes.size();
		}
		else
		{
			CodeLogger.log("Error countIngress, node doesn't exist", DEPTH.PARENT);
			System.exit(0);
			return -1;
		}
	}
}
