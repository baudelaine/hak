package com.bpshparis.hak.ccadalogger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import com.fasterxml.jackson.databind.ObjectMapper;

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
	Map<String, Object> hak = new HashMap<String, Object>();
	
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
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    	arg0.getServletContext().removeAttribute("ic");
		System.out.println("Context has been destroyed...");    	
    }
    
    public void initVCAP_SERVICES() throws FileNotFoundException, IOException{
    	
    	String value = props.getProperty("VCAP_SERVICES");
    	
    	if(value != null && !value.trim().isEmpty()){
			Path path = Paths.get(realPath + value);
			Charset charset = StandardCharsets.UTF_8;
			if(Files.exists(path)){
				vcap_services = new String(Files.readAllBytes(path), charset);
				System.out.println("VCAP_SERVICES read from " + value + ".");
			}
    	}
    	else{
    		vcap_services = System.getenv("VCAP_SERVICES");
			System.out.println("VCAP_SERVICES read from System ENV.");
    	}

    }
    
    @SuppressWarnings("unchecked")
	public void initDB(){

    	String serviceName = props.getProperty("CLOUDANT_NAME");
    	String dbname = props.getProperty("DB_NAME");
    	
		ObjectMapper mapper = new ObjectMapper();
		
		String url = "";
		String username = "";
		String password = "";
            	
		try {
		
			Map<String, Object> input = mapper.readValue(vcap_services, new TypeReference<Map<String, Object>>(){});
			
			List<Map<String, Object>> l0s = (List<Map<String, Object>>) input.get(serviceName);
			
			for(Map<String, Object> l0: l0s){
				for(Map.Entry<String, Object> e: l0.entrySet()){
					if(e.getKey().equalsIgnoreCase("credentials")){
						System.out.println(e.getKey() + "=" + e.getValue());
						Map<String, Object> credential = (Map<String, Object>) e.getValue();
						url = (String) credential.get("url");
						username = (String) credential.get("username");
						password = (String) credential.get("password");
					}
				}
			}
			
			dbClient = ClientBuilder.url(new URL(url))
			        .username(username)
			        .password(password)
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
    
    @SuppressWarnings("unchecked")
	public void initHAK(){

    	String serviceName = props.getProperty("HAK_NAME");
    	
		ObjectMapper mapper = new ObjectMapper();
		
		String ipAddress = "";
		int port = 0;
		int packetSize = 0;
		List<Object> loggers = new ArrayList<Object>();
            	
		try {
		
			Map<String, Object> input = mapper.readValue(vcap_services, new TypeReference<Map<String, Object>>(){});
			
			List<Map<String, Object>> l0s = (List<Map<String, Object>>) input.get(serviceName);
			
			for(Map<String, Object> l0: l0s){
				for(Map.Entry<String, Object> e: l0.entrySet()){
					if(e.getKey().equalsIgnoreCase("credentials")){
						System.out.println(e.getKey() + "=" + e.getValue());
						Map<String, Object> credential = (Map<String, Object>) e.getValue();
						ipAddress = (String) credential.get("ipAddress");
						port = (Integer) credential.get("port");
						packetSize = (Integer) credential.get("packetSize");
						loggers = (List<Object>) credential.get("loggers");
					}
				}
			}
			
			hak.put("ipAddress", ipAddress);
			hak.put("port", port);
			hak.put("packetSize", packetSize);
			hak.put("loggers", loggers);

			System.out.println("Hear And Know service initialized: UDP://" + hak.get("ipAddress") + ":" + 
					hak.get("port") + " with " + ( (List<Object>) hak.get("loggers")).size() + " loggers.");
			
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
    	
    }    
    
}
