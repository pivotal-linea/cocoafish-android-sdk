package com.cocoafish.sdk;

import org.json.JSONException;
import org.json.JSONObject;

public class CCKeyValuePair extends CCObject {
	private String key;
	private String value;
	private java.util.Date createdDate;
	private java.util.Date updatedDate;

	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public java.util.Date getCreatedDate()
	{
		return createdDate;
	}
	
	public java.util.Date getUpdatedDate()
	{
		return updatedDate;
	}
	
	public CCKeyValuePair(JSONObject jObject) throws CocoafishError {		
		super (jObject);
		try {
			key = jObject.getString("key");
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCKeyValuePair: Missing key");
		}
		try {
			value = jObject.getString("value");
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCKeyValuePair: Missing value");
		}
	
	}
}
	
