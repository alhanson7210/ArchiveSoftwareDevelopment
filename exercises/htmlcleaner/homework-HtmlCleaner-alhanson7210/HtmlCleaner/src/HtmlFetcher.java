import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * A specialized version of {@link HttpsFetcher} that follows redirects and returns HTML content if
 * possible.
 *
 * @see HttpsFetcher
 */
public class HtmlFetcher {

	/**
	 * Returns {@code true} if and only if there is a "Content-Type" header and the first value of
	 * that header starts with the value "text/html" (case-insensitive).
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isHtml(Map<String, List<String>> headers) {
		if (headers == null) {
			return false;
		}
		int first = 0;
		String type = "Content-Type";
		String content = "text/html";
		boolean hasType = headers.containsKey(type);
		return hasType? headers.get(type).get(first).startsWith(content) : false;
	}

	/**
	 * Parses the HTTP status code from the provided HTTP headers, assuming the status line is stored
	 * under the {@code null} key.
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return the HTTP status code or -1 if unable to parse for any reasons
	 */
	public static int getStatusCode(Map<String, List<String>> headers) {
		if (headers == null) {
			return -1;
		}
		if (headers.containsKey(null)) {
			List<String> values = headers.get(null);
			if (values == null) {
				return -1;
			}
			String[] status = String.join(" ", values).split(" ");
			if (status.length < 3) {
				return -1;
			}
			String code = status[1];
			if (code.matches("\\d+")) {
				return Integer.valueOf(code).intValue();
			}
		}
		return -1;
	}

	/**
	 * Returns {@code true} if and only if the HTTP status code is between 300 and 399 (inclusive) and
	 * there is a "Location" header with at least one value.
	 *
	 * @param headers the HTTP/1.1 headers to parse
	 * @return {@code true} if the headers indicate the content type is HTML
	 */
	public static boolean isRedirect(Map<String, List<String>> headers) {
		if (headers == null) {
			return false;
		}
		if (headers.containsKey("Location")) {
			List<String> local = headers.get("Location");
			if (local == null) {
				return false;
			}
			int status = getStatusCode(headers);
			if (local.size() > 0 && status > 299 && status < 400) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Fetches the resource at the URL using HTTP/1.1 and sockets. If the status code is 200 and the
	 * content type is HTML, returns the HTML as a single string. If the status code is a valid
	 * redirect, will follow that redirect if the number of redirects is greater than 0. Otherwise,
	 * returns {@code null}.
	 *
	 * @param url the url to fetch
	 * @param redirects the number of times to follow redirects
	 * @return the html or {@code null} if unable to fetch the resource or the resource is not html
	 *
	 * @see HttpsFetcher#openConnection(URL)
	 * @see HttpsFetcher#printGetRequest(PrintWriter, URL)
	 * @see HttpsFetcher#getHeaderFields(BufferedReader)
	 * @see HttpsFetcher#getContent(BufferedReader)
	 *
	 * @see String#join(CharSequence, CharSequence...)
	 *
	 * @see #isHtml(Map)
	 * @see #isRedirect(Map)
	 */
	public static String fetch(URL url, int redirects) {
		if (url == null) {
			return null;
		}
		try {
			Map<String, List<String>> headers = HttpsFetcher.fetchURL(url);
			int code = getStatusCode(headers);
			if (isHtml(headers) && code == 200) {
				List<String> content = headers.get("Content");
				String html = String.join("\n", content);
				return html;
			} else if (isRedirect(headers) && redirects > 0) {
				redirects--;
				if (redirects < 0) {
					return null;
				}
				return fetch(headers.get("Location").get(0), redirects);
			}

		} catch (IOException e) {
			System.err.println("Failed fetching url: " + url.toString());
		}
		return null;
	}

	/**
	 * Converts the {@link String} url into a {@link URL} object and then calls
	 * {@link #fetch(URL, int)}.
	 *
	 * @param url the url to fetch
	 * @param redirects the number of times to follow redirects
	 * @return the html or {@code null} if unable to fetch the resource or the resource is not html
	 *
	 * @see #fetch(URL, int)
	 */
	public static String fetch(String url, int redirects) {
		try {
			return fetch(new URL(url), redirects);
		}
		catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Converts the {@link String} url into a {@link URL} object and then calls
	 * {@link #fetch(URL, int)} with 0 redirects.
	 *
	 * @param url the url to fetch
	 * @return the html or {@code null} if unable to fetch the resource or the resource is not html
	 *
	 * @see #fetch(URL, int)
	 */
	public static String fetch(String url) {
		return fetch(url, 0);
	}

	/**
	 * Calls {@link #fetch(URL, int)} with 0 redirects.
	 *
	 * @param url the url to fetch
	 * @return the html or {@code null} if unable to fetch the resource or the resource is not html
	 */
	public static String fetch(URL url) {
		return fetch(url, 0);
	}
}
