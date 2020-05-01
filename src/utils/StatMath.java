package utils;

import java.util.ArrayList;

public class StatMath 
{

	public StatMath() 
	{

	}
	
	/* Scale the data down so the different features are aligned,
	 * this doesn't change the relative distance between the points */
	public static double[][] normalize(double[][] arr)
	{
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		// Loop rows
		for (int i = 0; i < arr.length; i++) 
		{
			// Loop cols
			for (int j = 0; j < arr[i].length; j++) 
			{
				max = Math.max(arr[i][j], max);
				min = Math.min(arr[i][j], min);
			}
		}

		for (int i = 0; i < arr.length; i++) 
		{   
			for (int j = 0; j < arr[i].length; j++) 
			{
				arr[i][j] = (arr[i][j] - min)/(max - min);
			}
		}

		return arr;
	 }
	
	public static double[][] centerOnCols(double[][] input)
	{
		int rows = input.length;
		int cols = input[0].length;
		
		double[][] output = new double[rows][cols];
		
		for (int i = 0; i < cols; i++)
		{
			double[] column = new double[rows];
			
			for (int j = 0; j < rows; j++)
			{
				column[j] = input[j][i];
			}
			
			double mean = mean(column);
			double std = standardDeviation(column, false);
			
			for (int j = 0; j < rows; j++)
			{
				output[j][i] = (input[j][i] - mean) / std;
			}
		}
		
		return output;
	}
	
	public static double[][] centerOnRows(double[][] input)
	{
		int rows = input.length;
		int cols = input[0].length;
		
		double[][] output = new double[rows][cols];
		
		for (int i = 0; i < rows; i++)
		{
			double[] column = new double[rows];
			
			for (int j = 0; j < cols; j++)
			{
				column[j] = input[i][j];
			}
			
			double mean = mean(column);
			double std = standardDeviation(column, false);
			
			for (int j = 0; j < cols; j++)
			{
				output[i][j] = (input[i][j] - mean) / std;
			}
		}
		
		return output;
	}
	 
	/* Sums an array to a double value */
	 public static double sum(double[] arr) 
	 {
		 double sum = 0.0;
		 
		 for (int i = 0; i < arr.length; i++)
			 sum += arr[i];
		 
		 return sum;
	 }

	 public static int sum(ArrayList<Integer> arr) 
	 {
		 int sum = 0;
		 
		 for (int i = 0; i < arr.size(); i++)
			 sum += arr.get(i);
		 
		 return sum;
	 }
	 
	 /* For each position in the input array, add it
	  * to all previous values and place it in the 
	  * output array */
	 public static double[] cumulativeSum(double[] in) 
	 {
		 double[] out = new double[in.length];
		 int total = 0;
		    
		 for (int i = 0; i < in.length; i++) 
		 {
			 total += in[i];
			 out[i] = total;
		 }
		 
		 return out;
	 }
	 
	 /* Reverse the position of an array, no  other sorting
	  * is performed */
	 public static double[] reverse(double[] arr)
	 {
		 double[] reversed = new double[arr.length];
		 
		 for (int i = 0; i < arr.length; i++)
		 {
			 reversed[arr.length - (i + 1)] = arr[i];
		 }
		 
		 return reversed;
	 }

	 /* 99% of the data should be within 3 standard deviations
	  * 95% of the data should be within 2 standard deviations
	  * 66% of the data should be within 1 standard deviation
	  * */
	 public static double standardDeviation(double[] data, final boolean isSampleData)
	 { 
		 return Math.sqrt(variance(data, isSampleData));
	 }
	 
	 /* Variance : It is a measure of the variability or it simply measures
	  * how spread the data set is. Mathematically, it is the average squared 
	  * deviation from the mean score. We use the following formula to 
	  * compute variance var(x). */
	 public static double variance(double[] data, final boolean isSampleData)
	 {
		 double variance = 0.0;
		 int len = data.length;
		 double mean = mean(data);
		 double[] sqrtMeans = new double[len];
		 
		 for (int i = 0; i < len; i++)
		 {
			 double meanSq = ((data[i] - mean) * (data[i] - mean));
			 
			 if (isSampleData == true)
				 meanSq = (meanSq / (len + 1));
			 else
				 meanSq = (meanSq / len);
			 
			 sqrtMeans[i] = meanSq;
		 }
		 
		 variance = StatMath.sum(sqrtMeans);
		 
		 return variance;
	 }
	 
	 /* Covariance a measure of the extent to which corresponding elements from two 
	  * sets of ordered data move in the same direction.
	  * 
	  * The SIGN of the result is important:
	  * IF positive - both dimensions increase together
	  * IF negative - when one dimension increases, the other decreases
	  * IF zero - the dimensions are independent of each other
	  * 
	  *  Since covariance can only be calculated between two features, to
	  *  apply this to an n-dimensional dataset, you can create a covariance
	  *  matrix to calculate all covariances along all dimensions 
	  *  
	  *  http://www.cs.otago.ac.nz/cosc453/student_tutorials/principal_components.pdf*/
	 public static double covariance(double[] data1, double[] data2)
	 {
		 double covariance = 0.0;
		 int len = data1.length;
		 
		 
		 double meanOfData1 = StatMath.mean(data1);
		 double meanOfData2 = StatMath.mean(data2);
		 
		 for (int i = 0; i < len; i++)
		 {
			 covariance += (data1[i] - meanOfData1) * (data2[i] - meanOfData2);
		 }
		 
		 covariance = (1.0 / len) * covariance;
		 
		 return covariance;
	 }
	 
	 public static double mean(double[] data)
	 {
		 int len = data.length;
		 double total = 0;
		 
		 for (int i = 0; i < len; i++)
		 {
			 total += data[i];
		 }
		 
		 return (total / (double) len);
	 }
	 
	 public static double max(double[] data)
	 {
		 int len = data.length - 1;
		 double max = Double.MIN_VALUE;
		 
		 for (int i = 0; i < len; i++)
		 {
			 if (data[i] > max)
				 max = data[i];
		 }
		 
		 return max;
	 }
	 
	 public static double min(double[] data)
	 {
		 int len = data.length;
		 double min = Double.MAX_VALUE;
		 
		 for (int i = 0; i < len - 1; i++)
		 {
			 if (data[i] < min)
				 min = data[i];
		 }
		 
		 return min;
	 }
	 
	 public static int minIndex(double[] data)
	 {
		 int len = data.length;
		 double min = Double.MAX_VALUE;
		 int index = -1;
		 
		 for (int i = 0; i < len; i++)
		 {
			 if (data[i] < min)
			 {
				 min = data[i];
				 index = i;
			 }
		 }
		 
		 return index;
	 }
	 
	 public static int maxIndex(double[] data)
	 {
		 int len = data.length;
		 double max = Double.MIN_VALUE;
		 int index = -1;
		 
		 for (int i = 0; i < len; i++)
		 {
			 if (data[i] > max)
			 {
				 max = data[i];
				 index = i;
			 }
		 }
		 
		 return index;
	 }
}
