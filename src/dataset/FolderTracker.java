package dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import universal.CSVLoader;
import universal.CSVWriter;
import universal.CodeLogger;
import universal.CodeLogger.DEPTH;
import utils.OS;

public class FolderTracker 
{
	private final String DATA_FOLDER = "/Users/Phil/Google Drive/Datasets";
	private LinkedHashMap<Integer, String> files = new LinkedHashMap<Integer, String>();
	private int count = 0;
	private int selectedFile = -1;
	
	public FolderTracker(int id)
	{
		loadLog();
		scanFolder(new File(DATA_FOLDER), false);
		writeLog();
		
		selectedFile = id;
		
		CodeLogger.log("Tracking: " + files.get(id), DEPTH.PARENT);
	}
	
	public FolderTracker()
	{
		loadLog();
		scanFolder(new File(DATA_FOLDER), true);
		writeLog();
	}
	
	public String path()
	{
		return files.get(selectedFile);
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
		CSVLoader loader = new CSVLoader(OS.dataDirectory + "DataConfig.txt");
		
		for (int i = 0; i < loader.rows; i++)
		{
			files.put(Integer.valueOf(loader.data.get(i).get(0)), loader.data.get(i).get(1));
			count++;
		}
	}
	
	private void writeLog()
	{
		CSVWriter csvWriter = new CSVWriter(OS.dataDirectory + "DataConfig.txt");
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
