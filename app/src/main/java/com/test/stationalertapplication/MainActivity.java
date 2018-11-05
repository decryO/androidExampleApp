package com.test.stationalertapplication;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide().setDuration(10000));
        //setContentViewを↑のgetWindowより上に配置するとエラーが出るのでここに置く
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectMenuItems();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapsFragment fragment = new MapsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void selectMenuItems() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.action_settings){
                    SettingActivity setFragment = new SettingActivity();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.addToBackStack("");
                    transaction.replace(R.id.container, setFragment);
                    transaction.commit();
                }else if(id == R.id.menu_search){
                    gotoSearch();
                }
                return false;
            }
        });
    }

    private void gotoSearch(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 100);
        overridePendingTransition(0, 0);
    }
}
