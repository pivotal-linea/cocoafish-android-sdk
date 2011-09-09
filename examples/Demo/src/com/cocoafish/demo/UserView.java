package com.cocoafish.demo;

import java.io.IOException;
import java.util.List;

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

import com.cocoafish.sdk.*;

public class UserView extends Activity {

    static final int LAUNCH_SIGNUP = 0;
    List<CCCheckin> listOfCheckin;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
			if (Cocoafish.getDefaultInstance().getCurrentUser() == null) {
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
	    	CCRestfulRequest signinRequest = new CCRestfulRequest(Cocoafish.getDefaultInstance());
	    	signinRequest.loginUser(login, password);
        	showUserView();
		} catch (CocoafishError e) {
			errorMsg = e.getMessage();
    	} catch (IOException e) {
			errorMsg = "Network Error: " + e.getLocalizedMessage();
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
	    CCRestfulRequest logoutRequest = new CCRestfulRequest(Cocoafish.getDefaultInstance());
	    try {
			logoutRequest.logoutUser();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
        
        View fbButton = findViewById(R.id.facebookAuth);
        fbButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		final String[] PERMISSIONS =
        	        new String[] {"publish_stream", "email", "read_stream", "offline_access"};
        		Cocoafish.getDefaultInstance().facebookAhtorize(UserView.this, PERMISSIONS, null);
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
            CCUser currentUser = Cocoafish.getDefaultInstance().getCurrentUser();
            name.setText(currentUser.getFirst() + " " + currentUser.getLast());
        } catch (Exception e) {
			e.printStackTrace();
        }
        performRefresh();
    }
  
    protected void showCheckins(List<CCCheckin> checkins) {
        
    	if (checkins == null) {
    		return;
    	}
    	InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    		
        ListView list = (ListView)findViewById(R.id.CheckinListView);
        
        CheckinAdapter adapter = new CheckinAdapter(this, checkins, false);
        
        list.setAdapter(adapter);
    }
    
	private class GetCheckinsTask extends AsyncTask<Void, Void, List<CCCheckin>> {
		private final ProgressDialog dialog = new ProgressDialog(UserView.this);
	    private String errorMsg = null;
	    protected void onPreExecute()
	    {
	        dialog.setMessage("Loading...");
	        dialog.show();
	    }
	    
	    protected void onPostExecute(List<CCCheckin> checkins) {
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
		protected List<CCCheckin> doInBackground(Void...params) {
			CCRestfulRequest request = null;
			List<CCCheckin> checkins = null;
			try {
				request = new CCRestfulRequest(Cocoafish.getDefaultInstance());
				checkins = request.getCheckinsForUser(Cocoafish.getDefaultInstance().getCurrentUser().getObjectId(), 
								CCRestfulRequest.FIRST_PAGE, CCRestfulRequest.DEFAULT_PER_PAGE);
			} catch (CocoafishError e) {
				errorMsg = e.getLocalizedMessage();
			} catch (IOException e) {
				errorMsg = e.getLocalizedMessage();
			}
			return checkins;
		}
	}

}
