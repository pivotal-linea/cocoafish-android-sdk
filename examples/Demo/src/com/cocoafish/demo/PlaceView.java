package com.cocoafish.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.ListView;
import android.widget.TextView;

import com.cocoafish.sdk.CCMeta;
import com.cocoafish.sdk.CCRequestMethod;
import com.cocoafish.sdk.CCResponse;
import com.cocoafish.sdk.Cocoafish;
import com.cocoafish.sdk.CocoafishError;

public class PlaceView extends Activity {

    static final int LAUNCH_SIGNUP = 0;
    List<JSONObject> listOfCheckin = new ArrayList<JSONObject>();
    JSONObject place;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeview);
        Intent intent = getIntent(); 
        String p = intent.getStringExtra("place"); 
        if (p != null ) {
        	try {
				place = new JSONObject( p );
	        	TextView name = (TextView)findViewById(R.id.PlaceName);
	            name.setText(place.getString("name"));
	            
			} catch (JSONException e) {
				e.printStackTrace();
			} 
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
        
		if (DemoApplication.getSdk().getCurrentUser() == null) {
			// disable check in button
			checkinButton.setEnabled(false);
		} else 
			checkinButton.setEnabled(true);

    	new GetCheckinsTask().execute();

    }

    protected void performRefresh() {
    	new GetCheckinsTask().execute();
    }
    
    protected void performCheckin()  {

    	JSONObject checkin = null;
    	String errorMsg = null;
    	try {
    		Cocoafish sdk = DemoApplication.getSdk();
    		Map<String, Object> data = new HashMap<String, Object>();
			try {
				data.put("place_id", place.getString("id") );
				} catch (JSONException e) {
				e.printStackTrace();
			}
			
			CCResponse response = response = sdk.sendRequest("checkins/create.json", CCRequestMethod.POST, data, false);

    		JSONObject responseJSON = response.getResponseData();
    		CCMeta meta = response.getMeta();
    		if("ok".equals(meta.getStatus()) 
    		    && meta.getCode() == 200 
    		    && "createCheckin".equals(meta.getMethod())) {
				JSONArray checkins = null;
    		  try {
    			  checkins = responseJSON.getJSONArray("checkins");
    			  checkin = checkins.getJSONObject(0);
    			  listOfCheckin.add(0, checkin);
    			  } catch (JSONException e) {
				e.printStackTrace();
    		  }
    		  showCheckins();
    		} else {
    			errorMsg = meta.getMessage();
    		}

		} catch (CocoafishError e) {
			errorMsg = e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
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
    
	private class GetCheckinsTask extends AsyncTask<Void, Void, List<JSONObject>> {
		private final ProgressDialog dialog = new ProgressDialog(PlaceView.this);
	    private String errorMsg = null;
	    
	    protected void onPreExecute()
	    {
	        dialog.setMessage("Loading...");
	        dialog.show();
	    }
	    
	    protected void onPostExecute(List<JSONObject> checkins) {
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
		protected List<JSONObject> doInBackground(Void...params) {
			List<JSONObject> checkins = new ArrayList<JSONObject>();
			try {
				Cocoafish sdk = DemoApplication.getSdk();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("place_id", place.getString("id"));
				CCResponse response = sdk.sendRequest("checkins/search.json", CCRequestMethod.GET, data, false);
				JSONObject responseJSON = response.getResponseData();
				CCMeta meta = response.getMeta();
				if("ok".equals(meta.getStatus()) 
				    && meta.getCode() == 200 
				    && "searchCheckins".equals(meta.getMethod())) {
				  JSONArray checkinsJArray = responseJSON.getJSONArray("checkins");
				  for( int i = 0 ; i < checkinsJArray.length() ; i++ ){
					  checkins.add(checkinsJArray.getJSONObject(i));
				  }
				  
				}
			} catch (CocoafishError e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return checkins;
		}
	}

}
