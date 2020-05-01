package investigation.agents;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import investigation.Belief;
import investigation.Desire;
import investigation.Investigation;
import universal.Classifier.TYPE;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import universal.Utils;

/* 
 * Agents may recieve investigation objects containing all
 * necessary information about the target and results set.
 * 
 * TODO: Allow agents to dynamically create investigations
 * 
 *  */
public class TerminalAgent extends Agent
{	
	private String terminalCMD = null;
		
	public TerminalAgent(Desire desire, String terminalCMD)
	{
		super(SPECIALITY.TERMINAL, desire);
		
		this.terminalCMD = terminalCMD;
	}
	
	/* 
	 * This method builds the commands by taking in the investigation which represents all of the available 
	 * data and its internal command that it can perform.
	 * 
	 *  All possible command variations from the data are then created and tested for running.
	 */
	private ArrayList<List<String>> buildCommands(Investigation inv)
	{
		/* 
		 *  Commands = Array of Lists for the full commands e.g., [[nmap 127.0.0.1], nmap[127.0.0.2]]
		 *  varTypes = Array of HashMaps (K = TYPE, V = Array of Data e.g. [127.0.0.1, 127.0.0.2]
		 *  orderedVars = Array of ordered variable combinations e.g., [[127.0.0.1, UDP], [127.0.0.1, TCP], [127.0.0.2, UDP], [127.0.0.2, TCP]]
		 */
		ArrayList<List<String>> commands = new ArrayList<List<String>>();
		ArrayList<HashMap<TYPE, ArrayList<String>>> varTypes = new ArrayList<HashMap<TYPE, ArrayList<String>>>();
		ArrayList<ArrayList<String>> orderVars = new ArrayList<ArrayList<String>>();
		
		/*
		 * original = The command with <TYPE> placeholders 
		 */
		char[] original = terminalCMD.toCharArray();

		/*
		 * Loop over each character in the placeholder command to find the <TYPE>
		 */
		for (int i = 0; i < original.length; i++)
		{
			if (original[i] == '<')
			{
				String strType = terminalCMD.substring(i + 1, terminalCMD.toString().indexOf('>', i)); // Finds the <TYPE> between the brackets
				HashMap<TYPE, ArrayList<String>> temp = new HashMap<TYPE, ArrayList<String>>();
				temp.put(Belief.checkType(strType), new ArrayList<String>());
				varTypes.add(temp);
			}
		}
		
		/*
		 * Loops over the <TYPES> from varTypes and finds all data that matches the TYPE
		 */
		for (int i = 0; i < varTypes.size(); i++)
		{
			for (TYPE t : varTypes.get(i).keySet())
			{
				for (int j = 0; j < inv.getBeliefs().size(); j++)
				{
					if (inv.getBelief(j).getTypes().contains(t))
					{
						varTypes.get(i).get(t).add(inv.getBelief(j).getRaw());
						
						if (orderVars.size() < (i + 1))
							orderVars.add(new ArrayList<String>());
						
						orderVars.get(i).add(inv.getBelief(j).getRaw());
					}
				}
			}
		}
		
		/* 
		 * Filter out any variable types that the agent doesn't have data for 
		 */
		
		boolean notFound = false;
		
		for (int i = 0; i < varTypes.size(); i++)
		{
			for (TYPE j : varTypes.get(i).keySet())
			{
				if (varTypes.get(i).get(j).size() == 0)
				{
					notFound = true;
					varTypes.remove(i);
				}
			}
		}
				
		/* 
		 * Recursively loops the nested array list to find all unique and ordered combinations of variables 
		 */
		ArrayList<String> combinations = new ArrayList<String>();
		int combinationCounter = 0;
		
		if (varTypes.size() > 0)
		{
			combinations = recurse(orderVars, new ArrayList<Integer>(), 0);
			combinationCounter = (combinations.size() / varTypes.size());
		}
		else if (varTypes.size() == 0 && notFound == false)
		{
			List<String> singleCMD = new ArrayList<String>();
			singleCMD.add(terminalCMD);
			commands.add(singleCMD);
		}
		
		/* 
		 * With the order of TYPEs known and the order of variables in the combinations array,
		 * loop over the original command and replace the <TYPE> placeholder with the ordered
		 * variable from the combinations array
		 */
		int counter = 0;
		for (int j = 0; j < combinationCounter; j++)
		{
			List<String> command = new ArrayList<String>();
			
			boolean inTag = false;
			StringBuilder part = new StringBuilder();
			
			for (int i = 0; i < original.length; i++)
			{
				if (original[i] == ' ')
				{
					command.add(part.toString());
					part = new StringBuilder();
				}
				else if (original[i] == '<')
				{
					inTag = true;
					part.append(combinations.get(counter++));
				}
				else if (original[i] == '>')
				{
					inTag = false;
				}
				else if (inTag == false)
				{
					part.append(original[i]);
				}						
			}	
			
			command.add(part.toString());
			commands.add(command);
		}
		
		return commands;
	}
	
	private ArrayList<String> recurse(ArrayList<ArrayList<String>> inputs, ArrayList<Integer> index, int depth)
	{
		ArrayList<String> results = new ArrayList<String>();
				
		for (int k = 0; k < inputs.get(depth).size(); k++)
		{	
			index.add(k);
			
			if (depth < inputs.size() - 1)
				results.addAll(recurse(inputs, index, (depth + 1)));
			
			if (index.size() == inputs.size())
			{				
				for (int j = 0; j < index.size(); j++)
					results.add(inputs.get(j).get(index.get(j)));
			}
			
			index.remove(index.size() - 1);
		}	
		
		return results;
	}
	
	@Override
	public ArrayList<String> search()
	{	
		busy = true;
		ArrayList<String> results = new ArrayList<String>();
		
		for (List<String> command : buildCommands(this.investigation))
		{
			if (commandHistory.contains(Utils.sha256Hash(command.toString())))
				continue;
			
			CodeLogger.log("Agent-" + id + " is about to run (" + Agent.currentThread().getName() + "): " + command, DEPTH.PARENT, true);
			commandHistory.add(Utils.sha256Hash(command.toString()));
			Utils.delay(3);
					
			try
			{
				ProcessBuilder pb = new ProcessBuilder(command);
				pb.directory(new File("/"));
				Process proc = pb.start();
				proc.waitFor();
				
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
				
				String s = null;
				while ((s = stdInput.readLine()) != null) 
				{
				    results.add(s);
				}
				
				while ((s = stdError.readLine()) != null)
				{
				    CodeLogger.err(s, DEPTH.CHILD);
				}
				
				stdInput.close();
				stdError.close();
			} 
			catch (IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
			
			busy = false;

		}
		
		return results;
	}

	@Override
	public boolean compatability(Investigation inv)
	{
		Set<TYPE> types = inv.getTypes();
		
		if (this.busy)
			return false;
				
		if (types.containsAll(desire.getConditions()) || desire.hasCondition(TYPE.NULL))
		{
			boolean newCommand = false;
			
			for (List<String> command : buildCommands(inv))
			{
				if (!commandHistory.contains(Utils.sha256Hash(command.toString())))
				{
					newCommand = true;
				}
			}
			
			if (newCommand)
				return true;
		}
		
		return false;
	}
}
