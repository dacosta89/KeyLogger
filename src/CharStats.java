
/**
 * Stores the statistics for the given character.  This includes the character, 
 * number of times it was typed, mean, variance, and standard deviation.
 * 
 * @author Does Not Compute
 */
public class CharStats {
	private char character;
	private int count;
	
	private long oldMean, newMean;
	private long oldStd, newStd;
	
	/**
	 * Constructor for the char stats
	 * 
	 * @param c		the char being recorded
	 */
	public CharStats(char c) {
		count = 0;
		character = c;
	}

	/**
	 * Constructor for the char stats
	 * 
	 * @param c		the char being recorded
	 * @param dur	the first duration 
	 */
	public CharStats(char c, long dur) {
		count = 1;
		character = c;
		oldMean = newMean = dur;
		oldStd = 0;
	}
	
	/**
	 * Update the char stats (mean, var, and std dev)
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
	 * Get the char being recorded
	 * 
	 * @return	the char
	 */
	public char getChar() {
		return character;
	}
	
	/**
	 * Get the number of times this char was pressed
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
