package com.cocoafish.demo;

import java.util.ArrayList;
import java.util.List;

import com.cocoafish.sdk.CCResponse;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// Adapter to show a list of checkins
public class PlaceAdapter extends BaseAdapter  {
    private Context context;
    private List<CCResponse> listPlace = new ArrayList<CCResponse>();

    public PlaceAdapter(Context context) {
        this.context = context;
    }
    
    public void setPlaces(List<CCResponse> places) {
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
        CCResponse entry = listPlace.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.place_row, null);
            convertView.setBackgroundColor((position & 1) == 1 ? Color.WHITE : Color.LTGRAY);

        }
        TextView placeName = (TextView) convertView.findViewById(R.id.placeName);
        placeName.setTextColor(Color.BLACK);
        //placeName.setText(entry.getName());
       
        TextView placeAddress = (TextView) convertView.findViewById(R.id.placeAddress);
        placeAddress.setTextColor(Color.BLACK);
        //placeAddress.setText(entry.getFullAddress());

        return convertView;
    }


}