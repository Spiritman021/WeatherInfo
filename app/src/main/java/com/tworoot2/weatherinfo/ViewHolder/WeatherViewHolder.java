package com.tworoot2.weatherinfo.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tworoot2.weatherinfo.R;

public class WeatherViewHolder extends RecyclerView.ViewHolder {
    public TextView timeW, tempt;
    public ImageView weatherIcon;


    public WeatherViewHolder(View itemView) {
        super(itemView);
        timeW = (TextView) itemView.findViewById(R.id.timeW);
        tempt = (TextView) itemView.findViewById(R.id.tempt);
        weatherIcon = (ImageView) itemView.findViewById(R.id.weatherIcon);


    }
}
