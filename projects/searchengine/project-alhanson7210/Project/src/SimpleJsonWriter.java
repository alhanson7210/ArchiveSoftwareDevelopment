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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex Hanson
 * @version Spring 2020
 */
public class SimpleJsonWriter {

	/** root logger */
	private static final Logger log = LogManager.getRootLogger();
	
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 * @throws NullPointerException elements or the writer was null
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level)
			throws IOException, NullPointerException {
		// Need to throw the exception, otherwise, this is a silent failure
		if (level < 0) {
			throw new UnsupportedOperationException("Level was less than 0");
		}
		// write opening bracket
		writer.append("[");
		// create iterator
		Iterator<Integer> iter = elements.iterator();
		// declarations
		Integer element = null;
		// base case for one or nothing at all depending on the size
		while (iter.hasNext()) {
			//get next element
			element = iter.next();
			//go to new line
			writer.append("\n");
			//write element
			indent(element.toString(), writer, level + 1);
			//Add a comma when there is another element
			if (iter.hasNext()) {
				writer.append(",");
			}
		}
		// close bracket
		writer.append("\n");
		indent("]", writer, level);
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
	public static final void asArray(Collection<Integer> elements, Path path) throws IOException {
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
	public static final String asArray(Collection<Integer> elements) {
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
	 * @throws NullPointerException elements or the writer was null
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level)
			throws IOException, NullPointerException {
		// Need to throw the exception, otherwise, this is a silent failure
		if (level < 0) {
			throw new UnsupportedOperationException("Level was less than 0");
		}
		// write opening bracket
		writer.append("{");
		// Create iterator;
		Iterator<Entry<String, Integer>> iter = elements.entrySet().iterator();
		// declarations
		Entry<String, Integer> element = null;
		while (iter.hasNext()) {
			//get next element
			element = iter.next();
			//go to new line
			writer.append("\n");
			//write key
			quote(element.getKey(), writer, level + 1);
			//write element
			writer.append(": " + element.getValue().toString());
			//Add a comma when there is another element
			if (iter.hasNext()) {
				writer.append(",");
			}
		}
		// close bracket
		writer.append("\n");
		indent("}", writer, level);
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
	public static final void asObject(Map<String, Integer> elements, Path path) throws IOException {
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
	public static final String asObject(Map<String, Integer> elements) {
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
	 * Writes list of searches found to json format
	 * 
	 * @param searchesFound list of searches
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * 
	 * @throws IOException if an IO error occurs
	 */
	public static final void asObjectArray(List<ComparableSearchResult> searchesFound, Writer writer, int level) 
			throws IOException {
		writer.append("[");
		Iterator<ComparableSearchResult> results = searchesFound.iterator();
		while (results.hasNext()) {
			ComparableSearchResult cobj = results.next();
			writer.append("\n");
			indent("{", writer, level + 1);
			writer.append("\n");
			String where = "\"where\": \"" + cobj.getWhere() + "\"";
			indent(where, writer, level + 2);
			writer.append(",\n");
			String count = "\"count\": " + cobj.getCount();
			indent(count, writer, level + 2);
			writer.append(",\n");
			String score = "\"score\": " + String.format("%.8f", cobj.getScore());
			indent(score, writer, level + 2);
			writer.append("\n");
			indent("}", writer, level + 1);
			if (results.hasNext()) {
				writer.append(",");
			}
		}
		writer.append("\n");
		indent("]", writer, level);
	}
	
	/**
	 * Convert query results to json format
	 * 
	 * @param results unmodifiable view of queries
	 * @param path json file path to write formatted json to
	 * 
	 * @throws IOException if an IO error occurs
	 * @throws NullPointerException input was given as null
	 */
	public static void queryResultsToJson(Collection<Entry<String, List<ComparableSearchResult>>> results, Path path) 
			throws IOException, NullPointerException {
		log.info("Writing query to file...");
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			queryResultsToJson(results, writer);
		}
	}
	
	/**
	 * Convert query results to json format
	 * 
	 * @param results unmodifiable view of queries
	 * @param writer the writer to use
	 * 
	 * @throws IOException if an IO error occurs
	 * @throws NullPointerException input was given as null
	 */
	public static void queryResultsToJson(Collection<Entry<String, List<ComparableSearchResult>>> results, Writer writer) 
			throws IOException, NullPointerException {
		writer.append("{");
		Iterator<Entry<String, List<ComparableSearchResult>>> searchResults = results.iterator();
		while (searchResults.hasNext()) {
			Entry<String, List<ComparableSearchResult>> entry = searchResults.next();
			writer.append("\n");
			String queryString = entry.getKey();
			quote(queryString, writer, 1);
			writer.append(": ");
			List<ComparableSearchResult> searchesFound = entry.getValue();
			asObjectArray(searchesFound, writer, 1);
			if (searchResults.hasNext()) {
				writer.append(",");
			}
		}
		writer.append("\n");
		indent("}", writer, 0);
	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * 
	 * @throws IOException if an IO error occurs
	 * @throws NullPointerException elements or the writer was null
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException, NullPointerException {
		// Need to throw the exception, otherwise, this is a silent failure
		if (level < 0) {
			throw new UnsupportedOperationException("Level was less than 0");
		}
		// write opening bracket
		writer.append("{");
		// create iterator
		Iterator<String> keys = elements.keySet().iterator();
		// declarations
		Collection<Integer> integers = null;
		String key = null;
		while (keys.hasNext()) {
			//get next element(s)
			key = keys.next();
			integers = elements.getOrDefault(key, null);
			//go to new line
			writer.append("\n");
			//write key
			quote(key, writer, level + 1);
			writer.append(": ");
			//write element
			asArray(integers, writer, level + 1);
			//Add a comma when there is another element
			if (keys.hasNext()) {
				writer.append(",");
			}
		}
		// close bracket
		writer.append("\n");
		indent("}", writer, level);
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * 
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static final void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path)
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
	 * 
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static final String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
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
	 * Helper function for writing the wordIndex to file Creates the buffered reader
	 * for the given path Calls main function that writes to file
	 * 
	 * @param invertedIndex unmodifiable wordIndex as a collection of entries
	 * containing all the stems and their positions mapped from file(s)
	 * @param path the file path to use
	 * 
	 * @throws NullPointerException invertedIndex or the writer was null
	 * @throws IOException if an IO error occurs
	 */
	public static void wordIndexToSimpleJson(Collection<Entry<String, Map<String, Collection<Integer>>>> invertedIndex,
			Path path)
			throws IOException, NullPointerException {
		log.info("Writing inverted index to json");
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			wordIndexToSimpleJson(invertedIndex, writer);
		}
	}
	
	/**
	 * Given an unmodifiable collection and a writer, this method will write
	 * the given entries as json to a json file using the writer 
	 * 
	 * @param invertedIndex unmodifiable wordIndex as a collection of entries
	 * containing all the stems and their positions mapped from file(s)
	 * @param writer the writer to use
	 * 
	 * @throws NullPointerException invertedIndex or the writer was null
	 * @throws IOException if an IO error occurs
	 */
	public static void wordIndexToSimpleJson(Collection<Entry<String, Map<String, Collection<Integer>>>> invertedIndex,
			Writer writer) throws IOException, NullPointerException {
		// writing word index to Json
		Iterator<Entry<String, Map<String, Collection<Integer>>>> indexEntries = invertedIndex.iterator();
		Map.Entry<String, Map<String, Collection<Integer>>> indexEntry = null;
		writer.append("{");
		// Merge with while loop
		while (indexEntries.hasNext()) {
			//get next element
			indexEntry = indexEntries.next();
			//go to new line
			writer.append("\n");
			//write key 
			quote(indexEntry.getKey(), writer, 1);
			writer.append(": ");
			//write element
			asNestedArray(indexEntry.getValue(), writer, 1);
			//Add a comma when there is another element
			if (indexEntries.hasNext()) {
				writer.append(",");
			}
		}
		// last newline
		writer.append("\n");
		indent("}", writer, 0);
	}

	/**
	 * Indents using 2 spaces by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * 
	 * @throws IOException if an IO error occurs
	 */
	public static final void indent(Writer writer, int times) throws IOException {
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
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * 
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static final void indent(Integer element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * 
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static final void indent(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException if an IO error occurs
	 */
	public static final void quote(String element, Writer writer) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * 
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static final void quote(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		quote(element, writer);
	}
}