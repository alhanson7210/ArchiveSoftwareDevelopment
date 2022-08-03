import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * A special type of {@link Index} that indexes the locations from a file to the
 * stems found.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex L Hanson
 * @version Spring 2020
 */
public class WordIndex implements Index<String> {
	
	/** List of search queries and their results */
	private final TreeMap<String, List<ComparableSearchResult>> queries;
	
	/** wordIndex that stores all collected stems */
	private final TreeMap<String, Map<String, Collection<Integer>>> wordIndex;
	
	/** word count for each file in word index */
	private final TreeMap<String, Integer> wordCount;

	/**
	 * A special type of {@link Index} that indexes the locations words were found.
	 * Initialize map for the word index
	 */
	public WordIndex() {
		wordIndex = new TreeMap<>();
		wordCount = new TreeMap<>();
		queries = new TreeMap<>();
	}
	
	/**
	 * Finds all exact matches and generates a list of search results
	 * 
	 * @param queryWords list of unique query searches
	 * 
	 * @return a sorted list of comparable search results
	 */
	public List<ComparableSearchResult> exactSearch(List<String> queryWords) {
		//matches found in file(s)
		HashMap<String, ComparableSearchResult> matches = new HashMap<>();
		//loop through query words
		for (String queryWord: queryWords) {
			//exact match was found
			if(wordIndex.containsKey(queryWord)) {
				//add result
				ComparableSearchResult.addSearchResults(this, queryWord, matches);
			}
		}
		//return sorted results
		return ComparableSearchResult.resultSorter(matches.values());
	}
	
	/**
	 * Finds all partial matches and generates a list of search results
	 * 
	 * @param queryWords list of unique query searches
	 * 
	 * @return a sorted list of comparable search results
	 */
	public List<ComparableSearchResult> partialSearch(List<String> queryWords) {
		//matches found in file(s)
		HashMap<String, ComparableSearchResult> matches = new HashMap<>();
		Set<String> keySet = wordIndex.keySet();
		//loop through key set
		for (String key : keySet) {
			//loop through query words
			partialHelper(queryWords, matches, key);
		}
		//return sorted results
		return ComparableSearchResult.resultSorter(matches.values());
	}
	
	/**
	 * helper method to add search result to the results object
	 * @param queryWords list of unique query searches
	 * @param matches found in file
	 * @param key from the index
	 */
	private void partialHelper(List<String> queryWords, HashMap<String, ComparableSearchResult> matches, String key) {
		for (String queryWord: queryWords) {
			//partial match was found
			if (key.startsWith(queryWord)) {
				//add result
				ComparableSearchResult.addSearchResults(this, key, matches);
			}
		}
	}
	
	/**
	 * Adds the element and position from a given file.
	 *
	 * @param filePath where element was found
	 * @param stem     the element found
	 * @param position the position the element was found
	 * 
	 * @return {@code true} if the index changed as a result of the call
	 */
	@Override
	public boolean add(String stem, String filePath, int position) {
		//checking validity
		if (stem != null && filePath != null && position > 0) {
			//Adds the element and position from a given file.
			wordIndex.putIfAbsent(stem, new TreeMap<>());
			wordIndex.get(stem).putIfAbsent(filePath, new TreeSet<Integer>());
			return wordIndex.get(stem).get(filePath).add(position);
		}
		return false;
	}
	
	/**
	 * Building this word index up from another word index
	 * @param local another inverted index
	 * @param correctedFilePath file path to add stems to in the inverted index
	 * @return true if able to add all from local word index
	 */
	public boolean addAll(WordIndex local, String correctedFilePath) {
		Map<String, Map<String, Collection<Integer>>> localIndex = local.wordIndex;
		for(String stem : localIndex.keySet()) {
			if (!this.wordIndex.containsKey(stem)) {
				this.wordIndex.putIfAbsent(stem, localIndex.get(stem));
			}
			else if (!this.wordIndex.get(stem).containsKey(correctedFilePath)) {
				this.wordIndex.get(stem).putIfAbsent(correctedFilePath, localIndex.get(stem).get(correctedFilePath));
			}
			else if (!this.wordIndex.get(stem).get(correctedFilePath).addAll(localIndex.get(stem).get(correctedFilePath))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Adds the completed file count to the word indexes wordCount
	 * 
	 * @param correctedPath file path to add fileCount to if needed
	 * @param count number of words in file
	 */
	public void addFileCount(String correctedPath, int count) {
		//checking validity
		if (correctedPath != null && count > 0) {
			//Adds the completed file count
			wordCount.putIfAbsent(correctedPath, count);
		}
	}
	
	/**
	 * Gets the file count of a correctedPath
	 * 
	 * @param correctedPath file path to add fileCount to if needed
	 * @return file count if word count contains the the path or 0
	 */
	public int getFileCount(String correctedPath) {
		return wordCount.getOrDefault(correctedPath, 0);
	}
	
	/**
	 * Add query listing to queries map
	 * @param queryString words to be found in the word index
	 * @param queryResults list of search results found
	 */
	public void addQueryListing(String queryString, List<ComparableSearchResult> queryResults) {
		if (queryString != null && queryResults != null) {
			queries.putIfAbsent(queryString, queryResults);
		}
	}

	/**
	 * Determines whether the element is stored in the index.
	 *
	 * @param stem the element to lookup
	 * 
	 * @return {@true} if the element is stored in the index
	 */
	@Override
	public boolean contains(String stem) {
		return stem != null ? wordIndex.containsKey(stem) : false;
	}

	/**
	 * Determines whether the stem and filePath is stored in the index.
	 * 
	 * @param stem     word stem from a file
	 * @param filePath path to which the stem came from
	 * 
	 * @return true if the word index contains the filePath
	 */
	public boolean contains(String stem, String filePath) {
		return stem != null && filePath != null ? wordIndex.get(stem).containsKey(filePath) : false;
	}

	/**
	 * Determines whether the element is stored in the index and the position is
	 * stored for that element.
	 *
	 * @param stem     the element to lookup
	 * @param position the position of that element to lookup
	 * 
	 * @return {@true} if the element and position is stored in the index
	 */
	@Override
	public boolean contains(String stem, String filePath, int position) {
		return stem != null && filePath != null ? wordIndex.get(stem).get(filePath).contains(position) : false;
	}

	/**
	 * Returns an unmodifiable view of the stems stored in the index.
	 *
	 * @return an unmodifiable view of the stems stored in the index
	 * 
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	@Override
	public Collection<String> getElements() {
		return Collections.unmodifiableCollection(wordIndex.keySet());
	}
	
	/**
	 * Set of file path entries within index for a given stem
	 * @param stem the element found
	 * @return a set of file path entries for a stem
	 */
	public Set<Map.Entry<String,Collection<Integer>>> getFilePathEntries(String stem) {
		return stem != null? 
				Collections.unmodifiableSet(wordIndex.get(stem).entrySet())
				: Collections.emptySet();
	}
	
	/**
	 * Returns an unmodifiable view of the positions stored in the index for the
	 * provided element, or an empty collection if the element is not in the index.
	 *
	 * @param stem the element to lookup
	 * 
	 * @return an unmodifiable view of the positions stored for the element
	 */
	@Override
	public Collection<Integer> getPositions(String stem, String filePath) {
		return stem != null && filePath != null? 
				  Collections.unmodifiableCollection(wordIndex.get(stem).get(filePath))
				: Collections.emptyList();
	}
	
	/**
	 * Something to see how to handle the word index better in the driver
	 * 
	 * @return wordIndex as a collection of entries
	 */
	public Collection<Entry<String, Map<String, Collection<Integer>>>> getUnmodifiableElements() {
		return Collections.unmodifiableCollection(wordIndex.entrySet());
	}
	
	/**
	 * Unmodifiable file count map
	 * @return an unmodifiable file count map to to write as json later
	 */
	public Map<String, Integer> getUnmodifiableFileCount() {
		return Collections.unmodifiableMap(wordCount);
	}
	
	/**
	 * Unmodifiable map of word index
	 * @return index map access
	 */
	public Map<String, Map<String, Collection<Integer>>> getUnmodifiableMap() {
		return Collections.unmodifiableMap(wordIndex);
	}
	
	/**
	 * Query elements found in search
	 * 
	 * @return Unmodifiable Query Elements
	 */
	public Collection<Entry<String, List<ComparableSearchResult>>> getUnmodifiableQueryElements() {
		return Collections.unmodifiableCollection(queries.entrySet());
	}

	/**
	 * Returns the number of stems stored in the index.
	 *
	 * @return 0 if the index is empty, otherwise the number of element in the index
	 */
	@Override
	public int numElements() {
		return wordIndex.size();
	}
	
	/**
	 * Returns the number of positions stored for the given element.
	 *
	 * @param stem the element to lookup
	 * 
	 * @return 0 if the element is not in the index or has no positions, otherwise
	 *         the number of positions stored for that element
	 */
	public int numPositions(String stem) {
		Function<Map<String, Collection<Integer>>, Integer> positionsInMap = (map) -> {
			return map.size() == 0 ? 0 : 
				map.entrySet().stream()
				.mapToInt(e -> e.getValue().size())
				.reduce(0, Integer::sum);
		};
		return stem != null ? positionsInMap.apply(wordIndex.get(stem)) : 0;
	}
	
	/**
	 * Returns the number of positions for a file path and given stem
	 * 
	 * @param stem the element to lookup
	 * @param filePath where element was found
	 * 
	 * @return 0 if the element is not in the index or has no positions, otherwise
	 *         the number of positions stored for that element
	 */
	@Override
	public int numPositions(String stem, String filePath) {
		return stem != null &&  filePath != null? wordIndex.get(stem).get(filePath).size() : 0;
	}
}