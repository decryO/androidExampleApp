package com.test.stationalertapplication;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button button1, button2, button3, button4, button5;
    private TextView PrefectureText, LineText, StationText;
    // 駅の座標を入れる
    private String pregoalLat, pregoalLng;
    private Double goalLat, goalLng;
    private LatLng latLng, goallatLng;
    // 範囲を決めるシークバー
    private SeekBar radiusSeek;
    // シークバーで決めた範囲を入れる
    private int alertLine = 0;
    // TextViewに表示するだけ
    private TextView radiusView, serCon;
    OkHttpClient client = new OkHttpClient();
    // JSONファイルを取得する際、URLに都道府県名を記載する必要があるので、
    // preListへxmlファイルのリストを入れ、dialogで取得した数値で名前を出す
    private String[] preList;
    //ここにJSONで得た路線を入れて、つぎのDialogで使う
    private ArrayList<String> preArray = new ArrayList<>();
    // これは路線,駅用
    private ArrayList<String> stationArray = new ArrayList<>();
    // 駅名
    private String goalStation;

    // 駅座標を取得する際、JSONは路線選択に使用したものでよいので、
    // ここに作っておいて、どこからでも呼べるようにする。
    private JSONArray stationList;

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        latLng = new LatLng(34.985458, 135.7577551);
        radiusSeek = view.findViewById(R.id.seekBar);
        radiusView = view.findViewById(R.id.textView);
        preList = getResources().getStringArray(R.array.prefectures);

        button1 = view.findViewById(R.id.button);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        button4 = view.findViewById(R.id.button4);
        button5 = view.findViewById(R.id.button5);
        //button2.setVisibility(View.INVISIBLE);
        button4.setVisibility(View.INVISIBLE);
        button5.setVisibility(View.INVISIBLE);

        PrefectureText = view.findViewById(R.id.ToDoHuText);
        LineText = view.findViewById(R.id.RosenText);
        StationText = view.findViewById(R.id.EkiText);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // GoogleMapのオプションはLayoutに記載してある
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertLine == 0 || goallatLng == null) {
                    FancyToast.makeText(getActivity(), "距離、または駅が選択されていません！", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
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
                    intent.putExtra("goalStation", goalStation);
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

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("目的地の都道府県選択")
                        .setItems(R.array.prefectures, new DialogInterface.OnClickListener() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String ans = preList[which];
                                PrefectureText.setText(ans);
                                //ここで作り直さないともう一度都道府県選択をした場合に路線リストが以前選択された都道府県の路線が残ったままとなり、
                                //ものすごい長いリストとなってわかりにくくなってしまう。
                                preArray = new ArrayList<>();
                                //FancyToast.makeText(getActivity(), "取得した文字は" + ans, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                                new MyAsyncTask() {
                                    @Override
                                    protected String doInBackground(Void... params) {
                                        String res = null;
                                        try {
                                            String result = run("http://express.heartrails.com/api/json?method=getLines&prefecture=" + ans);
                                            JSONObject resJson = new JSONObject(result);
                                            JSONObject pre = resJson.getJSONObject("response");
                                            JSONArray linelist = pre.getJSONArray("line");
                                            for (int i = 0; i < linelist.length(); i++) {
                                                String station = linelist.getString(i);
                                                preArray.add(station);
                                            }
                                            int list_count = linelist.length();
                                            res = String.valueOf(list_count);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return res;
                                    }
                                }.execute();
                                button4.setVisibility(View.VISIBLE);
                                button5.setVisibility(View.INVISIBLE);
                                LineText.setText("");
                                StationText.setText("");
                            }
                        })
                        .show();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preArray.size() != 0) {
                    final CharSequence[] cs = preArray.toArray(new CharSequence[preArray.size()]);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("目的地への路線選択")
                            .setItems(cs, new DialogInterface.OnClickListener() {
                                @SuppressLint("StaticFieldLeak")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String ans = preArray.get(which);
                                    //preArrayを初期化した理由と同様
                                    stationArray = new ArrayList<>();
                                    LineText.setText(ans);
                                    //FancyToast.makeText(getActivity(), "取得した文字は" + ans, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                                    new MyAsyncTask() {
                                        @Override
                                        protected String doInBackground(Void... params) {
                                            String res = null;
                                            try {
                                                String result = run("http://express.heartrails.com/api/json?method=getStations&line=" + ans);
                                                JSONObject resJson = new JSONObject(result);
                                                JSONObject ans = resJson.getJSONObject("response");
                                                stationList = ans.getJSONArray("station");
                                                for (int i = 0; i < stationList.length(); i++) {
                                                    JSONObject anst = stationList.getJSONObject(i);
                                                    String station = anst.getString("name");
                                                    stationArray.add(station);
                                                }
                                                int list_count = stationList.length();
                                                res = String.valueOf(list_count);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            return res;
                                        }
                                    }.execute();
                                    button5.setVisibility(View.VISIBLE);
                                    StationText.setText("");
                                }
                            }).show();
                } else {
                    FancyToast.makeText(getActivity(), "先に都道府県を選択してください！", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stationArray.size() != 0) {
                    final CharSequence[] cs = stationArray.toArray(new CharSequence[stationArray.size()]);
                    new AlertDialog.Builder(getActivity())
                            .setTitle("目的地の駅選択")
                            .setItems(cs, new DialogInterface.OnClickListener() {
                                @SuppressLint("StaticFieldLeak")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goalStation = stationArray.get(which);
                                    StationText.setText(goalStation);
                                    //FancyToast.makeText(getActivity(), "取得した文字は" + goalStation, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                                    try {
                                        JSONObject anst = stationList.getJSONObject(which);
                                        pregoalLat = anst.getString("y");
                                        pregoalLng = anst.getString("x");
                                        goalLat = Double.valueOf(pregoalLat);
                                        goalLng = Double.valueOf(pregoalLng);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    goallatLng = new LatLng(goalLat, goalLng);
                                    mMap.addMarker(new MarkerOptions()
                                            .position(goallatLng)
                                            .title(goalStation));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(goallatLng, 13));
                                }
                            }).show();

                } else {
                    FancyToast.makeText(getActivity(), "先に路線を選択してください！", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }
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
                if(goallatLng == null) {
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(alertLine)
                            .strokeColor(Color.argb(0x77, 0, 0, 0))
                            .fillColor(Color.argb(0x55, 0, 255, 255)));
                }else{
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(goallatLng)
                            .radius(alertLine)
                            .strokeColor(Color.argb(0x77, 0, 0, 0))
                            .fillColor(Color.argb(0x55, 0, 255, 255)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // OkHTTPのURLとかひとまとめにしたやつ
    // いじらないこと
    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }
}