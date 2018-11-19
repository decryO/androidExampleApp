package com.test.stationalertapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SearchActivity extends Activity {

    private SearchView searchView;
    private String searchWord;
    private Toolbar toolbar;
    OkHttpClient client = new OkHttpClient();
    private String prefecture, line, station;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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

    private SearchView.OnQueryTextListener onQueryTextListener =  new SearchView.OnQueryTextListener() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public boolean onQueryTextSubmit(final String query) {
            if(query != ""){
                //FancyToast.makeText(getApplicationContext(), "入力された文字は"+query, FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                new MyAsyncTask() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        try {
                            String result = run("http://express.heartrails.com/api/json?method=getStations&name=" + query);
                            JSONObject resJson = new JSONObject(result);
                            JSONObject pre = resJson.getJSONObject("response");
                            if(pre.has("station")){
                                JSONArray linelist = pre.getJSONArray("station");
                                JSONObject anst = linelist.getJSONObject(1);
                                prefecture = anst.getString("prefecture");
                                line = anst.getString("line");
                                station = anst.getString("name");
//                                Intent data = new Intent();
//                                Bundle bundle = new Bundle();
//                                bundle.putString("prefecture", prefecture);
//                                bundle.putString("line", line);
//                                bundle.putString("name", station);
//                                data.putExtras(bundle);
//                                setResult(RESULT_OK, data);
//                                finish();
                            }else{

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
                        //FancyToast.makeText(getApplicationContext(), "入力された文字は", FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
                        chooseLine();
                    }
                }.execute();
                //searchView.clearFocus();
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    private void chooseLine(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("目的の駅がある路線を選択してください");
        builder.setMessage("");
        builder.show();
    }

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
