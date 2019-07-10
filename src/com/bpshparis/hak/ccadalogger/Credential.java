package com.bpshparis.hak.ccadalogger;

import java.util.ArrayList;
import java.util.List;

public class Credential {
	
	String id = "";
	String name = "";
	String apikey = "";
	String url = "";
	String role = "";
	String ipAddress = "";
	List<Logger> loggers = new ArrayList<Logger>();
	int packetSize = 0;
	int port = 0;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getApikey() {
		return apikey;
	}
	public void setApikey(String apikey) {
		this.apikey = apikey;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public List<Logger> getLoggers() {
		return loggers;
	}
	public void setLoggers(List<Logger> loggers) {
		this.loggers = loggers;
	}
	public int getPacketSize() {
		return packetSize;
	}
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
}