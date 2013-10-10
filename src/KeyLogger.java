import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;


/**
 * The Keylogger program is an implementation of authentication using
 * a typing profile or fingerprint. The user can type in the text field
 * to create a profile using the provided text as a guide. Then another
 * profile can be made and the two can be compared.
 * 
 * @author Does Not Compute
 */
public class KeyLogger extends JFrame implements ActionListener {

	private static final long serialVersionUID = 626134475266909632L;

	JLabel baseProfileLabel;
	JLabel newProfileLabel;
	JButton baseProfileButton;
	JButton newProfileButton;
	JButton compareProfileButton;
	JTextArea displayText;
	JTextArea inputText;
	KeyHandler baseProfileKeyHandler;
	KeyHandler newProfileKeyHandler;
	TypingProfile baseProfile;
	TypingProfile newProfile;
	String copyText = 	"the quick brown fox jumps over the lazy dog\n" +
						"pack my box with five dozen liquor jugs\n" +
						"The Quick Brown Fox Jumps Over The Lazy Dog\n" +
						"Pack My Box With Five Dozen Liquor Jugs\n";


	/**
	 * A simple constructor to set up the GUI.
	 */
	public KeyLogger() {

		this.setName("Rush 2112");
		setSize(800, 400);
		setLayout(new BorderLayout());

		JPanel center = new JPanel();
		center.setLayout(new GridLayout(2,1));

		displayText = new JTextArea();
		inputText = new JTextArea();
		displayText.setEditable(false);
		displayText.setText(copyText);
		displayText.setBackground(Color.lightGray);
		inputText.setEditable(false);

		displayText.setLocation(1, 1);
		displayText.setBorder(new LineBorder(new Color(0, 0, 0)));
		inputText.setLocation(2, 1);
		inputText.setBorder(new LineBorder(new Color(0, 0, 0)));

		displayText.setLineWrap(true);
		displayText.setWrapStyleWord(true);
		inputText.setLineWrap(true);
		inputText.setWrapStyleWord(true);

		center.add(displayText);
		center.add(inputText);
		this.add(center, BorderLayout.CENTER);

		Box east = Box.createVerticalBox();
		this.add (east,BorderLayout.EAST);

		// Base Profile
		east.add(Box.createVerticalStrut(20));
		baseProfileLabel = new JLabel("Base Profile: ");
		east.add(baseProfileLabel);
		east.add(Box.createVerticalStrut(40));

		baseProfileButton = new JButton("Create Base Profile");
		east.add(baseProfileButton);
		baseProfileButton.addActionListener(this);

		// New comparison profile
		east.add(Box.createVerticalStrut(40));
		newProfileLabel = new JLabel("New Profile: ");
		east.add(newProfileLabel);
		east.add(Box.createVerticalStrut(40));

		newProfileButton = new JButton("Create New Profile");
		east.add(newProfileButton);
		newProfileButton.addActionListener(this);

		east.add(Box.createVerticalStrut(60));

		compareProfileButton = new JButton("Compare Profiles");
		east.add(compareProfileButton);
		compareProfileButton.addActionListener(this);
	}

	/**
	 * Capture the button events.
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();	

		if (obj == baseProfileButton) {
			String baseProfileName = JOptionPane.showInputDialog(null, "Base Profile Name : ", "Create Base Profile", 1);
			baseProfileLabel.setText("Base Profile: " + baseProfileName);
			baseProfile = new TypingProfile(baseProfileName);
			baseProfileKeyHandler = new KeyHandler(baseProfile);
			inputText.addKeyListener(baseProfileKeyHandler);
			inputText.setEditable(true);
		}
		else if (obj == newProfileButton) {
			if (baseProfileKeyHandler != null){
				String newProfileName = JOptionPane.showInputDialog(null, "New Profile Name : ", "Create New Profile", 1);
				newProfileLabel.setText("New Profile: " + newProfileName);
				newProfile = new TypingProfile(newProfileName);
				newProfileKeyHandler = new KeyHandler(newProfile);
				inputText.addKeyListener(newProfileKeyHandler);
			}
		}
		else if (obj == compareProfileButton) {
			if (baseProfile != null && newProfile != null) {
				float r = compareProfiles(baseProfile, newProfile);
				System.out.println("Percent equal: " + r);
			}
		}
	}

	/**
	 * The main function that compares two profiles. We focus on single character
	 * durations, pair durations, and common word durations.
	 * 
	 * @param p1	base profile
	 * @param p2	another profile
	 * @return		value between 0 and 1 - 1 being a perfect match
	 */
	float compareProfiles(TypingProfile p1, TypingProfile p2) {
		
		// to perform the comparison we give everything a weight
		
		float charWeight = 0.3f;		// single chars worth 30%
		
		float pairCharWeight = 0.75f;	// in each pair the individual chars are 75%
		float pairTotalWeight = 0.25f;	// the total pair duration is worth 25%
		float pairWeight = 0.3f;		// in total char pairs worth 30%
		
		float wordCharWeight = 0.75f;	// in each word the individual chars are 75%
		float wordTotalWeight = 0.25f;	//
		float wordWeight = 0.3f;		// in total common words worth 30%
		
		float shiftCorrectWeight = 0.4f;	// shift correctness is worth 40%
		float leftShiftWeight = 0.3f;		// left shift is 30%
		float rightShiftWeight = 0.3f;		// right shift is 30%
		float shiftWeight = 0.1f;			// in total shift is worth 10%
		
		float charMatch = 0;
		float pairMatch = 0;
		float wordMatch = 0;
		float shiftMatch = 0;

		char[] charList = TypingProfile.getSingleCharList();
		Map<Character, CharStats> p1CharMap = p1.getSingleCharDurationMap();
		Map<Character, CharStats> p2CharMap = p2.getSingleCharDurationMap();

		String[] PairList = TypingProfile.getPairCharList();    //gets the original's profile Pair List
		Map<String, StringStats> p1PairMap = p1.getPairCharDurationMap();  //Hashes the pair to the average duration
		Map<String, StringStats> p2PairMap = p2.getPairCharDurationMap();  //

		String[] WordList = TypingProfile.getCommonWordList();    //gets the original's profile Word List
		Map<String, StringStats> p1WordMap = p1.getCommonWordDurationMap();  //Hashes the word to the average duration
		Map<String, StringStats> p2WordMap = p2.getCommonWordDurationMap();  //
		
		
		// first compare the individual characters
		int numCharsCompared = 0;
		for (char c : charList) {                       
			CharStats p1CharTemp = p1CharMap.get(c);	// stats for each profiles chars
			CharStats p2CharTemp = p2CharMap.get(c);
			long p1CharMean = p1CharTemp.getMean();
			long p2CharMean = p2CharTemp.getMean();
			if(p1CharTemp != null && p2CharTemp != null &&
					(p1CharMean != 0 || p2CharMean != 0)) {

				long p1CharStdDev = p1CharTemp.getStdDev();
				//long p2CharStdDev = p2CharTemp.getStdDev();
				if((p2CharMean <= p1CharMean + p1CharStdDev) && 
						(p2CharMean >= p1CharMean - p1CharStdDev)) {
					charMatch++;
				}
				numCharsCompared++;
			}
		}
		if (numCharsCompared == 0) {
			charMatch = 0;
		} else {
			charMatch = charMatch/numCharsCompared;
		}
		System.out.println("Char correctness: " + charMatch);

		
		// compare the pairs of chars
		int numPairsCompared = 0;
		for (String pair : PairList) {                       
			StringStats p1Pair = p1PairMap.get(pair);	//original stats for each pair
			StringStats p2Pair = p2PairMap.get(pair);	//current time data user is trying to authenticate
			if(p1Pair != null && p2Pair != null &&
					(p1Pair.getStringMean() != 0 || p2Pair.getStringMean() != 0)) {
				//compare the two profile times, give the first profile a range because
				//it would be impossible to type the same two letter pair with exactly
				//the same times
				int matchChars = 0;
				for(int j=0; j<p1Pair.getStringLength(); j++) {
					long p1CharMean = p1Pair.getCharMean(j);  //gets the mean times for each individual
					long p2CharMean = p2Pair.getCharMean(j);  //char in the string
					long p1CharStdDev = p1Pair.getCharStdDev(j);
					//long p2CharStdDev = p2Pair.getCharStdDev(j);
					if((p2CharMean <= p1CharMean + p1CharStdDev) && 
							(p2CharMean >= p1CharMean - p1CharStdDev)) {
						matchChars++;
					}
				}
				float matchCharsRatio = ((float)matchChars)/p1Pair.getStringLength();
				float matchCharPair = matchCharsRatio*pairCharWeight;	// total correctness of the pair
				
				long p1StringMean = p1Pair.getStringMean();		//gets the times for the total word duration
				long p2StringMean = p2Pair.getStringMean();		//and compares them. This is not as important
				long p1StringStdDev = p1Pair.getStringStdDev();
				//long p2StringStdDev = p2Pair.getStringStdDev();
				if((p2StringMean <= p1StringMean + p1StringStdDev) && 
						(p2StringMean >= p1StringMean - p1StringStdDev)) {
					matchCharPair += pairTotalWeight;
				}
				numPairsCompared++;
				pairMatch += matchCharPair;
			}
		}
		if (numPairsCompared == 0) {
			pairMatch = 0;
		} else {
			pairMatch = pairMatch/numPairsCompared;
		}
		System.out.println("Pair correctness: " + pairMatch);

		
		// compare the common words
		int numWordsCompared = 0;
		for (String word : WordList) {                       
			StringStats p1String = p1WordMap.get(word);
			StringStats p2String = p2WordMap.get(word);
			if(p1String != null && p2String != null &&
					(p1String.getStringMean() != 0 || p2String.getStringMean() != 0)) {
				//compare the two profile times, give the first profile a range because
				//it would be impossible to type the same two word pair with exactly
				//the same times
				int matchChars = 0;
				for(int j=0; j< p1String.getStringLength(); j++) {
					long p1CharMean = p1String.getCharMean(j);	//gets the times for each individual
					long p2CharMean = p2String.getCharMean(j);	//char in the string
					long p1CharStdDev = p1String.getCharStdDev(j);
					//long p2CharStdDev = p2String.getCharStdDev(j);
					if((p2CharMean <= p1CharMean + p1CharStdDev) && 
							(p2CharMean >= p1CharMean - p1CharStdDev)) {
						matchChars++;
					}
				}
				float matchCharsRatio = ((float)matchChars)/p1String.getStringLength();
				float matchWord = matchCharsRatio*wordCharWeight;	// total correctness of the word
				
				long p1StringMean = p1String.getStringMean();	//gets the times for the total word duration
				long p2StringMean = p2String.getStringMean();	//and compares them. This is not as important
				long p1StringStdDev = p1String.getStringStdDev();
				//long p2StringStdDev = p2String.getStringStdDev();
				if((p2StringMean <= p1StringMean + p1StringStdDev) && 
						(p2StringMean >= p1StringMean - p1StringStdDev)) {
					matchWord += wordTotalWeight;
				}
				numWordsCompared++;
				wordMatch += matchWord;
			}
		}
		if (numWordsCompared == 0) {
			wordMatch = 0;
		} else {
			wordMatch = wordMatch/numWordsCompared;
		}
		System.out.println("Word correctness: " + wordMatch);
		
		
		// compare the shift habits
		float p1ShiftChecker = p1.isUsesShiftCorrectly();
		float p2ShiftChecker = p2.isUsesShiftCorrectly();
		ShiftStats p1LeftShiftStats = p1.getLeftShiftStats();
		ShiftStats p1RightShiftStats = p1.getRightShiftStats();
		ShiftStats p2LeftShiftStats = p2.getLeftShiftStats();
		ShiftStats p2RightShiftStats = p2.getRightShiftStats();
		
		float shiftThreshold = 0.05f;
		if (Math.abs(p1ShiftChecker - p2ShiftChecker) < shiftThreshold) {
			shiftMatch += shiftCorrectWeight;
		}
		
		long p1LeftShiftMean = p1LeftShiftStats.getMean();
		long p1RightShiftMean = p1RightShiftStats.getMean();
		long p1LeftShiftStdDev = p1LeftShiftStats.getStdDev()/2;
		long p1RightShiftStdDev = p1RightShiftStats.getStdDev()/2;
		long p2LeftShiftMean = p2LeftShiftStats.getMean();
		long p2RightShiftMean = p2RightShiftStats.getMean();
		//long p2LeftShiftStdDev = p2LeftShiftStats.getStdDev();
		//long p2RightShiftStdDev = p2RightShiftStats.getStdDev();
		
		if((p2LeftShiftMean <= p1LeftShiftMean+p1LeftShiftStdDev) && 
				(p2LeftShiftMean >= p1LeftShiftMean-p1LeftShiftStdDev)) {
			shiftMatch += leftShiftWeight;
		}
		
		if((p2RightShiftMean <= p1RightShiftMean+p1RightShiftStdDev) && 
				(p2RightShiftMean >= p1RightShiftMean-p1RightShiftStdDev)) {
			shiftMatch += rightShiftWeight;
		}
		System.out.println("Shift correctness: " + shiftMatch);
		
		// calculate the total percentage
		float r = 	charMatch * charWeight + 
					pairMatch * pairWeight + 
					wordMatch * wordWeight + 
					shiftMatch * shiftWeight;
		
		System.out.println("Total correctness: " + r);
		return r;
	}

	/**
	 * Launch the key logger.
	 * 
	 * @param args	no args are used
	 */
	public static void main(String[] args) {
		KeyLogger kl = new KeyLogger();

		try {
			kl.setIconImage(ImageIO.read(new File("keyboard_key128.png")));
		} catch (IOException e) {}

		kl.setTitle("Keylogger Profiler");
		kl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		kl.setVisible(true);
	}
}
