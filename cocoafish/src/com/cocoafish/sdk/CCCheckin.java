package com.cocoafish.sdk;

import org.json.JSONException;
import org.json.JSONObject;

public class CCCheckin extends CCObject {
	private CCUser user;
	private CCPlace place;
	
	public CCUser getUser() {
		return user;
	}
	
	public CCPlace getPlace() {
		return place;
	}
	
	public CCCheckin(JSONObject jObject) throws CocoafishError {
		super(jObject);
		
		try {
			user = new CCUser(jObject.getJSONObject("user"));
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCCheckin: Missing user");
		}
		try {
			place = new CCPlace(jObject.getJSONObject("place"));
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCCheckin: Missing place");
		}
		
		if (createdDate == null) {
			throw new CocoafishError("Invalid Server Response: CCCheckin: Missing createdDate");
		}

	}
	
}


