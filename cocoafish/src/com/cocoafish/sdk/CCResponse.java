package com.cocoafish.sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class CCResponse implements Serializable {
	protected CCMeta meta;
	protected transient JSONObject responseData;
    protected String responseDataString;
	protected CCResponse[] compoundResponses;
	
	public CCMeta getMeta()
	{
		return meta;
	}
	
	public JSONObject getResponseData()
	{
		return responseData;
	}

    public String getResponseDataString()
    {
        return responseDataString;
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
            responseDataString = responseData.toString();
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
}

