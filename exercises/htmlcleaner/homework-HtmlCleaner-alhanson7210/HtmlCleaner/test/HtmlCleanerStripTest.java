import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Tests the {@link HtmlCleaner} class, specifically the individual methods that strip different
 * HTML components.
 */
@TestMethodOrder(Alphanumeric.class)
public class HtmlCleanerStripTest {

  /** The format used to produce output when a test fails. */
  public static final String format = "%nHTML:%n%s%n%nExpected:%n%s%n%nActual:%n%s%n%n";

  /**
   * Helper method to compare the expect and actual text are equal.
   *
   * @param test the text being tested
   * @param expected the expected output
   * @param actual the actual output
   */
  public static void test(String test, String expected, String actual) {
    Assertions.assertEquals(expected, actual, () -> String.format(format, test, expected, actual));
  }

  /**
   * Tests the {@link HtmlCleaner#stripEntities(String)} method.
   *
   * @see HtmlCleaner#stripEntities(String)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class A_EntityTests {
    /**
     * Tests "2010&ndash;2011". (View Javadoc to see rendering.)
     */
    @Test
    @Order(1)
    public void testNamed() {
      String test = "2010&ndash;2011";
      String expected = "20102011";
      String actual = HtmlCleaner.stripEntities(test);

      test(test, expected, actual);
    }

    /**
     * Tests "2010&#8211;2011". (View Javadoc to see rendering.)
     */
    @Test
    @Order(2)
    public void testNumbered() {
      String test = "2010&#8211;2011";
      String expected = "20102011";
      String actual = HtmlCleaner.stripEntities(test);

      test(test, expected, actual);
    }

    /**
     * Tests "2010&#x2013;2011". (View Javadoc to see rendering.)
     */
    @Test
    @Order(3)
    public void testHexadecimal() {
      String test = "2010&#x2013;2011";
      String expected = "20102011";
      String actual = HtmlCleaner.stripEntities(test);

      test(test, expected, actual);
    }

    /**
     * Tests "touche&#769;!". (View Javadoc to see rendering.)
     */
    @Test
    @Order(4)
    public void testAccent1() {
      String test = "touche&#769;!";
      String expected = "touche!";
      String actual = HtmlCleaner.stripEntities(test);

      test(test, expected, actual);
    }

    /**
     * Tests "touch&eacute;!". (View Javadoc to see rendering.)
     */
    @Test
    @Order(5)
    public void testAccent2() {
      String test = "touch&eacute;!";
      String expected = "touch!";
      String actual = HtmlCleaner.stripEntities(test);

      test(test, expected, actual);
    }

    /**
     * Tests "hello&mdash;good&dash;bye". (View Javadoc to see rendering.)
     */
    @Test
    @Order(6)
    public void testMultiple() {
      String test = "hello&mdash;good&dash;bye";
      String expected = "hellogoodbye";
      String actual = HtmlCleaner.stripEntities(test);

      test(test, expected, actual);
    }

    /**
     * Tests "hello & good-bye".
     */
    @Test
    @Order(7)
    public void testAmpersand() {
      String test = "hello & good-bye";
      String expected = "hello & good-bye";
      String actual = HtmlCleaner.stripEntities(test);

      test(test, expected, actual);
    }

    /**
     * Tests "hello & good-bye;".
     */
    @Test
    @Order(8)
    public void testAndSemicolon() {
      String test = "hello & good-bye;";
      String expected = "hello & good-bye;";
      String actual = HtmlCleaner.stripEntities(test);

      test(test, expected, actual);
    }
  }

  /**
   * Tests the {@link HtmlCleaner#stripComments(String)} method.
   *
   * @see HtmlCleaner#stripComments(String)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class B_CommentTests {

    /**
     * Tests text with only a simple comment.
     */
    @Test
    @Order(1)
    public void testSimple() {
      String test = "<!-- hello -->";
      String expected = " ";
      String actual = HtmlCleaner.stripComments(test);

      test(test, expected, actual);
    }

    /**
     * Tests text with a single comment within other text.
     */
    @Test
    @Order(2)
    public void testABC() {
      String test = "A<!-- B -->C";
      String expected = "A C";
      String actual = HtmlCleaner.stripComments(test);

      test(test, expected, actual);
    }

    /**
     * Tests a comment broken up by newlines.
     */
    @Test
    @Order(3)
    public void testNewLine() {
      String test = "A<!--\n B\r\n -->C";
      String expected = "A C";
      String actual = HtmlCleaner.stripComments(test);

      test(test, expected, actual);
    }

    /**
     * Tests a tag within a comment.
     */
    @Test
    @Order(4)
    public void testTags() {
      String test = "A<!-- <b>B</b> -->C";
      String expected = "A C";
      String actual = HtmlCleaner.stripComments(test);

      test(test, expected, actual);
    }

    /**
     * Tests a comment with a different closing slash style.
     */
    @Test
    @Order(5)
    public void testSlashes() {
      String test = "A<!-- B //-->C";
      String expected = "A C";
      String actual = HtmlCleaner.stripComments(test);

      test(test, expected, actual);
    }

    /**
     * Tests text with multiple comments.
     */
    @Test
    @Order(6)
    public void testMultiple() {
      String test = "A<!-- B -->C D<!-- E -->F";
      String expected = "A C D F";
      String actual = HtmlCleaner.stripComments(test);

      test(test, expected, actual);
    }
  }

  /**
   * Tests the {@link HtmlCleaner#stripTags(String)} method.
   *
   * @see HtmlCleaner#stripTags(String)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class C_TagTests {

    /**
     * View Javadoc to see HTML rendering of test case:
     *
     * <pre>
     * <b>hello</b> world!
     * </pre>
     */
    @Test
    @Order(1)
    public void testSimple() {
      String test = "<b>hello</b> world!";
      String expected = "hello world!";
      String actual = HtmlCleaner.stripTags(test);

      test(test, expected, actual);
    }

    /**
     * View Javadoc to see HTML rendering of test case:
     *
     * <pre>
     * <b>hello
     * </b> world!
     * </pre>
     */
    @Test
    @Order(2)
    public void testSimpleNewLine() {
      String test = "<b>hello\n</b> world!";
      String expected = "hello\n world!";
      String actual = HtmlCleaner.stripTags(test);

      test(test, expected, actual);
    }

    /**
     * View Javadoc to see HTML rendering of test case:
     *
     * <pre>
     * <a
     *  name=toc>table of contents</a>
     * </pre>
     */
    @Test
    @Order(3)
    public void testAttributeNewline() {
      String test = "<a \n name=toc>table of contents</a>";
      String expected = "table of contents";
      String actual = HtmlCleaner.stripTags(test);

      test(test, expected, actual);
    }

    /**
     * View Javadoc to see HTML rendering of test case:
     *
     * <pre>
     * <p>Hello, <strong>world</strong>!</p>
     * </pre>
     */
    @Test
    @Order(4)
    public void testNestedTags() {
      String test = "<p>Hello, <strong>world</strong>!</p>";
      String expected = "Hello, world!";
      String actual = HtmlCleaner.stripTags(test);

      test(test, expected, actual);
    }

    /**
     * View Javadoc to see HTML rendering of test case:
     *
     * <pre>
     * <p>Hello, <br/>world!</p>
     * </pre>
     */
    @Test
    @Order(5)
    public void testLineBreak() {
      String test = "<p>Hello, <br/>world!</p>";
      String expected = "Hello, world!";
      String actual = HtmlCleaner.stripTags(test);

      test(test, expected, actual);
    }
  }

  /**
   * Tests the {@link HtmlCleaner#stripElement(String, String)} method.
   *
   * @see HtmlCleaner#stripElement(String, String)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class D_ElementTests {
    /**
     * Tests text with a single style element on a single line.
     */
    @Test
    @Order(1)
    public void testStyle() {
      String test = "<style type=\"text/css\">body { font-size: 10pt; }</style>";
      String expected = " ";
      String actual = HtmlCleaner.stripElement(test, "style");

      test(test, expected, actual);
    }

    /**
     * Tests text with a single style element across multiple lines.
     */
    @Test
    @Order(2)
    public void testStyleNewline1() {
      String test = "<style type=\"text/css\">\r\nbody { font-size: 10pt; }\n</style>";
      String expected = " ";
      String actual = HtmlCleaner.stripElement(test, "style");

      test(test, expected, actual);
    }

    /**
     * Tests text with a single style element with the style tag containing a newline.
     */
    @Test
    @Order(3)
    public void testStyleNewline2() {
      String test = "<style \n type=\"text/css\">\nbody { font-size: 10pt; }\n</style>";
      String expected = " ";
      String actual = HtmlCleaner.stripElement(test, "style");

      test(test, expected, actual);
    }

    /**
     * Tests text with multiple elements.
     */
    @Test
    @Order(4)
    public void testMultiple() {
      String test = "a<test>b</test>c<test>d</test>e";
      String expected = "a c e";
      String actual = HtmlCleaner.stripElement(test, "test");

      test(test, expected, actual);
    }

    /**
     * Tests text with mixed elements.
     */
    @Test
    @Order(5)
    public void testMixed() {
      String test = "<title>Hello</title><script>potato</script> world";
      String expected = "<title>Hello</title>  world";
      String actual = HtmlCleaner.stripElement(test, "script");

      test(test, expected, actual);
    }
  }

  /**
   * Tests the {@link HtmlCleaner#stripHtml(String)} method.
   *
   * @see HtmlCleaner#stripHtml(String)
   */
  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  public class E_CleanTests {

    /**
     * Tests text with no HTML.
     */
    @Test
    @Order(1)
    public void testNoHTML() {
      String test = "hello & good-bye;";
      String expected = "hello & good-bye;";
      String actual = HtmlCleaner.stripHtml(test);

      test(test, expected, actual);
    }

    /**
     * Tests text with one line of simple HTML.
     */
    @Test
    @Order(2)
    public void testOneLine() {
      String test = "<b>hello</p>&amp;<script>potato</script>world";
      String expected = "hello world";
      String actual = HtmlCleaner.stripHtml(test);

      test(test, expected, actual);
    }

    /**
     * Tests text with simple HTML.
     */
    @Test
    @Order(3)
    public void testSimplePage() {
      StringBuilder html = new StringBuilder();
      html.append("<!DOCTYPE html>\n");
      html.append("<html>\n");
      html.append("<head>\n");
      html.append("    <meta charset=\"utf-8\">\n");
      html.append("    <script type=\"text/javascript\" src=\"d3.v3.js\"></script>\n");
      html.append("    <style type=\"text/css\">\n");
      html.append("    body {\n");
      html.append("        font-size: 10pt;\n");
      html.append("        font-family: sans-serif;\n");
      html.append("    }\n");
      html.append("    </style>\n");
      html.append("</head>\n");
      html.append("<body>\n");
      html.append("Hello, world! &copy;2013\n");
      html.append("</body>\n");
      html.append("</html>\n");

      String expected = "Hello, world! 2013";
      String actual = HtmlCleaner.stripHtml(html.toString());

      // note trim to remove blank lines
      test(html.toString(), expected, actual.strip());
    }

    /**
     * Tests an actual HTML file.
     *
     * @throws IOException if unable to read test files
     */
    @Test
    @Order(4)
    public void testPanagrams() throws IOException {
      Path htmlPath = Path.of("test", "pangrams.html");
      Path textPath = Path.of("test", "pangrams.txt");

      Assertions.assertTrue(Files.isReadable(htmlPath));
      Assertions.assertTrue(Files.isReadable(textPath));

      String html = Files.readString(htmlPath, StandardCharsets.UTF_8);
      String expected = Files.readString(textPath, StandardCharsets.UTF_8);
      String actual = HtmlCleaner.stripHtml(html);

      test(html.toString(), expected, actual);
    }
  }
}
