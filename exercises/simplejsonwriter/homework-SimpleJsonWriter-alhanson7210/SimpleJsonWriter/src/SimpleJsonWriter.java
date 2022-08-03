import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines are used to
 * separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class concurrently,
 * access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex Hanson
 * @version Spring 2020
 */
public class SimpleJsonWriter { 
  //SimpleJsonWriter could be written as an Atomic Class told deal with currency instead of access being done externally

  /**
   * Writes the elements as a pretty JSON array.
   *
   * @param elements the elements to write
   * @param writer the writer to use
   * @param level the initial indent level
   * @throws IOException if an IO error occurs
   */
  public static void asArray(Collection<Integer> elements, Writer writer, int level)
      throws IOException {
    // TODO Fill in the asArray method using iteration (not string replacement)	  
	if (writer == null) {
		//throw new IOException("The given writer may not have been initialized; it was given as null");
		System.err.printf("Writer was given as null");
		return;
	}
	
	if (elements == null) {
		//throw new NullPointerException("");
		System.err.printf("Elements was given as null");
		return;
	}
	
	if (level < 0) {
		//throw new UnsupportedOperationException("Level was less than 0");
		System.err.printf("Level was less than 0");
		return;
	}
	
	//synchronized(this) { concurrency: this try block belongs inside for synchronized statements }
	try {
		//write opening bracket
		writer.append("[");
		
		//create iterator
		Iterator<Integer> iter = elements.iterator();
		
		//declarations
		Integer element = null;
		
		//base case for one or nothing at all depending on the size
		if (elements.size() > 0) {
			element = iter.next();
			writer.append("\n");
			indent(element.toString(), writer, level+1);
		}
		
		//iterate through integer elements
		while (iter.hasNext()) {
			element = iter.next();
			writer.append(",\n");
			indent(element.toString(), writer, level+1);
		}
		
		//close bracket
		writer.append("\n");
		indent("]", writer, level);

	} catch (IOException e) { throw new IOException("Error using writer to file");
	} catch (NoSuchElementException e) { throw new NoSuchElementException("Iterator failed grabbing the next value");
	} catch (NullPointerException e) { throw new NullPointerException("Getting first element may have failed");
	} catch (Exception e) { return; //throw new UnsupportedOperationException("Missing an exception case for asArray possibly")
	}
  }

  /**
   * Writes the elements as a pretty JSON array to file.
   *
   * @param elements the elements to write
   * @param path the file path to use
   * @throws IOException if an IO error occurs
   *
   * @see #asArray(Collection, Writer, int)
   */
  public static void asArray(Collection<Integer> elements, Path path) throws IOException {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      asArray(elements, writer, 0);
    }
  }

  /**
   * Returns the elements as a pretty JSON array.
   *
   * @param elements the elements to use
   * @return a {@link String} containing the elements in pretty JSON format
   *
   * @see #asArray(Collection, Writer, int)
   */
  public static String asArray(Collection<Integer> elements) {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    try {
      StringWriter writer = new StringWriter();
      asArray(elements, writer, 0);
      return writer.toString();
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Writes the elements as a pretty JSON object.
   *
   * @param elements the elements to write
   * @param writer the writer to use
   * @param level the initial indent level
   * @throws IOException if an IO error occurs
   */
  public static void asObject(Map<String, Integer> elements, Writer writer, int level)
      throws IOException {
    // TODO Fill in the asObject method using iteration (not string replacement)
	if (writer == null) {
		//throw new IOException("The given writer may not have been initialized; it was given as null");
		System.err.printf("Writer was given as null");
		return;
	}
	
	if (elements == null) {
		//throw new NullPointerException("");
		System.err.printf("Elements was given as null");
		return;
	}
	
	if (level < 0) {
		//throw new UnsupportedOperationException("Level was less than 0");
		System.err.printf("Level was less than 0");
		return;
	}
	
	//synchronized(this) { concurrency: this try block belongs inside for synchronized statements }
	try {
		//write opening bracket
		writer.append("{");
		
		//Create iterator;
		Iterator<Entry<String, Integer>> iter = elements.entrySet().iterator();
		
		//declarations
		Entry<String, Integer> element = null;
		
		//base case for one or nothing at all depending on the size
		if (elements.size() > 0) {
			element = iter.next();
			writer.append("\n");
			quote(element.getKey(), writer, level+1);
			writer.append(": " + element.getValue().toString());
		}
		
		//loop through map entries
		while (iter.hasNext()) {
			element = iter.next();
			writer.append(",\n");
			quote(element.getKey(), writer, level+1);
			writer.append(": " + element.getValue().toString());
		}
		
		//close bracket
		writer.append("\n");
		indent("}", writer, level);

	} catch (IOException e) { throw new IOException("Error using writer to file");
	} catch (NoSuchElementException e) { throw new NoSuchElementException("Iterator failed grabbing the next value");
	} catch (IllegalStateException e) { throw new IllegalStateException("Getting the entry failed for get key or get value");
	} catch (NullPointerException e) { throw new NullPointerException("Getting first element may have failed");
	} catch (Exception e) { return; //throw new UnsupportedOperationException("Missing an exception case for asObject possibly")
	}
  }

  /**
   * Writes the elements as a pretty JSON object to file.
   *
   * @param elements the elements to write
   * @param path the file path to use
   * @throws IOException if an IO error occurs
   *
   * @see #asObject(Map, Writer, int)
   */
  public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      asObject(elements, writer, 0);
    }
  }

  /**
   * Returns the elements as a pretty JSON object.
   *
   * @param elements the elements to use
   * @return a {@link String} containing the elements in pretty JSON format
   *
   * @see #asObject(Map, Writer, int)
   */
  public static String asObject(Map<String, Integer> elements) {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    try {
      StringWriter writer = new StringWriter();
      asObject(elements, writer, 0);
      return writer.toString();
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Writes the elements as a pretty JSON object with a nested array. The generic notation used
   * allows this method to be used for any type of map with any type of nested collection of integer
   * objects.
   *
   * @param elements the elements to write
   * @param writer the writer to use
   * @param level the initial indent level
   * @throws IOException if an IO error occurs
   */
  public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements,
      Writer writer, int level) throws IOException {
    // TODO Fill in the asNestedArray method using iteration (not string replacement)
    /*
     * The generic notation:
     *
     * Map<String, ? extends Collection<Integer>> elements
     *
     * ...may be confusing. You can mentally replace it with:
     *
     * HashMap<String, HashSet<Integer>> elements
     */
		if (writer == null) {
			//throw new IOException("The given writer may not have been initialized; it was given as null");
			System.err.printf("Writer was given as null");
			return;
		}
		
		if (elements == null) {
			//throw new NullPointerException("");
			System.err.printf("Elements was given as null");
			return;
		}
		
		if (level < 0) {
			//throw new UnsupportedOperationException("Level was less than 0");
			System.err.printf("Level was less than 0");
			return;
		}
		
		//synchronized(this) { concurrency: this try block belongs inside for synchronized statements }
		try {
			//write opening bracket
			writer.append("{");

			//create iterator
			Iterator<String> keys = elements.keySet().iterator();
			
			//declarations
			Collection<Integer> integers = null;
			String key = null;
			
			//base case for one or nothing at all depending on the size
			if (elements.size() > 0) {
				key = keys.next();
				integers = elements.getOrDefault(key, null);
				writer.append("\n");
				quote(key, writer, level+1);
				writer.append(": ");
				asArray(integers,writer, level + 1);
			}
			
			//loop through keys
			while (keys.hasNext()) {
				key = keys.next();
				integers = elements.getOrDefault(key, null);
				writer.append(",\n");
				quote(key, writer, level+1);
				writer.append(": ");
				asArray(integers,writer, level + 1);
			}
			
			//close bracket
			writer.append("\n");
			indent("}", writer, level);

		} catch (IOException e) { throw new IOException("Error using writer to file");
		} catch (NoSuchElementException e) { throw new NoSuchElementException("Iterator failed grabbing the next value");
		} catch (IllegalStateException e) { throw new IllegalStateException("Getting the entry failed for get key or get value");
		} catch (ClassCastException e) { throw new ClassCastException("Key may be an invalid type for map");
		} catch (NullPointerException e) { throw new NullPointerException("GetOrDefault may have failed since Integers may be null");
		} catch (Exception e) { return; //throw new UnsupportedOperationException("Missing an exception case for asNestedArray possibly")
		}
  }

  /**
   * Writes the elements as a nested pretty JSON object to file.
   *
   * @param elements the elements to write
   * @param path the file path to use
   * @throws IOException if an IO error occurs
   *
   * @see #asNestedArray(Map, Writer, int)
   */
  public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path)
      throws IOException {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      asNestedArray(elements, writer, 0);
    }
  }

  /**
   * Returns the elements as a nested pretty JSON object.
   *
   * @param elements the elements to use
   * @return a {@link String} containing the elements in pretty JSON format
   *
   * @see #asNestedArray(Map, Writer, int)
   */
  public static String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    try {
      StringWriter writer = new StringWriter();
      asNestedArray(elements, writer, 0);
      return writer.toString();
    } catch (IOException e) {
      return null;
    }
  }

  /**
   * Indents using 2 spaces by the number of times specified.
   *
   * @param writer the writer to use
   * @param times the number of times to write a tab symbol
   * @throws IOException if an IO error occurs
   */
  public static void indent(Writer writer, int times) throws IOException {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    for (int i = 0; i < times; i++) {
      writer.write(' ');
      writer.write(' ');
    }
  }

  /**
   * Indents and then writes the element.
   *
   * @param element the element to write
   * @param writer the writer to use
   * @param times the number of times to indent
   * @throws IOException if an IO error occurs
   *
   * @see #indent(String, Writer, int)
   * @see #indent(Writer, int)
   */
  public static void indent(Integer element, Writer writer, int times) throws IOException {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    indent(element.toString(), writer, times);
  }

  /**
   * Indents and then writes the element.
   *
   * @param element the element to write
   * @param writer the writer to use
   * @param times the number of times to indent
   * @throws IOException if an IO error occurs
   *
   * @see #indent(Writer, int)
   */
  public static void indent(String element, Writer writer, int times) throws IOException {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    indent(writer, times);
    writer.write(element);
  }

  /**
   * Writes the element surrounded by {@code " "} quotation marks.
   *
   * @param element the element to write
   * @param writer the writer to use
   * @throws IOException if an IO error occurs
   */
  public static void quote(String element, Writer writer) throws IOException {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    writer.write('"');
    writer.write(element);
    writer.write('"');
  }

  /**
   * Indents and then writes the element surrounded by {@code " "} quotation marks.
   *
   * @param element the element to write
   * @param writer the writer to use
   * @param times the number of times to indent
   * @throws IOException if an IO error occurs
   *
   * @see #indent(Writer, int)
   * @see #quote(String, Writer)
   */
  public static void quote(String element, Writer writer, int times) throws IOException {
    // THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
    indent(writer, times);
    quote(element, writer);
  }

  /**
   * A simple main method that demonstrates this class.
   *
   * @param args unused
   */
  public static void main(String[] args) {
    // MODIFY AS NECESSARY TO DEBUG YOUR CODE

    TreeSet<Integer> elements = new TreeSet<>();
    System.out.println("Empty:");
    System.out.println(asArray(elements));

    elements.add(65);
    System.out.println("\nSingle:");
    System.out.println(asArray(elements));

    elements.add(66);
    elements.add(67);
    System.out.println("\nSimple:");
    System.out.println(asArray(elements));
  }
}
