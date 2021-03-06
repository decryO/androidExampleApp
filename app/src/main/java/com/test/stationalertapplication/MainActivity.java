package com.test.stationalertapplication;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    public boolean isAllowedLocation = false;

    private SQLiteDatabase db;
    private DBOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 縦画面
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide().setDuration(10000));
        //setContentViewを↑のgetWindowより上に配置するとエラーが出るのでここに置く
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        if (Build.VERSION.SDK_INT >= 23) {
            checkGPSPermission();
        }

        if (helper == null) {
            helper = new DBOpenHelper(getApplicationContext());
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }

        selectMenuItems();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (tabLayout.getSelectedTabPosition() == 0) {
            inflater.inflate(R.menu.main_menu, menu);
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            inflater.inflate(R.menu.sub_menu, menu);
        }
        return true;
    }

    public void selectMenuItems() {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_settings) {
                    gotoSetting();
                } else if (id == R.id.action_about) {
                    gotoAboutThisApp();
                } else if (id == R.id.menu_search) {
                    gotoSearch();
                }
                return false;
            }
        });
    }

    public void checkGPSPermission() {
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        ArrayList<String> reqPermissions = new ArrayList<>();

        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            isAllowedLocation = true;
            Log.d("debug", "ああああああああああ");
        } else {
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!reqPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, reqPermissions.toArray(new String[reqPermissions.size()]), 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isAllowedLocation = true;
                        } else {
                            new AlertDialog.Builder(this, R.style.alertDialogTheme)
                                    .setTitle("位置情報の取得ができません")
                                    .setMessage("位置情報パーミッションの許可をしてください")
                                    .setPositiveButton("設定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkGPSPermission();
                                        }
                                    })
                                    .setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                }
            }
        }
    }

    public void clearStopFragment() {
        tabLayout.setVisibility(View.INVISIBLE);
        StopServiceFragment stopServiceFragment = new StopServiceFragment();
        transaction.remove(stopServiceFragment);
        transaction.commit();
    }

    private void gotoSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void gotoAboutThisApp() {
        Intent intent = new Intent(this, AboutAppActivity.class);
        startActivity(intent);
    }

    private void gotoSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}
