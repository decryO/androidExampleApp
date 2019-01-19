package com.test.stationalertapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<Setting> settinglist;

    public SettingAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSettinglist(ArrayList<Setting> settinglist) {
        this.settinglist = settinglist;
    }

    @Override
    public int getCount() {
        return settinglist.size();
    }

    @Override
    public Object getItem(int position) {
        return settinglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return settinglist.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_layout, parent, false);

        ((TextView)convertView.findViewById(R.id.list_title)).setText(settinglist.get(position).getTitle());
        ((TextView)convertView.findViewById(R.id.list_ringtone_title)).setText(settinglist.get(position).getSubtitle());

        return convertView;
    }
}
