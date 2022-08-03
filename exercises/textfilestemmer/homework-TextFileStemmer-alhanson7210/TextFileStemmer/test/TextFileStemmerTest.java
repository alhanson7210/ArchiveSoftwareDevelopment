import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Tests of the {@link TextFileStemmer} class.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 *
 * @see TextFileStemmer
 */
@TestMethodOrder(Alphanumeric.class)
public class TextFileStemmerTest {

  // Right-click a nested class to run the tests in that nested class only.

  /**
   * Collection of tests.
   *
   * @see TextFileStemmer#listStems(String)
   * @see TextFileStemmer#listStems(String, opennlp.tools.stemmer.Stemmer)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class A_ListStemsTests {

    /**
     * Tests expected output for given test case.
     *
     * @param line the line to stem
     * @param output the expected output
     */
    public void test(String line, String[] output) {
      List<String> expected = Arrays.stream(output).collect(Collectors.toList());
      List<String> actual = TextFileStemmer.listStems(line);

      assertEquals(expected, actual);
    }

    // Test cases from: http://snowballstem.org/algorithms/english/stemmer.html
    // Right-click individual test methods to run only that test.

    /**
     * Runs a single test case.
     */
    @Test
    @Order(1)
    public void testOneWord() {
      String line = "conspicuously";
      String[] output = {"conspicu"};
      test(line, output);
    }

    /**
     * Runs a single test case.
     */
    @Test
    @Order(2)
    public void testEmpty() {
      test("", new String[] {});
    }

    /**
     * Runs a single test case.
     */
    @Test
    @Order(3)
    public void testGroupOne() {
      String[] input = {"consign", "consigned", "consigning", "consignment", "consist", "consisted",
          "consistency", "consistent", "consistently", "consisting", "consists", "consolation",
          "consolations", "consolatory", "console", "consoled", "consoles", "consolidate",
          "consolidated", "consolidating", "consoling", "consolingly", "consols", "consonant",
          "consort", "consorted", "consorting", "conspicuous", "conspicuously", "conspiracy",
          "conspirator", "conspirators", "conspire", "conspired", "conspiring", "constable",
          "constables", "constance", "constancy", "constant"};

      String[] output = {"consign", "consign", "consign", "consign", "consist", "consist",
          "consist", "consist", "consist", "consist", "consist", "consol", "consol", "consolatori",
          "consol", "consol", "consol", "consolid", "consolid", "consolid", "consol", "consol",
          "consol", "conson", "consort", "consort", "consort", "conspicu", "conspicu", "conspiraci",
          "conspir", "conspir", "conspir", "conspir", "conspir", "constabl", "constabl", "constanc",
          "constanc", "constant"};

      String line = String.join(", ", input);
      test(line, output);
    }

    /**
     * Runs a single test case.
     */
    @Test
    @Order(4)
    public void testGroupTwo() {
      String[] input = {"KNACK", "KNACKERIES", "KNACKS", "KNAG", "KNAVE", "KNAVES", "KNAVISH",
          "KNEADED", "KNEADING", "KNEE", "KNEEL", "KNEELED", "KNEELING", "KNEELS", "KNEES", "KNELL",
          "KNELT", "KNEW", "KNICK", "KNIF", "KNIFE", "KNIGHT", "KNIGHTLY", "KNIGHTS", "KNIT",
          "KNITS", "KNITTED", "KNITTING", "KNIVES", "KNOB", "KNOBS", "KNOCK", "KNOCKED", "KNOCKER",
          "KNOCKERS", "KNOCKING", "KNOCKS", "KNOPP", "KNOT", "KNOTS"};

      String[] output = {"knack", "knackeri", "knack", "knag", "knave", "knave", "knavish", "knead",
          "knead", "knee", "kneel", "kneel", "kneel", "kneel", "knee", "knell", "knelt", "knew",
          "knick", "knif", "knife", "knight", "knight", "knight", "knit", "knit", "knit", "knit",
          "knive", "knob", "knob", "knock", "knock", "knocker", "knocker", "knock", "knock",
          "knopp", "knot", "knot"};

      String line = String.join(" **** ", input);
      test(line, output);
    }
  }

  /**
   * Collection of tests.
   *
   * @see TextFileStemmer#uniqueStems(String)
   * @see TextFileStemmer#uniqueStems(String, opennlp.tools.stemmer.Stemmer)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class B_UniqueStemsTests extends A_ListStemsTests {
    @Override
    public void test(String line, String[] output) {
      Set<String> expected = Arrays.stream(output).collect(Collectors.toSet());
      Set<String> actual = TextFileStemmer.uniqueStems(line);

      assertEquals(expected, actual);
    }
  }

  /**
   * Collection of tests.
   *
   * @see TextFileStemmer#listStems(Path)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class C_ListStemFileTests {

    /**
     * Tests expected output for given test case.
     *
     * @param path the file path to stem
     * @param output the expected output
     * @throws IOException if I/O error occurs
     */
    public void test(Path path, String[] output) throws IOException {
      List<String> expected = Arrays.stream(output).collect(Collectors.toList());
      List<String> actual = TextFileStemmer.listStems(path);

      assertEquals(expected, actual);
    }

    /**
     * Runs a single test case.
     * @throws IOException if I/O error occurs
     */
    @Test
    @Order(1)
    public void testWords() throws IOException {
      Path path = Path.of("test", "words.tExT");
      String[] output = {"observa", "observ", "observacion", "observ", "observ", "observ", "observ",
          "observ", "observ", "observ", "observ", "observ", "observ", "perfor", "perfor", "perforc",
          "perform", "perform", "perform", "perform", "perform", "perform", "perform", "perform"};
      test(path, output);
    }

    /**
     * Runs a single test case.
     * @throws IOException if I/O error occurs
     */
    @Test
    @Order(2)
    public void testSymbols() throws IOException {
      Path path = Path.of("test", "symbols.txt");
      String[] output = {"antelop", "antelop", "antelop", "antelop", "antelop", "antelop",
          "antelop", "antelop", "antelop", "antelop"};
      test(path, output);
    }

    /**
     * Runs a single test case.
     * @throws IOException if I/O error occurs
     */
    @Test
    @Order(3)
    public void testAnimals() throws IOException {
      Path path = Path.of("test", "animals.text");
      String[] output = {"okapi", "okapi", "mongoos", "lori", "lori", "lori", "axolotl", "narwhal",
          "platypus", "echidna", "tarsier"};
      test(path, output);
    }

    /**
     * Runs a single test case.
     * @throws IOException if I/O error occurs
     */
    @Test
    @Order(4)
    public void testStemmer() throws IOException {
      Path input = Path.of("test", "voc.txt");
      Path output = Path.of("test", "output.txt");

      String[] expected = TextParser.parse(Files.readString(output, StandardCharsets.UTF_8));
      test(input, expected);
    }
  }

  /**
   * Collection of tests.
   *
   * @see TextFileStemmer#uniqueStems(Path)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class D_UniqueStemFileTests extends C_ListStemFileTests {

    @Override
    public void test(Path path, String[] output) throws IOException {
      Set<String> expected = Arrays.stream(output).collect(Collectors.toSet());
      Set<String> actual = TextFileStemmer.uniqueStems(path);

      assertEquals(expected, actual);
    }
  }
}
