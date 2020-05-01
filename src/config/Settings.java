package config;

import universal.CodeClock;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;

public class Settings 
{
	/* Version */
	public static final float VERSION = (float) 1.0;
	public static final String TITLE = "NetworkPlayground";
	public static final String ARCHITECTURE = "StringBuilder";
	public static final String WORKING_DIR = "/Users/Phil/Google Drive/Programming/mac-eclipse-workspace/NetworkPlayground";
	public static final String DATA_DIR = WORKING_DIR + "/src/data";
	
	/* Print Settings */
	public static final boolean PRINT = false;
	public static final boolean PRINT_SUMMARY = false;
	
	/* PCAP File Loading Settings */
	public static final boolean PRINT_PCAP_LOADING = true;
	public static final int LOAD_LIMIT = Integer.MAX_VALUE;
	public static final int SKIP_AMOUNT = 0;
	
	/* Live Network Monitoring Settings */
	public static int captureLimit = 300;
	
	/* Performance */
	public static final String MAX_MEMORY = CodeClock.formatBytes(Runtime.getRuntime().maxMemory(), true);
	public static final int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();
	public static final boolean STORE_FRAMES = false;
	public static final int AVERAGE_PACKET_SIZE_BITS = 400;
	public static int loadedFileSizeBits = 0;
	public static final boolean RELEASE_BINARY_AFTER_PARSE = true;
	
	/* Substructures */
	public static final boolean VALIDATE_NETFLOW = true;
	public static final boolean VALIDATE_URL = false;
	public static final boolean STORE_FRAMES_IN_FLOW = true;
		
	@SuppressWarnings("unused")
	public Settings() 
	{
		if (SKIP_AMOUNT > 0 && LOAD_LIMIT == Integer.MAX_VALUE)
		{
			CodeLogger.log("Integer Overflow in PcapParser", DEPTH.ROOT);
			System.exit(0);
		}
	}
}
