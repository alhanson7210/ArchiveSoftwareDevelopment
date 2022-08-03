import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the {@link TextFileFinder} class.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2020
 */
@TestMethodOrder(Alphanumeric.class)
public class TextFileFinderTest {

  /** Path to directory of text files */
  public static final Path root = Path.of(".", "text", "simple").normalize();

  /**
   * Runs before any tests to make sure environment is setup.
   */
  @BeforeAll
  public static void checkEnvironment() {
    Assumptions.assumeTrue(Files.isDirectory(root));
    Assumptions.assumeTrue(Files.exists(root.resolve("hello.txt")));
  }

  /**
   * Tests that text extensions are detected properly.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class A_TextExtensionTests {

    /**
     * Tests files that SHOULD be considered text files.
     *
     * @param file the file name
     */
    @Order(1)
    @ParameterizedTest
    @ValueSource(strings = {"animals_copy.text", "capital_extension.TXT", "empty.txt",
        "position.teXt", "words.tExT"})
    public void testIsTextFile(String file) {
      Path path = root.resolve(file);
      Assertions.assertTrue(TextFileFinder.isText.test(path));
    }

    /**
     * Tests files that SHOULD NOT be considered text files.
     *
     * @param file the file name
     */
    @Order(2)
    @ParameterizedTest
    @ValueSource(strings = {"double_extension.txt.html", "no_extension", "wrong_extension.html",
        "dir.txt", "nowhere.txt"})
    public void testIsNotTextFile(String file) {
      Path path = root.resolve(file);
      Assertions.assertFalse(TextFileFinder.isText.test(path));
    }
  }

  /**
   * Tests the directory listing.
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class B_FindListTests {

    /**
     * Tests the list has the expected number of paths.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    @Order(1)
    public void testListSize() throws IOException {
      Assertions.assertEquals(14, TextFileFinder.list(root).size());
    }

    /**
     * Tests the stream has the expected number of paths.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    @Order(2)
    public void testStreamSize() throws IOException {
      Assertions.assertEquals(14, TextFileFinder.find(root).count());
    }

    /**
     * Tests the listing includes all of the expected paths.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    @Order(3)
    public void testPaths() throws IOException {
      Set<Path> actual = TextFileFinder.find(root).collect(Collectors.toSet());

      Set<Path> expected = new HashSet<>();
      Collections.addAll(expected, root.resolve("symbols.txt"),
          root.resolve("dir.txt").resolve("findme.Txt"), root.resolve("empty.txt"),
          root.resolve(".txt").resolve("hidden.txt"), root.resolve("position.teXt"),
          root.resolve("animals_copy.text"), root.resolve("digits.txt"),
          root.resolve("capital_extension.TXT"), root.resolve("animals_double.text"),
          root.resolve("a").resolve("b").resolve("c").resolve("d").resolve("subdir.txt"),
          root.resolve("words.tExT"), root.resolve("animals.text"), root.resolve("hello.txt"),
          root.resolve("capitals.txt"));

      Assertions.assertTrue(expected.equals(actual));
    }
  }

  /**
   * Tests that the expected approach is taken.
   */
  @Nested
  public class C_ApproachTests {

    /*
     * These only approximately determine if a lambda function was used and the File class was NOT
     * used.
     */

    /**
     * Tests that the {@link TextFileFinder#isText} is not an anonymous class.
     */
    @Test
    @Order(1)
    public void testAnonymous() {
      Assertions.assertFalse(TextFileFinder.isText.getClass().isAnonymousClass());
    }

    /**
     * Tests that the {@link TextFileFinder#isText} is not an enclosing class.
     */
    @Test
    @Order(2)
    public void testEnclosingClass() {
      Assertions.assertNull(TextFileFinder.isText.getClass().getEnclosingClass());
    }

    /**
     * Tests that the {@link TextFileFinder#isText} is not a synthetic class.
     */
    @Test
    @Order(3)
    public void testSyntheticClass() {
      Assertions.assertTrue(TextFileFinder.isText.getClass().isSynthetic());
    }

    /**
     * Tests that the {@link TextFileFinder#isText} is likely a lambda function.
     */
    @Test
    @Order(4)
    public void testClassName() {
      String actual = TextFileFinder.isText.getClass().getCanonicalName();
      String[] parts = actual.split("[$]+");

      Assertions.assertTrue(parts[1].contentEquals("Lambda"));
    }

    /**
     * Tests that the java.io.File class does not appear in the implementation code.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    @Order(5)
    public void testFileClass() throws IOException {
      String source =
          Files.readString(Path.of(".", "src", "TextFileFinder.java"), StandardCharsets.UTF_8);
      Assertions.assertFalse(source.contains("import java.io.File;"));
      Assertions.assertFalse(source.contains(".toFile()"));
    }
  }

}
