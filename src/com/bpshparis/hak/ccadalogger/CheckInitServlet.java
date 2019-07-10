package com.bpshparis.hak.ccadalogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "CheckInit", urlPatterns = { "/CheckInit" })
public class CheckInitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckInitServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Map<String, Object> datas = new HashMap<String, Object>();

		try {
			
			datas.put("FROM", this.getServletName());
			datas.put("INIT", request.getServletContext().getAttribute("init"));
//			// Let see if mails.json exists in /res/mails and load it
//			List<Mail> mails = new ArrayList<Mail>();
//			String mailsPath = getServletContext().getRealPath("/res/mails");
//			Path mailsFile = Paths.get(mailsPath + "/mails.json");
//			if(Files.exists(mailsFile)){
//				InputStream is = new ByteArrayInputStream(Files.readAllBytes(mailsFile));
//				mails = Tools.MailsListFromJSON(is);
//			}
//			datas.put("MAILCOUNT", mails.size());

			datas.put("STATUS", "OK");
				
		}
		
		catch (Exception e) {
			// TODO Auto-generated catch block
			datas.put("STATUS", "KO");
			datas.put("EXCEPTION", e.getClass().getName());
			datas.put("MESSAGE", e.getMessage());
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			datas.put("STACKTRACE", sw.toString());
			e.printStackTrace(System.err);
		}

		finally{
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(Tools.toJSON(datas));
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}