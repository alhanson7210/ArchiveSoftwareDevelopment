import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Search engine called Finder that can crawl from given urls or the default one in order to allow a user to search the web for links relatively
 * @author UxDeveloperxU
 *
 */
public class HomeServlet extends HttpServlet {

	/** Identifier used for serialization (unused). */
	private static final long serialVersionUID = 1L;

	/** The title to use for this web page. */
	private static final String TITLE = "Search Engine";

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();
	
	/** the reference to the web crawler for accumulator*/
	private WebCrawler crawler;
	
	/** searches made by the user */
	private List<String> suggestions;
	
	/** string results for a query*/
	private Set<String> webResults;
	
	/** current long in time*/
	private String loginTime;
	
	/** previous time */
	private String previousTime;
	
	/** search statistics*/
	private List<ComparableSearchResult> statistics;
	
	/** duration */
	private Double elapsedTime;
	
	/** search queries entered in by the user */
	private Set<String> searchHistory;
	
	/**
	 * Constructor
	 * @param crawler resource for searching the index in a thread safe manner
	 */
	public HomeServlet(WebCrawler crawler) {
		super();
		this.crawler = crawler;
		this.suggestions = new ArrayList<>();
		this.webResults = new HashSet<>();
		this.statistics = new ArrayList<>();
		this.searchHistory = new HashSet<>();
		this.elapsedTime = 0.0;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");
		PrintWriter out = response.getWriter();
		loginTime = getDate();
		previousTime = previousTime == null? loginTime : previousTime;
		//writeHeader
		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>%n");
		out.printf("<head>%n");
		out.printf("	<meta charset=\"utf-8\">%n");
		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		out.printf("	<title>%s</title>%n", TITLE);
		out.printf("	<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bulma@0.8.2/css/bulma.min.css\">%n");
		out.printf("	<script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>%n");
		out.printf("    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>");
		out.printf("</head>%n");
		//writeBody
		out.printf("%n");
		out.printf("<body style=\"background-color:whitesmoke;\">%n");
		//writeDisplay
		out.printf("	<section class=\"hero is-primary is-bold\">%n");
		out.printf("	  <div class=\"hero-body\">%n");
		out.printf("	    <div class=\"container\">%n");
		out.printf("	      <h1 class=\"title\">%n");
		out.printf("	        Search Engine%n");
		out.printf("	      </h1>%n");
		out.printf("	      <h2 class=\"subtitle\">%n");
		out.printf("					<i class=\"fas fa-calendar-alt\"></i>%n");
		out.printf("					&nbsp;Updated %s%n", loginTime);
		out.printf("	      </h2>%n");
		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");
		//Write NavBar
		out.printf("<section>%n");
		out.printf("<nav class=\"navbar is-black\">\n" + 
				   "  <div class=\"navbar-brand\">\n" + 
				   "    <a class=\"navbar-item\" href=\"/home\">\n" + 
				   "      <img src=\"/brand.png\" width=\"28\" height=\"28\">\n" + 
				   "    </a>\n" + 
				   "    <a class=\"navbar-item\" href=\"/home\">Finder</a>" +
				   "    <div class=\"navbar-burger burger\" data-target=\"navigationControlBar\">\n" + 
				   "      <span></span>\n" + 
				   "      <span></span>\n" + 
				   "      <span></span>\n" + 
				   "    </div>\n" + 
				   "  </div>\n" + 
				   "  <div id=\"navigationControlBar\" class=\"navbar-menu\">\n" + 
				   "    <div class=\"navbar-start\">\n" + 
				   "      <a class=\"navbar-item\" href=\"/home\">\n" + 
				   "        Home\n" + 
				   "      </a>\n" + 
				   "      <a class=\"navbar-item\" href=\"/index\">\n" + 
				   "        Index\n" + 
				   "      </a>\n" +
				   "      <a class=\"navbar-item\" href=\"/location\">\n" + 
				   "        Location\n" + 
				   "      </a>\n" +
				   "      <a class=\"navbar-item\" href=\"/shutdown\">\n" + 
				   "        Shutdown\n" + 
				   "      </a>\n" +
				   "    </div>\n" + 
				   "  </div>\n" + 
				   "</nav>\n");
		out.printf("</section>%n");
		//last login time
		out.printf("    <br>%n<center><p>Last log in time is: %s</p></center>\n", previousTime);
		//write Crawl
		out.printf("    <section class=\"section\">%n");
		out.printf("        <div class=\"container\">%n");
		out.printf("            <h3 class=\"title\">New Crawl</h3>");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("				    <label class=\"label\">Seed Url</label>%n");
		out.printf("				    <div class=\"control\">%n");
		out.printf("				        <input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter new url here.\">%n", "seed");
		out.printf("				    </div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			        <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-comment\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						Crawl%n");
		out.printf("					</button>%n");
		out.printf("			    </div>%n");
		out.printf("			</form>%n");
		out.printf("        </div>%n");
		out.printf("    </section>%n");
		//writeForm
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Query Search</h2>%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("			    <button class=\"button\" name=\"lucky\" type=\"%s\" value=\"%s\">I'm Feeling Lucky!</button>%n", "submit", "clicked");
		out.printf("			</form>%n<br>%n");
		//search history
		synchronized(searchHistory) {
			if (searchHistory.isEmpty()) {
				out.printf("		<p>No user history yet.</p>%n<br>%n");
			}
			else {
				out.printf("		<p>Search History</p>%n");
				out.printf("		<div style=\"height:70px;overflow:scroll;border:dashed black;padding: 10px;\">\n");
				for (String search : searchHistory) {
					out.printf("	    <p class=\"box\" style=\"background-color:#2f4c8a;overflow:hidden;color:black;\">%n");
					out.printf("            Query: %s%n", search);
					out.printf("	    </p>%n");
					out.printf("%n");
				}
				out.printf("		</div>\n<br>%n");
				out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
				out.printf("			    <button class=\"button\" name=\"clear\" type=\"%s\" value=\"%s\">Clear Search History</button>%n", "submit", "clear");
				out.printf("			</form>%n<br>%n");
			}
		}
		//suggested
		synchronized(suggestions) {
			out.printf("			<h4>Suggested Queries</h4>\n");
			if (suggestions.isEmpty()) {
				out.printf("			    <p class=\"box\">No Suggested queries at this time.</p>%n");
			}
			else {
				for (String query : suggestions) {
					out.printf("			    <p class=\"box\">%n");
					out.printf("			        %s%n", query);
					out.printf("			    </p>%n");
				}
			}
		}
		//form
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("				    <label class=\"label\">Query String</label>%n");
		//exact or partial search
		out.printf("				    <div class=\"control\">%n" + 
				   "				      <div class=\"select\">%n" + 
				   "				        <select name=\"exact\">%n" + 
				   "				          <option value=\"partial\">partial</option>%n" + 
				   "				          <option value=\"exact\">exact</option>%n" + 
				   "				        </select>%n" + 
				   "				      </div>\n" + 
				   "				    </div>%n");
		out.printf("				    <br>%n");
		//multi-query search 
		out.printf("				    <div class=\"control\">%n");
		out.printf("				        <input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter search here.\">%n", "search");
		out.printf("				    </div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		//submit form with POST
		out.printf("				<div class=\"control\">%n");
		out.printf("			        <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-comment\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						Search%n");
		out.printf("					</button>%n");
		out.printf("			    </div>%n");
		out.printf("			</form>%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		//write statistics
		synchronized(statistics) {
			out.printf("    <section class=\"section\">%n");
			out.printf("        <div class=\"container\">%n");
			out.printf("			<h2 class=\"title\">Search Statistics</h2>%n");
			
			if (statistics.isEmpty()) {
				out.printf("		<p>No results found.</p>%n");
			}
			else {
				out.printf("		<p>Total number of results: %d</p>%n", statistics.size());
				out.printf("		<p>Time required to calculate and fetch results: %s</p>%n", elapsedTime);
				out.printf("             <br>%n");
				out.printf("		<div style=\"height:210px;overflow:scroll;border:dashed black;padding:10px;\">\n");
				for(ComparableSearchResult result: statistics) {
					out.printf("	    <div class=\"box\" style=\"background-color:#2f4c8a; color:black;overflow:hidden;\">%n");
					out.printf("             <a href=\"%s\" class\"control\" style=\"color:white;\">%s</a>%n", result.getWhere(), result.getWhere());
					out.printf("             <p>The score for this result is: %.8f</p>%n", result.getScore());
					out.printf("             <p>The count for this result is: %d</p>%n", result.getCount());
					out.printf("	    </div>%n");
					out.printf("%n");
				}
				out.printf("		</div>\n");
			}
		}
		out.printf("%n");
		out.printf("        </div>%n");
		out.printf("    </section>%n");
		//results
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Results</h2>%n");
		synchronized(webResults) {
			if (webResults.isEmpty()) {
				out.printf("		<p>No results found.</p>%n");
			}
			else {
				out.printf("		<div style=\"height:210px;overflow:scroll;border:dashed black;padding: 10px;\">\n");
				for (String link : webResults) {
					out.printf("	    <div class=\"box\" style=\"background-color:#2f4c8a;overflow:hidden;\">%n");
					out.printf("            <a href=\"%s\" class\"control\" style=\"color:white;\">%s</a>%n", link, link);
					out.printf("	    </div>%n");
					out.printf("%n");
				}
				out.printf("		</div>\n");
			}
		}
		out.printf("%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		//write closing and js
		out.printf("%n");
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("	      This request was handled by thread %s.%n", Thread.currentThread().getName());
		out.printf("	    </p>%n");
		out.printf("	  </div>%n");
		out.printf("	</footer>%n");
		out.printf("<script>$(document).ready(function() {\n" + 
				"\n" + 
				"  // Check for click events on the navbar burger icon\n" + 
				"  $(\".navbar-burger\").click(function() {\n" + 
				"\n" + 
				"      // Toggle the \"is-active\" class on both the \"navbar-burger\" and the \"navbar-menu\"\n" + 
				"      $(\".navbar-burger\").toggleClass(\"is-active\");\n" + 
				"      $(\".navbar-menu\").toggleClass(\"is-active\");\n" + 
				"\n" + 
				"  });\n" + 
				"});</script>");
		out.printf("</body>%n");
		out.printf("</html>%n");
		previousTime = loginTime;
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");
		String seed = request.getParameter("seed");
		seed = seed == null? "" : seed;
		seed = StringEscapeUtils.escapeHtml4(seed);
		//new crawl
		if (!seed.isBlank() && !seed.isEmpty()) {
			try {
				crawler.crawlFromSeedUrl(seed);
			} catch (MalformedURLException | InterruptedException e) {
				log.info("Failed to crawl from new seed url");
			}
		}
		//lucky button parameter
		String lucky = request.getParameter("lucky");
		lucky = lucky == null? "" : lucky;
		lucky = StringEscapeUtils.escapeHtml4(lucky);
		//clear search button history parameter
		String clear = request.getParameter("clear");
		clear = clear == null? "" : clear;
		clear = StringEscapeUtils.escapeHtml4(clear);
		//re-routing checking
		if (lucky.equals("clicked")) {
			synchronized(statistics) {
				if (statistics.size() != 0) {
					response.sendRedirect(statistics.get(0).getWhere());
					return;
				}
			}
		} else if (clear.equals("clear")) {
			synchronized(searchHistory) {
				searchHistory.clear();
			}
		}
		
		Instant start = Instant.now();
		String search = request.getParameter("search");
		search = search == null ? "" : search;
		search = StringEscapeUtils.escapeHtml4(search);
		
		String exact = request.getParameter("exact");
		exact = exact == null? "" : exact;
		exact = StringEscapeUtils.escapeHtml4(exact);
		
		List<ComparableSearchResult> results = new ArrayList<>();
		if (!search.isBlank() && !search.isEmpty()) {
			
			synchronized(searchHistory) {
				searchHistory.add(search);
			}
			
			synchronized(suggestions) {
				suggestions.add(search);
				if (suggestions.size() > 5) {
					suggestions.remove(0);
				}
			}
			
			results = !exact.isEmpty() && !exact.isBlank() && exact.equals("exact")? crawler.searchQuery(search, true) : crawler.searchQuery(search);
			
			synchronized(webResults) {
				webResults.clear();
				for(ComparableSearchResult result: results) {
					webResults.add(result.getWhere());
				}
			}
		}
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		log.info("Elapsed: " + seconds + " seconds");
		//search statistics
		synchronized(statistics) {
			statistics = results;
		}
		//set time for statistics
		synchronized(elapsedTime) {
			elapsedTime = seconds;
		}
		//reload page
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}