package com.test.stationalertapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private String prefecture, line, station;
    // 駅の座標を入れる
    private String pregoalLat, pregoalLng;
    private Double goalLat, goalLng;
    private JSONArray linelist;
    OkHttpClient client = new OkHttpClient();
    private ArrayList<String> stationArray = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fragmentManager = getSupportFragmentManager();
        searchView = findViewById(R.id.search_view);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("key.canceledData", "キャンセル");
                setResult(RESULT_CANCELED, data);
                finish();
            }
        });
        setupSearchView();
        this.searchView.setOnQueryTextListener(onQueryTextListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public boolean onQueryTextSubmit(final String query) {
            if (query != "") {
                //FancyToast.makeText(getApplicationContext(), "入力された文字は"+query, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                new MyAsyncTask() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            stationArray = new ArrayList<>();
                            String result = run("http://express.heartrails.com/api/json?method=getStations&name=" + query);
                            JSONObject resJson = new JSONObject(result);
                            JSONObject pre = resJson.getJSONObject("response");
                            if (pre.has("station")) {
                                linelist = pre.getJSONArray("station");
                                for (int i = 0; i < linelist.length(); i++) {
                                    JSONObject anst = linelist.getJSONObject(i);
                                    String station = anst.getString("line");
                                    stationArray.add(station);
                                }
                            } else {

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return super.doInBackground(voids);
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if (stationArray.size() <= 1) {
                            try {
                                JSONObject anst = linelist.getJSONObject(0);
                                pregoalLat = anst.getString("y");
                                pregoalLng = anst.getString("x");
                                prefecture = anst.getString("prefecture");
                                line = anst.getString("line");
                                station = anst.getString("name");
                                goalLat = Double.valueOf(pregoalLat);
                                goalLng = Double.valueOf(pregoalLng);
                                FancyToast.makeText(getApplicationContext(), "駅：" + prefecture + "\n路線：" + line + "\n駅名：" + station + "\n緯度：" + goalLat + "\n経度：" + goalLng, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();

                                Bundle bundle = new Bundle();
                                bundle.putString("prefecture", prefecture);
                                bundle.putString("line", line);
                                bundle.putString("station", station);
                                bundle.putDouble("Lat", goalLat);
                                bundle.putDouble("Lng", goalLng);

                                setFraqgment(1, bundle);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                Bundle bundle = new Bundle();
                                bundle.putString("Title", "「" + query + "」駅は見つかりませんでした。");
                                bundle.putString("Message", "検索ワードに「駅」を含むと見つからない駅があります。\n" +
                                        "検索ワードに誤字や脱字はありませんか？\n" +
                                        "駅が検索できない場合は路線から駅を選択してください。");
                                setFraqgment(3, bundle);
                            }
                        } else {
                            chooseLine();
                        }
                    }
                }.execute();
                searchView.clearFocus();
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.length() == 1) {
                setFraqgment(0, null);
            }
            return true;
        }
    };

    private void chooseLine() {
        final CharSequence[] cs = stationArray.toArray(new CharSequence[stationArray.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("目的の駅がある路線を選択してください");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    JSONObject anst = linelist.getJSONObject(which);
                    pregoalLat = anst.getString("y");
                    pregoalLng = anst.getString("x");
                    prefecture = anst.getString("prefecture");
                    line = anst.getString("line");
                    station = anst.getString("name");
                    goalLat = Double.valueOf(pregoalLat);
                    goalLng = Double.valueOf(pregoalLng);
                    FancyToast.makeText(getApplicationContext(), "駅：" + prefecture + "\n路線：" + line + "\n駅名：" + station + "\n緯度：" + goalLat + "\n経度：" + goalLng, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();

                    Bundle bundle = new Bundle();
                    bundle.putString("prefecture", prefecture);
                    bundle.putString("line", line);
                    bundle.putString("station", station);
                    bundle.putDouble("Lat", goalLat);
                    bundle.putDouble("Lng", goalLng);

                    setFraqgment(1, bundle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }

    //SearchActivityのresult_containerへ入れるフラグメントの操作メソッド
    private void setFraqgment(int type, Bundle bundle) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ProgressFragment P_fragment = new ProgressFragment();
        SearchResultFragment S_fragment = new SearchResultFragment();
        NothingResultFragment N_fragment = new NothingResultFragment();
        switch (type) {
            case 0:     //検索途中のProgressBarをだすだけ
                transaction.replace(R.id.result_container, P_fragment);
                transaction.commit();
                break;
            case 1:     //検索が成功し、サービスを開始できる状態にする
                S_fragment.setArguments(bundle);
                transaction.replace(R.id.result_container, S_fragment);
                transaction.commit();
                break;
            case 3:     //検索結果がないとき
                N_fragment.setArguments(bundle);
                transaction.replace(R.id.result_container, N_fragment);
                transaction.commit();
                break;
        }
    }

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
