import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * A thread-safe version of {@link IndexedSet} using a read/write lock.
 *
 * @param <E> element type
 * @see IndexedSet
 * @see SimpleReadWriteLock
 */
public class ThreadSafeIndexedSet<E> extends IndexedSet<E> {

  /** The lock used to protect concurrent access to the underlying set. */
  private SimpleReadWriteLock lock;

  /**
   * Initializes an unsorted thread-safe indexed set.
   */
  public ThreadSafeIndexedSet() {
    // NOTE: DO NOT MODIFY THIS METHOD
    this(false);
  }

  /**
   * Initializes a thread-safe indexed set.
   *
   * @param sorted whether the set should be sorted
   */
  public ThreadSafeIndexedSet(boolean sorted) {
    // NOTE: DO NOT MODIFY THIS METHOD
    super(sorted);
    lock = new SimpleReadWriteLock();
  }

  /**
   * Returns the identity hashcode of the lock object. Not particularly useful.
   * @return the identity hashcode of the lock object
   */
  public int lockCode() {
    // NOTE: DO NOT MODIFY THIS METHOD
    return System.identityHashCode(lock);
  }

  // TODO: OVERRIDE AND IMPLEMENT NECESSARY METHODS
  /**
   * Adds an element to our set.
   *
   * @param element element to add
   * @return true if the element was added (false if it was a duplicate)
   *
   * @see Set#add(Object)
   */
  @Override
  public boolean add(E element) {
	  lock.writeLock().lock();
	  try {
		  return super.add(element);
	  } finally {
		  lock.writeLock().unlock();
	  }
  }
  
  /**
   * Adds the collection of elements to our set.
   *
   * @param elements elements to add
   * @return true if any elements were added (false if were all duplicates)
   *
   * @see Set#addAll(Collection)
   */
  @Override
  public boolean addAll(Collection<E> elements) {
	  lock.writeLock().lock();
	  try {
		  return super.addAll(elements);
	  } finally {
		  lock.writeLock().unlock();
	  }
  }
  
  /**
   * Adds values from one {@link IndexedSet} to another.
   *
   * @param elements elements to add
   * @return true if any elements were added (false if were all duplicates)
   *
   * @see Set#addAll(Collection)
   */
  @Override
  public boolean addAll(IndexedSet<E> elements) {
	  lock.writeLock().lock();
	  try {
		  return super.addAll(elements);
	  } finally {
		  lock.writeLock().unlock();
	  }
  }
  
  /**
   * Returns the number of elements in our set.
   *
   * @return number of elements
   *
   * @see Set#size()
   */
  @Override
  public int size() {
	  lock.readLock().lock();
	  try {
		  return super.size();
	  } finally {
		  lock.readLock().unlock();
	  }
  }
  
  /**
   * Returns whether the element is contained in our set.
   *
   * @param element element to search for
   * @return true if the element is contained in our set
   *
   * @see Set#contains(Object)
   */
  @Override
  public boolean contains(E element) {
	  lock.readLock().lock();
	  try {
		  return super.contains(element);
	  } finally {
		  lock.readLock().unlock();
	  }
  }
  
  /**
   * Gets the element at the specified index based on iteration order. The element at this index may
   * change over time as new elements are added.
   *
   * @param index index of element to get
   * @return element at the specified index or null of the index was invalid
   */
  @Override
  public E get(int index) {
	  lock.writeLock().lock();
	  try {
		  return super.get(index);
	  } finally {
		  lock.writeLock().unlock();
	  }
  }
  
  @Override
  public String toString() {
	  lock.readLock().lock();
	  try {
		  return super.toString();	  
	  } finally {
		  lock.readLock().unlock();
	  }
  }
  
  /**
   * Returns an unsorted copy of this set.
   *
   * @return unsorted copy
   *
   * @see HashSet#HashSet(Collection)
   */
  @Override
  public IndexedSet<E> unsortedCopy() {
	  lock.readLock().lock();
	  try {
		  return super.unsortedCopy();	  
	  } finally {
		  lock.readLock().unlock();
	  }
  }
  
  /**
   * Returns a sorted copy of this set.
   *
   * @return sorted copy
   *
   * @see TreeSet#TreeSet(Collection)
   */
  @Override
  public IndexedSet<E> sortedCopy() {
	  lock.readLock().lock();
	  try {
		  return super.sortedCopy();	  
	  } finally {
		  lock.readLock().unlock();
	  }
  }
}
