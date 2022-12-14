import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses URL links from the anchor tags within HTML text.
 */
public class LinkParser {

  /**
   * Removes the fragment component of a URL (if present), and properly encodes the query string (if
   * necessary).
   *
   * @param url the url to clean
   * @return cleaned url (or original url if any issues occurred)
   */
  public static URL clean(URL url) {
    try {
      return new URI(
          url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
          url.getPath(), url.getQuery(), null).toURL();
    } catch (MalformedURLException | URISyntaxException e) {
      return url;
    }
  }

  /**
   * Returns a list of all the HTTP(S) links found in the href attribute of the anchor tags in the
   * provided HTML. The links will be converted to absolute using the base URL and cleaned (removing
   * fragments and encoding special characters as necessary).
   *
   * @param base the base url used to convert relative links to absolute
   * @param html the raw html associated with the base url
   * @return cleaned list of all http(s) links in the order they were found
   * @throws MalformedURLException no protocol was specified, 
   * an unknown protocol, spec was null, or the parsed URL fails the protocol syntax compliance
   */
  public static ArrayList<URL> listLinks(URL base, String html) throws MalformedURLException {
    // TODO Fill in listLinks(...) implementation
	ArrayList<URL> urls = new ArrayList<>();
	//bit complex
	//String regex1 = "(?msi)<a\\s*[^>]*?\\s*href\\s*=\\s*\"([^\"]*)\"\\s*[^>]*?\\s*>";
	//String regex2 = "(?msi)<a\\s*[^>]*?\\s*href\\s*=\\s*\"([^\"]*.*?)\"\\s*.*?\\s*>";
	//simplest
	String regex3 = "(?msi)<a\\s*[^>]*?\\s*href\\s*=\\s*\"(.*?)\"\\s*.*?\\s*>";
	Pattern pattern = Pattern.compile(regex3);
	Matcher matches = pattern.matcher(html);
	int idx = 0;
	while(matches.find(idx) && idx < html.length()) {
		urls.add(clean(new URL(base, html.substring(matches.start(1), matches.end(1)))));
		idx = (matches.start() == matches.end())? matches.end() + 1 : matches.end();
	}
	return urls;
  }

  /**
   * Demonstrates this class.
   *
   * @param args unused
   * @throws MalformedURLException if unable to create URLs
   */
  public static void main(String[] args) throws MalformedURLException {
    // this demonstrates cleaning
    URL valid = new URL("https://docs.python.org/3/library/functions.html?highlight=string#format");
    System.out.println(" Link: " + valid);
    System.out.println("Clean: " + clean(valid));
    System.out.println();

    // this demonstrates encoding
    URL space = new URL("https://www.google.com/search?q=hello world");
    System.out.println(" Link: " + space);
    System.out.println("Clean: " + clean(space));
    System.out.println();

    // this throws an exception
    URL invalid = new URL("javascript:alert('Hello!');");
    System.out.println(invalid);
  }
}
