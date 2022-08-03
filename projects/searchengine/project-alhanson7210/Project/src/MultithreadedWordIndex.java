import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * A special type of {@link WordIndex} that indexes the locations from a file to the
 * stems found in a multithreaded fashion.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex L Hanson
 * @version Spring 2020
 */
public class MultithreadedWordIndex extends WordIndex {
	
	/** lock for wordIndex that stores all collected stems */
	public ReadWriteLock indexLock;
	/** lock for wordIndex that stores all collected stems */
	public ReadWriteLock countLock;
	/** lock for wordIndex that stores all collected stems */
	public ReadWriteLock queryLock;
	/** multithreading default */
	public static final int MULTITHREAD_DEFAULT = 5;
	/** single thread default */
	public static final int SINGLE_THREAD_DEFAULT = 1;
	/**
	 * Constructor for threads
	 */
	public MultithreadedWordIndex() {
		super();
		this.indexLock = new ReadWriteLock();
		this.countLock = new ReadWriteLock();
		this.queryLock = new ReadWriteLock();
		
	}
	
	/*
	 * Finds all exact matches and generates a list of search results
	 * 
	 * @param queryWords list of unique query searches
	 * 
	 * @return a sorted list of comparable search results
	 */
	@Override
	public List<ComparableSearchResult> exactSearch(List<String> queryWords) {
		indexLock.readLock().lock();
		try {
			return super.exactSearch(queryWords);
		} finally {
			indexLock.readLock().unlock();
		}
	}
	
	/**
	 * Finds all partial matches and generates a list of search results
	 * 
	 * @param queryWords list of unique query searches
	 * 
	 * @return a sorted list of comparable search results
	 */
	@Override
	public List<ComparableSearchResult> partialSearch(List<String> queryWords) {
		indexLock.readLock().lock();
		try {
			return super.partialSearch(queryWords);
		} finally {
			indexLock.readLock().unlock();
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
		//Adds the element and position from a given file.
		indexLock.writeLock().lock();
		try {
			return super.add(stem, filePath, position);
		} finally {
			indexLock.writeLock().unlock();
		}
	}
	
	/**
	 * Add an inverted index to this multithreaded one
	 * @param correctedFilePath file path to add stems to in the inverted index
	 * @param local another inverted index
	 * @return if add all from the local word index
	 */
	public boolean addAll(WordIndex local, String correctedFilePath) {
		indexLock.writeLock().lock();
		try {
			return super.addAll(local, correctedFilePath);
		} finally {
			indexLock.writeLock().unlock();
		}
	}
	
	/**
	 * Adds the completed file count to the word indexes wordCount
	 * 
	 * @param correctedPath file path to add fileCount to if needed
	 * @param count number of words in file
	 */
	@Override
	public final void addFileCount(String correctedPath, int count) {
		//Adds the completed file count
		countLock.writeLock().lock();
		try {
			super.addFileCount(correctedPath, count);
		} finally {
			countLock.writeLock().unlock();
		}
	}
	
	/**
	 * Add query listing to queries map
	 * @param queryString words to be found in the word index
	 * @param queryResults list of search results found
	 */
	@Override
	public void addQueryListing(String queryString, List<ComparableSearchResult> queryResults) {
		queryLock.writeLock().lock();
		try {
			super.addQueryListing(queryString, queryResults);
		} finally {
			queryLock.writeLock().unlock();
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
		indexLock.readLock().lock();
		try {
			return super.contains(stem);
		} finally {
			indexLock.readLock().unlock();
		}
	}

	/**
	 * Determines whether the stem and filePath is stored in the index.
	 * 
	 * @param stem     word stem from a file
	 * @param filePath path to which the stem came from
	 * 
	 * @return true if the word index contains the filePath
	 */
	@Override
	public boolean contains(String stem, String filePath) {
		indexLock.readLock().lock();
		try {
			return super.contains(stem, filePath);
		} finally {
			indexLock.readLock().unlock();
		}
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
		indexLock.readLock().lock();
		try {
			return super.contains(stem, filePath, position);
		} finally {
			indexLock.readLock().unlock();
		}
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
		indexLock.readLock().lock();
		try {
			return super.getElements();
		} finally {
			indexLock.readLock().unlock();
		}
	}
	
	/**
	 * Gets the file count of a correctedPath
	 * 
	 * @param correctedPath file path to add fileCount to if needed
	 * @return file count if word count contains the the path or 0
	 */
	public int getFileCount(String correctedPath) {
		countLock.readLock().lock();
		try {
			return super.getFileCount(correctedPath);
		} finally {
			countLock.readLock().unlock();
		}
	}
	
	/**
	 * Set of file path entries within index for a given stem
	 * @param stem the element found
	 * @return a set of file path entries for a stem
	 */
	@Override
	public Set<Map.Entry<String,Collection<Integer>>> getFilePathEntries(String stem) {
		indexLock.readLock().lock();
		try {
			return super.getFilePathEntries(stem);
		} finally {
			indexLock.readLock().unlock();
		}
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
		indexLock.readLock().lock();
		try {
			return super.getPositions(stem, filePath);
		} finally {
			indexLock.readLock().unlock();
		}
	}

	/**
	 * Something to see how to handle the word index better in the driver
	 * 
	 * @return wordIndex as a collection of entries
	 */
	@Override
	public Collection<Entry<String, Map<String, Collection<Integer>>>> getUnmodifiableElements() {
		indexLock.readLock().lock();
		try {
			return super.getUnmodifiableElements();
		} finally {
			indexLock.readLock().unlock();
		}
	}
	
	/**
	 * unmodifiable file count map
	 * @return an unmodifiable file count map to write as json later
	 */
	@Override
	public Map<String, Integer> getUnmodifiableFileCount() {
		countLock.readLock().lock();
		try {
			return super.getUnmodifiableFileCount();
		} finally {
			countLock.readLock().unlock();
		}
	}
	
	/**
	 * Unmodifiable file count map
	 * @return an unmodifiable file count map to to write as json later
	 */
	@Override
	public Map<String, Map<String, Collection<Integer>>> getUnmodifiableMap() {
		indexLock.readLock().lock();
		try {
			return super.getUnmodifiableMap();
		} finally {
			indexLock.readLock().unlock();
		}
	}
	
	/**
	 * Query elements found in search
	 * @return Unmodifiable Query Elements
	 */
	@Override
	public Collection<Entry<String, List<ComparableSearchResult>>> getUnmodifiableQueryElements() {
		queryLock.readLock().lock();
		try {
			return super.getUnmodifiableQueryElements();
		} finally {
			queryLock.readLock().unlock();
		}
	}

	/**
	 * Returns the number of stems stored in the index.
	 *
	 * @return 0 if the index is empty, otherwise the number of element in the index
	 */
	@Override
	public int numElements() {
		indexLock.readLock().lock();
		try {
			return super.numElements();
		} finally {
			indexLock.readLock().unlock();
		}
	}
	
	/**
	 * Returns the number of positions stored for the given element.
	 *
	 * @param stem the element to lookup
	 * 
	 * @return 0 if the element is not in the index or has no positions, otherwise
	 *         the number of positions stored for that element
	 */
	@Override
	public int numPositions(String stem) {
		indexLock.readLock().lock();
		try {
			return super.numPositions(stem);
		} finally {
			indexLock.readLock().unlock();
		}
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
		indexLock.readLock().lock();
		try {
			return super.numPositions(stem, filePath);
		} finally {
			indexLock.readLock().unlock();
		}
	}
}
