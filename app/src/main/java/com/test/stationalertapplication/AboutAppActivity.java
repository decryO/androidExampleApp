package com.test.stationalertapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class AboutAppActivity extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.app_about_list_view);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        adapter.add("オープンソースライブラリについて");
        adapter.add("オープンソースライブラリについて");
        adapter.add("オープンソースライブラリについて");
        adapter.add("オープンソースライブラリについて");
        adapter.add("オープンソースライブラリについて");

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        Intent intent = new Intent(getActivity(), OssLicensesMenuActivity.class);
                        intent.putExtra("title", "オープンソースライブラリ");
                        startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);

        getActivity().setTitle(R.string.about_this);
    }
}
