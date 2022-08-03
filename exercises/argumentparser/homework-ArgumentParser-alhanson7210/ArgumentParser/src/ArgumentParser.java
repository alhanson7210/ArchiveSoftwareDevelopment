import java.nio.file.Path;
import java.util.Map;
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
   * Stores command-line arguments in key = value pairs.
   */
  private final Map<String, String> map;

  /**
   * Initializes this argument map. 
   */
  public ArgumentParser() {
    // TODO Properly initialize map below
    this.map = new HashMap<String, String>();
  }

  /**
   * Initializes this argument map and then parsers the arguments into flag/value pairs where
   * possible. Some flags may not have associated values. If a flag is repeated, its value is
   * overwritten.
   *
   * @param args the command line arguments to parse
   */
  public ArgumentParser(String[] args) {
    // DO NOT MODIFY; THIS METHOD IS PROVIDED
    this();
    parse(args);
  }

  /**
   * Parses the arguments into flag/value pairs where possible. Some flags may not have associated
   * values. If a flag is repeated, its value is overwritten.
   *
   * @param args the command line arguments to parse
   */
  public void parse(String[] args) {
    // TODO Fill in parse methods
	/*
	 * Cases:
	 * flag w/no previous no previous_flag - beginning & assign flag to map if absent:
	 * -> set previous flag to true and previous to the current argument
	 * 
	 * value w/no previous no previous_flag- beginning & ignore value
	 * -> set previous flag to false and previous to current argument
	 * 
	 * flag w/previous previous_flag - any position & ignore assignment of a value & assign flag to map if absent
	 * -> set previous flag to true and previous to the current argument
	 * 
	 * value w/previous previous_flag - any position & assign value to map if absent or replace existing value
	 * -> set previous flag to false and previous to current argument
	 * 
	 * flag w/previous - no previous_flag - any & assign flag to map if absent:
	 * -> set previous flag to true and previous to the current argument
	 * 
	 * value w/previous - no previous_flag - any & ignore the value and iterate
	 * -> set previous flag to false and previous to the current argument
	 */
	if (args == null) throw new NullPointerException("Args is null");
	
	String previous = null;
	boolean previous_flag = false;
	
	for (String arg : args) {
		Boolean flag = true;
		
		if (arg == null) {
			previous = null;
			previous_flag = false;
			continue;
		}
		
		if (arg.isBlank()) {
			//flag = false;
			previous = arg;
			previous_flag = false;
			continue;
		}
		
		if (arg.length() >= 2) {
			if (!arg.startsWith("-")) flag = false;
			if (Character.isDigit(arg.charAt(1))) flag = false;
		} 
		else flag = false;
		//flag happens to be first element
		//or the previous element was a value and the current is a flag
		if ( (flag && (previous == null)) /*|| (!previous_flag && flag)*/) {
			this.map.putIfAbsent(arg, null);
			previous_flag = true;
			previous = arg;
			continue;
		}
		
		//value and null previous
		if ( (!flag && (previous == null))) {
			previous_flag = false;
			previous = arg;
			continue;
		}
		
		//flag previous and previous flag
		if(flag && (previous != null) && previous_flag) {
			if (this.map.containsKey(arg)) {
				this.map.replace(arg, null);
			} else {
				this.map.putIfAbsent(arg, null);
			}
			previous_flag = true;
			previous = arg;
			continue;
			
		}
		
		//previous flag belongs to this current value
		if(!flag && (previous != null) && previous_flag) {
			//may cause a NullPointerException
			if (this.map.containsKey(previous)) {
				//may cause a NullPointerException
				this.map.replace(previous, arg);
			} else {
				this.map.putIfAbsent(previous, arg);
			}
			
			previous_flag = false;
			previous = arg;
			continue;
		}
		
		//previous is a value and the current is a flag
		if ( flag && (previous != null) && !previous_flag) {
			if (this.map.containsKey(arg)) {
				this.map.replace(arg, null);
			} else {
				this.map.putIfAbsent(arg, null);
			}
			previous_flag = true;
			previous = arg;
			continue;
		}
		//vale value
		if (!flag && (previous != null) && !previous_flag) {
			previous_flag = false;
			previous = arg;
			continue;
		}
		
		previous = arg;
	}
    //throw new UnsupportedOperationException("Not yet implemented.");
  }

  /**
   * Determines whether the argument is a flag. Flags start with a dash "-" character, followed by
   * at least one other non-digit character.
   *
   * @param arg the argument to test if its a flag
   * @return {@code true} if the argument is a flag
   *
   * @see String#startsWith(String)
   * @see String#length()
   * @see String#charAt(int)
   * @see Character#isDigit(char)
   */
  public static boolean isFlag(String arg) {
    // TODO Fill in isFlag method without looping
	if (arg == null) return false;
	if (arg.isBlank()) return false;
	
	if (arg.length() < 2) return false;
	if (!arg.startsWith("-")) return false;
	if (Character.isDigit(arg.charAt(1))) return false;
	
	return true;
    //throw new UnsupportedOperationException("Not yet implemented.");
  }

  /**
   * Determines whether the argument is a value. Anything that is not a flag is considered a value.
   *
   * @param arg the argument to test if its a value
   * @return {@code true} if the argument is a value
   *
   * @see String#startsWith(String)
   * @see String#length()
   */
  public static boolean isValue(String arg) {
    // DO NOT MODIFY; THIS METHOD IS PROVIDED
    return !isFlag(arg);
  }

  /**
   * Returns the number of unique flags.
   *
   * @return number of unique flags
   */
  public int numFlags() {
    // TODO Fill in numFlags method without looping
	return this.map.size();
    //throw new UnsupportedOperationException("Not yet implemented.");
  }

  /**
   * Determines whether the specified flag exists.
   *
   * @param flag the flag find
   * @return {@code true} if the flag exists
   */
  public boolean hasFlag(String flag) {
    // TODO Fill in hasFlag method without looping
	return this.map.containsKey(flag);
    //throw new UnsupportedOperationException("Not yet implemented.");
  }

  /**
   * Determines whether the specified flag is mapped to a non-null value.
   *
   * @param flag the flag to find
   * @return {@code true} if the flag is mapped to a non-null value
   */
  public boolean hasValue(String flag) {
    // TODO Fill in hasValue method without looping
	return (this.map.getOrDefault(flag, null) != null)? true: false; //this.map.containsValue(flag);
    //throw new UnsupportedOperationException("Not yet implemented.");
  }

  /**
   * Returns the value to which the specified flag is mapped as a {@link String}, or null if there
   * is no mapping.
   *
   * @param flag the flag whose associated value is to be returned
   * @return the value to which the specified flag is mapped, or {@code null} if there is no mapping
   */
  public String getString(String flag) {
    // TODO Fill in get method without looping 
	try {
		return this.map.get(flag);
	} catch (NullPointerException e) {
		return null;
	}
    //throw new UnsupportedOperationException("Not yet implemented.");
  }

  /**
   * Returns the value to which the specified flag is mapped as a {@link String}, or the default
   * value if there is no mapping.
   *
   * @param flag the flag whose associated value is to be returned
   * @param defaultValue the default value to return if there is no mapping
   * @return the value to which the specified flag is mapped, or the default value if there is no
   *         mapping
   */
  public String getString(String flag, String defaultValue) {
    // DO NOT MODIFY; THIS METHOD IS PROVIDED
    String value = getString(flag);
    return value == null ? defaultValue : value;
  }

  /**
   * Returns the value to which the specified flag is mapped as a {@link Path}, or {@code null} if
   * unable to retrieve this mapping (including being unable to convert the value to a {@link Path}
   * or no value exists).
   *
   * This method should not throw any exceptions!
   *
   * @param flag the flag whose associated value is to be returned
   * @return the value to which the specified flag is mapped, or {@code null} if unable to retrieve
   *         this mapping
   *
   * @see Path#of(String, String...)
   */
  public Path getPath(String flag) {
    // TODO Fill in getPath method without looping
	  try {
		  return Path.of(getString(flag));
	  } catch (NullPointerException e) {
		  return null;
	  }
    //throw new UnsupportedOperationException("Not yet implemented.");
  }

  /**
   * Returns the value the specified flag is mapped as a {@link Path}, or the default value if
   * unable to retrieve this mapping (including being unable to convert the value to a {@link Path}
   * or if no value exists).
   *
   * This method should not throw any exceptions!
   *
   * @param flag the flag whose associated value will be returned
   * @param defaultValue the default value to return if there is no valid mapping
   * @return the value the specified flag is mapped as a {@link Path}, or the default value if there
   *         is no valid mapping
   */
  public Path getPath(String flag, Path defaultValue) {
    // DO NOT MODIFY; THIS METHOD IS PROVIDED
    Path value = getPath(flag);
    return value == null ? defaultValue : value;
  }

  @Override
  public String toString() {
    // DO NOT MODIFY; THIS METHOD IS PROVIDED
    return this.map.toString();
  }

  /**
   * A simple main method that parses the command-line arguments provided and prints the result to
   * the console.
   *
   * @param args the command-line arguments to parse
   */
  public static void main(String[] args) {
    // Modify as needed to debug code
	 String[] a = {"", "-a", "42", "-b", "bat", "cat", "-d", "-e", "elk", "-1", "-e", "-f"};
    var map = new ArgumentParser(a);
    String[] b = {"-p", "."};
    var m = new ArgumentParser(b);
    System.out.println(map);
    System.out.println(m);
  }
}
