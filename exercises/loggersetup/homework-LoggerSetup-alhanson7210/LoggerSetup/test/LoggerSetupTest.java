import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Runs a couple of tests to make sure Log4j2 is setup.
 *
 * NOTE: There are better ways to test log configuration---we will keep it simple here because we
 * just want to make sure you can run and configure Log4j2.
 *
 * This is also not the most informative configuration---it is just one of the most testable ones
 * that require you to learn about how to handle stack trace output.
 */
public class LoggerSetupTest {

  /** Used to capture console output. */
  private static ByteArrayOutputStream capture = null;

  /**
   * Setup that runs before each test.
   *
   * @throws IOException if an I/O error occurs
   */
  @BeforeAll
  public static void setup() throws IOException {
    // delete any old debug files
    Files.deleteIfExists(Path.of("debug.log"));

    // capture all system console output
    PrintStream original = System.out;
    capture = new ByteArrayOutputStream();
    System.setOut(new PrintStream(capture));

    // run main() only ONCE to avoid duplicate entries
    // and shutdown log manager to flush the debug files
    LoggerSetup.main(null);
    LogManager.shutdown();

    // restore system.out
    System.setOut(original);
    System.out.println(capture.toString());
  }

  /**
   * Open the debug.log file as a list and compare to expected.
   *
   * @throws IOException if an I/O error occurs
   */
  @Test
  public void testFile() throws IOException {
    // test file output is as expected
    List<String> expected = Files.readAllLines(Path.of("test", "debug.log"));
    List<String> actual = Files.readAllLines(Path.of("debug.log"));
    assertTrue(expected.equals(actual), "Compare debug.log and test/debug.log in Eclipse.");
  }

  /**
   * Captures the console output and compares to expected.
   *
   * @throws IOException if an I/O error occurs
   */
  @Test
  public void testConsole() throws IOException {
    // this is the expected console output
    List<String> expected = List.of("wren", "egret eagle", "Catching finch", "ibis", "wren",
        "egret eagle", "Catching finch");

    // split capture into lines and then strip any extra whitespace before comparing
    String[] captured = capture.toString().split("[\\n\\r]+");
    List<String> actual = Arrays.stream(captured).map(String::strip).collect(Collectors.toList());

    assertEquals(expected, actual);
  }

  /**
   * Open the .classpath file as a set and compare to expected. This is to help
   * detect when the user library is not created properly. If you fail this test,
   * double-check that the user library is named "log4j2" in all lowercase, and
   * that the jar files are not added directly to the classpath.
   *
   * @throws IOException if an I/O error occurs
   */
  @Test
  public void testClasspath() throws IOException {
    // use sets as classpath entry order is not important

    Set<String> expected = Files.readAllLines(Path.of("test", ".classpath"))
        .stream()
        .map(String::strip)
        .collect(Collectors.toSet());

    Set<String> actual = Files.readAllLines(Path.of(".classpath"))
      .stream()
      .map(String::strip)
      .collect(Collectors.toSet());

    assertTrue(expected.equals(actual), "Make sure the classpath is setup properly in Eclipse.");
  }
}
