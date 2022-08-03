import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utility class for parsing text in a consistent manner.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex L Hanson
 * @version Spring 2020
 */
public class TextParser {

	/** Regular expression that matches any whitespace. **/
	public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");

	/** Regular expression that matches non-alphabetic characters. **/
	public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");

	/** Regular expression that matches any grouping of digits */
	public static final Pattern DIGITS_REGEX = Pattern.compile("\\d+");

	/**
	 * Dictates where is a number is a digit of some variable length and is greater than zero
	 * @param number is what needs to be verified
	 * @return valid number to use
	 */
	public static boolean validNumber(String number) {
		if (digitChecker(number)) {
			int count = Integer.valueOf(number);
			if (count > 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines where a string can be converted to a number
	 * @param number thread number
	 * @return count validation
	 */
	public static final boolean digitChecker(String number) {
		return number == null? false : TextParser.DIGITS_REGEX.matcher(number).matches();
	}

	/**
	 * Cleans the text by removing any non-alphabetic characters (e.g. non-letters like digits,
	 * punctuation, symbols, and diacritical marks like the umlaut) and converting the remaining
	 * characters to lowercase.
	 *
	 * @param text the text to clean
	 * 
	 * @return cleaned text
	 * 
	 * @throws NullPointerException text will cause this if it is null
	 */
	public static String clean(String text) throws NullPointerException {
		String cleaned = Normalizer.normalize(text, Normalizer.Form.NFD);
		cleaned = CLEAN_REGEX.matcher(cleaned).replaceAll("");
		return cleaned.toLowerCase();
	}

	/**
	 * Splits the supplied text by whitespaces.
	 *
	 * @param text the text to split
	 * 
	 * @return an array of {@link String} objects
	 * 
	 * @throws NullPointerException text will cause this if it is null
	 */
	public static String[] split(String text) throws NullPointerException {
		return text.isBlank() ? new String[0] : SPLIT_REGEX.split(text.strip());
	}

	/**
	 * Cleans the text and then splits it by whitespace.
	 *
	 * @param text the text to clean and split
	 * 
	 * @return an array of {@link String} objects
	 * 
	 * @throws NullPointerException text will cause this if it is null
	 *
	 * @see #clean(String)
	 * @see #parse(String)
	 */
	public static String[] parse(String text) throws NullPointerException {
		return split(clean(text));
	}
}