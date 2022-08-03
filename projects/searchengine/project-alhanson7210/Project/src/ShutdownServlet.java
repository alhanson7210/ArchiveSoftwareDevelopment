import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
//import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Shutdown the server
 *
 */
public class ShutdownServlet extends HttpServlet {

	/** Identifier used for serialization (unused). */
	private static final long serialVersionUID = 1L;

	/** The title to use for this web page. */
	private static final String TITLE = "Shutdown Servlet";

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();
	
	/** server */
	private Server server;
	
	/** handlers */
	private HandlerList handlers;

	/**
	 * Constructor
	 * @param server needed server resource to shutdown
	 * @param handlers needed resource handlers to shutdown
	 */
	public ShutdownServlet(Server server, HandlerList handlers) {
		super();
		this.server = server;
		this.handlers = handlers;
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
		out.printf("	        Administrator Shutdown%n");
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
		out.printf("			<h2 class=\"title\">Shutdown server</h2>%n");
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("					<label class=\"label\">Username</label>%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf("						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter username here.\">%n", "username");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-user\"></i>%n");
		out.printf("						</span>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"field\">%n");
		out.printf("				  <label class=\"label\">Password</label>%n");
		out.printf("				  <div class=\"control\">%n");
		out.printf("				    <input class=\"input\" type=\"password\" name=\"%s\" placeholder=\"Enter password here.\">%n", "password");
		out.printf("				  </div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-comment\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						Shutdown server%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");
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
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		username = username == null? "" : username;
		password = password == null? "" : password;
		
		username = StringEscapeUtils.escapeHtml4(username);
		password = StringEscapeUtils.escapeHtml4(password);
		if (username.equals("root") && password.equals("rootaccess")) {
			try {
				new Thread() {
	                @Override
	                public void run() {
	                    try {
	                        server.stop();
	                        handlers.stop();
	                        log.info("Successfully stopped Jetty");
	                    } catch (Exception ex) {
	                        log.info("Failed to stop Jetty");
	                    }
	                }
	            }.start();
			} finally {
				log.info("Checked shutdown");
			}
		}
		response.sendRedirect(request.getServletPath());
	}
}