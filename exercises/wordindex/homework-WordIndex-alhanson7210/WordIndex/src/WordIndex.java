import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A special type of {@link Index} that indexes the locations words were found.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author Alex L Hanson
 * @version Spring 2020
 * @param <T> Generic Type
 */
public class WordIndex<T> implements Index<T> {
     // TODO Modify class declaration to implement the Index interface for String elements
     // TODO Modify anything within this class as necessary
	 private final Map<T, Collection<Integer>> wordIndex;
	 
	 /**
	   * Adds the element and position.
	   *
	   * @param element the element found
	   * @param position the position the element was found
	   * @return {@code true} if the index changed as a result of the call
	   */
	  @Override
	  public boolean add(T element, int position) {
		  if (element == null || position < 0) return false;
		  this.wordIndex.putIfAbsent(element, new TreeSet<Integer>());
		  return this.wordIndex.get(element).add(position)? true : false;
	  }

	  /**
	   * Returns the number of positions stored for the given element.
	   *
	   * @param element the element to lookup
	   * @return 0 if the element is not in the index or has no positions, otherwise the number of
	   *         positions stored for that element
	   */
	  @Override
	  public int numPositions(T element) {
		  return contains(element)? this.wordIndex.get(element).size() : 0;
	  }

	  /**
	   * Returns the number of element stored in the index.
	   *
	   * @return 0 if the index is empty, otherwise the number of element in the index
	   */
	  @Override
	  public int numElements() {
		  return this.wordIndex.size();
	  }

	  /**
	   * Determines whether the element is stored in the index.
	   *
	   * @param element the element to lookup
	   * @return {@true} if the element is stored in the index
	   */
	  @Override
	  public boolean contains(T element) {
		  return element != null? this.wordIndex.containsKey(element): false; 
	  }

	  /**
	   * Determines whether the element is stored in the index and the position is stored for that
	   * element.
	   *
	   * @param element the element to lookup
	   * @param position the position of that element to lookup
	   * @return {@true} if the element and position is stored in the index
	   */
	  @Override
	  public boolean contains(T element, int position) {
		  return contains(element)? this.wordIndex.get(element).contains(position) : false;
	  }

	  /**
	   * Returns an unmodifiable view of the elements stored in the index.
	   *
	   * @return an unmodifiable view of the elements stored in the index
	   * @see Collections#unmodifiableCollection(Collection)
	   */
	  @Override
	  public Collection<T> getElements() {
		  return Collections.unmodifiableCollection(this.wordIndex.keySet());
	  }

	  /**
	   * Returns an unmodifiable view of the positions stored in the index for the provided element, or
	   * an empty collection if the element is not in the index.
	   *
	   * @param element the element to lookup
	   * @return an unmodifiable view of the positions stored for the element
	   */
	  @Override
	  public Collection<Integer> getPositions(T element) {
		  return contains(element)? Collections.unmodifiableCollection(this.wordIndex.get(element)) : Collections.emptyList();
	  }

	  /**
	   * A special type of {@link Index} that indexes the locations words were found.
	   * Initialize map for the word index
	   */
	 public WordIndex() {
		 this.wordIndex = new TreeMap<>();
	 }
}
