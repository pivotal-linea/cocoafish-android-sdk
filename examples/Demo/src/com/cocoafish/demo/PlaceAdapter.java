package com.cocoafish.demo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cocoafish.sdk.CCResponse;

// Adapter to show a list of checkins
public class PlaceAdapter extends BaseAdapter  {
    private Context context;
    private List<JSONObject> listPlace = new ArrayList<JSONObject>();

    public PlaceAdapter(Context context) {
        this.context = context;
    }
    
    public void setPlaces(List<JSONObject> places) {
    	this.listPlace=places;
        notifyDataSetChanged();
    }

    public int getCount() {
        return listPlace.size();
    }

    public Object getItem(int position) {
        return listPlace.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
    	JSONObject entry = listPlace.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.place_row, null);
            convertView.setBackgroundColor((position & 1) == 1 ? Color.WHITE : Color.LTGRAY);

        }
        TextView placeName = (TextView) convertView.findViewById(R.id.placeName);
        placeName.setTextColor(Color.BLACK);
        try {
			placeName.setText(entry.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
       
        TextView placeAddress = (TextView) convertView.findViewById(R.id.placeAddress);
        placeAddress.setTextColor(Color.BLACK);
        try {
			placeAddress.setText(entry.getString("address"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

        return convertView;
    }


}