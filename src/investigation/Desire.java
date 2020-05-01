package investigation;

import java.util.HashSet;
import java.util.Set;

import tree.Tree;
import universal.Classifier.TYPE;

public class Desire
{
	private Set<TYPE> conditions = new HashSet<TYPE>();
	private Set<TYPE> effects = new HashSet<TYPE>();
	private Tree beliefMap = new Tree();
	private String description = "-1";
	
	public Desire(String description, String conditions, String effects)
	{
		this.description = description;
		
		if (conditions.length() > 0)
		{
			for (String s : (conditions + " ").split(" "))
			{
				this.conditions.add(Belief.checkType(s));
			}
		}
		
		if (effects.length() > 0)
		{
			for (String s : (effects + " ").split(" "))
			{
				this.effects.add(Belief.checkType(s));
			}
		}
				
		beliefMap.relate(TYPE.IP, TYPE.PORT);
		beliefMap.relate(TYPE.IP, TYPE.USER);
		beliefMap.relate(TYPE.IP, TYPE.OS);
		beliefMap.relate(TYPE.IP, TYPE.IPV4);
		beliefMap.relate(TYPE.IP, TYPE.IPV6);
		beliefMap.relate(TYPE.IPV4, TYPE.PORT);
		beliefMap.relate(TYPE.IPV6, TYPE.PORT);
		
		beliefMap.relate(TYPE.MAC, TYPE.IP);
		beliefMap.relate(TYPE.MAC, TYPE.VENDOR);
		
		beliefMap.relate(TYPE.BSSID, TYPE.SSID);
		beliefMap.relate(TYPE.BSSID, TYPE.IP);
		beliefMap.relate(TYPE.BSSID, TYPE.VENDOR);
		
		beliefMap.relate(TYPE.PORT, TYPE.SERVICE);
		
		beliefMap.relate(TYPE.SERVICE, TYPE.USER);
		beliefMap.relate(TYPE.SERVICE, TYPE.UPTIME);
		beliefMap.relate(TYPE.SERVICE, TYPE.VERSION);
		beliefMap.relate(TYPE.SERVICE, TYPE.VERSION);
		
		beliefMap.relate(TYPE.URL, TYPE.USER);
		beliefMap.relate(TYPE.URL, TYPE.IP);
		beliefMap.relate(TYPE.URL, TYPE.VENDOR);
		
		beliefMap.relate(TYPE.VERSION, TYPE.CVE_VULNERABILITY);
	}
	
	public int sizeOfConditions()
	{
		return conditions.size();
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public boolean hasCondition(TYPE con)
	{
		if (this.conditions.contains(con))
			return true;
		
		return false;		
	}
	
	public Set<TYPE> getConditions()
	{
		return conditions;
	}
}
