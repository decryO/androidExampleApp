package com.test.stationalertapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class AboutAppActivity extends AppCompatActivity {

    private ListView listView;
    private Toolbar toolbar;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.app_about_list_view);

        toolbar.setTitle(R.string.about_this);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("駅情報について");
        adapter.add("オープンソースライブラリについて");

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(getApplicationContext(), StationDetailActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent OssIntent = new Intent(getApplicationContext(), OssLicensesMenuActivity.class);
                        OssIntent.putExtra("title", "オープンソースライブラリ");
                        startActivity(OssIntent);
                        break;
                }
            }
        });
    }
}
