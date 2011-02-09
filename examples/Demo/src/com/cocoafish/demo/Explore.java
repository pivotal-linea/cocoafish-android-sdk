
package com.cocoafish.demo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cocoafish.sdk.CCPlace;
import com.cocoafish.sdk.CCRestfulRequest;
import com.cocoafish.sdk.CocoafishError;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Explore extends MapActivity {
	private MapView map=null;
	private MyLocationOverlay me=null;
	private List<CCPlace> places;
	private SitesOverlay overlay;
	private BaloonLayout noteBaloon;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		map=(MapView)findViewById(R.id.map);
		
		map.setBuiltInZoomControls(true);
		
		Drawable marker=getResources().getDrawable(R.drawable.marker);
		
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		
		overlay = new SitesOverlay(marker);
		map.getOverlays().add(overlay);
		
		me=new MyLocationOverlay(this, map);
		map.getOverlays().add(me);
		
		LayoutInflater  layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        noteBaloon = (BaloonLayout) layoutInflater.inflate(R.layout.baloon, null);
        RelativeLayout.LayoutParams layoutParams   = new RelativeLayout.LayoutParams(200,100);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        noteBaloon.setLayoutParams(layoutParams);
        
		noteBaloon.findViewById(R.id.placeInfo).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				overlay.showPlace();
			}
		});
		((TextView)noteBaloon.findViewById(R.id.placeName)).setTextColor(Color.BLACK);
		((TextView)noteBaloon.findViewById(R.id.placeAddress)).setTextColor(Color.BLACK);

		final ListView list = (ListView) findViewById(R.id.placeList);
		PlaceAdapter placeAdapter = new PlaceAdapter(this);
	    list.setAdapter(placeAdapter);
	    list.setClickable(true);
	    list.setFocusable(true);
	    list.setSelected(true);
	    list.setOnItemClickListener(new ListView.OnItemClickListener() {
	  	      @Override
		      public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

			   	    list.setSelection(position);

		    	 	CCPlace place = (CCPlace) list.getItemAtPosition(position);

		    	    Intent myIntent = new Intent(getBaseContext(), PlaceView.class);
		    	    myIntent.putExtra("place", (Parcelable)place);
		    	    startActivity(myIntent);
		      }
	    });
	    	    
		findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		    	new GetPlacesTask().execute();
			}
		});
		
		findViewById(R.id.refreshList).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		    	new GetPlacesTask().execute();
			}
		});
		
		findViewById(R.id.listButton).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showListView();
			}
		});
		findViewById(R.id.mapButton).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideListView();
			}
		});

		hideListView();
    	new GetPlacesTask().execute();
		//findViewById(R.id.listView).setVisibility(View.INVISIBLE);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		me.enableCompass();
	}		
	
	@Override
	public void onPause() {
		super.onPause();
		
		me.disableCompass();
	}		
	
 	@Override
	protected boolean isRouteDisplayed() {
		return(false);
	}
	
 	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_S) {
			map.setSatellite(!map.isSatellite());
			return(true);
		}
		else if (keyCode == KeyEvent.KEYCODE_Z) {
			map.displayZoomControls(true);
			return(true);
		}
		
		return(super.onKeyDown(keyCode, event));
	}

	private GeoPoint getPoint(double lat, double lon) {
		return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
	}
	
	private void showListView() {
		findViewById(R.id.mapButton).setVisibility(View.VISIBLE);
		findViewById(R.id.refreshList).setVisibility(View.VISIBLE);
		findViewById(R.id.placeList).setVisibility(View.VISIBLE);
		findViewById(R.id.listView).setVisibility(View.VISIBLE);
		findViewById(R.id.listView).setBackgroundResource(R.color.white);
		ListView list = (ListView) findViewById(R.id.placeList);
	    ((PlaceAdapter)list.getAdapter()).setPlaces(places);
	    map.setEnabled(false);
	}
	
	private void hideListView() {
		findViewById(R.id.listView).setVisibility(View.INVISIBLE);
		findViewById(R.id.refreshList).setVisibility(View.INVISIBLE);
		findViewById(R.id.mapButton).setVisibility(View.INVISIBLE);
		findViewById(R.id.placeList).setVisibility(View.INVISIBLE);
	    map.setEnabled(true);
	}
	
	public void setPlaces(List<CCPlace> newPlaces) {
		places = newPlaces;
		overlay.update();
		ListView list = (ListView) findViewById(R.id.placeList);
	    ((PlaceAdapter)list.getAdapter()).setPlaces(places);
	}
	
	private class GetPlacesTask extends AsyncTask<Void, Void, List<CCPlace>> {

	    private final ProgressDialog dialog = new ProgressDialog(Explore.this);
	    private String errorMsg = null;
	    protected void onPreExecute()
	    {
	        dialog.setMessage("Loading...");
	        dialog.show();
	    }
	     protected void onPostExecute(List<CCPlace> places) {
	      
	    	 if(this.dialog.isShowing())
	         {
	             this.dialog.dismiss();
	         }
	    	 if (errorMsg != null) {
	     		AlertDialog alertDialog = new AlertDialog.Builder(Explore.this).create();
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
	    	 } else if (places != null && !places.isEmpty()) {
	    		 setPlaces(places);

	    	 } 
	     }

		@Override
		protected List<CCPlace> doInBackground(Void...params) {
			CCRestfulRequest request = null;
			List<CCPlace> places = null;
			try {
				request = new CCRestfulRequest();
				places = request.getPlaces(CCRestfulRequest.FIRST_PAGE, CCRestfulRequest.DEFAULT_PER_PAGE);
			} catch (CocoafishError e) {
				errorMsg = e.getLocalizedMessage();
			} catch (IOException e) {
				errorMsg = "Network Error: " + e.getLocalizedMessage();
			}
			return places;
		}
		
	}
		
	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items=new ArrayList<OverlayItem>();
		private Drawable marker=null;
		private CCPlace selectedPlace = null;
	    private void fitPoints() {
	    	
	    	if (places == null || places.size() == 0) {
	    		return;
	    	}
	    	// set min and max for two points  
	    	int nwLat = -90 * 1000000;  
	    	int nwLng = 180 * 1000000;  
	    	int seLat = 90 * 1000000;  
	    	int seLng = -180 * 1000000;  
	    	// find bounding lats and lngs  

	    	for (CCPlace place : places) {
	    		nwLat = Math.max(nwLat, (int)(place.getLatitude() * 1000000));
	    		nwLng = Math.min(nwLng, (int)(place.getLongitude() * 1000000));
	    		seLat = Math.min(seLat, (int)(place.getLatitude() * 1000000));
	    		seLng = Math.max(seLng, (int)(place.getLongitude() * 1000000));
	    	}
	    	GeoPoint center = new GeoPoint((nwLat + seLat) / 2, (nwLng + seLng) / 2);  
	    	// add padding in each direction  
	    	int spanLatDelta = (int) (Math.abs(nwLat - seLat) * 1.1);  
	    	int spanLngDelta = (int) (Math.abs(seLng - nwLng) * 1.1);  
	    	       
	    	// pop the baloon for the first pin
	    	onTap(0);
	    	
	    	// fit map to points  
	    	map.getController().animateTo(center);  
	    	map.getController().zoomToSpan(spanLatDelta, spanLngDelta); 
	    	
	    } 
	    
		public SitesOverlay(Drawable marker) {
			super(marker);
			this.marker=marker;
		}
		
		public void update() {
			
			items.clear();
			if (places == null) {
				return;
			}
			for (CCPlace place : places) {
				items.add(new OverlayItem(getPoint(place.getLatitude(), place.getLongitude()),
						place.getName(), place.getAddress1()));
	    	}
			populate();
			fitPoints();
		}
		
		@Override
		protected OverlayItem createItem(int i) {
			return(items.get(i));
		}
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			
			boundCenterBottom(marker);
		}
		
	
		@Override
		protected boolean onTap(int i) {
		
			OverlayItem item=getItem(i);
			GeoPoint geo=item.getPoint();

			CCPlace place= places.get(i);
			if (selectedPlace == place) {
				// user tapped on the same pin again, this will remove the baloon
				map.removeView(noteBaloon);
				selectedPlace = null;
				return true;
			}
			selectedPlace = place;

			map.removeView(noteBaloon);
			map.getController().animateTo(geo);
			
			((TextView)noteBaloon.findViewById(R.id.placeName)).setText(place.getName());
			((TextView)noteBaloon.findViewById(R.id.placeAddress)).setText(place.getFullAddress());
			map.addView(noteBaloon, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,70,geo,MapView.LayoutParams.BOTTOM_CENTER));
			
			return(true);
		}
		
		
		@Override
		public int size() {
			return(items.size());
		}
		
		void showPlace() {
			if (selectedPlace == null) {
				return;
			}
			Intent myIntent = new Intent(getBaseContext(), PlaceView.class);
		    myIntent.putExtra("place", (Parcelable)selectedPlace);
		    startActivity(myIntent);
		}
	}

}