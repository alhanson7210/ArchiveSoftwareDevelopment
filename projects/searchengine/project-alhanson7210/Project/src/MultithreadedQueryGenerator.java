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
public class MultithreadedQueryGenerator {
	/** logger */
	private static final Logger log = LogManager.getRootLogger();
	
	/**
	 * Generate queries from the index and those results to the inverted index
	 * @param index accumulating, thread safe index for query results
	 * @param tasks work queue to provide tasks to
	 * @param queryPath path used to generate query words from
	 * @param exactSearchFlag delegate for deciding which search to use
	 * @throws IOException issues while reading file or attempting to open file
	 * @throws UnsupportedOperationException user error related to invalid object use
	 * @throws FileNotFoundException invalid path or file doesn't exist on the file system
	 * @throws NullPointerException an argument was given as null
	 * @throws InterruptedException the work queue was interrupted
	 */
	public static void generateQueries(MultithreadedWordIndex index, WorkQueue tasks, Path queryPath, boolean exactSearchFlag) 
			throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException, InterruptedException {
		//grab all the lines from a query path
        List<String> lines = Files.readAllLines(queryPath, StandardCharsets.UTF_8);
        log.info("Read all lines");
        //loop through lines from the file of query strings to search for in word index
        for (String line : lines) {
            //make runnable
        	if (QueryGenerator.lineIsValid(line)) {
        		Runnable queryTask = new MultithreadedQueryGenerator.QueryTask(line, exactSearchFlag, index);
        		tasks.execute(queryTask);
        	}
        }
        tasks.finish();
        log.info("Queries...");
	}
	
	/**
	 * 
	 * @param index accumulating, thread safe index for query results
	 * @param line line from the file of query strings to search for in word index
	 * @param exactSearchFlag delegate for deciding which search to use
	 * @throws NullPointerException an argument was given as null
	 * @throws FileNotFoundException invalid path or file doesn't exist on the file system
	 * @throws UnsupportedOperationException user error related to invalid object use
	 * @throws IOException issues while reading file or attempting to open file
	 */
	public static void parseLine(MultithreadedWordIndex index, String line, boolean exactSearchFlag) throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException {
		//create a list //add all unique stems from the line
		List<String> query = new ArrayList<>(TextFileStemmer.uniqueStems(line));
		//sort stems
		Collections.sort(query);
		//make query string
		String queryString = String.join(" ", query);
		//check for valid query string 
		if (QueryGenerator.lineIsValid(queryString)) {
			//reference for list of results //find exact or partial searches and get the results
			List<ComparableSearchResult> queryResults = exactSearchFlag? index.exactSearch(query) : index.partialSearch(query);
			//add exact or partial search to queries found
			index.addQueryListing(queryString, queryResults);
		}
	}
	
	/**
	 * Runnable for generating queries from the index given
	 * Note: could be a separate class but I would rather
	 * hide some of the implementation
	 * @author Alex L Hanson
	 */
	public static class QueryTask implements Runnable {
		/** line from query file to parse */
		private final String line;
		/** search flag for exact or partial search */
		private final boolean exactSearchFlag;
		/** word index to add stems to */
		private final MultithreadedWordIndex index;

		/**
		 * Constructor
		 * @param line line from query file to parse
		 * @param exactSearchFlag search flag for exact or partial search
		 * @param index word index map from the given MultithreadedWordIndex
		 */
		public QueryTask (String line, boolean exactSearchFlag, MultithreadedWordIndex index) {
			this.line = line;
			this.exactSearchFlag = exactSearchFlag;
			this.index = index;
		}

		@Override
		public void run() {
			try {
				//check line
				parseLine(index, line, exactSearchFlag);
			} catch (NullPointerException e) {
				log.warn("Either the path given was null, the query line, or the index could be null");
			} catch (FileNotFoundException e) {
				log.warn("file path was not found");
			} catch (UnsupportedOperationException e) {
				log.warn("Couldn't convert path to a file");
			} catch (IOException e) {
				log.warn("Error occurred creating tree map");
			}
		}
	}
}
