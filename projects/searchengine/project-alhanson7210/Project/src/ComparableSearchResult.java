import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Create a class that stores a single search result, 
 * including the location, total word count of the 
 * location, and number of times the query occurs at 
 * that location.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex L Hanson
 * @version Spring 2020
 */
public class ComparableSearchResult implements Comparable<ComparableSearchResult> {

	/** location where the query word(s) where found */
	private final String where;
	
	/** total matches */
	private Integer count;
	
	/** total matches divided by the total words in file(where) */
	private Double score;
	
	/**
	 * Constructor for search result
	 * 
	 * @param where location where the query word(s) where found
	 * @param count total matches
	 * @param score total matches divided by the total words in file(where)
	 */
	public ComparableSearchResult(String where, Integer count, Double score) {
		this.where = where;
		this.count = count;
		this.score = score;
	}
	
	/**
	 * Adds a search result to a map of matches
	 * @param index the word index
	 * @param queryWord query from list of unique query searches
	 * @param matches map of matches found in file(s)
	 * 
	 */
	public static void addSearchResults(WordIndex index, String queryWord, HashMap<String, ComparableSearchResult> matches) {
		//entry set of all indexes in inverted index
		Map<String, Map<String, Collection<Integer>>> map = index.getUnmodifiableMap();
		Map<String, Integer> fileCounts = index.getUnmodifiableFileCount();
		Set<String> filePathKeys = map.get(queryWord).keySet();
		//loop through the indexes
		for (String filePathKey : filePathKeys) {
			//default starting value
			ComparableSearchResult result = new ComparableSearchResult(filePathKey, 0, 0.0);
			//create new comparable search result if needed
			matches.putIfAbsent(filePathKey, result);
			//add count of file positions for the given
			result = matches.get(filePathKey);
			//number of positions in file for a given stem
			int positions = map.get(queryWord).get(filePathKey).size();
			//number of words in file
			int count = fileCounts.getOrDefault(filePathKey, 1);
			//add positions to result and set new adjusted score
			result.addCount(positions, count);
		}
	}
	
	/**
	 * Helper: Sort collection view of values from a map of comparable search results
	 * 
	 * @param values are all ComparableSearchResult from a results mapping
	 * 
	 * @return sorted results 
	 */
	public static List<ComparableSearchResult> resultSorter(Collection<ComparableSearchResult> values) {
		//create a list //add all values
		List<ComparableSearchResult> results = new ArrayList<>(values);
		//sort the list
		Collections.sort(results);
		//return results
		return results;
	}
	
	@Override
	public int compareTo(ComparableSearchResult o) {
		//compare results
		int value;
		int score = Double.compare(this.score, o.getScore());
		int count = Integer.compare(this.count, o.getCount());
        if (score != 0) {
            //making this less zero achieves the effect making list being sorted in Descending order
            value = compareScore(o);
        }
        //count based comparison
        else if (count != 0) {
            //making this less zero achieves the effect making list being sorted in Descending order
            value = compareCount(o);
        }
        //just return the string comparison at this point
        else {
            value = compareLocation(o);
        }
        return value;
	}
	
	/**
	 * compare the score 
	 * @param o other ComparableSearchResult
	 * @return {@code 1} if this score should be prioritized, otherwise -1
	 */
	private int compareScore(ComparableSearchResult o) {
		return Double.compare(this.getScore(), o.getScore()) < 0? 1 : -1;
	}
	
	/**
	 * compare the count
	 * @param o other ComparableSearchResult
	 * @return {@code 1} if this count should be prioritized, otherwise -1
	 */
	private int compareCount(ComparableSearchResult o) {
		return Integer.compare(this.getCount(), o.getCount()) < 0? 1 : -1;
	}
	
	/**
	 * compare the location
	 * @param o other ComparableSearchResult
	 * @return string comparison
	 */
	private int compareLocation(ComparableSearchResult o) {
		return this.where.compareToIgnoreCase(o.getWhere());
	}
	
	/**
	 * get location
	 * @return the where
	 */
	public String getWhere() {
		return where;
	}

	/**
	 * get count
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * get score
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * Set score
	 * @param s new score to be set
	 */
	private void setScore(Double s) {
		this.score = s;
	}
	
	/**
	 * Adds another value to count;
	 * 
	 * @param totalWords file count 
	 */
	public void addCount(Integer totalWords) {
		this.count++;
		setScore(Double.valueOf(this.count)/Double.valueOf(totalWords));
	}
	
	/**
	 * Adds another value to count
	 * 
	 * @param positions amount of positions in file
	 * @param totalWords file count
	 */
	public void addCount(Integer positions, Integer totalWords) {
		this.count += positions;
		setScore(Double.valueOf(this.count)/Double.valueOf(totalWords));
	}
}