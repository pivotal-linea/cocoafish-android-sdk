package com.cocoafish.demo;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.cocoafish.sdk.CCRequestMethod;
import com.cocoafish.sdk.CocoafishError;


public class SignUp extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        View signupButton = findViewById(R.id.signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		try {
					performSignup();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    }});
        
        View cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		finish();
        }});
    }

    public void performSignup() {
		final ProgressDialog dialog = new ProgressDialog(SignUp.this);
	    dialog.setMessage("Registering...");
	    dialog.show();
	    
    	String firstName = ((EditText) findViewById(R.id.first_name)).getText().toString();
    	String lastName = ((EditText) findViewById(R.id.last_name)).getText().toString();
    	String email = ((EditText) findViewById(R.id.email_address)).getText().toString();
    	String password = ((EditText) findViewById(R.id.pw)).getText().toString();

    	String errorMsg = null;
		try {
			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("email", email);
			dataMap.put("password", password);
			dataMap.put("password_confirmation", password);
			dataMap.put("first_name", firstName);
			dataMap.put("last_name", lastName);
			DemoApplication.getSdk().sendRequest("users/create.json", CCRequestMethod.POST, dataMap, false);
	    	/*CCRestfulRequest signupRequest;
	    	signupRequest = new CCRestfulRequest(Cocoafish.getDefaultInstance());
	    	signupRequest.registerUser(email, "", firstName, lastName, password);*/
	    	Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
		} catch (CocoafishError e) {
			errorMsg = e.getMessage();
			
		} 
		dialog.dismiss();
		
		if (errorMsg != null) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("Signup Failed");
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

}
