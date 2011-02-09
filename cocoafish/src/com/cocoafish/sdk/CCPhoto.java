package com.cocoafish.sdk;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


public class CCPhoto extends CCObject {
	private String filename;
	private int size;
	private String collectionName;
	private String md5;
	private boolean processed = false;
	private String contentType;
	private HashMap<PhotoSize, String> urls;
	public enum PhotoSize {
		SQUARE,
		THUMB,
		SMALL,
		MEDIUM_500,
		MEDIUM_640,
		ORIGINAL
	};
	
	public String getFilename() {
		return filename;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getCollectionName() {
		return collectionName;
	}
	
	public String getMd5() {
		return md5;
	}

	
	public String getContentType() {
		return contentType;
	}

	public boolean getProcessed() {
		return processed;
	}
	
	public String getUrl(PhotoSize photoSize) {
		return urls.get(photoSize);
	}
	
	public CCPhoto(JSONObject jObject) throws CocoafishError {
		super(jObject);
		try {
			filename = jObject.getString("filename").trim();
		} catch (JSONException e1) {
			throw new CocoafishError("Invalid Server Response: " + this.getClass().getName() + ": Missing filename");
		}
		
		try {
			size = Integer.parseInt(jObject.getString("size").trim());
		} catch (NumberFormatException e) {
			throw new CocoafishError("Invalid Server Response: " + this.getClass().getName() + ": size should be a number");
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: " + this.getClass().getName() + ": missing size");
		}
		
		try {
			collectionName = jObject.getString("collectionName").trim();
		} catch (JSONException e1) {
			throw new CocoafishError("Invalid Server Response: " + this.getClass().getName() + ": Missing collectionName");
		}
		
		try {
			md5 = jObject.getString("md5").trim();
		} catch (JSONException e1) {
			throw new CocoafishError("Invalid Server Response: " + this.getClass().getName() + ": Missing md5");
		}
		
		try {
			processed = new Boolean(jObject.getString("processed")).booleanValue();
		} catch (JSONException e2) {
			throw new CocoafishError("Invalid Server Response: " + this.getClass().getName() + ": Missing updatedAt");
		}
		
		try {
			contentType = jObject.getString("contentType").trim();
		} catch (JSONException e1) {
			if (processed) {
				throw new CocoafishError("Invalid Server Response: " + this.getClass().getName() + ": Missing contentType");
			}
		}
		
		try {
			JSONObject urlObjects = jObject.getJSONObject("urls");
			urls = new HashMap<PhotoSize, String>();
			urls.put(PhotoSize.SQUARE, urlObjects.getString("square").trim());
			urls.put(PhotoSize.THUMB, urlObjects.getString("small").trim());
			urls.put(PhotoSize.SMALL, urlObjects.getString("square").trim());
			urls.put(PhotoSize.MEDIUM_500, urlObjects.getString("square").trim());
			urls.put(PhotoSize.MEDIUM_640, urlObjects.getString("square").trim());
			urls.put(PhotoSize.ORIGINAL, urlObjects.getString("square").trim());
			
		} catch (JSONException e) {
			if (processed) {
				throw new CocoafishError("Invalid Server Response: " + this.getClass().getName() + ": Missing urls");
			}
		}
	}
		
}


