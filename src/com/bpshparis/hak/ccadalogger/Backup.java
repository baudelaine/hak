package com.bpshparis.hak.ccadalogger;

import java.util.ArrayList;
import java.util.List;

public class Backup {

	String _id = "";
	String _rev = null;
	List<Logger> loggers = new ArrayList<Logger>();

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_rev() {
		return _rev;
	}
	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	public List<Logger> getLoggers() {
		return loggers;
	}
	public void setLoggers(List<Logger> loggers) {
		this.loggers = loggers;
	}
	
}
