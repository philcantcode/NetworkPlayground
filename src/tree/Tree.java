package tree;

import java.util.ArrayList;

import universal.Utils;

public class Tree
{
	public ArrayList<Node> nodes = new ArrayList<Node>();
	
	public Tree()
	{
		
	}
	
	public void createRoot(Object label)
	{	
		boolean found = false;
		
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.get(i).label == label || nodes.get(i).label.equals(label))
			{
				found = true;
			}
		}
		
		if (!found)
			nodes.add(new Node(label));
	}
	
	public void relate(Object parent, Object child)
	{
		createRoot(parent);
		createRoot(child);
		
		nodes.get(getNodeIndex(child)).addParent(parent);
		nodes.get(getNodeIndex(parent)).addChild(child);
	}
	
	public void visualise()
	{
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.get(i).parent == null)
			{
				System.out.println("|- " + nodes.get(i).label);
				parseTree(nodes.get(i).label, 4);
			}
		}
	}
	
	private void parseTree(Object label, int depth)
	{
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.get(i).label == label || nodes.get(i).label.equals(label))
			{
				for (int j = 0; j < nodes.get(i).children.size(); j++)
				{
					System.out.print(Utils.repeat(depth, " ") + "|- " + nodes.get(i).children.get(j) + "\n");
					parseTree(nodes.get(i).children.get(j), depth + 4);
				}
			}
		}
	}
	
	public Node getNode(Object label)
	{
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.get(i).label == label || nodes.get(i).label.equals(label))
			{
				return nodes.get(i);
			}
		}
		
		return null;
	}
	
	public int getNodeIndex(Object label)
	{
		for (int i = 0; i < nodes.size(); i++)
		{
			if (nodes.get(i).label == label || nodes.get(i).label.equals(label))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	class Node
	{
		public Object label = null;
		public Object parent = null;
		public ArrayList<Object> children = new ArrayList<Object>();
		
		public Node(Object label)
		{
			this.label = label;
		}
		
		public void addChild(Object label)
		{
			if (!children.contains(label))
			{
				children.add(label);
			}
		}
		
		public void addParent(Object label)
		{
			this.parent = label;
		}
	}
}
