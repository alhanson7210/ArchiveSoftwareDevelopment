import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Runs all of the tests associated with this homework. Do not run this class until the individual
 * test classes are passing!
 */
@TestMethodOrder(Alphanumeric.class)
public class HtmlCleanerTest {

  /**
   * @see HtmlCleanerStripTest
   */
  @Nested
  public class A_NestedStripTests extends HtmlCleanerStripTest {

  }

  /**
   * @see HtmlFetcherTest
   */
  @Nested
  public class B_NestedFetchTests extends HtmlFetcherTest {

  }

  /**
   * @see HtmlCleanerRemoteTest
   */
  @Nested
  public class C_NestedCleanerTest extends HtmlCleanerRemoteTest {

  }
}

