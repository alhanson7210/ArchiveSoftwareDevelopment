import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Tests the {@link WordIndex} class.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
public class WordIndexTest {

  /** Placeholder for index object being tested. */
  private Index<String> index;

  /**
   * Tests of an empty index.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class EmptyIndexTests {

    /**
     * Creates an empty index before each test.
     */
    @BeforeEach
    public void createEmpty() {
      /*
       * DO NOT CHANGE THIS! If you are getting a compile error, then you have not implemented the
       * Index interface properly yet.
       */
      index = new WordIndex<>();
    }

    /**
     * Tests that there are no elements.
     */
    @Test
    @Order(1)
    public void testNumElements() {
      Assertions.assertEquals(0, index.numElements());
    }

    /**
     * Tests that there are no positions for an element not in our index.
     */
    @Test
    @Order(2)
    public void testNumPositions() {
      Assertions.assertEquals(0, index.numPositions("empty"));
    }

    /**
     * Tests that element does not exist as expected.
     */
    @Test
    @Order(3)
    public void testContainsWord() {
      Assertions.assertFalse(index.contains("empty"));
    }

    /**
     * Tests that position does not exist as expected.
     */
    @Test
    @Order(4)
    public void testContainsPosition() {
      Assertions.assertFalse(index.contains("empty", 42));
    }

    /**
     * Tests that no elements are fetched as expected.
     */
    @Test
    @Order(5)
    public void testGetElements() {
      Assertions.assertTrue(index.getElements().isEmpty());
    }

    /**
     * Tests that no positions are fetched as expected.
     */
    @Test
    @Order(6)
    public void testGetPositions() {
      Assertions.assertTrue(index.getPositions("empty").isEmpty());
    }
  }

  /**
   * Tests of an index with a single initial element.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class SingleAddTests {

    /**
     * Creates an index with a single element before each test.
     */
    @BeforeEach
    public void createEmpty() {
      index = new WordIndex<>();
      index.add("answer", 42);
    }

    /**
     * Test number of elements.
     */
    @Test
    @Order(1)
    public void testNumElements() {
      Assertions.assertEquals(1, index.numElements());
    }

    /**
     * Tests number of positions for element.
     */
    @Test
    @Order(2)
    public void testNumPositions() {
      Assertions.assertEquals(1, index.numPositions("answer"));
    }

    /**
     * Tests element exists in index.
     */
    @Test
    @Order(3)
    public void testContainsWord() {
      Assertions.assertTrue(index.contains("answer"));
    }

    /**
     * Tests position does NOT exist for element.
     */
    @Test
    @Order(4)
    public void testContainsPositionFalse() {
      Assertions.assertFalse(index.contains("answer", 0));
    }

    /**
     * Tests position DOES exist for element.
     */
    @Test
    @Order(5)
    public void testContainsPositionTrue() {
      Assertions.assertTrue(index.contains("answer", 42));
    }

    /**
     * Tests element is fetched properly.
     */
    @Test
    @Order(6)
    public void testGetElements() {
      Assertions.assertTrue(index.getElements().contains("answer"));
    }

    /**
     * Tests position is fetched properly.
     */
    @Test
    @Order(7)
    public void testGetPositions() {
      Assertions.assertTrue(index.getPositions("answer").contains(42));
    }

    /**
     * Tests size of elements fetched.
     */
    @Test
    @Order(8)
    public void testGetElementsSize() {
      Assertions.assertEquals(1, index.getElements().size());
    }

    /**
     * Tests size of positions fetched.
     */
    @Test
    @Order(9)
    public void testGetPositionsSize() {
      Assertions.assertEquals(1, index.getPositions("answer").size());
    }

    /**
     * Tests adding same element/position pair twice has no impact.
     */
    @Test
    @Order(10)
    public void testDoubleAdd() {
      Assertions.assertFalse(index.add("answer", 42));
    }

    /**
     * Tests adding new position for element.
     */
    @Test
    @Order(11)
    public void testAddNewPosition() {
      Assertions.assertTrue(index.add("answer", 12));
    }

    /**
     * Tests adding new element.
     */
    @Test
    @Order(12)
    public void testAddNewElement() {
      Assertions.assertTrue(index.add("question", 42));
    }

    /**
     * Tests adding array of one element.
     */
    @Test
    @Order(13)
    public void testAddAllFalse() {
      Assertions.assertFalse(index.addAll(new String[] {"answer"}, 42));
    }

    /**
     * Tests adding array with two elements.
     */
    @Test
    @Order(14)
    public void testAddAllTrue() {
      Assertions.assertTrue(index.addAll(new String[] {"question", "answer"}, 41));
    }

    /**
     * Tests that attempts to modify elements in index fails.
     */
    @Test
    @Order(15)
    public void testElementsModification() {
      Collection<String> elements = index.getElements();

      try {
        elements.clear();
      } catch (Exception e) {
        System.err.println("Unable to modify elements.");
      }

      Assertions.assertTrue(index.contains("answer"));
    }

    /**
     * Tests that attempts to modify positions in index fails.
     */
    @Test
    @Order(16)
    public void testPositionsModification() {
      Collection<Integer> positions = index.getPositions("answer");

      try {
        positions.clear();
      } catch (Exception e) {
        System.err.println("Unable to modify positions.");
      }

      Assertions.assertTrue(index.contains("answer", 42));
    }
  }
}
