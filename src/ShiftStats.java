
/**
 * Stores the statistics for the given shift.  This includes the number of times
 * it was typed, mean, variance, and standard deviation.
 * 
 * @author Does Not Compute
 */
public class ShiftStats {

	private int count;
	
	private long oldMean, newMean;
	private long oldStd, newStd;
	
	/**
	 * Constructor for shift stats
	 */
	public ShiftStats() {
		count = 0;
	}

	/**
	 * Constructor for shift stats
	 * 
	 * @param dur	duration for a shift
	 */
	public ShiftStats(long dur) {
		count = 1;
		oldMean = newMean = dur;
		oldStd = 0;
	}
	
	/**
	 * Update the shift stats (mean, var, and std dev)
	 * 
	 * @param dur	duration the char was held
	 */
	public void update(long dur) {
		count ++;
		
		if (count == 1) {
			oldMean = newMean = dur;
			oldStd = 0;
		} else {
			newMean = oldMean + (dur - oldMean)/count;  
            newStd = oldStd + (dur - oldMean)*(dur - newMean);  
  
            // set up for next iteration  
            oldMean = newMean;   
            oldStd = newStd;  
		}
	}
	
	/**
	 * Get the number of times this shift was pressed
	 * 
	 * @return	the count
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Get the mean duration
	 * 
	 * @return	the mean
	 */
	public long getMean() {
		if (count > 0) {
			return newMean;
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the variance of the duration
	 * 
	 * @return	the variance
	 */
	public long getVariance() {
		if (count > 1) {
			return newStd/(count-1);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the standard deviation of the duration
	 * 
	 * @return	the standard deviation
	 */
	public long getStdDev() {
		return (long) Math.sqrt(getVariance());
	}
}
