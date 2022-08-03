import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author UxDeveloperxU
 *
 */
public class QueryGenerator {
	
	/** root logger */
	private static final Logger log = LogManager.getRootLogger();
	
	/**
	 * Check if a line is blank or empty
	 * @param info line of information to be verified
	 * @return if a line is not blank or empty
	 */
	public static boolean lineIsValid(String info) {
		boolean validLine = false;
		if (info != null) {
			validLine = !info.isBlank() && !info.isEmpty();
		}
		return validLine;
	}
	
	/**
	 * Generate queries from the index and those results to the inverted index
	 * @param index accumulating, inverted index for query results
	 * @param queryPath path used to generate query words from
	 * @param exactSearchFlag delegate for deciding which search to use
	 * @throws IOException issues while reading file or attempting to open file
	 * @throws UnsupportedOperationException user error related to invalid object use
	 * @throws FileNotFoundException invalid path or file doesn't exist on the file system
	 * @throws NullPointerException an argument was given as null
	 */
	public static void generateQueries(WordIndex index, Path queryPath, boolean exactSearchFlag) throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException {
		//grab all the lines from a query path
		List<String> lines = Files.readAllLines(queryPath, StandardCharsets.UTF_8);
		log.info("Read all lines");
		//loop through lines from the file of query strings to search for in word index
		for (String line : lines) {
			//check line
			if (lineIsValid(line)) {
				parseLine(index, line, exactSearchFlag);
			}
		}
		log.info("Queries...");
	}
	
	/**
	 * Create a list of unique stems from the line, sort the stems, and find exact or partial searches and get the results
	 * @param index accumulating, inverted index for query results
	 * @param line information to be stemmed
	 * @param exactSearchFlag delegate for deciding which search to use
	 * @throws NullPointerException an argument was given as null
	 * @throws FileNotFoundException invalid path or file doesn't exist on the file system
	 * @throws UnsupportedOperationException user error related to invalid object use
	 * @throws IOException issues while reading file or attempting to open file
	 */
	public static void parseLine(WordIndex index, String line, boolean exactSearchFlag) throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException {
		//create a list //add all unique stems from the line
		List<String> query = new ArrayList<>(TextFileStemmer.uniqueStems(line));
		//sort stems
		Collections.sort(query);
		//make query string
		String queryString = String.join(" ", query);
		//check for valid query string 
		if (lineIsValid(queryString)) {
			//reference for list of results //find exact or partial searches and get the results
			List<ComparableSearchResult> queryResults = exactSearchFlag? index.exactSearch(query) : index.partialSearch(query);
			//add exact or partial search to queries found
			index.addQueryListing(queryString, queryResults);
		}
	}
}
