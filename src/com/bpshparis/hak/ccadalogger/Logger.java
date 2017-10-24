package com.bpshparis.hak.ccadalogger;


import java.util.ArrayList;
import java.util.List;

public class Logger {

	int flag = 0;
	String uid = "";
	String tid = "";
	List<Position> positions = new ArrayList<Position>();
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public List<Position> getPositions() {
		return positions;
	}
	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}
	
}
