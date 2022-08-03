import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Class responsible for running this project based on the provided command-line arguments. See the
 * README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex L Hanson
 * @version Spring 2020
 */
public class Driver {
	/** logger */
	private static final Logger log = LogManager.getRootLogger();
	/** thread count */
	private static int threads = MultithreadedWordIndex.SINGLE_THREAD_DEFAULT;
	/** web crawler default limit */
	private static int limit;
	/** port flag */
	private static final String portFlag = "-port";
	/** url flag */
	private static final String urlFlag = "-url";
	/** limit flag */
	private static final String limitFlag = "-limit";
	/** thread flag */
	private static final String threadFlag = "-threads";
	/** index flag */
	private static final String indexFlag = "-index";
	/** counts flag */
	private static final String countsFlag = "-counts";
	/** results flag */
	private static final String resultsFlag = "-results";
	/** path flag */
	private static final String pathFlag = "-path";
	/** query flag */
	private static final String queryFlag = "-query";
	/** exact flag */
	private static final String exactFlag = "-exact";
	/** index file */
	private static final String defaultIndexJsonFile = "index.json";
	/** counts file */
	private static final String defaultCountsJsonFile = "counts.json";
	/** results file */
	private static final String defaultResultsJsonFile = "results.json";
	/** port number */
	private static int PORT;
	/**thread count */
	private static boolean validThreadCount;
	/**default output */
	private static String outputFile;
	/**if able, create json output file */
	private static Path indexJsonPath;
	/**default output */
	private static String countsOutputFile;
	/**if able, create json output file */
	private static Path countsJsonPath;
	/**default output */
	private static String resultsOutputFile;
	/**if able, create json output file */
	private static Path resultsJsonPath;
	/**arguments to parse */
	private static ArgumentParser parser;
	/**inverted index */
	private static WordIndex wordIndex;
	/**pool thread work queue */
	private static WorkQueue tasks;
	/** web crawler */
	private static WebCrawler crawler;
	/**
	 * Initializes the classes necessary based on the provided command-line arguments. This includes
	 * (but is not limited to) how to build or search an inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		// store initial start time
		Instant start = Instant.now();
		//usage recommendations
		boolean hasArguments = Driver.argumentChecker(args);
		if (hasArguments) {
			Driver.setDefaults();
			boolean hasIndex = Driver.buildIndex();
			if (hasIndex) {
				Driver.writeIndexJson();
				Driver.writeFileCountJson();
				Driver.searchIndex();
				Driver.writeResultsJson();
			} else {
				Driver.usage();
			}
			if (validThreadCount) {
				Driver.tasks.shutdown();
			}
		} else {
			Driver.usage();
		}
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		log.info("Elapsed: " + seconds + " seconds");
	}
	
	/** Usage recommendations based on the provided command-line arguments */
	public static void usage() {
		log.info(
			"The '-path' flag expects to have a valid path to a directory or file\n"
			+ "If  '-index' flag does not have a path or is an invalid path, then it will default to 'index.json'\n"
			+ "The '-query' flag expects to have a valid path to a text file\n"
			+ "If  '-counts' flag does not have a path or is an invalid path, then it will default to 'counts.json'\n"
			+ "If  '-results' flag does not have a path or is an invalid path, then it will default to 'results.json'\n"
			+ "The '-exact' flag given as the flag itself\n"
			+ "Invalid arguments given with no flags will not be ran\n"
			+ "Supported flags:\n\t'path' 'index' 'query' 'counts' 'results' 'exact'\n"
			+ "This programs arguments should be oriented as such:\n"
			+ "\t-path path -index path -query path -counts path -results path -exact\n"
			+ "\totherwise, it will be the given path to output the json to\n"
			+ "Please mirror this example as close as possible with necessary arguments to avoid issues:\n"
			+ "\t-path \"../../project-tests/Project Tests/input/text/simple/\" -index index-text-simple-hello.json"
		);
	}
	
	/**
	 * Verify flag and value pairs for the argument parser
	 * @param args arguments to verify
	 * @return {@code true} if the parser has flags
	 */
	private static boolean argumentChecker(String[] args) {
		boolean hasArgs = false;
		if (args != null && args.length != 0) {
			//arguments given
			log.info(Arrays.toString(args));
			//create argument parser
			parser = new ArgumentParser(args);
			if (parser.numFlags() != 0) {
				hasArgs = true;
			}
		}
		return hasArgs;
	}
	
	/**
	 * check flag pair
	 * @param flag string flag
	 * @return true only if flag has a valid pair
	 */
	private static boolean validFlagPair(String flag) {
		return parser.hasFlag(flag) && parser.getString(flag) != null;
	}
	
	/**
	 * Enable threading if necessary
	 */
	private static void countValidation() {
		validThreadCount = false;
		//Thread count validation
		boolean enableThreading = parser.hasFlag(threadFlag) || parser.hasFlag(urlFlag) || parser.hasFlag(portFlag);
		if (enableThreading) {
			//get thread count
			validThreadCount = true;
			checkNewAssignment();
			tasks = new WorkQueue(threads);
		}
	}
	
	/**
	 * new assignment or default int values
	 */
	private static void checkNewAssignment() {
		String threadCount = parser.getString(threadFlag);
		threads = TextParser.validNumber(threadCount)? Integer.valueOf(threadCount) : MultithreadedWordIndex.MULTITHREAD_DEFAULT;
		String limitVar = parser.getString(limitFlag);
		limit = TextParser.validNumber(limitVar)? Integer.valueOf(limitVar) : WebCrawler.DEFAULT;
		String portNum = parser.getString(portFlag);
		PORT = TextParser.validNumber(portNum) ? Integer.valueOf(portNum): 8080;
	}
	
	/**
	 * setting flag defaults
	 */
	private static void setDefaults() {
		countValidation();
		//default output
		outputFile = parser.defaultGenertor.apply(indexFlag, defaultIndexJsonFile);
		//if able, create json output file
		indexJsonPath = TextFileFinder.pathGenerator.apply(outputFile);
		log.info("index operations set");
		//default output
		countsOutputFile = parser.defaultGenertor.apply(countsFlag, defaultCountsJsonFile);
		//if able, create json output file
		countsJsonPath = TextFileFinder.pathGenerator.apply(countsOutputFile);
		log.info("counts operations set");
		//default output
		resultsOutputFile = parser.defaultGenertor.apply(resultsFlag, defaultResultsJsonFile);
		//if able, create json output file
		resultsJsonPath = TextFileFinder.pathGenerator.apply(resultsOutputFile);
		log.info("results operations set");
	}

	/**
	 * Build index in its respective contexts while branching
	 * @return {@code true} if index was built successfully
	 */
	private static boolean buildIndex() {
		boolean hasNotFailed = false;
		try {
			//path checking
			hasNotFailed = buildBranching();
		} catch (NullPointerException e) { 
			log.warn("Received null input");
		} catch (FileNotFoundException e) { 
			log.warn("Output file was not found");
		} catch (InterruptedException e) {
			log.warn("The Queue was interrupted");
		} catch (IOException e) { 
			log.warn("Error occured getting path");
		}
		return hasNotFailed;
	}
	
	/**
	 * Decision matrix for building in a serial, multithreading, or crawler context
	 * @return true if building has successfully completed
	 * @throws IOException issues while reading or attempting to read file
	 * @throws InterruptedException the work queue was interrupted
	 * @throws UnsupportedOperationException user error related to incorrect object use
	 * @throws FileNotFoundException given path was not found within the file system
	 * @throws NullPointerException an argument was given as null
	 * @throws Exception Issues occurred running the server for the search engine
	 */
	private static boolean buildBranching() throws NullPointerException, FileNotFoundException, UnsupportedOperationException, InterruptedException, IOException {
		boolean hasNotFailed = false;
		if (validFlagPair(pathFlag)) {
			//printing path given
			log.info("The path given is " + parser.getString(pathFlag));
			//this should get back a stream of all text files
			Path originalPath = parser.getPath(pathFlag);
			//create inverted index
			hasNotFailed = build(originalPath);
		} else if (validFlagPair(urlFlag)) {
			String seed = parser.getString(urlFlag);
			crawler = new WebCrawler(tasks, seed, limit);
			wordIndex = crawler.crawlFromSeedUrl();
			hasNotFailed = true;
		}

//		-url "https://www.amazon.com/" -limit 50 -port 8080
		if (parser.hasFlag(portFlag)) {
			startWebServlet();
			hasNotFailed = true;
		}
		
		return hasNotFailed;
	}
	
	/**
	 * Start a server and its respective servlets
	 */
	private static void startWebServlet() {
		try {
			//server
			Server server = new Server(PORT);
			//handlers
			ResourceHandler resourceHandler = new ResourceHandler();
			ServletHandler handler = new ServletHandler();
			//list of handlers
			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { resourceHandler, handler });
			//handler for serving local images
			resourceHandler.setDirectoriesListed(true);
			resourceHandler.setResourceBase("web");
			//create servlets
			MultithreadedWordIndex index = (MultithreadedWordIndex) wordIndex;
			ServletHolder home = new ServletHolder(new HomeServlet(crawler));
			ServletHolder invertedIndex = new ServletHolder(new IndexServlet(index));
			ServletHolder location = new ServletHolder(new LocationServlet(index));
			ServletHolder shutdown = new ServletHolder(new ShutdownServlet(server, handlers));
			//add servlets
			handler.addServletWithMapping(home, "/home");
			handler.addServletWithMapping(invertedIndex, "/index");
			handler.addServletWithMapping(location, "/location");
			handler.addServletWithMapping(shutdown, "/shutdown");
			//set server handler and start the server
			server.setHandler(handlers);
			server.start();
			server.join();
			
		}  catch (Exception e) {
			log.warn("Server failed");
		}
	}
	
	/**
	 * Build the inverted index in a serial context or a multithreading context
	 * @param originalPath file path to build the inverted index from
	 * @return if index was built
	 * @throws InterruptedException the work queue was interrupted
	 * @throws NullPointerException an argument was given as null
	 * @throws FileNotFoundException given path was not found within the file system
	 * @throws UnsupportedOperationException user error related to incorrect object use
	 * @throws IOException issues while reading or attempting to read file
	 */
	private static boolean build(Path originalPath) throws InterruptedException, NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException {
		boolean hasNotFailed = false;
		if (Files.isReadable(originalPath)) {
			log.info("Processing word index...");
			wordIndex = validThreadCount? MultithreadedIndexBuilder.build(tasks, originalPath) : IndexBuilder.build(originalPath);
			log.info("Word index has been processed");
			hasNotFailed = true;
		}
		return hasNotFailed;
	}
	
	/**
	 * Write the index results found to a json file
	 */
	private static void writeIndexJson() {
		try {
			//convert wordIndex into Json //readability checking
			if (indexJsonPath != null && Files.isReadable(indexJsonPath)) {
				log.info("Writing word index to json...");
				SimpleJsonWriter.wordIndexToSimpleJson(wordIndex.getUnmodifiableElements(), indexJsonPath);
				log.info("Word index finished written to file");
			}
		} catch (NullPointerException | IOException e) {
			log.info("Failed to write index to file");
		}
	}
	
	/**
	 * Write file count results to a json file
	 */
	private static void writeFileCountJson() {
		try {
			//get map of counts
			Map<String, Integer> counts =  wordIndex.getUnmodifiableFileCount();
			//check for null value
			if (countsJsonPath != null && counts != null && Files.isReadable(countsJsonPath)) {
				log.info("Counts json file: " + countsJsonPath + " construction...");
				log.info("Writing to file");
				SimpleJsonWriter.asObject(counts, countsJsonPath);
				log.info("Counts json file completed " + resultsJsonPath);
			}
		} catch (IOException e) {
			log.info("Failed to write count to file");
		}
	}
	
	/**
	 * Generate query results after branching conditions are met
	 */
	private static void searchIndex() {
		//query generating
		try {
			//get query path
			Path queryPath = parser.getPath(queryFlag);
			if (queryPath == null) {
				return;
			}
			//printing query text file given
			log.info("The query given is " + parser.getString(queryFlag));
			//check query file path flag
			boolean exactSearchFlag = parser.hasFlag(exactFlag)? true : false;
			searchBranching(queryPath, exactSearchFlag);
		} catch (NullPointerException e) { 
			log.warn("Received null input");
		} catch(UnsupportedOperationException e) {
			log.warn("Unsupported map operation occurred within inverted index");
		} catch (IOException e) { 
			log.warn("Error handling file paths, writing to file, or generating queries");
		} catch (InterruptedException e) {
			log.warn("Could not read file for query path or results path");
		}
	}
	
	/**
	 * Branching based on if a crawler is needed, or if general implementation is needed
	 * @param queryPath path to generate query words from
	 * @param exactSearchFlag delegate for search exact matches or partial matches
	 * @throws NullPointerException an argument was given as null
	 * @throws FileNotFoundException given path was not found within the file system
	 * @throws UnsupportedOperationException user error related to incorrect object use
	 * @throws IOException issues while reading or attempting to read file
	 * @throws InterruptedException work queue was interrupted
	 */
	private static void searchBranching(Path queryPath, boolean exactSearchFlag) 
			throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException, InterruptedException {
		if (validFlagPair(urlFlag)) {
			crawler.generate(queryPath, exactSearchFlag);
		} else {
			boolean meetsGenerationCondition = validFlagPair(queryFlag);
			Driver.generate(queryPath, exactSearchFlag, meetsGenerationCondition);
		}
	}
	
	/**
	 * If generation is needed, search in a multithreaded context or in a serial context
	 * @param queryPath path to generate query words from
	 * @param exactSearchFlag delegate for search exact matches or partial matches
	 * @param meetsGenerationCondition {@code true}valid flag pair for query flag
	 * @throws NullPointerException an argument was given as null
	 * @throws FileNotFoundException given path was not found within the file system
	 * @throws UnsupportedOperationException user error related to incorrect object use
	 * @throws IOException issues while reading or attempting to read file
	 * @throws InterruptedException work queue was interrupted
	 */
	private static void generate(Path queryPath, boolean exactSearchFlag, boolean meetsGenerationCondition) throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException, InterruptedException {
		if (meetsGenerationCondition) {
			search(queryPath, exactSearchFlag);
		}
	}
	
	/**
	 * Search in a multithreaded context or in a serial context
	 * @param queryPath path to generate query words from
	 * @param exactSearchFlag delegate for search exact matches or partial matches
	 * @throws NullPointerException an argument was given as null
	 * @throws FileNotFoundException given path was not found within the file system
	 * @throws UnsupportedOperationException user error related to incorrect object use
	 * @throws IOException issues while reading or attempting to read file
	 * @throws InterruptedException work queue was interrupted
	 */
	private static void search(Path queryPath, boolean exactSearchFlag) throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException, InterruptedException {
		//readability checking
		if (Files.isReadable(queryPath)) {
			log.info("Conducting searches..."); 
			if (validThreadCount && wordIndex instanceof MultithreadedWordIndex) {
				MultithreadedWordIndex index = (MultithreadedWordIndex) wordIndex;
				MultithreadedQueryGenerator.generateQueries(index, tasks, queryPath, exactSearchFlag);
			} else {
				QueryGenerator.generateQueries(wordIndex, queryPath, exactSearchFlag);
			}
			log.info("Searches conducted");
		}
	}
	
	/**
	 * Write search results json to a given file path
	 */
	private static void writeResultsJson() {
		try {
			//convert results into Json //readability checking
			if (resultsJsonPath != null && Files.isReadable(resultsJsonPath)) {
				log.info("Converting results into Json...");
				SimpleJsonWriter.queryResultsToJson(wordIndex.getUnmodifiableQueryElements(), resultsJsonPath);
				log.info("Results json file: " + resultsJsonPath + " construction...");
			}
		} catch (NullPointerException | IOException e) {
			log.info("Failed to write resuls to file");
		}
	}
}