package com.cocoafish.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cocoafish.sdk.CCMeta;
import com.cocoafish.sdk.CCRequestMethod;
import com.cocoafish.sdk.CCResponse;
import com.cocoafish.sdk.Cocoafish;
import com.cocoafish.sdk.CocoafishError;

public class UserView extends Activity {

    static final int LAUNCH_SIGNUP = 0;
    List<CCResponse> listOfCheckin;
    private Cocoafish sdk;
    private DemoSession session;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sdk = DemoApplication.getSdk();
        session = DemoApplication.getSession();
        try {
			if ( session.getAttribute("User") == null) {
				showLoginView();
			} else {
            	showUserView();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
  
    @Override
    public void onPause()
    {
    	super.onPause();
    	// Ugly solution to hide the soft keyboard by forcing it to show first, then toggle it
    	InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    	inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 
    			InputMethodManager.HIDE_IMPLICIT_ONLY); 
    	inputManager.toggleSoftInput(0, 0);

    }
    
    protected void performLogin() {
		final ProgressDialog dialog = new ProgressDialog(UserView.this);
		dialog.setMessage("Login...");
	    dialog.show();
	    
    	String login = ((EditText) findViewById(R.id.email_address)).getText().toString();
    	String password = ((EditText) findViewById(R.id.pw)).getText().toString();
    	String errorMsg = null;
    	try {
	    	HashMap<String, Object> dataMap = new HashMap<String, Object>();
	    	dataMap.put("login", login);
	    	dataMap.put("password", password);
	    	CCResponse response = sdk.sendRequest("users/login.json", CCRequestMethod.POST, dataMap, false);
	    	CCMeta meta = response.getMeta();
	    	if( !"OK".equals( meta.getStatus() ) )
	    			throw new CocoafishError(meta.getMessage());
	    	
	    	JSONObject responseJSON = response.getResponseData();
	    	JSONArray usersArr = responseJSON.getJSONArray("users");
	    	JSONObject userInfo = usersArr.getJSONObject(0);
	    	session.setAttribute("User", userInfo);
	    	
	    	showUserView();
		} catch (CocoafishError e) {
			errorMsg = e.getMessage();
    	} catch (JSONException e) {
    		errorMsg = e.getMessage();
    	} 
		dialog.dismiss();
		
		if (errorMsg != null) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("Login Failed");
    		alertDialog.setMessage(errorMsg);
    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    		   public void onClick(DialogInterface dialog, int which) {
    		      // here you can add functions
    			   dialog.dismiss();
    		   }
    		});
    		alertDialog.setIcon(R.drawable.icon);
    		alertDialog.show();
		}
    }
    
    protected void performRefresh() {
    	new GetCheckinsTask().execute();
    }
    
    protected void performLogout() {
	    try {
			CCResponse response = sdk.sendRequest("users/logout.json", CCRequestMethod.GET, null, false);
			
			session.setAttribute("User", null);	
		} catch (CocoafishError e) {
			e.printStackTrace();
		}
		showLoginView();
    }
    
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == LAUNCH_SIGNUP) {
            if (resultCode == RESULT_OK) {
            	showUserView();
            }
        }
    }

    protected void showLoginView()
    {
		setContentView(R.layout.signin);

        View loginButton = findViewById(R.id.signin);
        loginButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		performLogin();
	    }});
        
        View signupButton = findViewById(R.id.signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		startActivityForResult(new Intent(UserView.this, SignUp.class), LAUNCH_SIGNUP);
	    }});
    }
    
    protected void showUserView()
    {
        setContentView(R.layout.userview);
        View refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		performRefresh();
	    }});
        
        View logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		performLogout();
        }});
       
        try {
            TextView name = (TextView)findViewById(R.id.UserName);
            JSONObject userInfo = (JSONObject) session.getAttribute("User");
            name.setText( userInfo.getString("first_name") + " " + userInfo.getString("last_user") );
        } catch (Exception e) {
			e.printStackTrace();
        }
        performRefresh();
    }
  
    protected void showCheckins(List<CCResponse> checkins) {
        
    	if (checkins == null) {
    		return;
    	}
    	InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    		
        ListView list = (ListView)findViewById(R.id.CheckinListView);
        
        CheckinAdapter adapter = new CheckinAdapter(this, checkins, false);
        
        list.setAdapter(adapter);
    }
    
	private class GetCheckinsTask extends AsyncTask<Void, Void, List<CCResponse>> {
		private final ProgressDialog dialog = new ProgressDialog(UserView.this);
	    private String errorMsg = null;
	    protected void onPreExecute()
	    {
	        dialog.setMessage("Loading...");
	        dialog.show();
	    }
	    
	    protected void onPostExecute(List<CCResponse> checkins) {
	    	 if(this.dialog.isShowing())
	         {
	             this.dialog.dismiss();
	         }
	    	 if (errorMsg != null) {
	     		AlertDialog alertDialog = new AlertDialog.Builder(UserView.this).create();
	    		alertDialog.setTitle("Failed");
	    		alertDialog.setMessage(errorMsg);
	    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	    		   public void onClick(DialogInterface dialog, int which) {
	    		      // here you can add functions
	    			   dialog.dismiss();
	    		   }
	    		});
	    		alertDialog.setIcon(R.drawable.icon);
	    		alertDialog.show();
	    	 } else {
	    		 showCheckins(checkins);
	    	 }
	    }

		@Override
		protected List<CCResponse> doInBackground(Void...params) {
			/*CCRestfulRequest request = null;
			List<CCResponse> checkins = null;
			try {
				request = new CCRestfulRequest(DemoApplication.getSdk());
				checkins = request.getCheckinsForUser(DemoApplication.getSdk().getCurrentUser().getObjectId(), 
								CCRestfulRequest.FIRST_PAGE, CCRestfulRequest.DEFAULT_PER_PAGE);
			} catch (CocoafishError e) {
				errorMsg = e.getLocalizedMessage();
			} catch (IOException e) {
				errorMsg = e.getLocalizedMessage();
			}
			return checkins;*/
			return null;
		}
	}

}
