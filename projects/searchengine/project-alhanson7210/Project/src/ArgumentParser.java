import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiFunction;

import java.util.HashMap;

/**
 * Parses and stores command-line arguments into simple key = value pairs.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex Hanson
 * @version Spring 2020
 */
public class ArgumentParser {
	  
	/**
	 * Given a flag, a specified string output is returned 
	 * assuming the argument parser actually contains the given flag
	 * 
	 */
	public final BiFunction<String, String, String> defaultGenertor = (flag, defaultValue) -> {
		//initialization
        String output = null;
        boolean containsFlag = this.hasFlag(flag);
        //set to default and change if necessary
        if (containsFlag) {
        	output = defaultValue;
        	String value = this.getString(flag);
        	if (value != null) {
        		output = value;
        	}
        }
        //return default output
        return output;
    };
    
	/** Stores command-line arguments in key = value pairs */
	private final Map<String, String> arguments;

	/** Initializes this argument map */
	public ArgumentParser() {
		arguments = new HashMap<String, String>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into flag/value
	 * pairs where possible. Some flags may not have associated values. If a flag is
	 * repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public ArgumentParser(String[] args) {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		this();
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may not
	 * have associated values. If a flag is repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 * @throws NullPointerException args was null
	 */
	public void parse(String[] args) {
		//checking the command line arguments to parse
		if (args != null) {
			//default value
			String previous = null;
			//check for flags and values
			for (String arg : args) {
				if (ArgumentParser.isFlag(arg)) {
					arguments.put(arg, null);
				} 

				if (ArgumentParser.isFlag(previous) && ArgumentParser.isValue(arg)) {
					arguments.put(previous, arg);
				}
				
				previous = arg;
			}
		}
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-digit character.
	 *
	 * @param arg the argument to test if its a flag
	 * 
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 * @see String#charAt(int)
	 * @see Character#isDigit(char)
	 */
	public static boolean isFlag(String arg) {
		return arg != null && !arg.isBlank() && arg.length() > 2 && arg.startsWith("-") && !Character.isDigit(arg.charAt(1));
	}

	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * 
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 */
	public static final boolean isValue(String arg) {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return arguments.size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag find
	 * 
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return arguments.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * 
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		return (arguments.getOrDefault(flag, null) != null) ? true : false;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * 
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping
	 */
	public String getString(String flag) {
		return arguments.getOrDefault(flag, null);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or the default value if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * 
	 * @return the value to which the specified flag is mapped, or the default value
	 *         if there is no mapping
	 */
	public final String getString(String flag, String defaultValue) {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		String value = getString(flag);
		return value == null ? defaultValue : value;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path}, or
	 * {@code null} if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * 
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 *
	 * @see Path#of(String, String...)
	 */
	public Path getPath(String flag) {
		try {
			return Path.of(getString(flag));
		} catch (InvalidPathException | NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid mapping
	 * 
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *         default value if there is no valid mapping
	 */
	public final Path getPath(String flag, Path defaultValue) {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		Path value = getPath(flag);
		return value == null ? defaultValue : value;
	}

	@Override
	public String toString() {
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		return arguments.toString();
	}
}