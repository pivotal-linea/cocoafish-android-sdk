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
    List<CCCheckin> listOfCheckin = new ArrayList<CCCheckin>();
    CCPlace place;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeview);
        Intent intent = getIntent(); 
        Parcelable p = intent.getParcelableExtra("place"); 
        if (p != null && p instanceof CCPlace) {
        	place = (CCPlace) p; 
        	TextView name = (TextView)findViewById(R.id.PlaceName);
            name.setText(place.getName());
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
			if (Cocoafish.getDefaultInstance().getCurrentUser() == null) {
				// disable check in button
				checkinButton.setEnabled(false);
			}
        } catch (Exception e) {
			checkinButton.setEnabled(true);

        } catch (CocoafishError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	new GetCheckinsTask().execute();

    }

    protected void performRefresh() {
    	new GetCheckinsTask().execute();
    }
    
    protected void performCheckin()  {

    	CCCheckin checkin = null;
    	String errorMsg = null;
    	try {
	    	CCRestfulRequest checkinRequest = new CCRestfulRequest();
	    	checkin = checkinRequest.checkinPlace(place.getObjectId());
	    	listOfCheckin.add(0, checkin);
			showCheckins();

		} catch (CocoafishError e) {
			errorMsg = e.getMessage();
		} catch (IOException e) {
			errorMsg = "Network Error: " + e.getMessage();
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
    
	private class GetCheckinsTask extends AsyncTask<Void, Void, List<CCCheckin>> {
		private final ProgressDialog dialog = new ProgressDialog(PlaceView.this);
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
		protected List<CCCheckin> doInBackground(Void...params) {
			CCRestfulRequest request = null;
			List<CCCheckin> checkins = null;
			try {
				request = new CCRestfulRequest();
				checkins = request.getCheckinsForPlace(place.getObjectId(), CCRestfulRequest.FIRST_PAGE, CCRestfulRequest.DEFAULT_PER_PAGE);
			} catch (CocoafishError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return checkins;
		}
	}

}
