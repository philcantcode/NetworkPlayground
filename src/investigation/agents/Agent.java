package investigation.agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import auxiliary.Mapping;
import investigation.Belief;
import investigation.Contract;
import investigation.Desire;
import investigation.Investigation;
import investigation.PentestHandle;
import universal.Classifier;
import universal.Classifier.TYPE;
import universal.CodeClock;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;

public abstract class Agent extends Thread
{
	protected int id = -1;
	protected SPECIALITY speciality = null;
	protected Desire desire = null;
	protected boolean busy = false;
	
	private final int THREAD_SLEEP = 2000;
	
	protected Investigation investigation = null;
	protected Set<String> commandHistory = new HashSet<String>();
	protected Classifier classifier = new Classifier();
	
	/* Shared Information Exchange */
	protected static BlockingQueue<Investigation> investigationPool = new ArrayBlockingQueue<>(10);
	protected static BlockingQueue<Contract> contractBridge = new ArrayBlockingQueue<>(10);

	public Agent(SPECIALITY speciality, Desire desire)
	{
		this.id = PentestHandle.agents.size();
		this.speciality = speciality;	
		this.desire = desire;
		
		CodeLogger.log("Agent-" + id + " " + desire.getDescription() + " " + desire.getConditions(), DEPTH.CHILD, true);
		PentestHandle.agents.add(this);
		start();
	}
	
	protected enum SPECIALITY
	{
		TERMINAL,
		CUSTOM
	}
	
	protected void processResults(ArrayList<String> results, List<TYPE> filterList)
	{
		ArrayList<Belief> resultSet = new ArrayList<Belief>();
		resultSet.forEach(b -> b.setAgentID(id));
		
		for (String line : results)
		{
			for (String element : line.split(" "))
			{
				for (Mapping map : classifier.identifyType(element, true))
				{
					resultSet.add(new Belief(this.id, map.getRaw(), map.getTypes()));
				}
			}
		}
		
		for (Belief b : resultSet)
		{
			if (!filterList.containsAll(b.getTypes()))
			{
				CodeLogger.log("Analysis: " + b.getRaw() + "\t" + b.getTypes(), DEPTH.CHILD, false);
			}
		}	
				
		/* Add the beliefs from the resultSet to the investigation
		 * before sending it back */
		this.investigation.appendBeliefs(resultSet);
	}
	
	public boolean hasCondition(TYPE condition)
	{
		if (desire.hasCondition(condition))
			return true;
		
		return false;
	}
	
	/*
	 * Adds the investigtaion back to the queue
	 */
	public void queue()
	{
		CodeLogger.log("Queueing Invesitgation (size: " + investigation.getBeliefs().size() + ")", DEPTH.CHILD);
		investigationPool.add(this.investigation);
	}
	
	public void queue(Investigation inv)
	{
		this.investigation = inv;
		queue();
	}
	
	public int getID()
	{
		return id;
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			CodeLogger.log("Agent-" + this.id + " available (" + investigationPool.size() + ")", DEPTH.CHILD, false);
			if (investigationPool.size() > 0)
			{
				Investigation inv = investigationPool.peek();
				
				if (inv == null)
					continue;
				
				if (compatability(inv) && inv.getReturnTo() != this.id)
				{
					this.investigation = investigationPool.poll();
					
					if (this.investigation != null)
					{
						CodeClock clock = new CodeClock(false, false);
						processResults(this.search(), Classifier.FILTER_NON_SPECIFIC);
						clock.end();
						queue();
					}
				}
			}
			
			try
			{
				Thread.sleep(THREAD_SLEEP);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private boolean limiterChecks(Investigation inv)
	{
		for (Belief b : inv.getBeliefs())
		{
			
		}
		
		return false;
	}
	
	/* 
	 * Agent must overwrite this method to perform checks to see
	 * if the invesitgation meets its requirements. 
	 */
	public abstract boolean compatability(Investigation inv);
	
	/* Agent must overwrite this method to perform its search
	 * functionaltiy
	 */
	public abstract ArrayList<String> search();

}
