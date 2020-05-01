package graph;

public class Edge 
{
	public Node source = null;
	public Node destination = null;
	public String label = null;	
	
	private int edgeCount = 0;
	private int normCount = 0;
	private int malCount = 0;
	
	protected Edge(Node source, Node destination, Boolean isMalicious) 
	{
		this.source = source;
		this.destination = destination;
		this.label = labelBuilder(source.label, destination.label);
		this.edgeCount++;
	}
	
	public int getEdgeCount()
	{
		return this.edgeCount;
	}
	
	public void increaseEdgeCount(Boolean isMalicious)
	{
		this.edgeCount++;
		
		if (isMalicious)
			this.malCount++;
		else if (!isMalicious)
			this.normCount++;
	}
	
	public static String labelBuilder(Object source, Object destination)
	{
		return source + "-to-" + destination;
	}
}
