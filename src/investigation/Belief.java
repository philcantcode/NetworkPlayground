package investigation;

import java.util.HashSet;
import java.util.Set;

import universal.Classifier.TYPE;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import universal.Utils;

public class Belief
{
	private int agentID = -1;
	private Set<TYPE> types = new HashSet<TYPE>();
	private String raw = null;
	private boolean disabled = false;
	private String hash = null;
	private boolean limitConstrained = false;
	
	public Belief(int agentID, String raw, Set<TYPE> types)
	{
		this.agentID = agentID;
		this.raw = raw;
		this.types.addAll(types);
		hash = Utils.sha256Hash(raw);
	}
	
	public Belief(int agentID, String raw, TYPE type)
	{
		this.agentID = agentID;
		this.raw = raw;
		this.types.add(type);
		hash = Utils.sha256Hash(raw);
	}
	
	public Belief(String raw)
	{
		this.raw = raw;
		hash = Utils.sha256Hash(raw);
	}
	
	public Belief(String raw, Set<TYPE> types)
	{
		this.raw = raw;
		this.types.addAll(types);
		hash = Utils.sha256Hash(raw);
	}
	
	public void setAgentID(int agentID)
	{
		this.agentID = agentID;
	}
	
	public static TYPE checkType(String type)
	{
		for (TYPE t : TYPE.values())
		{
			if (t.name().equals(type))
			{
				return t;
			}
		}
		
		CodeLogger.err("The type \"" + type + "\" is not valid: Check pentest-agents.txt", DEPTH.ROOT);
		
		return null;
	}
	
	public static boolean containsType(Set<Belief> beliefs, TYPE type)
	{
		for (Belief b : beliefs)
		{
			if (b.types.contains(type))
				return true;
		}
		
		return false;
	}
	
	public Set<TYPE> getTypes()
	{
		return this.types;
	}
	
	public String getRaw()
	{
		return this.raw;
	}
	
	public void print()
	{
		CodeLogger.log("Belief: " + raw + "\t" + types, DEPTH.CHILD);
	}
	
	public void addType(TYPE t)
	{
		this.types.add(t);
	}
	
	public void addTypes(Set<TYPE> ts)
	{
		this.types.addAll(ts);
	}
	
	public Belief getBelief()
	{
		return this;
	}
	
	public String getHash()
	{
		return this.hash;
	}
	
	public void limit(boolean limit)
	{
		this.limitConstrained = limit;
	}
	
	public boolean getLimit()
	{
		return this.limitConstrained;
	}

}
