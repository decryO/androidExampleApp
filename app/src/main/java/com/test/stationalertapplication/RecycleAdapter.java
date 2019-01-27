package com.test.stationalertapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    
    private ArrayList<String> lineData = new ArrayList<>();
    private ArrayList<String> stationData = new ArrayList<>();
    private ArrayList<Integer> alertLineData = new ArrayList<>();
    private ArrayList<String> timeData = new ArrayList<>();
    private OnRecyclerListener mListener;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView presetLine, presetStation, presetAlertLine, timehold;

        public ViewHolder(View v) {
            super(v);

            presetLine = (TextView)v.findViewById(R.id.preset_line);
            presetStation = (TextView)v.findViewById(R.id.preset_station);
            presetAlertLine = (TextView)v.findViewById(R.id.preset_alertline);
            timehold = (TextView)v.findViewById(R.id.numbertext);
        }
    }

    public RecycleAdapter(Context context, ArrayList<String> lineData, ArrayList<String> stationData, ArrayList<Integer> alertLineData, ArrayList<String> timeData,OnRecyclerListener listener) {
        this.mContext = context;
        this.lineData = lineData;
        this.stationData = stationData;
        this.alertLineData = alertLineData;
        this.timeData = timeData;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_layout, parent, false);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {
        holder.presetLine.setText(lineData.get(i));
        holder.presetStation.setText(stationData.get(i));
        holder.presetAlertLine.setText("半径" + String.valueOf(alertLineData.get(i)) + "m");
        holder.timehold.setText(timeData.get(i));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRecyclerClicked(v, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stationData.size();
    }

    public void refresh() {
        notifyDataSetChanged();
    }
}
