package com.cocoafish.demo;

import java.util.HashMap;
import java.util.Map;

public class DemoSession {

	private Map<String, Object> map = null;
	
	public DemoSession(){
		map = new HashMap<String, Object>();
	}
	
	public void setAttribute(String key, Object value){
		if(value != null)
			map.put(key, value);
		else 
			map.remove(key);
	}
	
	public Object getAttribute(String key){
		return map.get(key);
	}

	public Map<String, Object> getMap() {
		return map;
	}
	
}
