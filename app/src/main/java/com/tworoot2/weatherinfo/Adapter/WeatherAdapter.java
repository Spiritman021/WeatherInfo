package com.tworoot2.weatherinfo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tworoot2.weatherinfo.MainActivity;
import com.tworoot2.weatherinfo.ModelClass.PrevWeatherModel;
import com.tworoot2.weatherinfo.R;
import com.tworoot2.weatherinfo.ViewHolder.WeatherViewHolder;

import java.util.ArrayList;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    private Context context;
    private ArrayList<PrevWeatherModel> arrayList;

    public WeatherAdapter(Context context, ArrayList<PrevWeatherModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {

        PrevWeatherModel currentItem = arrayList.get(holder.getAdapterPosition());

        holder.tempt.setText(currentItem.getTempt());
        holder.timeW.setText(currentItem.getTime());
        Glide.with(context).load(currentItem.getIconUrl()).into(holder.weatherIcon);

    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }
}
