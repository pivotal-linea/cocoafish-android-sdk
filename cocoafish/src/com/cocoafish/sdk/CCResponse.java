package com.cocoafish.sdk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CCResponse {
	protected CCMeta meta;
	protected JSONObject responseData;
	protected CCResponse[] compoundResponses;
	
	public CCMeta getMeta()
	{
		return meta;
	}
	
	public JSONObject getResponseData()
	{
		return responseData;
	}
	
	public CCResponse[] getCompoundResponses()
	{
		return compoundResponses;
	}
	
	public CCResponse(JSONObject jObject) throws CocoafishError 
	{
		try {
			meta = new CCMeta(jObject.getJSONObject("meta"));
		} catch (JSONException e1) {
			throw new CocoafishError("Invalid Server Response: Response missing meta");
		} catch (Exception e1) {
			throw new CocoafishError(e1.getLocalizedMessage());
		}
		try {
			responseData = jObject.getJSONObject("response");
			// check if this is a compound response
			JSONArray responseArray = responseData.getJSONArray("responses");
			if (responseArray.length() > 0) {
				compoundResponses = new CCResponse[responseArray.length()];
				for (int i = 0; i < responseArray.length(); i++) {  
					CCResponse tmpResponse = new CCResponse(responseArray.getJSONObject(i));
					compoundResponses[i] = tmpResponse;
				}
			}
		} catch (Exception e) {
			
		}
		 
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getArrayOfObjects(String tagName) throws CocoafishError {
		List<T> returnArray = null;
		try {
			JSONArray jsonObjArray = responseData.getJSONArray(tagName);
			returnArray = new ArrayList<T>(jsonObjArray.length());
			for (int i = 0; i < jsonObjArray.length(); i++) {
				T curObj = null;
				if (tagName == "users") {
					curObj = (T) new CCUser(jsonObjArray.getJSONObject(i));
				} else if (tagName == "places") {
					curObj = (T) new CCPlace(jsonObjArray.getJSONObject(i));
				} else if (tagName == "checkins") {
					curObj = (T) new CCCheckin(jsonObjArray.getJSONObject(i));
				} else if (tagName == "statuses") {
					curObj = (T) new CCStatus(jsonObjArray.getJSONObject(i));
				} else if (tagName == "photos") {
					curObj = (T) new CCPhoto(jsonObjArray.getJSONObject(i));
				} else if (tagName == "keyvalues") {
					curObj = (T) new CCKeyValuePair(jsonObjArray.getJSONObject(i));
				}
				returnArray.add(curObj);
			}
		} catch (Exception e) {
			throw new CocoafishError(e.getLocalizedMessage());
		}
		
		return returnArray;
	}
	
	public List<CCUser> getUsersFromResponse() throws CocoafishError {
		List<CCUser> users = getArrayOfObjects("users");
		return users;
	}
	
	public List<CCPlace> getPlacesFromResponse() throws CocoafishError {
		List<CCPlace> places = getArrayOfObjects("places");
		return places;
	}
	
	public List<CCCheckin> getCheckinsFromResponse() throws CocoafishError  {
		List<CCCheckin> checkins = getArrayOfObjects("checkins");
		return checkins;
	}
	
	public List<CCStatus> getStatusesFromResponse() throws CocoafishError  {
		List<CCStatus> statuses = getArrayOfObjects("statuses");
		return statuses;
	}
	
	public List<CCPhoto> getPhotosFromResponse() throws CocoafishError  {
		List<CCPhoto> photos = getArrayOfObjects("photos");
		return photos;
	}
	
	public List<CCKeyValuePair> getKeyvaluesFromResponse() throws CocoafishError  {
		List<CCKeyValuePair> keyvalues = getArrayOfObjects("keyvalues");
		return keyvalues;
	}
}

