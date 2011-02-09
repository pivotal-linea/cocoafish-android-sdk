package com.cocoafish.sdk;

import org.json.JSONException;
import org.json.JSONObject;

public class CCStatus extends CCObject {
	private String status;
	
	public String getStatus() {
		return status;
	}
	
	public CCStatus(JSONObject jObject) throws CocoafishError {
		super(jObject);
		
		try {
			status = jObject.getString("status").trim();
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCStatus: Missing status");
		}
	}


}
