package engine;

import java.util.ArrayList;

import universal.Types;

public class FrameBuilder 
{
	private ArrayList<String> stack = new ArrayList<String>();
	
	public FrameBuilder() 
	{
		
	}
	
	public void buildEthernet(String destination, String source, String type)
	{
		stack.add(destination.replaceAll(":", ""));
		stack.add(source.replaceAll(":", ""));
		stack.add(type);
	}
	
	public void buildARP(String hwType, String protoType, String hwSize, String protoSize, String opCode, String sendMac, String sendIP, String destMac, String destIP)
	{
		stack.add(hwType);
		stack.add(protoType);
		stack.add(hwSize);
		stack.add(protoSize);
		stack.add(opCode);
		stack.add(sendMac.replaceAll(":", ""));
		stack.add(Types.ipToHex(sendIP));
		stack.add(destMac.replaceAll(":", ""));
		stack.add(Types.ipToHex(destIP));
	}
	
	public String compile()
	{
		String hex = "";
		
		for (String s : stack)
		{
			hex += s;
		}
		
		return hex + "\\";
	}
}
