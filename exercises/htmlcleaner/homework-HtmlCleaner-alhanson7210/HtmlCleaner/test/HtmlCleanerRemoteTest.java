import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Tests the {@link HtmlCleaner} class, specifically the individual methods that strip different
 * HTML components.
 */
@TestMethodOrder(OrderAnnotation.class)
public class HtmlCleanerRemoteTest {

  /**
   * Fetches the HTML from a URL and cleans the text, making sure the end result matches the
   * expected.
   *
   * @param url the URL to fetch
   * @param expected the expected text after removing the html
   */
  public static void test(URL url, String expected) {
    Assertions.assertTimeoutPreemptively(Duration.ofSeconds(30), () -> {
      String html = HtmlFetcher.fetch(url);
      String actual = HtmlCleaner.stripHtml(html).strip();

      Assertions.assertEquals(expected, actual);
    });
  }

  /**
   * Tests the cleaned HTML of a simple remote HTML web page.
   *
   * @throws MalformedURLException if unable to create URL
   */
  @Test
  @Order(1)
  public void testHello() throws MalformedURLException {
    URL url = new URL("https://www.cs.usfca.edu/~cs212/simple/hello.html");
    String expected = "Hello World!\n    Hello, world. Hello... World? HELLO WORLD!";

    test(url, expected);
  }

  /**
   * Tests the cleaned HTML of a simple remote HTML web page with multiple links.
   *
   * @throws MalformedURLException if unable to create URL
   */
  @Test
  @Order(2)
  public void testGuten() throws MalformedURLException {
    URL url = new URL("https://www.cs.usfca.edu/~cs212/guten/");
    String expected = String.join("\n", List.of(
        "Great Expectations by Charles Dickens (1867)",
        "The Oak Ridge ALGOL Compiler for the Control Data Corporation 1604 Preliminary Programmer's Manual",
        "On the Origin of Species by Charles Darwin (1859)",
        "Leaves of Grass by Walt Whitman (1855)",
        "The Adventures of Sherlock Holmes by Arthur Conan Doyle (1892)",
        "Practical Grammar and Composition by Thomas Wood (1910)",
        "The Elements of Style by William Strunk"));

    test(url, expected);
  }

  /**
   * Tests the cleaned HTML of a simple remote HTML web page with many links.
   *
   * @throws MalformedURLException if unable to create URL
   */
  @Test
  @Order(3)
  public void testBirds() throws MalformedURLException {
    URL url = new URL("https://www.cs.usfca.edu/~cs212/birds/birds.html");
    String expected = String.join("\n",
        List.of("Here is a list of birds:", "", "", "	albatross", "	blackbird", "	bluebird",
            "	cardinal", "	chickadee", "	crane", "	crow", "	cuckoo", "	dove",
            "	duck", "	eagle", "	egret", "	falcon", "	finch", "	goose", "	gull",
            "	hawk", "	heron", "	hummingbird", "	ibis", "	kingfisher", "	loon",
            "	magpie", "	mallard", "	meadowlark", "	mockingbird", "	nighthawk", "	osprey",
            "	owl", "	pelican", "	pheasant", "	pigeon", "	puffin", "	quail", "	raven",
            "	roadrunner", "	robin", "	sandpiper", "	sparrow", "	starling", "	stork",
            "	swallow", "	swan", "	tern", "	turkey", "	vulture", "	warbler", "	woodpecker",
            "	wren", "	yellowthroat", "", "", "", "Home"));

    test(url, expected);
  }
}
