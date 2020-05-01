package attacks;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class PingScanLan 
{

	 public PingScanLan()
	 {	 
        Vector<String> devices = new Vector<>(); // stores the list of available/connected devices
        String myip = null;
        
		try {
			myip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		// IP of the PC in which the code is running/localhost
        if(myip.equals("127.0.0.1"))
        {
            System.out.println("This PC is not connected to any network!");
        }
        else 
        {
            String mynetworkips = new String();
 
            for(int i = myip.length() - 1; i >= 0; --i)
            {
                if(myip.charAt(i)=='.')
                {
                    mynetworkips=myip.substring(0,i+1);
                    break;
                }
            }
 
            System.out.println("My Device IP: " + myip+"\n"); // Shows this PC's IP
            System.out.println("Search log:");
 
            for(int i=1; i<= 254; ++i)
            {
                try {
                    InetAddress addr = InetAddress.getByName(mynetworkips + new Integer(i).toString());
 
                    if (addr.isReachable(100))
                    {
                        System.out.println("Available: " + addr.getHostAddress()); 
                        devices.add(addr.getHostAddress());
                    }
                    else 
                	{
                    	System.out.println("Not available: "+ addr.getHostAddress());
                	}
 
                }
                catch (IOException ioex) {
                }
            }
 
            System.out.println("\nAll Connected devices(" + devices.size() +"):");
            for(int i=0;i<devices.size();++i) System.out.println(devices.get(i));
        }
    }
}
