package simpledb;

import simpledb.Predicate.Op;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

	private int buckets;
	private int min;
	private int max;
	private int width;
	private int value;
	
	private int[][] bucketInfo;

    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
    	this.buckets = buckets;
    	this.min = min;
    	this.max = max;
    	this.width = (max - min) / buckets + 1;
    	this.bucketInfo = new int[buckets][3];
    	
    	for(int i = 0; i < buckets; i++){
    		bucketInfo[i][1] = min + i * width;
    		bucketInfo[i][2] = bucketInfo[i][1] + width - 1;
    	}
    	bucketInfo[buckets-1][2] = max;
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	int bucket = (v - min) / width;
    	
    	if(v <= max && v >= min){
    		bucketInfo[bucket][0]++;
    		this.value++;
    	}
    	else
    		return;

    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {
    	
    	double selectivity = 0.0;
    	
    	if (v < min) {
			switch (op) {
			case GREATER_THAN:
			case GREATER_THAN_OR_EQ:
			case NOT_EQUALS:
				return 1.0;

			default:
				return 0.0;
			}
		}
		if (v > max) {
			switch (op) {
			case LESS_THAN:
			case LESS_THAN_OR_EQ:
			case NOT_EQUALS:
				return 1.0;
			default:
				return 0.0;
			}
		}

    	int bucket = (v - min) / width;
    	int bucketWidth = bucketInfo[bucket][2] - bucketInfo[bucket][1] + 1;
    	
    	switch (op) {
		case LESS_THAN_OR_EQ:
			selectivity = (double) this.bucketInfo[bucket][0] / bucketWidth;
		case LESS_THAN:
			selectivity += (double) (v - this.bucketInfo[bucket][2]) / bucketWidth * this.bucketInfo[bucket][0];
			for (int i = 0; i < bucket; i++) 
				selectivity += this.bucketInfo[i][0];
			break;
		case GREATER_THAN_OR_EQ:
			selectivity = (double) this.bucketInfo[bucket][0] / bucketWidth;
		case GREATER_THAN:
			selectivity += (double) (this.bucketInfo[bucket][2] - v) / bucketWidth * this.bucketInfo[bucket][0];
			for (int i = bucket + 1; i < this.buckets; i++)
				selectivity += this.bucketInfo[i][0];
			break;
		case EQUALS:
			selectivity = (double) this.bucketInfo[bucket][0] / bucketWidth;
			break;
		case NOT_EQUALS:
			return 1.0 - (double) this.bucketInfo[bucket][0] / bucketWidth / this.value;
		default:
			break;
		}
    	
         return selectivity / this.value;
        
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        
        return 1.0;
       
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
       
         return null;

    }
}
