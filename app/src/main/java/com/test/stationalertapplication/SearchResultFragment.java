package com.test.stationalertapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.shashank.sony.fancytoastlib.FancyToast;

public class SearchResultFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button button1, button2;
    private TextView PrefectureText, LineText, StationText;
    // 駅の座標を入れる
    private String prefecture, line, station;
    private Double goalLat, goalLng;
    private LatLng latLng, goallatLng;
    // 範囲を決めるシークバー
    private SeekBar radiusSeek;
    // シークバーで決めた範囲を入れる
    private int alertLine = 0;
    // TextViewに表示するだけ
    private TextView radiusView, serCon;


    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_searchresult, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        latLng = new LatLng(34.985458, 135.7577551);
        radiusSeek = view.findViewById(R.id.result_seekBar);
        radiusView = view.findViewById(R.id.result_textView);

        button1 = view.findViewById(R.id.result_button);

        PrefectureText = view.findViewById(R.id.result_ToDoHuText);
        LineText = view.findViewById(R.id.result_RosenText);
        StationText = view.findViewById(R.id.result_EkiText);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // GoogleMapのオプションはLayoutに記載してある
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        Bundle bundle = getArguments();
        prefecture = bundle.getString("prefecture");
        line = bundle.getString("line");
        station = bundle.getString("station");
        goalLat = bundle.getDouble("Lat");
        goalLng = bundle.getDouble("Lng");

        goallatLng = new LatLng(goalLat, goalLng);

        PrefectureText.setText(prefecture);
        LineText.setText(line);
        StationText.setText(station);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(goallatLng, 13));

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertLine == 0) {
                    FancyToast.makeText(getActivity(), "距離が選択されていません！", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                } else {
                    ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                    for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
                        if (GetLocationService.class.getName().equals(serviceInfo.service.getClassName())) {
                            FancyToast.makeText(getActivity(), "Serviceは起動中です！", FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();
                            return;
                        }
                    }
                    FancyToast.makeText(getActivity(), alertLine+"mでアラームを設定しました", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    Intent intent = new Intent(getActivity(), GetLocationService.class);
                    intent.putExtra("Lat", goalLat);
                    intent.putExtra("Lng", goalLng);
                    intent.putExtra("alertLine", (double) alertLine / 1000);
                    intent.putExtra("goalStation", station);
                    getActivity().startService(intent);
                    //button1.setVisibility(View.INVISIBLE);
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GetLocationService.class);
                getActivity().stopService(intent);
                FancyToast.makeText(getActivity(), "アラームを停止しました", FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
            }
        });

        // シークバーを動かして半径を確認できるようにするため、ここに配置する。
        // CircleがmMapを必要としているため。
        radiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alertLine = progress * 100;
                radiusView.setText("半径：" + String.valueOf(alertLine) + "m");
                mMap.clear();
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(goallatLng)
                        .radius(alertLine)
                        .strokeColor(Color.argb(0x77, 0, 0, 0))
                        .fillColor(Color.argb(0x55, 0, 255, 255)));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}