package com.cocoafish.demo;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cocoafish.sdk.CCResponse;

// Adapter to show a list of checkins
public class CheckinAdapter extends BaseAdapter {
    private Context context;
    private boolean isPlaceView;
    private List<JSONObject> listCheckin;

    public CheckinAdapter(Context context, List<JSONObject> listCheckin, boolean isPlaceView) {
        this.context = context;
        this.listCheckin = listCheckin;
        this.isPlaceView = isPlaceView;
    }

    public int getCount() {
        return listCheckin.size();
    }

    public Object getItem(int position) {
        return listCheckin.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
    	JSONObject entry = listCheckin.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.checkin_row, null);
        }
        TextView checkinInfo = (TextView) convertView.findViewById(R.id.checkinInfo);
        if (isPlaceView) {
			try {
				JSONObject userJSON = userJSON = entry.getJSONObject("user");
				checkinInfo.setText( userJSON.getString("first_name") + " checked in");
			} catch (JSONException e) {
				e.printStackTrace();
			}
        } else {
        	try {
				JSONObject placeJSON = entry.getJSONObject("place");
				checkinInfo.setText("Checked in at " + placeJSON.getString("name") );
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }

        TextView checkinDate = (TextView) convertView.findViewById(R.id.checkinDate);
        try {
			checkinDate.setText(entry.getString("created_at"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

        return convertView;
    }


}