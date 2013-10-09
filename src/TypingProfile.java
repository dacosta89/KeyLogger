import java.util.HashMap;
import java.util.Map;

/**
 * A typing profile captures the "fingerprint" of the user's typing and
 * can be used to identify a user at a later date.
 * 
 * @author Does Not Compute
 */
public class TypingProfile {

	private String name;

	// Characters that we are recording duration for
	private static char[] singleCharList = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z'};

	private static String[] pairCharList = {"th", "he", "re", "an", "er", "in", "it", "to", "of" }; // insert common 2 char combos here
	private static String[] commonWordList = {"the", "and", "quick", "brown", "fox", "box", "with", "jugs"};

	private HashMap<Character, CharStats> singleCharStatsMap;
	private HashMap<String, StringStats> pairCharDurationMap;
	private HashMap<String, StringStats> commonWordDurationMap;

	private ShiftStats leftShiftStats, rightShiftStats;

	private int numCorrectShifts = 0;

	/**
	 * Constructor creates a profile with the given name.
	 * 
	 * @param name
	 */
	public TypingProfile(String name) {
		this.name = name;

		singleCharStatsMap = new HashMap<Character, CharStats>(singleCharList.length);
		for (char c : singleCharList) {
			singleCharStatsMap.put(c, new CharStats(c));
		}
		pairCharDurationMap = new HashMap<String, StringStats>(pairCharList.length);
		for (String p : pairCharList) {
			pairCharDurationMap.put(p, new StringStats(p));
		}

		commonWordDurationMap = new HashMap<String, StringStats>(commonWordList.length);
		for (String w : commonWordList) {
			commonWordDurationMap.put(w, new StringStats(w));
		}

		leftShiftStats = new ShiftStats();
		rightShiftStats = new ShiftStats();
	}

	/**
	 * Updates the average that a given character has been pressed.
	 * 
	 * @param c		the character
	 * @param dur	the duration
	 */
	public void updateAverageSingleDuration(char c, long dur) {
		CharStats oldStats = singleCharStatsMap.get(c);
		if (oldStats != null) {
			oldStats.update(dur);
			singleCharStatsMap.put(c, oldStats);
		}
	}

	/**
	 * Updates the average time it took to type a pair of characters.
	 * 
	 * @param ss	string stats
	 */
	public void updateAveragePairDuration(StringStats ss) {
		StringStats oldStats = pairCharDurationMap.get(ss.getString());
		if (oldStats != null) {
			oldStats.update(ss);
			pairCharDurationMap.put(ss.getString(), oldStats);
		}
	}

	/**
	 * Updates the average time it took to type a common word.
	 * 
	 * @param ss	string stats
	 */
	public void updateAverageCommonWordDuration(StringStats ss) {
		StringStats oldStats = commonWordDurationMap.get(ss.getString());
		if (oldStats != null) {
			oldStats.update(ss);
			commonWordDurationMap.put(ss.getString(), oldStats);
		}
	}

	/**
	 * Updates the left shift count, average duration, and keeps track of whether
	 * the user is using the correct shift for each character.
	 * 
	 * @param c		character being shifted
	 * @param dur	duration shift was held
	 */
	public void updateLeftShift(char c, long dur) {
		leftShiftStats.update(dur);
		char upperC = Character.toUpperCase(c);
		if (upperC == 'Y' || upperC == 'U' || upperC == 'I' || upperC == 'O' || upperC == 'P' ||
				upperC == 'H' || upperC == 'J' || upperC == 'K' || upperC == 'L' || 
				upperC == 'N' || upperC == 'M'){
			numCorrectShifts++;
		}
	}

	/**
	 * Updates the right shift count, average duration, and keeps track of whether
	 * the user is using the correct shift for each character.
	 * 
	 * @param c		character being shifted
	 * @param dur	duration shift was held
	 */
	public void updateRightShift(char c, long dur) {
		rightShiftStats.update(dur);
		char upperC = Character.toUpperCase(c);
		if (upperC == 'Q' || upperC == 'W' || upperC == 'E' || upperC == 'R' || upperC == 'T' ||
				upperC == 'A' || upperC == 'S' || upperC == 'D' || upperC == 'F' || upperC == 'G' || 
				upperC == 'Z' || upperC == 'X' || upperC == 'C' || upperC == 'V' || upperC == 'B'){
			numCorrectShifts++;
		}
	}

	/**
	 * String representation of the profile;
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(name);
		for (char c : singleCharList) {
			CharStats cs = singleCharStatsMap.get(c);
			s.append("\nChar: " + c + " Avg: " + cs.getMean() + 
					" Count: " + cs.getCount());
		}
		for (String p : pairCharList) {
			StringStats ss = pairCharDurationMap.get(p);
			s.append("\nPair: " + p + " Avg: " + ss.getStringMean() + 
					" Count: " + ss.getCount());
		}
		for (String w : commonWordList) {
			StringStats ss = commonWordDurationMap.get(w);
			s.append("\nWord: " + w + " Avg: " + ss.getStringMean() + 
					" Count: " + ss.getCount());
		}
		if (numCorrectShifts > 0)
			s.append("\nuses shift correctly: " + ((float)numCorrectShifts)/(leftShiftStats.getCount()+rightShiftStats.getCount()));
		return s.toString();
	}

	/**
	 * Get the name of the profile.
	 * 
	 * @return 	name of the profile
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get an array of characters being recorded.
	 * 
	 * @return	array of characters being recorded
	 */
	public static char[] getSingleCharList() {
		return singleCharList;
	}

	/**
	 * Get an array of character pairs being recorded.
	 * 
	 * @return	array of character pairs being recorded
	 */
	public static String[] getPairCharList() {
		return pairCharList;
	}

	/**
	 * Get an array of common words being recorded.
	 * 
	 * @return	array of common words being recorded
	 */
	public static String[] getCommonWordList() {
		return commonWordList;
	}

	/**
	 * Get a map of average durations mapped to single characters.
	 * 
	 * @return	map of average durations for each character
	 */
	public Map<Character, CharStats> getSingleCharDurationMap() {
		return singleCharStatsMap;
	}

	/**
	 * Get a map of stringStatistics mapped to character pairs.
	 * 
	 * @return	map of string stats for each character pair
	 */
	public Map<String, StringStats> getPairCharDurationMap() {
		return pairCharDurationMap;
	}

	/**
	 * Get a map of string statistics mapped to common words.
	 * 
	 * @return	map of string stats for each common word
	 */
	public Map<String, StringStats> getCommonWordDurationMap() {
		return commonWordDurationMap;
	}

	/**
	 * Gets the left shift statistics
	 * 
	 * @return	left shift stats
	 */
	public ShiftStats getLeftShiftStats() {
		return leftShiftStats;
	}

	/**
	 * Gets the right shift usage statistics
	 * 
	 * @return	right shift stats
	 */
	public ShiftStats getRightShiftStats() {
		return rightShiftStats;
	}

	/**
	 * Returns a rating between 0 and 1.
	 * 
	 * @return	rating between 0 and 1 (1 is the best)
	 */
	public float isUsesShiftCorrectly() {
		return ((float)numCorrectShifts)/(leftShiftStats.getCount() + rightShiftStats.getCount());
	}
}
