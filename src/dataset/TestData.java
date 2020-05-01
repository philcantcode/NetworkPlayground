package dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestData 
{

	public static final double[][] IRIS_DATA = new double[][] 
	{ 
		{ 5.0, 3.5, 1.4, 0.2},
	    { 4.9, 3, 1.4, 0.2},
	    { 4.7, 3.2, 1.3, 0.2},
	    { 4.6, 3.0, 1.5, 0.2},
	    { 5, 3.6, 1.4, 0.2},
	    { 5.4, 3.9, 1.7, 0.4},
	    { 4.6, 3.4, 1.4, 0.3},
	    { 5, 3.4, 1.5, 0.2},
	    { 4.4, 2.9, 1.4, 0.2},
	    { 4.9, 3.0, 1.5, 0.1},
	    { 5.4, 3.7, 1.5, 0.2},
	    { 4.8, 3.4, 1.6, 0.2},
	    { 4.8, 3, 1.4, 0.1},
	    { 4.3, 3, 1.0, 0.1},
	    { 5.8, 4, 1.2, 0.2},
	    { 5.7, 4.4, 1.5, 0.4},
	    { 5.4, 3.9, 1.3, 0.4},
	    { 5.0, 3.5, 1.4, 0.3},
	    { 5.7, 3.8, 1.7, 0.3},
	    { 5.0, 3.8, 1.5, 0.3},
	    { 5.4, 3.4, 1.7, 0.2},
	    { 5.0, 3.7, 1.5, 0.4},
	    { 4.6, 3.6, 1, 0.2},
	    { 5.0, 3.3, 1.7, 0.5},
	    { 4.8, 3.4, 1.9, 0.2},
	    { 5, 3, 1.6, 0.2},
	    { 5, 3.4, 1.6, 0.4},
	    { 5.2, 3.5, 1.5, 0.2},
	    { 5.2, 3.4, 1.4, 0.2},
	    { 4.7, 3.2, 1.6, 0.2},
	    { 4.8, 3.0, 1.6, 0.2},
	    { 5.4, 3.4, 1.5, 0.4},
	    { 5.2, 4.0, 1.5, 0.1},
	    { 5.5, 4.2, 1.4, 0.2},
	    { 4.9, 3.0, 1.5, 0.1},
	    { 5, 3.2, 1.2, 0.2},
	    { 5.5, 3.5, 1.3, 0.2},
	    { 4.9, 3.0, 1.5, 0.1},
	    { 4.4, 3, 1.3, 0.2},
	    { 5.0, 3.4, 1.5, 0.2},
	    { 5, 3.5, 1.3, 0.3},
	    { 4.5, 2.3, 1.3, 0.3},
	    { 4.4, 3.2, 1.3, 0.2},
	    { 5, 3.5, 1.6, 0.6},
	    { 5.7, 2.8, 4.5, 1.3},
	    { 6.3, 3.3, 4.7, 1.6},
	    { 4.9, 2.4, 3.3, 1},
	    { 6.6, 2.9, 4.6, 1.3},
	    { 5.2, 2.7, 3.9, 1.4},
	    { 5, 2, 3.5, 1},
	    { 5.9, 3, 4.2, 1.5},
	    { 6, 2.2, 4, 1},
	    { 6.0, 2.9, 4.7, 1.4},
	    { 5.6, 2.9, 3.6, 1.3},
	    { 6.7, 3.0, 4.4, 1.4},
	    { 5.6, 3, 4.5, 1.5},
	    { 5.8, 2.7, 4.0, 1},
	    { 6.2, 2.2, 4.5, 1.5},
	    { 5.6, 2.5, 3.9, 1.1},
	    { 5.9, 3.2, 4.8, 1.8},
	    { 6.0, 2.8, 4, 1.3},
	    { 6.3, 2.5, 4.9, 1.5},
	    { 6.0, 2.8, 4.7, 1.2},
	    { 6.4, 2.9, 4.3, 1.3},
	    { 6.6, 3, 4.4, 1.4},
	    { 6.8, 2.8, 4.8, 1.4},
	    { 6.7, 3, 5, 1.7},
	    { 6, 2.9, 4.5, 1.5},
	    { 5.7, 2.6, 3.5, 1},
	    { 5.5, 2.4, 3.8, 1.1},
	    { 5.5, 2.4, 3.7, 1},
	    { 5.8, 2.7, 3.9, 1.2},
	    { 6, 2.7, 5.0, 1.6},
	    { 5.4, 3, 4.5, 1.5},
	    { 6, 3.4, 4.5, 1.6},
	    { 6.7, 3.0, 4.7, 1.5},
	    { 6.3, 2.3, 4.4, 1.3},
	    { 5.6, 3, 4.0, 1.3},
	    { 5.5, 2.5, 4, 1.3},
	    { 5.5, 2.6, 4.4, 1.2},
	    { 6.0, 3, 4.6, 1.4},
	    { 5.8, 2.6, 4, 1.2},
	    { 5, 2.3, 3.3, 1},
	    { 5.6, 2.7, 4.2, 1.3},
	    { 5.7, 3, 4.2, 1.2},
	    { 5.7, 2.9, 4.2, 1.3},
	    { 6.2, 2.9, 4.3, 1.3},
	    { 5.0, 2.5, 3, 1.1},
	    { 5.7, 2.8, 4.0, 1.3}
    };
    
    public static final double[][] RANDOM_DATA(int rows, int cols)
    {
    	double[][] grid = new double[rows][cols];
    	
    	for (int i = 0; i < rows; i++) 
    	{
    	    for (int j = 0; j < cols; j++) 
    	    {
    	        grid[i][j] = (int)(Math.random()*10);
    	    }
    	}
    	
    	return grid;
    }
    
    public static final double[][] AGRAWAL_DATA()
    {
    	int count = 0;
    	double[][] data = new double[100][];
    	
    	try 
    	{
			Scanner s = new Scanner(new File("src/config/agrawal_data.csv"));
			
			s.nextLine(); // TAGS
			
			while (s.hasNextLine())
			{
				String[] line = s.nextLine().split(",");
				data[count] = new double[] {Double.valueOf(line[0]), Double.valueOf(line[1]), Double.valueOf(line[2]), Double.valueOf(line[3])};
				
				count++;
			}
		} 
    	catch (FileNotFoundException e) 
    	{
			e.printStackTrace();
		}
    	
    	return data;
    }
    
    public static final double[][] AGRAWAL_DATA_REVERSED()
    {
    	int count = 0;
    	double[][] data = new double[100][];
    	
    	try 
    	{
			Scanner s = new Scanner(new File("src/config/agrawal_data.csv"));
			
			s.nextLine(); // TAGS
			
			while (s.hasNextLine())
			{
				String[] line = s.nextLine().split(",");
				data[count] = new double[] {Double.valueOf(line[1]), Double.valueOf(line[2]), Double.valueOf(line[3]), Double.valueOf(line[0])};
				
				count++;
			}
		} 
    	catch (FileNotFoundException e) 
    	{
			e.printStackTrace();
		}
    	
    	return data;
    }

}
