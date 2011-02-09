package com.cocoafish.sdk;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class Cocoafish {

	private static final String COOKIES_FILE = "CocofishCookiesFile";
	private static CookieStore cookieStore = new BasicCookieStore();

	private String appId;
	private CCUser currentUser;
	private static Cocoafish defaultInstance = null;
	private Context curApplicationContext;
	private Facebook authenticatedFacebook = null; 
	private DialogListener customFacebookLoginListener = null; 
	
	public static void initialize(String appId, String facebookAppId, Context context) {
	    if (defaultInstance == null) {
	    	defaultInstance = new Cocoafish(appId, facebookAppId, context);
	    	defaultInstance.loadLoginInfo();
	    }
	}
	
	public static Cocoafish getDefaultInstance() throws CocoafishError {
		if (defaultInstance == null) {
			throw new CocoafishError("Cocoafish is not initialized");
		}
		return defaultInstance;
	}
	
	// private constructor
	private Cocoafish(String appId, String facebookAppId, Context context) {
		this.appId = appId;
		this.curApplicationContext = context;
		if (facebookAppId != null) {
			authenticatedFacebook = new Facebook(facebookAppId);
		}
	}
	
	public void facebookAhtorize(Activity activity, String[] permissions,
            final DialogListener customListener) {
		customFacebookLoginListener = customListener;
		authenticatedFacebook.authorize(activity, permissions, new FacebookLoginListener());
	}
	
	public Facebook getFacebook() {
		return authenticatedFacebook;
	}
	
	public String getAppId() {
		return appId;
	}
	
	public Context getCurApplicationContext() {
		return curApplicationContext;
	}
	
	public void setCurrentUser(CCUser user) {
		currentUser = user;
		if (user != null) {
			saveLoginInfo();
		} else {
			clearLoginInfo();
		}
	}
	
	public CCUser getCurrentUser() {
		return currentUser;
	}
	
	public CookieStore getCookieStore() {
		return cookieStore;
	}
	
	protected void loadLoginInfo() {
		   try {

			   // read the user cookies
			   FileInputStream fis = null;
			   ObjectInputStream in = null;
			 
			   fis = curApplicationContext.openFileInput(COOKIES_FILE);
			   in = new ObjectInputStream(fis);
			
			   currentUser = (CCUser)in.readObject();
			   int size = in.readInt();
			   for (int i = 0; i < size; i++) {
				   SerializableCookie cookie = (SerializableCookie)in.readObject();
		           cookieStore.addCookie(cookie);
			   }
			
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }
	   
	   // Saving current user name after login and signup
	protected void saveLoginInfo() {
		   // We need an Editor object to make preference changes.
		   try {
			   // save the cookies
			   List<Cookie> cookies = cookieStore.getCookies();
		        if (cookies.isEmpty()) {
		            // should not happen when we have a current user but no cookies
		        	return;
		        } else {
		        	final List<Cookie> serialisableCookies = new ArrayList<Cookie>(cookies.size());
		            for (Cookie cookie : cookies) {
		                serialisableCookies.add(new SerializableCookie(cookie));
		            }
		           
		            FileOutputStream fos = null;
		            ObjectOutputStream out = null;
		            try
		            {		            	
		            	fos = curApplicationContext.openFileOutput(COOKIES_FILE, Context.MODE_PRIVATE);
		            	out = new ObjectOutputStream(fos);
		            	out.writeObject(currentUser);
		            	out.writeInt(cookies.size());
		            	for (Cookie cookie : serialisableCookies) {
		            		out.writeObject(cookie);
		            	}
		            	out.flush();
		            	out.close();
		            }
		            catch(IOException ex)
		            {
		            	ex.printStackTrace();
		            }
		            
		        }
			   
			   
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }
	
	protected void clearLoginInfo() {
		curApplicationContext.getFileStreamPath(COOKIES_FILE).delete(); 
	}
	
    public class FacebookLoginListener implements DialogListener {

        public void onComplete(Bundle values) {
        	// login with cocoafish server using facebook access token
        	CCRestfulRequest request = new CCRestfulRequest();
        	try {
				request.facebookUserLogin(authenticatedFacebook.getAccessToken(), authenticatedFacebook.getAppId());
			} catch (IOException e) {
				if (customFacebookLoginListener != null) {
					customFacebookLoginListener.onFacebookError(new FacebookError(e.getLocalizedMessage()));
				}
			} catch (CocoafishError e) {
				if (customFacebookLoginListener != null) {
					customFacebookLoginListener.onFacebookError(new FacebookError(e.getLocalizedMessage()));
				}
			}
			if (customFacebookLoginListener != null) {
				customFacebookLoginListener.onComplete(values);
			}
			customFacebookLoginListener = null;
        }

        public void onCancel() {
			customFacebookLoginListener = null;

        }

        public void onError(DialogError e) {
            e.printStackTrace();
			customFacebookLoginListener = null;

        }

        public void onFacebookError(FacebookError e) {
            e.printStackTrace();
			customFacebookLoginListener = null;
        }
    }
}