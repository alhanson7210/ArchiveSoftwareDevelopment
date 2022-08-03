import java.util.Collection;
import java.util.Collections;

/**
 * An index to store elements and the locations those elements were found.
 *
 * @param <T> the type of element to store
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public interface Index<T> {
	
  /**
   * Adds the element and position.
   *
   * @param filePath where element was found
   * @param stem the element found
   * @param position the position the element was found
   * @return {@code true} if the index changed as a result of the call
   */
  public boolean add(T stem, String filePath, int position);
  
  /**
   * Attempts to add all positions of a stem from a given file path
   * 
   * @param stems list of the elements found
   * @param filePath file path to which the stem was found in
   * @param start position to add a list of stems by
   * @return boolean if all were added
   */
  public default boolean addAll(T[] stems, String filePath, int start) {
	//checks for null return from index and checks validity of the start position
	  if (stems == null || filePath == null || start < 0) {
		  return false;
	  }
	//attempts to add all positions from a given start position in file
	  int s = start;
	  for (T stem : stems) {
		//stops if a failure occurs
		  if (!add(stem, filePath, s++)) {
			  return false;
		  }
	  }
	  //all were added
	  return true;
  }
  
  /**
   * Attempts to add all positions of a stem from a given file path
   * 
   * @param stem the element found
   * @param filePath file path to which the stem was found in
   * @param positions list of the positions found for a stem
   * @return boolean if all were added
   */
  public default boolean addAll(T stem, String filePath, Collection<Integer> positions) {
	  //checks for null return from index and checks validity of positions
	  if (stem == null || filePath == null || positions == null) {
		  return false;
	  }
	  //attempts to add all positions
	  for (Integer position: positions) {
		  //stops if a failure occurs
		  if (!add(stem, filePath, position)) {
			  return false;
		  }
	  }
	  //added all elements
	  return true;
  }

  /**
   * Adds the elements in order with starting position 1.
   *
   * @param elements the element to add
   * @param filePath file path to which the stem was found in
   * @return {@code true} if the index changed as a result of the call
   *
   * @see #addAll(T[], String, int)
   */
  public default boolean addAll(T[] elements, String filePath) {
	//this changes to mirror what's needed for the inverted Index
    return addAll(elements, filePath, 1);
  };

  /**
   * Returns the number of positions stored for the given element.
   *
   * @param element the element to lookup
   * @param filePath path to which the stem came from
   * 
   * @return 0 if the element is not in the index or has no positions, otherwise the number of
   *         positions stored for that element
   */
  public int numPositions(T element, String filePath);

  /**
   * Returns the number of element stored in the index.
   *
   * @return 0 if the index is empty, otherwise the number of element in the index
   */
  public int numElements();

  /**
   * Determines whether the element is stored in the index.
   *
   * @param stem the element to lookup
   * @return {@true} if the element is stored in the index
   */
  public boolean contains(T stem);
  
  /**
   * Determines whether the element and file path are stored in the index.
   * 
   * @param stem word stem from a file
   * @param filePath path to which the stem came from
   * @return true if the word index contains the filePath
   */
  public boolean contains(T stem, String filePath);
  
  /**
   * Determines whether the element is stored in the index and the position is stored for that
   * element.
   *
   * @param stem the element to lookup
   * @param position the position of that element to lookup
   * @param filePath file path to which the stem was found in
   * @return {@true} if the element and position is stored in the index
   */
  public boolean contains(T stem, String filePath, int position);

  /**
   * Returns an unmodifiable view of the elements stored in the index.
   *
   * @return an unmodifiable view of the elements stored in the index
   * @see Collections#unmodifiableCollection(Collection)
   */
  public Collection<T> getElements();
  
  /**
   * Returns an unmodifiable view of the positions stored in the index for the provided element, or
   * an empty collection if the element is not in the index.
   *
   * @param stem the element to lookup
   * @param filePath file path to which the stem was found in
   * @return an unmodifiable view of the positions stored for the element
   */
  public Collection<Integer> getPositions(T stem, String filePath);

}