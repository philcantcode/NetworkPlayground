package investigation.agents;

import java.util.ArrayList;

import investigation.Desire;
import investigation.Executable;
import investigation.Investigation;

/* 
 * Agents may recieve investigation objects containing all
 * necessary information about the target and results set.
 * 
 * TODO: Allow agents to dynamically create investigations
 * 
 *  */
public class CustomAgent extends Agent implements Executable
{	
	public Executable executable = null;
		
	public CustomAgent(Desire desire, Executable exe)
	{
		super(SPECIALITY.CUSTOM, desire);
		
		this.executable = exe;
	}
	
	public ArrayList<String> search()
	{	
		busy = true;
		ArrayList<String> results = new ArrayList<String>();		
		executable.custom();
		
		return results;
	}
	
	@Override
	public void custom()
	{
		
	}

	@Override
	public boolean compatability(Investigation inv)
	{
		return false;
	}
}
