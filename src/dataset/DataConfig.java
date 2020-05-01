package dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import universal.CSVLoader;
import universal.CSVWriter;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import utils.OS;

public class DataConfig 
{
	private final String DATA_FOLDER = "/Users/Phil/Dropbox/Datasets/";
	private LinkedHashMap<Integer, String> files = new LinkedHashMap<Integer, String>();
	private int count = 0;
	private int selectedFile = -1;
	
	public DataConfig(int id)
	{
		loadLog();
		scanFolder(new File(DATA_FOLDER), false);
		writeLog();
		
		selectedFile = id;
		
		CodeLogger.log("Set File To: " + files.get(id), DEPTH.PARENT);
	}
	
	public DataConfig()
	{
		loadLog();
		scanFolder(new File(DATA_FOLDER), true);
		writeLog();
	}
	
	private int keyOf(String path)
	{
		int key = -1;
		
		for (int k : files.keySet())
		{
			if (files.get(k).equals(path))
				key = k;
		}
		
		return key;
	}
	
	private void scanFolder(File folder, boolean print)
	{		
		for (File f : folder.listFiles())
		{
			if (!f.isHidden() && f.isFile())
			{				
				if (files.containsValue(f.getAbsolutePath()))
					count = keyOf(f.getAbsolutePath());
				else
					files.put(++count, f.getAbsolutePath());
				
				if (print)
					CodeLogger.log(count + ": " + files.get(count), DEPTH.CHILD);
			}
			else if (!f.isHidden() && f.isDirectory())
			{	
				scanFolder(f, print);
			}
		}
	}
	
	private void loadLog()
	{
		CSVLoader loader = new CSVLoader(OS.workingDirectory + "/src/config/DataConfig.txt");
		
		for (int i = 0; i < loader.rows; i++)
		{
			files.put(Integer.valueOf(loader.data.get(i).get(0)), loader.data.get(i).get(1));
			count++;
		}
	}
	
	private void writeLog()
	{
		CSVWriter csvWriter = new CSVWriter(OS.workingDirectory + "/src/config/DataConfig.txt");
		csvWriter.clearFile();
		
		for (int k : files.keySet())
		{
			ArrayList<String> row = new ArrayList<String>();
			row.add(String.valueOf(k));
			row.add(files.get(k));
			
			csvWriter.addRow(row);
		}
		
		csvWriter.flush();
	}
	
	public File file()
	{
		return new File(files.get(selectedFile));
	}

}
