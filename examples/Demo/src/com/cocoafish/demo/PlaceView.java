package com.cocoafish.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.cocoafish.sdk.*;

public class PlaceView extends Activity {

    static final int LAUNCH_SIGNUP = 0;
    List<CCResponse> listOfCheckin = new ArrayList<CCResponse>();
    CCResponse place;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeview);
        Intent intent = getIntent(); 
        Parcelable p = intent.getParcelableExtra("place"); 
        if (p != null && p instanceof CCResponse) {
        	place = (CCResponse) p; 
        	TextView name = (TextView)findViewById(R.id.PlaceName);
//            name.setText(place.getName());
        }
        
        View doneButton = findViewById(R.id.done);
        doneButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		finish();
	    }});
        
        View refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		performRefresh();
	    }});
        
        View checkinButton = findViewById(R.id.checkin);
        checkinButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		performCheckin();
        }});
        
        try {
			if (DemoApplication.getSession().getAttribute("USer") == null) {
				// disable check in button
				checkinButton.setEnabled(false);
			}
        } catch (Exception e) {
			checkinButton.setEnabled(true);

        }
    	new GetCheckinsTask().execute();

    }

    protected void performRefresh() {
    	new GetCheckinsTask().execute();
    }
    
    protected void performCheckin()  {

    	CCResponse checkin = null;
    	String errorMsg = null;
    	try {
//	    	CCRestfulRequest checkinRequest = new CCRestfulRequest(DemoApplication.getSdk());
//	    	checkin = checkinRequest.checkinPlace(place.getObjectId());
    		DemoApplication.getSdk().sendRequest(null, null, null, false);
	    	listOfCheckin.add(0, checkin);
			showCheckins();

		} catch (CocoafishError e) {
			errorMsg = e.getMessage();
		} 
		
		if (errorMsg != null) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("Checkin Failed");
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
    

    protected void showCheckins() {
       	    	
        ListView list = (ListView)findViewById(R.id.CheckinListView);
        
        CheckinAdapter adapter = new CheckinAdapter(this, listOfCheckin, true);
        
        list.setAdapter(adapter);
    }
    
	private class GetCheckinsTask extends AsyncTask<Void, Void, List<CCResponse>> {
		private final ProgressDialog dialog = new ProgressDialog(PlaceView.this);
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
	     		AlertDialog alertDialog = new AlertDialog.Builder(PlaceView.this).create();
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
	    		 listOfCheckin = checkins;
	    		 showCheckins();
	    	 }
	     }

		@Override
		protected List<CCResponse> doInBackground(Void...params) {
			List<CCResponse> checkins = null;
//			try {
//				request = new CCRestfulRequest(DemoApplication.getSdk());
//				checkins = request.getCheckinsForPlace(place.getObjectId(), CCRestfulRequest.FIRST_PAGE, CCRestfulRequest.DEFAULT_PER_PAGE);
//			} catch (CocoafishError e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			return checkins;
		}
	}

}
