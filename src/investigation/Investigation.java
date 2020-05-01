package investigation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import auxiliary.Mapping;
import universal.Classifier;
import universal.Classifier.TYPE;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;

public class Investigation
{
	private int returnTo = -1;
	private ArrayList<Belief> beliefSet = new ArrayList<Belief>();
	private String description = null;
	private Classifier classifier = new Classifier();
	private ArrayList<Belief> limiter = new ArrayList<Belief>();
	
	public Investigation(String description, int returnTo)
	{		
		CodeLogger.log("New Investigation Opened: " + description, DEPTH.PARENT);
		
		this.returnTo = returnTo;
		this.description = description;
	}
	
	public void target(String raw, boolean limit)
	{			
		for (Mapping map : classifier.identifyType(raw, false))
		{
			Belief b = new Belief(map.getRaw(), map.getTypes());
			beliefSet.add(b);
			CodeLogger.log("Target Added: " + map.getRaw() + " " + map.getTypes(), DEPTH.CHILD);
			
			if (limit)
				limiter.add(b);
		}				
	}
	
	public Set<TYPE> getTypes()
	{
		Set<TYPE> types = new HashSet<TYPE>();
		
		for (Belief b : beliefSet)
		{
			types.addAll(b.getTypes());
		}
		
		return types;
	}
	
	public ArrayList<Belief> getBeliefs()
	{
		return beliefSet;
	}
	
	public int getReturnTo() 
	{
		return returnTo;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void appendBeliefs(ArrayList<Belief> beliefs)
	{
		this.beliefSet.addAll(beliefs);
	}
	
	public void conclude()
	{
		CodeLogger.log("Investigation Concluded", DEPTH.CHILD);
		System.exit(0);
	}
	
	public Belief getBelief(int index)
	{
		return beliefSet.get(index);
	}
	
	public void updateLimiter()
	{
		for (int i = 0; i < beliefSet.size(); i++)
		{
			for (TYPE t : beliefSet.get(i).getTypes())
			{
				for (Belief b : limiter)
				{
					for (TYPE tl : b.getTypes())
					{
						//TODO: FIX ME
					}
				}
			}
		}
	}
}
