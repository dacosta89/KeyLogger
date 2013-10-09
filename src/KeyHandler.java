import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Handles the key presses for a given profile.
 * 
 * @author Does Not Compute
 */
public class KeyHandler implements KeyListener {

	TypingProfile profile;
	String currentWord;
	long currentWordStart;
	Vector<Long> currentWordDurations;
	
	long lastReleaseTime;
	Hashtable<Character, Long> charStart;
	long leftShiftStart, rightShiftStart;
	boolean isShiftPressed = false;
	
	/**
	 * Constructor takes in a profile that it is recording for.
	 * 
	 * @param p		the profile being recorded
	 */
	public KeyHandler(TypingProfile p) {
		profile = p;
		currentWord = "";
		currentWordDurations = new Vector<Long>();
		
		charStart = new Hashtable<Character, Long>();
		for (char c : TypingProfile.getSingleCharList()) {
			charStart.put(c, (long) 0);
		}
	}
	
	/**
	 * Process a key pressed event.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		
		long time = System.nanoTime();
		char c = e.getKeyChar();
		int keyCode = e.getKeyCode();
		int keyLocation = e.getKeyLocation();
		
		switch(c) {
			case '\n':
				System.out.println(profile);
				break;
			case ' ':
				isShiftPressed = true;
				break;
			default:
				synchronized(this) {
					if (charStart.containsKey(c) || charStart.containsKey(Character.toLowerCase(c))) {
						charStart.put(c, time);
						if (currentWord.equals("")) {
							currentWordStart = time;
						}
						currentWord += c;
					}
				}
		}
		if (keyCode == KeyEvent.VK_SHIFT) {
			if (keyLocation == KeyEvent.KEY_LOCATION_LEFT) {
				leftShiftStart = time;
			} else if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) {
				rightShiftStart = time;
			}
		} else if (keyCode == KeyEvent.VK_BACK_SPACE && currentWord.length() > 0) {
			currentWord = currentWord.substring(0, currentWord.length() -1);
		}
	}

	/**
	 * Process a key released event
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		
		long time = System.nanoTime();
		char c = e.getKeyChar();
		int keyCode = e.getKeyCode();
		int keyLocation = e.getKeyLocation();
		
		synchronized(this) {
			if (charStart.containsKey(c) || charStart.containsKey(Character.toLowerCase(c))) {
				long dur = time - charStart.get(c);
				profile.updateAverageSingleDuration(c, dur);
				currentWordDurations.add(dur);
			}
		}
		
		if (keyCode == KeyEvent.VK_SHIFT && currentWord.length() > 0) {
			if (keyLocation == KeyEvent.KEY_LOCATION_LEFT) {
				profile.updateLeftShift(currentWord.charAt(currentWord.length() - 1), 
						time - leftShiftStart);
			} else if (keyLocation == KeyEvent.KEY_LOCATION_RIGHT) {
				profile.updateRightShift(currentWord.charAt(currentWord.length() - 1), 
						time - rightShiftStart);
			}
		}
		synchronized(this) {
			if (currentWord.length() > 1 && currentWordDurations.size() > 1) {
				if (c != ' ' && c != '\n' && c != '.' && c != ',') {
					String pair = currentWord.substring(currentWord.length() - 2, currentWord.length());
					long pTime = time - lastReleaseTime + currentWordDurations.get(currentWordDurations.size()-2);
					for (String p : TypingProfile.getPairCharList()) {
						if (p.equalsIgnoreCase(pair) && c == currentWord.charAt(currentWord.length() -1)) {
							profile.updateAveragePairDuration(
									new StringStats(p, 
											currentWordDurations.toArray(new Long[currentWord.length()]), 
											pTime));
						}
					}
				} else {
					profile.updateAverageCommonWordDuration(
							new StringStats(currentWord.toLowerCase(), 
									currentWordDurations.toArray(new Long[currentWordDurations.size()]), 
									lastReleaseTime-currentWordStart));
				}
			}
			if (c == ' ' || c == '\n' || c == '.' || c == ',') {
				currentWord = "";
				currentWordDurations.clear();
				isShiftPressed = false;
			}
		}
		lastReleaseTime = time;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
}
