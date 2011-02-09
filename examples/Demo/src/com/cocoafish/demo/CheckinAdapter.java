package com.cocoafish.demo;

import java.util.List;

import com.cocoafish.sdk.CCCheckin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// Adapter to show a list of checkins
public class CheckinAdapter extends BaseAdapter {
    private Context context;
    private boolean isPlaceView;
    private List<CCCheckin> listCheckin;

    public CheckinAdapter(Context context, List<CCCheckin> listCheckin, boolean isPlaceView) {
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
        CCCheckin entry = listCheckin.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.checkin_row, null);
        }
        TextView checkinInfo = (TextView) convertView.findViewById(R.id.checkinInfo);
        if (isPlaceView) {
        	checkinInfo.setText(entry.getUser().getFirst() + " checked in");
        } else {
        	checkinInfo.setText("Checked in at " + entry.getPlace().getName());
        }

        TextView checkinDate = (TextView) convertView.findViewById(R.id.checkinDate);
        checkinDate.setText(entry.getCreatedDate().toString());

        return convertView;
    }


}