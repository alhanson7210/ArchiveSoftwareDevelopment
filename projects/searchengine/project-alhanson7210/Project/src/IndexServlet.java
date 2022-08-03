import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * write the index as html
 *
 */
public class IndexServlet extends HttpServlet {

	/** Identifier used for serialization (unused). */
	private static final long serialVersionUID = 1L;

	/** The title to use for this web page. */
	private static final String TITLE = "Index Servlet";

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();
	
	/** the word index */
	private MultithreadedWordIndex index;

	/**
	 * Constructor
	 * @param index thread safe index to print results from
	 */
	public IndexServlet(MultithreadedWordIndex index) {
		super();
		this.index = index;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		PrintWriter out = response.getWriter();

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
		out.printf("%n");
		out.printf("<body style=\"background-color:whitesmoke;\">%n");
		out.printf("	<section class=\"hero is-primary is-bold\">%n");
		out.printf("	  <div class=\"hero-body\">%n");
		out.printf("	    <div class=\"container\">%n");
		out.printf("	      <h1 class=\"title\">%n");
		out.printf("	        Inverted Index%n");
		out.printf("	      </h1>%n");
		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");
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
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Locations</h2>%n");
		Map<String, Map<String, Collection<Integer>>> map = index.getUnmodifiableMap();
		for (String stem : map.keySet()) {
			Map<String, Collection<Integer>> count = map.get(stem);
			out.printf("            <h3>Word Stem: %s</h3>", stem);
			out.printf("            <div style=\"height:210px;overflow:scroll;border:dashed black;padding:10px;\">");
			for (Map.Entry<String, Collection<Integer>> entry : count.entrySet()) {
				out.printf("            <div class=\"box\" style=\"background-color:#2f4c8a;color:black;overflow:hidden;\">%n");
				out.printf("                <a href=\"%s\" class\"control\" style=\"color:white;\">%s</a>%n", entry.getKey(), entry.getKey());
				out.printf("                <p>The list of positions for this url is: %s</p>%n", entry.getValue().toString());
				out.printf("	        </div>%n");
			}
			out.printf("            </div>");
			out.printf("            <br>");
		}
		out.printf("		</div>%n");
		out.printf("	</section>%n");
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

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}
}