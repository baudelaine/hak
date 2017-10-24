package com.bpshparis.hak.ccadalogger;

import java.util.ArrayList;
import java.util.List;

public class Response {
	int flag = 0;
	List<Position> positions = new ArrayList<Position>();
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public List<Position> getPositions() {
		return positions;
	}
	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}
	
}
