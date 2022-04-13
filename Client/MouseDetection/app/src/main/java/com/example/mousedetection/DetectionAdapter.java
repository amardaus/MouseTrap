package com.example.mousedetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DetectionAdapter extends ArrayAdapter<Detection> {
    private Context context;
    private ArrayList<Detection> detectionList = new ArrayList<>();

    public DetectionAdapter(@NonNull Context ctx, ArrayList<Detection> list) {
        super(ctx, 0, list);
        context = ctx;
        detectionList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;
        if(listItem == null){
            listItem = LayoutInflater.from(context).inflate(R.layout.event_list_item,parent,false);
        }

        Detection detection = detectionList.get(position);

        ImageView imageView = (ImageView) listItem.findViewById(R.id.event_image);
        if(detection.ifVerified()){
            imageView.setImageResource(R.drawable.alert_green);
        }
        else{
            imageView.setImageResource(R.drawable.alert_red);
        }


        TextView dateView = (TextView) listItem.findViewById(R.id.event_date);
        dateView.setText(detection.getDate());

        TextView timeView = (TextView) listItem.findViewById(R.id.event_time);
        timeView.setText(detection.getTime());

        return listItem;
    }
}
