package com.bpshparis.hak.ccadalogger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.AllDocsRequest;
import com.cloudant.client.org.lightcouch.DocumentConflictException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class AppendSelectionsServlet
 */
@WebServlet(name = "CCadaLoggerServlet", urlPatterns = { "/CCadaLogger" })
public class CCadaLoggerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Properties props;
	Database db;
	CloudantClient dbClient;
	Map<String, Object> hak = new HashMap<String, Object>();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CCadaLoggerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		props = (Properties) getServletContext().getAttribute("props");
		hak = (Map<String, Object>) getServletContext().getAttribute("hak");
		db = (Database) getServletContext().getAttribute("db");
		dbClient = (CloudantClient) getServletContext().getAttribute("dbClient");
		
		Map<String, Object> reqParms = new HashMap<String, Object>();
		Map<String, Object> datas = new HashMap<String, Object>();
		List<Logger> backupDatas = null;

		try {

			datas.put("FROM", this.getServletName());

			if(ServletFileUpload.isMultipartContent(request)){

				List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				
				
				for (FileItem item : items) {
					if (!item.isFormField()) {
						// item is the file (and not a field)
						if(item.getFieldName().equalsIgnoreCase("backup")){
							BufferedReader br = new BufferedReader(new InputStreamReader(item.getInputStream()));
					        ObjectMapper mapper = new ObjectMapper();
					        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
					        backupDatas = Arrays.asList(mapper.readValue(br, Logger[].class));
						}
					}
					else {
						// item is field (and not a file)
						if (item.isFormField() && item.getFieldName().equalsIgnoreCase("parms")) {
							item.getFieldName();
				            String value = item.getString();
				            reqParms = Tools.fromJSON(new ByteArrayInputStream(value.getBytes()));
				            datas.put("REQ_PARMS", reqParms);
						}
					}
				}
			}
			else {

				reqParms = Tools.fromJSON(request.getInputStream());
				datas.put("REQ_PARMS", reqParms);
				
			}
			
			if(reqParms.containsKey("action")){
				
				String action = ((String) reqParms.get("action")).toLowerCase();
				
				switch(action){
					case "tstdbconn":
						datas.put("RESPONSE", "KO");
						for(String dbName: dbClient.getAllDbs()){
							System.out.println("dbName = " + dbName);
							if(dbName.equalsIgnoreCase(props.getProperty("DB_NAME"))){
								datas.put("RESPONSE", "OK");
								break;
							}
						}
						break;
						
					case "tstsrvconn":
						Object o = ((List<Object>) hak.get("loggers")).get(0);
						Position test = null;
						if(o != null){
							test = getPositions(Tools.toJSON(o)).get(0);
						}
						if(test != null){
							datas.put("RESPONSE", "OK");
						}
						else{
							datas.put("RESPONSE", "KO");
						}
						break;
						
					case "getloggers":
						List<Object> loggers = (List<Object>) hak.get("loggers");
						datas.put("RESPONSE", loggers);
						break;
						
					case "savetodb":
						if(backupDatas != null){
					        String sessionId = request.getSession().getId();
					        datas.putAll(saveToDb(backupDatas, sessionId));
						}
						break;
						
					case "getpositions":
						if(reqParms.containsKey("logger")){
							Map<String, Object> map = ( (Map<String, Object>) reqParms.get("logger"));
							String json = Tools.toJSON(map);
							List<Position> positions = getPositions(json);
							Logger logger = Tools.loggerFromJSON(new ByteArrayInputStream(json.getBytes()));
							logger.setPositions(positions);
							datas.put("RESPONSE", logger);
							break;
						}
						break;
						
					default:
						datas.put("USAGE", props.get("USAGE"));
						break;
					
				}
			}
			else {
				datas.put("USAGE", props.get("USAGE"));
			}
			
		}

		catch(JsonMappingException e){
			
//			String usage =  (String) props.get("USAGE");
			
//			InputStream is = new ByteArrayInputStream(usage.getBytes(StandardCharsets.UTF_8.name()));
			datas.put("USAGE", props.get("USAGE"));
			
//			datas.put("TEST_DB_CONN", Tools.toJSON(new ByteArrayInputStream(((String) props.get("TEST_DB_CONN")).getBytes(StandardCharsets.UTF_8.name()))));
//			datas.put("ANALYZE_USAGE", props.get("ANALYZE_USAGE"));
//			datas.put("WARNING_USAGE", props.get("WARNING_USAGE"));
//			datas.put("UPLOAD_EXAMPLE", props.get("UPLOAD_EXAMPLE"));
//			datas.put("ANALYZE_EXAMPLE", props.get("ANALYZE_EXAMPLE"));
			e.printStackTrace();

		}

		catch(Exception e){
			// TODO Auto-generated catch block
			datas.put("EXCEPTION", e.getClass().getName());
			datas.put("MESSAGE", e.getMessage());
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			datas.put("STACKTRACE", sw.toString());
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
	
	protected Map<String, Object> saveToDb(List<Logger> backupDatas, String sessionId){
		Map<String, Object> result = new HashMap<String, Object>();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.MONTH, 1);

		String date = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);
		String time = c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
		
		Backup backup = new Backup();
		backup.set_id(sessionId + "-" + date + "-" + time);
		backup.setLoggers(backupDatas);
		
		try{
			db.save(backup);
			result.put("RESPONSE", "OK");
		}
		catch (DocumentConflictException dce) {
			Map<String, String> error = new HashMap<String, String>();
			error.put("ERROR", "conflict");
			error.put("REASON", "Document update conflict.");
			result.put("RESPONSE", error);
		}
		
		return result;
	}
	
	protected boolean hostAvailable(){
		boolean result = true;
		
		Socket s;
		
		try {   
			s = new Socket((String) hak.get("ipAddress"), (int) hak.get("port"));
	        if(s.isConnected()){
	        	s.close();    
	        }               
	    } 
	    catch(UnknownHostException e){
	    	// unknown host 
	        result = false;
	        s = null;
	    } 
	    catch (IOException e) {
	    	// io exception, service probably not running 
	        result = false;
	        s = null;
	    } 
	    catch (NullPointerException e) {
	        result = false;
	        s=null;
	    }
		
		return result;
	}
	
	protected List<Position> getPositions(String logger){
		
		List<Position> result = new ArrayList<Position>();
		
		DatagramSocket socket = null ;

	    try{
	    	
			InetAddress host = InetAddress.getByName( (String) hak.get("ipAddress")) ;
			int port =  (int) hak.get("port");

			// Construct the socket
			socket = new DatagramSocket() ;

			// Construct the datagram packet
			byte[] data = logger.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, host, port) ;

			// Send it
			socket.send(packet) ;

			// Set a receive timeout, 2000 milliseconds
			socket.setSoTimeout(2000) ;

			// Prepare the packet for receive
			int packetSize = (int) hak.get("packetSize");
			packet.setData(new byte[packetSize]) ;

			// Wait for a response from the server
			socket.receive(packet) ;

			// get the response
			String json = new String(packet.getData());
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			Response response = mapper.readValue(json, new TypeReference<Response>(){});
			
			result = response.getPositions();

	    }
		catch( Exception e ){
			System.out.println( e ) ;
		}
		finally{
			if( socket != null ){
				socket.close() ;
			}
		}
		return result;
	}

	
}
