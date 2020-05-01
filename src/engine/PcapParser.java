package engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import config.Settings;
import frames.Frame;
import universal.CSVLoader;
import universal.CodeClock;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import universal.Types;
import universal.Utils;

public class PcapParser
{	
	private PcapParser() 
	{

	}
	
	public static int parsePCAP(String pcapPath)
	{		
		File f = new File(pcapPath);
		
		if (f.exists() && f.isFile() && !f.isHidden())
		{
			CodeLogger.log("Starting to parse " + f.getAbsolutePath(), DEPTH.PARENT);
			
			return parseFileStream(new File(pcapPath));
		}
		else
		{
			CodeLogger.log("Could Not Parse " + f.getAbsolutePath(), DEPTH.ROOT);
			
			return 0;
		}
	}
	
	private static int parseFileStream(File f)
	{
		int packetCount = 0;
		int actualPacketCount = 0;
		
		try
		{
			CodeClock clock = new CodeClock();
			SeekableByteChannel sbc = Files.newByteChannel(Paths.get(f.getAbsolutePath()));
			InputStream in = Channels.newInputStream(sbc);
			byte[] globalHeader = new byte[24];
			
			in.read(globalHeader);
			
			byte[] dlt = Arrays.copyOfRange(globalHeader, 20, 24);
			int dltNum = Types.hexToNum(Utils.flipBytes(Types.bytesToHex(dlt)));
			
			clock.restart();
			
			while (in.available() > 0)
			{	
				byte[] timestampSecs = new byte[4];
				byte[] timestampMills = new byte[4];
				byte[] octetsInFile = new byte[4];
				byte[] packetSizeBuf = new byte[4];
				
				in.read(timestampSecs);
				in.read(timestampMills);
				in.read(octetsInFile);
				in.read(packetSizeBuf);
								
				long date = ((long) Types.byteArrayToInteger(timestampSecs, ByteOrder.LITTLE_ENDIAN) * 1000) + Types.byteArrayToInteger(timestampMills, ByteOrder.LITTLE_ENDIAN);
				int packetSize = Types.byteArrayToInteger(packetSizeBuf, ByteOrder.LITTLE_ENDIAN);
				byte[] packetData = new byte[packetSize];

				if (packetCount >= Settings.SKIP_AMOUNT)
				{
					actualPacketCount++;
					in.read(packetData);
					new Frame(dltNum, Types.bytesToBin(packetData), packetCount, date);
				}
				else
				{
					in.skip(packetSize);
				}
															
				packetCount++; 
				
				// Stop parsing if > the load limit - be aware of Integer overflow
				if (packetCount >= (Settings.LOAD_LIMIT + Settings.SKIP_AMOUNT))
					break;
				
				if ((packetCount % CSVLoader.PRINT_INCREMENT) == 0 && Settings.PRINT_PCAP_LOADING)
				{
					CodeLogger.log("Loaded " + Utils.DECIMAL_FORMAT.format(packetCount) + " packets from " + f.getName() + " (" + clock.update() + "s / " + clock.memory() + ")", DEPTH.CHILD);
				}			
			}
			
			CodeLogger.log("Finished Loading " + Utils.DECIMAL_FORMAT.format(packetCount) + " packets from " + f.getName() + " (" + clock.update() + "s / " + clock.memory() + ")", DEPTH.CHILD);
			clock.end();
			in.close();
			
			Frame.printCaptureSummary();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return actualPacketCount;
	}
}
