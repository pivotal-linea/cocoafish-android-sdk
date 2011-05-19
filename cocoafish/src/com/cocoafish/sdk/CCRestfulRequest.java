package com.cocoafish.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CCRestfulRequest {
   private static final String BACKEND_URL = "http://api.cocoafish.com/v1";
   private static final String TAG = "CCRestfulTask";
   private static HttpClient httpClient = new DefaultHttpClient();
   public static final int DEFAULT_PER_PAGE = 10;
   public static final int FIRST_PAGE = 1;
   private CCResponse response = null;
   
   public synchronized CCResponse getResponse() {
	   return response;
   }
   
   public CCRestfulRequest()  {
   }
   
   public synchronized void logoutUser() throws IOException {
	   
	   try {
		String requestUrl = BACKEND_URL + "/users/logout.json?key=" + Cocoafish.getDefaultInstance().getAppId();

		response = performGet(requestUrl);
		Cocoafish.getDefaultInstance().setCurrentUser(null);

	   } catch (CocoafishError e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
	   
   }
   
   public synchronized CCUser facebookUserLogin(String accessToken, String facebookAppId) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/fb_login.json?key=" + Cocoafish.getDefaultInstance().getAppId();

	// Create post Data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
	   nameValuePairs.add(new BasicNameValuePair("facebook_app_id", facebookAppId));
	   
	   response = performPost(requestUrl, nameValuePairs);
	   
	   CCUser user = null;
	   if (response != null) {
		   if (response.getMeta().getCode() != 200) {
			   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
		   }
		   List<CCUser> users;
		   try {
			   users = response.getUsersFromResponse();
			   if (users != null && users.size() > 0) {
				   // set the current User object;
				   user = users.get(0);
				   Cocoafish.getDefaultInstance().setCurrentUser(user);
			   }
		   } catch (Exception e) {
			   throw (new CocoafishError(e.getLocalizedMessage()));
		   }
	   }
	   return user;   }
   
   public synchronized CCUser loginUser(String login, String password) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/users/login.json?key=" + Cocoafish.getDefaultInstance().getAppId();
	   
	   // Create post Data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("login", login));
	   nameValuePairs.add(new BasicNameValuePair("password", password));
	   
	   response = performPost(requestUrl, nameValuePairs);
	   
	   CCUser user = null;
	   if (response != null) {
		   if (response.getMeta().getCode() != 200) {
			   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
		   }
		   List<CCUser> users;
		   try {
			   users = response.getUsersFromResponse();
			   if (users != null && users.size() > 0) {
				   // set the current User object;
				   user = users.get(0);
				   Cocoafish.getDefaultInstance().setCurrentUser(user);
			   }
		   } catch (Exception e) {
			   throw (new CocoafishError(e.getLocalizedMessage()));
		   }
	   }
	   return user;
   }
   
   public synchronized CCUser registerUser(String email, String userName, String first, String last, String password) throws CocoafishError, IOException {
	   
	   // construct the request url
	   String requestUrl = 	BACKEND_URL + "/users/create.json?key=" + Cocoafish.getDefaultInstance().getAppId();
	   
	   // Add post data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("email", email));
	   nameValuePairs.add(new BasicNameValuePair("username", userName));
	   nameValuePairs.add(new BasicNameValuePair("first_name", first));
	   nameValuePairs.add(new BasicNameValuePair("last_name", last));
	   nameValuePairs.add(new BasicNameValuePair("password", password));
	   nameValuePairs.add(new BasicNameValuePair("password_confirmation", password));

	   response = performPost(requestUrl, nameValuePairs);

	   CCUser user = null;
	   if (response != null) {
		   if (response.getMeta().getCode() != 200) {
			   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
		   }
		   List<CCUser> users;
		   try {
			   users = response.getUsersFromResponse();
			   if (users != null && users.size() > 0) {
				   // set the current User object;
				   user = users.get(0);
				   Cocoafish.getDefaultInstance().setCurrentUser(user);
			   }
		   } catch (Exception e) {
			   throw (new CocoafishError(e.getLocalizedMessage()));
		   }
	   }
	   return user;
	}
   
   public synchronized void deleteUser() throws CocoafishError, IOException {
	   // construct the request url
	   String requestUrl = 	BACKEND_URL + "/users.json?app=" + Cocoafish.getDefaultInstance().getAppId();
	 
	   response = performDelete(requestUrl);
	   
	   if (response != null && response.meta.getCode() == 200) {
		   // delete was successful
		   Cocoafish.getDefaultInstance().setCurrentUser(null);
	   }

   }

   public synchronized List<CCPlace> getPlaces(int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/search.json?key=" + 
	   				Cocoafish.getDefaultInstance().getAppId()  + 
	   				"&page="+page+"&per_page=" + perPage;
	   response = performGet(requestUrl);
	   List<CCPlace> places = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   places = response.getPlacesFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return places;
   }
 
   public synchronized List<CCCheckin> getCheckinsForUser(String userId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/checkins/search.json?key=" + Cocoafish.getDefaultInstance().getAppId() + "&user_id=" + userId+
	   		"&page="+page+"&per_page=" + perPage;
	   response = performGet(requestUrl);
	   List<CCCheckin> checkins = null;

	   if (response != null && response.getMeta().getCode() == 200) {
		   checkins = response.getCheckinsFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return checkins;
   }
   
   public synchronized List<CCCheckin> getCheckinsForPlace(String placeId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/checkins/search.json?key=" + Cocoafish.getDefaultInstance().getAppId() + "&place_id=" + placeId +
	   		"&page="+page+"&per_page=" + perPage;
	   response = performGet(requestUrl);
	   
	   List<CCCheckin> checkins = null;

	   if (response != null && response.getMeta().getCode() == 200) {
		   checkins = response.getCheckinsFromResponse();
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return checkins;
   }
   
   public synchronized CCCheckin checkinPlace(String placeId) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/checkins/create.json?key=" + Cocoafish.getDefaultInstance().getAppId() + "&place_id="+placeId;
	   
	   CCCheckin checkin = null;
	   response = performPost(requestUrl, null);
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCCheckin> checkins = response.getCheckinsFromResponse();
		   if (checkins.size() > 0) {
			   checkin = checkins.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return checkin;
   }
   
   // user must be logged in
   public synchronized CCStatus createUserStatus(String newStatus) throws CocoafishError, IOException {
	   CCUser currentUser = Cocoafish.getDefaultInstance().getCurrentUser();
	   String requestUrl = BACKEND_URL + "/users/" + currentUser.getObjectId() + 
	   			"/status.json?key=" + Cocoafish.getDefaultInstance().getAppId();
	  
	   // Add post data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("status", newStatus));

	   CCStatus status = null;
	   response = performPost(requestUrl, nameValuePairs);
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCStatus> statuses = response.getStatusesFromResponse(); 
		   if (statuses.size() > 0) {
			   status = statuses.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return status;
   }
   
   public synchronized List<CCStatus> getUserStatuses(String userId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/users/" + userId + "/status/search.json?key=" + 
	   			Cocoafish.getDefaultInstance().getAppId()  + 
	   			"&page="+page+"&per_page=" + perPage;
	   response = performGet(requestUrl);
	   List<CCStatus> statuses = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   statuses = response.getStatusesFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return statuses;
   }
   
   public synchronized CCPlace getPlace(String placeId) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + placeId +".json?app=" + 
	   		Cocoafish.getDefaultInstance().getAppId();
	   
	   CCPlace place = null;
	   response = performGet(requestUrl);
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCPlace> places = response.getPlacesFromResponse(); 
		   if (places.size() > 0) {
			   place = places.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return place;
   }
   
   public synchronized CCStatus createPlaceStatus(String placeId, String newStatus) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + placeId + 
	   		"/status.json?app=" + Cocoafish.getDefaultInstance().getAppId();
	  
	   // Add post data
	   List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	   nameValuePairs.add(new BasicNameValuePair("status", newStatus));

	   response = performPost(requestUrl, nameValuePairs);
	   CCStatus status = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCStatus> statuses = response.getStatusesFromResponse(); 
		   if (statuses.size() > 0) {
			   status = statuses.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return status;
   }
   
   public synchronized List<CCStatus> getPlaceStatus(String placeId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + placeId + 
	   		"/status.json?app=" + Cocoafish.getDefaultInstance().getAppId() + 
	   		"&page="+page+"&per_page=" + perPage;
	   
	   response = performGet(requestUrl);
	   List<CCStatus> statuses = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   statuses = response.getStatusesFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return statuses;
   }
   
   public synchronized CCPhoto uploadPhoto(CCPlace place, File photoFile) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + place.getObjectId() + 
  		"/photos.json?app=" + Cocoafish.getDefaultInstance().getAppId();
	   
	   response = performUploadPhoto(requestUrl, null, photoFile);
	   CCPhoto photo = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCPhoto> photos = response.getPhotosFromResponse(); 
		   if (photos.size() > 0) {
			   photo = photos.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return photo;
   }
   
   public synchronized List<CCPhoto> getPhotosForPlace(String placeId, int page, int perPage) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/places/" + placeId + 
  		"/photos.json?app=" + Cocoafish.getDefaultInstance().getAppId() + 
  		"&page="+page+"&per_page=" + perPage;
  
	   response = performGet(requestUrl);
	   List<CCPhoto> photos = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   photos = response.getPhotosFromResponse(); 
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return photos;
   }
   
   public synchronized CCPhoto getPhoto(String photoId) throws CocoafishError, IOException {
	   String requestUrl = BACKEND_URL + "/photos/" + photoId + 
  			".json?app=" + Cocoafish.getDefaultInstance().getAppId();
  
	   response = performGet(requestUrl);
	   CCPhoto photo = null;
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCPhoto> photos = response.getPhotosFromResponse(); 
		   if (photos.size() > 0) {
			   photo = photos.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }
	   return photo;
   }
   
   public synchronized void storeKeyValue(String key, String value) throws CocoafishError, IOException {
	   	String requestUrl = BACKEND_URL + "/keyvalues.json?app=" + Cocoafish.getDefaultInstance().getAppId();
	   	
	    // Create post Data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("key", key));
		nameValuePairs.add(new BasicNameValuePair("value", value));

	   	response = performPost(requestUrl, nameValuePairs);
	   	
	   	if (response != null && response.getMeta().getCode() != 200) {
	   		throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   	}
 
   }
   
   public synchronized CCKeyValuePair getKeyValuePair(String key) throws CocoafishError, IOException {
	   String encodedKey = null;
	   try {
		   encodedKey = URLEncoder.encode(key, "UTF-8");
	   } catch (UnsupportedEncodingException e) {
		   throw new CocoafishError("Failed to encode key: " + key +". " + e.getLocalizedMessage());
	   }
	   String requestUrl = BACKEND_URL + "/keyvalues.json?app=" + Cocoafish.getDefaultInstance().getAppId() +
		   "&key="+encodedKey;
	   response = performGet(requestUrl);
	   CCKeyValuePair keyvalue = null;	
	   if (response != null && response.getMeta().getCode() == 200) {
		   List<CCKeyValuePair> keyvalues = response.getKeyvaluesFromResponse();
		   if (keyvalues.size() > 0) {
			   keyvalue = keyvalues.get(0);
		   }
	   } else if (response != null) {
		   throw (new CocoafishError(response.getMeta().getMessage(), response.getMeta().getCode()));
	   }   
	   return keyvalue;
   }
   
   private static String convertStreamToString(InputStream is) {
       /*
        * To convert the InputStream to String we use the BufferedReader.readLine()
        * method. We iterate until the BufferedReader return null which means
        * there's no more data to read. Each line will appended to a StringBuilder
        * and returned as String.
        */
       BufferedReader reader = new BufferedReader(new InputStreamReader(is));
       StringBuilder sb = new StringBuilder();

       String line = null;
       try {
           while ((line = reader.readLine()) != null) {
               sb.append(line + "\n");
           }
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try {
               is.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return sb.toString();
   }
   
   public static CCResponse performGet(String urlString) throws CocoafishError, IOException {
	   return performRestful(urlString, "GET", null, null);
   }
   
   public static CCResponse performDelete(String urlString) throws CocoafishError, IOException {
	   return performRestful(urlString, "DELETE", null, null);
   }
   
   public static CCResponse performPost(String urlString, List<NameValuePair> nameValuePairs) throws CocoafishError, IOException {
	   return performRestful(urlString, "POST", nameValuePairs, null);
   }
   
   public static CCResponse performUploadPhoto(String urlString, List<NameValuePair> nameValuePairs, File photoFile) throws CocoafishError, IOException {
	   return performRestful(urlString, "POST", nameValuePairs, photoFile);
   }
   
   protected static CCResponse performRestful(String urlString, String requestType, 
		   List<NameValuePair> nameValuePairs, File file) throws CocoafishError, IOException 
   {
	   CCResponse response = null;
	   Log.d(TAG, "performRestufl: " + requestType + ": " + urlString);
	   
	   try {
		   URL url = new URL(urlString);
		   HttpContext localContext = new BasicHttpContext();
	       localContext.setAttribute(ClientContext.COOKIE_STORE, Cocoafish.getDefaultInstance().getCookieStore());
	       HttpResponse httpResponse = null;
	       if (requestType == "POST" ) {
	    	   HttpPost pagePost = new HttpPost(url.toURI());
	    	   if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
	    		   pagePost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	    	   }
	    	   if (file != null) {
	    			MultipartEntity entity = new MultipartEntity();
	    			entity.addPart("file", new FileBody(file));
	    			pagePost.setEntity(entity);

	    	   }
	    	   httpResponse = httpClient.execute(pagePost, localContext);
	       } else if (requestType == "GET") {
	    	   HttpGet pageGet = new HttpGet(url.toURI());
	    	   httpResponse = httpClient.execute(pageGet, localContext);
	       } else if (requestType == "DELETE") {
	    	   HttpDelete pageDelete = new HttpDelete(url.toURI());
		       httpResponse = httpClient.execute(pageDelete, localContext);
	       }
	       HttpEntity entity = httpResponse.getEntity();
	     
	       if (entity != null) {

	    	   // A Simple JSON Response Read
	           InputStream instream = entity.getContent();
	           String result= convertStreamToString(instream);
	           Log.d(TAG,result);

	           // A Simple JSONObject Creation
	    	   JSONObject jsonObject = new JSONObject(result);
	           Log.d(TAG,"<jsonobject>\n"+jsonObject.toString()+"\n</jsonobject>");
			   response = new CCResponse(jsonObject);
	       }
	   } catch (URISyntaxException e) {
		   throw new CocoafishError(e.getLocalizedMessage());
	   } catch (JSONException e) {
		   throw new CocoafishError("Invalid server response: " + e.getLocalizedMessage());
	   }
	   
	   return response;
   }
   
}
