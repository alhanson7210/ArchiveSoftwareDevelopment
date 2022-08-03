import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the {@link SimpleJsonWriter} class.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
@TestMethodOrder(Alphanumeric.class)
public class SimpleJsonWriterTest {

  /** Location of actual output produced by running tests. */
  public static final Path ACTUAL_DIR = Path.of("out");

  /** Location of expected output. */
  public static final Path EXPECTED_DIR = Path.of("test");

  /**
   * Makes sure the actual output directory exists.
   *
   * @throws IOException if an I/O error occurs
   */
  @BeforeAll
  public static void setupEnvironment() throws IOException {
    Files.createDirectories(ACTUAL_DIR);
  }

  /**
   * Compares two files as String objects. Strips any trailing whitespace (including newlines)
   * before comparing.
   *
   * @param actualPath the path to the first file (the actual output)
   * @param expectPath the path to the second file (the expected output)
   *
   * @throws IOException if an I/O error occurs
   */
  public static void compareFiles(Path actualPath, Path expectPath) throws IOException {
    String actual = Files.readString(actualPath, StandardCharsets.UTF_8).stripTrailing();
    String expect = Files.readString(expectPath, StandardCharsets.UTF_8).stripTrailing();

    String debug = String.format("%nCompare %s and %s for differences.%n", actualPath, expectPath);
    assertEquals(expect, actual, () -> debug);
  }

  /**
   * Tests the {@link SimpleJsonWriter#asArray(Collection, Path)} method.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class A_ArrayTests {

    /**
     * Helper method for running tests in this nested class.
     *
     * @param elements the elements to write to JSON file
     * @param name the name of the expected output file
     *
     * @throws IOException if an I/O error occurs
     */
    public void runTest(Collection<Integer> elements, String name) throws IOException {
      Path actualPath = ACTUAL_DIR.resolve(name);
      Path expectPath = EXPECTED_DIR.resolve(name);
      Files.deleteIfExists(actualPath);

      SimpleJsonWriter.asArray(elements, actualPath);
      compareFiles(actualPath, expectPath);
    }

    /**
     * Tests the output of an empty set.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(1)
    @Test
    public void testEmptySet() throws IOException {
      String name = "json-array-empty.txt";
      runTest(new LinkedList<Integer>(), name);
    }

    /**
     * Tests the output of a set with a single element.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(2)
    @Test
    public void testSingleSet() throws IOException {
      String name = "json-array-single.txt";

      HashSet<Integer> elements = new HashSet<>();
      elements.add(42);

      runTest(elements, name);
    }

    /**
     * Tests the output of a simple set with several elements.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(3)
    @Test
    public void testSimpleSet() throws IOException {
      String name = "json-array-simple.txt";

      TreeSet<Integer> elements = new TreeSet<>();
      Collections.addAll(elements, 1, 2, 3, 4);

      runTest(elements, name);
    }

    /**
     * Tests the output of a simple list with several elements.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(4)
    @Test
    public void testSimpleList() throws IOException {
      String name = "json-array-simple.txt";

      List<Integer> elements = new ArrayList<>();
      Collections.addAll(elements, 1, 2, 3, 4);

      runTest(elements, name);
    }
  }

  /**
   * Tests the {@link SimpleJsonWriter#asObject(java.util.Map, Path)} method.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class B_ObjectTests {

    /**
     * Helper method for running tests in this nested class.
     *
     * @param elements the elements to write to JSON file
     * @param name the name of the expected output file
     *
     * @throws IOException if an I/O error occurs
     */
    public void runTest(Map<String, Integer> elements, String name) throws IOException {
      Path actualPath = ACTUAL_DIR.resolve(name);
      Path expectPath = EXPECTED_DIR.resolve(name);
      Files.deleteIfExists(actualPath);

      SimpleJsonWriter.asObject(elements, actualPath);
      compareFiles(actualPath, expectPath);
    }

    /**
     * Tests an empty map.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(1)
    @Test
    public void testEmptyMap() throws IOException {
      String name = "json-object-empty.txt";
      runTest(new TreeMap<String, Integer>(), name);
    }

    /**
     * Tests a map with one key/value pair.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(2)
    @Test
    public void testSinglePair() throws IOException {
      String name = "json-object-single.txt";

      HashMap<String, Integer> elements = new HashMap<>();
      elements.put("The Answer", 42);

      runTest(elements, name);
    }

    /**
     * Tests a map with several key/value pairs.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(3)
    @Test
    public void testSimpleMap() throws IOException {
      String name = "json-object-simple.txt";

      TreeMap<String, Integer> elements = new TreeMap<>();
      elements.put("a", 4);
      elements.put("b", 3);
      elements.put("c", 2);
      elements.put("d", 1);

      runTest(elements, name);
    }
  }

  /**
   * Tests the {@link SimpleJsonWriter#asNestedArray(java.util.Map, Path)} method.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class C_NestedObjectTests {

    /**
     * Helper method for running tests in this nested class.
     *
     * @param elements the elements to write to JSON file
     * @param name the name of the expected output file
     *
     * @throws IOException if an I/O error occurs
     */
    public void runTest(Map<String, ? extends Collection<Integer>> elements, String name)
        throws IOException {
      Path actualPath = ACTUAL_DIR.resolve(name);
      Path expectPath = EXPECTED_DIR.resolve(name);
      Files.deleteIfExists(actualPath);

      SimpleJsonWriter.asNestedArray(elements, actualPath);
      compareFiles(actualPath, expectPath);
    }

    /**
     * Tests an empty nested map.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(1)
    @Test
    public void testEmptyMap() throws IOException {
      String name = "json-object-empty.txt";
      runTest(new TreeMap<String, ArrayList<Integer>>(), name);
    }

    /**
     * Tests a nested map with a single entry.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(2)
    @Test
    public void testSingleEntry() throws IOException {
      String name = "json-nested-single.txt";

      HashMap<String, HashSet<Integer>> elements = new HashMap<>();
      elements.put("The Answer", new HashSet<>());
      elements.get("The Answer").add(42);

      runTest(elements, name);
    }

    /**
     * Tests a nested map with several entries.
     *
     * @throws IOException if an I/O error occurs
     */
    @Order(3)
    @Test
    public void testSimpleTree() throws IOException {
      String name = "json-nested-simple.txt";

      TreeMap<String, TreeSet<Integer>> elements = new TreeMap<>();
      elements.put("a", new TreeSet<>());
      elements.put("b", new TreeSet<>());
      elements.put("c", new TreeSet<>());

      elements.get("a").add(1);
      elements.get("b").add(2);
      elements.get("b").add(3);
      elements.get("b").add(4);

      runTest(elements, name);
    }
  }

  /**
   * Imperfect tests to try and determine if the approach has any issues.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class D_ApproachTests {

    /** Source code loaded as a String object. */
    private String source;

    /**
     * Sets up the parser object with a single test case.
     *
     * @throws IOException if an I/O error occurs
     */
    @BeforeEach
    public void setup() throws IOException {
      String name = SimpleJsonWriter.class.getSimpleName() + ".java";
      System.out.println(name);
      Path path = Path.of(".", "src", name);

      assertTrue(Files.isReadable(path), "Unable to access source code.");

      this.source = Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Tests that various methods do NOT appear in the source code.
     *
     * @param method the unauthorized method
     */
    @ParameterizedTest(name = "[{index}: \"{0}\"]")
    @ValueSource(strings = {"replace", "replaceAll", "replaceFirst", "split", "join"})
    public void testInvalidMethods(String method) {
      String regex = "\\." + method + "\\(";
      long count = Pattern.compile(regex).matcher(source).results().count();

      assertTrue(count < 1, "Found " + count + " calls of " + method + " in source code.");
    }
  }
}
