package substructures;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;

import config.Settings;
import universal.Utils;

public class URL
{	
	public static HashMap<String, URL> urlList = new HashMap<String, URL>();
	
	private static final String  URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
	private static final Matcher URL_MATCHER = URL_PATTERN.matcher("");
	private static final UrlValidator URL_VALIDATOR = new UrlValidator();
	
	private int count = 1;
	private String url = "";
	
	/* Never call the URL constructor manually, always go through the extractURLs method */
	private URL(String url) 
	{					
		this.url = url;
		
		urlList.put(url, this);
	}
			
	public static boolean validateURL(String text)
	{
		boolean match = false;
		
		if (Settings.VALIDATE_URL)
		{
			URL_MATCHER.reset(text);
			String url = "";
			
		    while (URL_MATCHER.find())
		    {
		        url = text.substring(URL_MATCHER.start(0), URL_MATCHER.end(0));
	
		        if (URL_VALIDATOR.isValid(url))
		        {
		        	if (urlList.keySet().contains(url))
		    		{
		    			urlList.get(url).count++;
		    		}
		    		else
		    		{
		    			new URL(url);
		    		}
		        	
		        	match = true;
		        }
		    }
		}
	    
	    return match;
	}
	
	public static void print()
	{
		Utils.printDivider("URL List");
		
		for (String s : urlList.keySet())
		{
			System.out.println("    " + urlList.get(s).url + ": " + urlList.get(s).count);
		}
	}	

}
