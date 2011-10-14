package com.cocoafish.test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.james.mime4j.field.datetime.DateTime;
import org.json.JSONObject;

import com.cocoafish.sdk.CCRequestMethod;
import com.cocoafish.sdk.CCResponse;
import com.cocoafish.sdk.Cocoafish;
import com.cocoafish.sdk.CocoafishError;

public class TestDriver {
	public static final String APP_ID = "MAuUFCbReJmCAzelmEGIktMBlCmI2I7R";
	public static final String FACEBOOK_APP_ID = "109836395704353";

	// test data of the user
	private static final String USER_PASSWORD = "password";
	private static final String USER_PASSWORD_CONFIRM = "password_confirmation";
	private static final String USER_NAME = "username";
	private static final String USER_EMAIL = "email";
	private static final String USER_FIRSTNAME = "first_name";
	private static final String USER_LASTNAME = "last_name";
	private static final String USER_ROLE = "role";
	private static final String USER_LOGIN = "login";
	private static final String ADDRESS = "address";
	private static final String CITY = "city";
	private static final String NAME = "name";
	private static final String PER_PAGE = "per_page";
	private static final String PLACE_ID = "place_id";
	
	private static final String URL_CREATE_USER = "users/create.json";
	private static final String URL_LOGIN_USER = "users/login.json";
	private static final String URL_UPDATE_USER = "users/update.json";
	private static final String URL_LOGOUT_USER = "users/logout.json";
	private static final String URL_SEARCH_PLACES = "places/search.json";
	private static final String URL_CREATE_PLACE = "places/create.json";
	private static final String URL_DELETE_PLACE = "places/delete.json";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
		// basic test
		// create user ( with photo )
		TestDriver td = new TestDriver();
		// create user ( without photo )
		
		// login user
		
		// logout user
		
		// delete user
		
		// update user
		
		// search places ( per_page = 10 )
	}
	
	public TestDriver(){
		sdk = new Cocoafish(APP_ID);
	}
	
	public TestDriver(Cocoafish fish){
		sdk = fish;
	}
	
	public void testSDK(){
		
		loginUser("jerrydong355", "Pass1234");
		deletePlace();
		//searchPlaces();
		//updateUser();
		logoutUser();
		int stop = 0;
		stop++;
	}
	
	public void loginUser(String username, String password){
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(USER_LOGIN, username);
		dataMap.put(USER_PASSWORD, password);
		
		try {
			CCResponse response = sdk.sendRequest( URL_LOGIN_USER, CCRequestMethod.POST, dataMap, isSecure);
			System.out.println(response);
		} catch (CocoafishError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logoutUser(){
		try {
			CCResponse response = sdk.sendRequest( URL_LOGOUT_USER, CCRequestMethod.GET, null, isSecure);
		} catch (CocoafishError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateUser(){
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(USER_FIRSTNAME, "Jerry_updated_09130918");
		dataMap.put(USER_LASTNAME, "Dong_updated_09130918");
		try {
			CCResponse response = sdk.sendRequest( URL_UPDATE_USER, CCRequestMethod.PUT, dataMap, isSecure);
		}  catch (CocoafishError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createUser(String username, boolean hasphoto){
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(USER_NAME, username);
		dataMap.put(USER_PASSWORD, "Pass1234");
		dataMap.put(USER_PASSWORD_CONFIRM, "Pass1234");
		dataMap.put(USER_FIRSTNAME, "Jerry");
		dataMap.put(USER_LASTNAME, "Dong");
		if(hasphoto)
		{
			File photo = new File("sdcard/Pictures/sunset.jpg"); 
			if(photo.isFile()){
				System.out.println("Congratulation!");
			}
			dataMap.put("photo", photo);
		}
		try {
			CCResponse response = sdk.sendRequest( URL_CREATE_USER, CCRequestMethod.POST, dataMap, isSecure);
		} catch (CocoafishError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void searchPlaces(){
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(PER_PAGE, "3");
		try {
			CCResponse response = sdk.sendRequest( URL_SEARCH_PLACES, CCRequestMethod.GET, dataMap, isSecure);
		}  catch (CocoafishError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createPlace(){
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(NAME, "place created by TestDriver");
		dataMap.put(ADDRESS, "598 Haight Street");
		dataMap.put(CITY, "San Francisco");
		try {
			CCResponse response = sdk.sendRequest( URL_CREATE_PLACE, CCRequestMethod.POST, dataMap, isSecure);
		}  catch (CocoafishError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deletePlace(){
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(PLACE_ID, "4e6ecbd36f70954271000f00");
		try {
			CCResponse response = sdk.sendRequest( URL_DELETE_PLACE, CCRequestMethod.DELETE, dataMap, isSecure);
		}  catch (CocoafishError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Cocoafish sdk;
	private boolean isSecure = false;
}
