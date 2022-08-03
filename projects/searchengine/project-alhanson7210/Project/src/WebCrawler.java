import java.net.MalformedURLException;
import java.net.URL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author UxDeveloperxU
 *
 */
public class WebCrawler {
	/** logger */
	private static final Logger log = LogManager.getRootLogger();
	/** default value for the link crawling limit */
	public static int DEFAULT = 50;
	/** index */
	private MultithreadedWordIndex index;
	/** work queue */
	private WorkQueue tasks; 
	/** list of urls*/
	private Set<URL> accumulator;
	/** url used for crawl */
	private URL seedUrl;
	/** seed url*/
	private String seed; 
	/** limit */
	private int limit;
	/**
	 * Constructor
	 * @param tasks work queue to create link tasks for
	 * @param seed uniform resource locator as text information
	 * @param limit upper bound for when to stop looking for web links
	 */
	public WebCrawler(WorkQueue tasks, String seed, int limit) {
		this.tasks = tasks;
		this.accumulator = new HashSet<>();
		this.seed = seed;
		this.limit = limit;
	}
	
	/**
	 * Crawl from a seed url
	 * @return seed built index from seed url
	 * @throws MalformedURLException Failed to format a given url
	 * @throws InterruptedException the work queue was interrupted
	 */
	public WordIndex crawlFromSeedUrl() 
			throws MalformedURLException, InterruptedException {
		seedUrl = new URL(seed);
		accumulator.add(seedUrl);
		MultithreadedWordIndex index = new MultithreadedWordIndex();
		this.index = index;
		crawl();
		tasks.finish();
		return index;
	}
	/**
	 * Crawl from a new seed url and increase the limit
	 * @param newSeed new seed to crawl from
	 * @return seed built index from seed url
	 * @throws MalformedURLException Failed to format a given url
	 * @throws InterruptedException the work queue was interrupted
	 */
	public WordIndex crawlFromSeedUrl(String newSeed) 
			throws MalformedURLException, InterruptedException {
		seed = newSeed;
		limit += 20;
		return crawlFromSeedUrl();
	}
	
	/**
	 * Crawl a seed url for stems to add to the index
	 * @throws MalformedURLException Failed to format a given url
	 */
	public void crawl() throws MalformedURLException {
		LinkTask task = new LinkTask(seedUrl);
		tasks.execute(task);
	}
	
	/**
	 * Generate queries from a query generator
	 * @param queryPath path used to generate query words from
	 * @param exactSearchFlag delegate for deciding which search to use
	 * @throws InterruptedException the work queue was interrupted
	 * @throws IOException issues while reading file or attempting to open file
	 * @throws UnsupportedOperationException user error related to invalid object use
	 * @throws FileNotFoundException path or file doesn't exist on the file system
	 * @throws NullPointerException an argument was given as null
	 */
	public void generate(Path queryPath, boolean exactSearchFlag) 
			throws NullPointerException, FileNotFoundException, UnsupportedOperationException, IOException, InterruptedException {
		MultithreadedQueryGenerator.generateQueries(index, tasks, queryPath, exactSearchFlag);
	}
	
	/**
	 * parse and search query string
	 * @param search from search engine
	 * @return return matches for 
	 */
	public List<ComparableSearchResult> searchQuery(String search) {
		return searchQuery(search, false);
	}
	
	/**
	 * parse and search query string
	 * @param search multi-word query from search engine
	 * @param exact delegate for deciding which search algorithm to use
	 * @return return matches for a given multi-word search
	 */
	public List<ComparableSearchResult> searchQuery(String search, boolean exact) {
		if (search == null) {
			return Collections.emptyList();
		}
		List<ComparableSearchResult> results = new ArrayList<>();
		List<String> queryWords = new ArrayList<>();
		try {
			queryWords.addAll(TextFileStemmer.uniqueStems(search));
			results.addAll(exact? index.exactSearch(queryWords) : index.partialSearch(queryWords));
		} catch (Exception e) {
			log.warn("Regardless of the exception, failed to get the stems for the " + search);
		}
		return results;
	}
	
	/**
	 * Get accumulator set of urls
	 * @return set of urls
	 */
	public Set<URL> getAccumulator() {
		return Collections.unmodifiableSet(accumulator);
	}
	
	/**
	 * 
	 * @author UxDeveloperxU
	 *
	 */
	private final class LinkTask implements Runnable {
		/** local urls */
		private List<URL> webLinks;
		/** url */
		private URL url;
		/** string of url for the index to use */
		private String webLink;
		/** html */ 
		private String seedHtml;
		/**
		 * constructor
		 * @param url web link to parse html for
		 */
		public LinkTask(URL url) {
			this.url = url;
			webLinks = new ArrayList<>();
		}
		
		@Override
		public void run() {
			if (urlIsValid()) {
				parseUrl();
			}
		}
		
		/**
		 * Verify the validity of a fetched url
		 * @return html was set and can be used
		 */
		private boolean urlIsValid() {
			seedHtml = HtmlFetcher.fetch(url, HtmlFetcher.REDIRECTS);
			return seedHtml == null? false : true;
		}
		
		/**
		 * Parsing process for a given url
		 */
		private void parseUrl() {
			try {
				 parseHtml();
				 addUrls();
				 addParsedHtml();
			} catch (MalformedURLException e) {
				log.info("Thread #" + Thread.currentThread().getId() + " failed parse a list of web links for: " + url.toString());
			}
		}
		
		/**
		 * Clean html content
		 * @param seedHtml fetched html from a url link
		 * @return parsed html
		 * @throws MalformedURLException Failed to format a given url
		 */
		private String parseHtml() throws MalformedURLException {
			seedHtml = HtmlCleaner.stripBlockElements(seedHtml);
			webLinks.addAll(LinkParser.listLinks(url, seedHtml));
			seedHtml = HtmlCleaner.stripTags(seedHtml);
			seedHtml = HtmlCleaner.stripEntities(seedHtml);
			return seedHtml;
		}
		
		/**
		 * from text file stemmer
		 */
		private void addUrls() {
			synchronized(accumulator) {
				for (URL link : webLinks) {
					addLink(link);
				}
			}
		}
		
		/** 
		 * add clean link to 
		 * @param link web link from parsed html content
		 */
		private final void addLink(URL link) {
			if (accumulator.size() < limit && !accumulator.contains(link)) {
				accumulator.add(link);
				LinkTask webLink = new LinkTask(link);
				tasks.execute(webLink);
			}
		}
		
		/**
		 * add parsed html to the index
		 * @param seedHtml parsed html content
		 */
		private void addParsedHtml() {
			webLink = url.toString();
			WordIndex wordMapping = new WordIndex();
			TextFileStemmer.parseHtml(wordMapping, webLink, seedHtml);
			index.addAll(wordMapping, webLink);
			index.addFileCount(webLink, wordMapping.getFileCount(webLink));
		}
	}
}
