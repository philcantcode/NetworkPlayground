package config;

import dataset.FolderTracker;
import experiments.Experiment;
import experiments.OneHotFactory;
import investigation.PentestHandle;
import universal.CSVLoader;
import universal.Classifier;
import universal.CodeLogger;
import universal.Types;
import universal.Utils;
import utils.NetUtils;
import utils.OS;

public class Start 
{
	/*
	 * Package Structure: 
	 * 		config : Startup and settings
	 * 		data : External resources
	 * 		engine : JNI interface for working with libcap in C
	 * 		frames : specification for each protocol
	 * 		natives : the C library for working with libpcap
	 * 		utils : various functions for doing stuff
	 * 
	 *  Frame Structurre:
	 *  	Frame is the top level class for every captured frame.
	 *  	For each sub-protocol a class is initialised and frame passed to it.
	 *  	Frame is put inside of a packet class from which the protocol class extends.
	 * */
	
	public static FolderTracker pcapFile;
	public static FolderTracker tagFile;
	public static FolderTracker jun13XmlFile;
		
	public static void main(String[] args) 
	{
		/* Initialise the utility classes */
		new Types();
		new OS();
		new Utils();
		new NetUtils();
		new Settings();
		new Classifier();
		new PentestHandle();
		new CodeLogger();
		
		//PentestHandle.loadAgentsv2();
		//System.exit(0);
		
		//PentestHandle.testInvestigation();

		//Creator.UNB_ISCX_MALICIOUS_FLOWS_EXTRACTOR();
		
		//CSVLoader.load("/Users/Phil/Dropbox/Datasets/OHF-Jun13-NoIP-CollapsedPorts.csv");
		
		Experiment onehot = new OneHotFactory();
		onehot.initMalicious(new CSVLoader("/Users/Phil/Google Drive/Datasets/Jun13Testbed/Malicious.csv").saveData().load());
		onehot.init();
		//new FlowProfile().init();
		//new SpotLight().init();
		
		/*
		exp = new Experiment();
		exp.initSunJun13Flows();
		exp.loadXmlTags();

		exp.parseDataset();
		
		OneHotFactory.printData();
		*/
		
		
		
		
//		int c = 0;
//		for (String hash : sunJun13Flows.attackHashFlows)
//		{
//			if (Netflow.netflowList.containsKey(hash))
//			{
//				System.out.println("Hash Found! " + c);
//				c++;
//			}
//		}
		
		//Attacks.spoofARP();
		
		//new NetHandler("en0", STATUS.ETHER_PROMISC_ON, 100).start();
		
//		Net net = new Net("en0");
//		net.hopper.addChannelSet(CHANNELS.LOW_CHANNELS);
//		net.hopper.addChannelSet(CHANNELS.GHZ_24_CHANNELS);
//		net.hopper.addChannelSet(CHANNELS.GHZ_5_CHANNELS);
//		net.hopper.addChannelSet(CHANNELS.UNII1_CHANNELS);
//		net.hopper.start();
	}
	
	private static void printSettings()
	{
		/* Print the Settings */
		Utils.printDivider("Info");
		System.out.println("    Version: " + Settings.TITLE + " v" + Settings.VERSION);
		System.out.println("    Operating System: " + OS.osVersion);
		System.out.println("    Working Directory: " + OS.workingDirectory);
		System.out.println("    User is Root: " + OS.isRoot);
		System.out.println("    Endianness: " + OS.getEndianness());
		Utils.printDivider("General Print Settings");
		System.out.println("    Print: " + Settings.PRINT + " (display packets as captured/loaded)");
		System.out.println("    Print Summary: " + Settings.PRINT_SUMMARY + " (display capture summary)");
		Utils.printDivider("PCAP Loading Settings");
		System.out.println("    Print Pcap Loading: " + Settings.PRINT_PCAP_LOADING + " (display count of loaded pcap frames)");
		System.out.println("    Pcap Print Increment: " + CSVLoader.PRINT_INCREMENT);
		System.out.println("    Pcap Load Limit: " + Settings.LOAD_LIMIT);
		System.out.println("    Pcap Skip Amount: " + Settings.SKIP_AMOUNT + " (skip first n-amount of frames)");
		Utils.printDivider("Live Capture Settings");
		System.out.println("    Capture Limit: " + Settings.captureLimit);
		Utils.printDivider("Performance");
		System.out.println("    Max Memory: " + Settings.MAX_MEMORY);
		System.out.println("    Num Processors: " + Settings.NUM_PROCESSORS);
		System.out.println("    Release Binary Strings: " + Settings.RELEASE_BINARY_AFTER_PARSE + " (sets bin string to null after parsing)");
		System.out.println("    Store Frames: " + Settings.STORE_FRAMES + " (stores frames in arraylists after parsing)");
		System.out.println("    Validate Netflows: " + Settings.VALIDATE_NETFLOW + " (parses frames for netflows)");
		System.out.println("    Validate URLs: " + Settings.VALIDATE_URL + " (parses frames for urls)");
		System.out.println();
	}

}
