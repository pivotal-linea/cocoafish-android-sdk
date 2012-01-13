package com.cocoafish.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class CCMeta implements Serializable {
	private String status;
	private int code;
	private String message;
	private String method;
	
	// Pagination info (optional)
	private CCPagination pagination;

	public String getStatus() {
		return status;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getMethod() {
		return method;
	}
	
	public CCPagination getPagination() {
		return pagination;
	}
	
	public CCMeta(JSONObject jObject) throws CocoafishError {
		try {
			status = jObject.getString("status").trim();
		} catch (JSONException e1) {
			throw new CocoafishError("Invalid Server Response: CCMeta: Missing stat");
		}
		try {
			code =  Integer.parseInt(jObject.getString("code").trim());
		} catch (NumberFormatException e1) {
			throw new CocoafishError("Invalid Server Response: CCMeta: code should be a number");
		} catch (JSONException e1) {
			throw new CocoafishError("Invalid Server Response: CCMeta: Missing code");
		}
		
		try {
			// optional
			message = jObject.getString("message").trim();
		} catch (Exception e) {
		}
		
		try {
			method = jObject.getString("method_name").trim();
		} catch (Exception e) {
		}
		
		try {
			pagination = new CCPagination(jObject);
		} catch (CocoafishError e) {
			
		}
	}
}