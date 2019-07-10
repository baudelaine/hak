package com.bpshparis.hak.ccadalogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Application Lifecycle Listener implementation class ContextListener
 *
 */
@WebListener
public class ContextListener implements ServletContextListener {

	InitialContext ic;
	String vcap_services;
	String realPath;
	Properties props = new Properties();
	Database db;
	CloudantClient dbClient;
//	Map<String, Object> hak = new HashMap<String, Object>();
	Resource hak = new Resource();
	List<Resource> resources;
	Map<String, Object> init = new HashMap<String, Object>();	
	
    /**
     * Default constructor. 
     */
    public ContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
       	try {
			ic = new InitialContext();
			arg0.getServletContext().setAttribute("ic", ic);
			realPath = arg0.getServletContext().getRealPath("/"); 
	    	props.load(new FileInputStream(realPath + "/res/conf.properties"));
			arg0.getServletContext().setAttribute("props", props);
	    	
			System.out.println("Context has been initialized...");
			
			initVCAP_SERVICES();
			System.out.println("VCAP_SERVICES has been initialized...");

			initDB();
			System.out.println("DB has been initialized...");
			arg0.getServletContext().setAttribute("db", db);
			System.out.println("DBCLIENT has been initialized...");
			arg0.getServletContext().setAttribute("dbClient", dbClient);

			initHAK();
			System.out.println("HAK has been initialized...");
			arg0.getServletContext().setAttribute("hak", hak);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	finally {
			arg0.getServletContext().setAttribute("init", init);
		}       	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    	arg0.getServletContext().removeAttribute("ic");
		System.out.println("Context has been destroyed...");    	
    }
    
    @SuppressWarnings("unchecked")
	public void initVCAP_SERVICES() throws Exception{
    	
		vcap_services = System.getenv("VCAP_SERVICES");
		System.out.println("VCAP_SERVICES read from System ENV.");

		System.out.println("vcap_services=" + vcap_services);
		
		if(vcap_services == null) {
			init.put("STATUS", "KO");
			init.put("RESULT","Watson services are not reachable.");
			init.put("TROUBLESHOOTING","VCAP_SERVICES environment variable should display something like: {\"0\":{\"credentials\":[{\"id\":\"0\",\"name\":\"Auto-generated service credentials\",\"apikey\":\"4Pe0RUDXt......");
			String msg = "VCAP_SERVICES is null";
			init.put("MSG", msg);
			throw new Exception(msg);
		}
		
		try {
			Map<String, Resource> json = (Map<String, Resource>) Tools.fromJSON(vcap_services, new TypeReference<Map<String, Resource>>(){});
			resources = Arrays.asList(json.values().toArray(new Resource[0]));
			init.put("STATUS", "OK");
			init.put("VCAP_SERVICES", json);
		}
		catch(Exception e) {
			init.put("STATUS", "KO");
			init.put("RESULT","Watson services are not reachable.");
			init.put("TROUBLESHOOTING","VCAP_SERVICES environment variable should display something like: {\"0\":{\"credentials\":[{\"id\":\"0\",\"name\":\"Auto-generated service credentials\",\"apikey\":\"4Pe0RUDXt......");
			String msg = "No valid resource were set.";
			init.put("MSG", msg);
			throw new Exception(msg);
		}
    }
    
    public void initDB(){

    	String serviceName = props.getProperty("CLOUDANT_NAME");
    	String dbname = props.getProperty("DB_NAME");
    	
		String url = "";
//		String username = "apikey";
//		String password = "";
            	
		try {
		
			
			for(Resource resource: resources) {
				if(resource.getService().equalsIgnoreCase(serviceName)) {
//					password = resource.getCredentials().get(0).getApikey();
					url = resource.getCredentials().get(0).getUrl();
				}
			}			
			
			dbClient = ClientBuilder.url(new URL(url))
//			        .username(username)
//			        .password(password)
			        .build();
		
			System.out.println("Server Version: " + dbClient.serverVersion());
			
			db = dbClient.database(dbname, true);
			
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
    	
    }    
    
    public void initHAK(){

    	try {
    		
	    	String serviceName = props.getProperty("HAK_NAME");
	
			for(Resource resource: resources) {
				if(resource.getService().equalsIgnoreCase(serviceName)) {
					hak = resource;
				}
			}			
	    	
			System.out.println("Hear And Know service initialized: UDP://" + hak.getCredentials().get(0).getIpAddress() + ":" + 
					hak.getCredentials().get(0).getPort() + " with " + ( hak.getCredentials().get(0).getLoggers().size() + " loggers."));
			
		}
		catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
    	
    }    
    
}
