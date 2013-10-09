/**
 * Stores the statistics for the given string.  This includes the string, its length, 
 * the number of times it was typed, the character statistics for each char, and
 * the mean, variance, and standard deviation for the string.
 * 
 * @author Does Not Compute
 */
public class StringStats {

	private String string;
	private int length;
	private int count;
	private CharStats[] charStats;
	
	private long oldMean, newMean;
	private long oldStd, newStd;
	
	/**
	 * Creates a string statistics of the given string with a count of 0.
	 * 
	 * @param s		the string
	 */
	public StringStats(String s) {
		count = 0;
		string = s;
		length = string.length();
		charStats = new CharStats[length];
		for (int i = 0; i < length; i++) {
			charStats[i] = new CharStats(s.charAt(i));
		}
	}
	
	/**
	 * Creates a string statistics given the individual char durations and
	 * the total duration for the word.
	 * 
	 * @param s				the string
	 * @param durations		array of durations
	 * @param dur			total duration
	 */
	public StringStats(String s, Long[] durations, long dur) {
		count = 1;
		string = s;
		length = string.length();
		charStats = new CharStats[length];
		for (int i = 0; i < charStats.length && i < durations.length; i++) {
			charStats[i] = new CharStats(s.charAt(i), durations[i]);
		}
		oldMean = newMean = dur;
		oldStd = 0;
	}
	
	/**
	 * Get the average time a character in the string was held down.
	 * 
	 * @param index		the index of the character in the string
	 * @return			average duration
	 */
	public long getCharMean(int index) {
		return charStats[index].getMean();
	}
	
	/**
	 * Get the variance of a character at a given index.
	 * 
	 * @param index		index of the char
	 * @return			variance of that char
	 */
	public long getCharVariance(int index) {
		return charStats[index].getVariance();
	}
	
	/**
	 * Get the standard deviation of a char at a given index.
	 * 
	 * @param index		index of the char
	 * @return			standard deviation of that char
	 */
	public long getCharStdDev(int index) {
		return charStats[index].getStdDev();
	}
	
	/**
	 * Get the string that stats are being provided for.
	 * 
	 * @return		the string
	 */
	public String getString() {
		return string;
	}
	
	/**
	 * Get the average total duration of the word.	Note that the average total duration
	 * will not be equal to the individual characters.  This is because sometimes people
	 * will not completely finish typing a character before typing another.
	 * 
	 * @return		average total time it takes to type the string
	 */
	public long getStringMean() {
		if (count > 0) {
			return newMean;
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the variance of the string
	 * 
	 * @return	variance of the string
	 */
	public long getStringVariance() {
		if (count > 1) {
			return newStd/(count-1);
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the string standard deviation
	 * 
	 * @return	standard deviation
	 */
	public long getStringStdDev() {
		return (long) Math.sqrt(getStringVariance());
	}
	
	/**
	 * How many times the string was typed.
	 * 
	 * @return		number of times the string was typed
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Get the length of the string.
	 * 
	 * @return		length of the string
	 */
	public int getStringLength() {
		return length;
	}
	
	/**
	 * Average another string stats object with this one.
	 * 
	 * @param ss	the other string stats
	 */
	public void update(StringStats ss) {
		
		if (string.equals(ss.getString()) && ss.getCount() == 1) {
			count++;
			
			long dur = ss.getStringMean();
			
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
			
			// update each character
			for (int i = 0; i < length; i++) {
				charStats[i].update(ss.getCharMean(i));
			}
		}
	}
}
