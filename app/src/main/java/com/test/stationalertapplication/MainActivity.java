package com.test.stationalertapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    public boolean isAllowedLocation = false;

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
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        if (Build.VERSION.SDK_INT >= 23) {
            checkGPSPermission();
        }

        selectMenuItems();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        MapsFragment fragment = new MapsFragment();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.container, fragment);
//        transaction.commit();
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

                if (id == R.id.action_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }else if (id == R.id.action_about) {
                    AboutAppActivity setFragment = new AboutAppActivity();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.addToBackStack("");
                    transaction.replace(R.id.container, setFragment);
                    transaction.commit();
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

    private void gotoSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}
