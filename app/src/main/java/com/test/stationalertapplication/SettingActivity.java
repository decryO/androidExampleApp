package com.test.stationalertapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    protected final int REQUEST_CODE_RINGTONE_PICKER = 1;

    private Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    private Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SharedPreferences data;
    private SharedPreferences.Editor editor;

    private String ringtone_String;
    private Uri ringtone_uri;
    Setting setting = new Setting();
    SettingAdapter adapter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // 縦画面
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        editor = data.edit();

        ringtone_String = data.getString("uri", null);
        if(ringtone_String != null) {
            ringtone_uri = Uri.parse(ringtone_String);
        }

        ListView listView = (ListView)findViewById(R.id.setting_list);
        ArrayList<Setting> list = new ArrayList<>();
        adapter = new SettingAdapter(SettingActivity.this);
        adapter.setSettinglist(list);
        listView.setAdapter(adapter);

        setting.setTitle("到着時のアラーム音");
        if(ringtone_String == null) {
            setting.setSubtitle("設定なし");
        }else{
            Log.d("あああああああああああああああああああああ", "ああああああああああああああああああ");
            ringtone = RingtoneManager.getRingtone(this, ringtone_uri);
            //ringtone_text.setText(ringtone.getTitle(this));
            setting.setSubtitle(ringtone.getTitle((this)));
        }
        list.add(setting);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(setting.getId() == 0){
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "タイトル");
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false); // サイレントは見せない
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM); // アラーム音
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);// デフォルトは表示しない
                    if(ringtone_String != null){
                        Log.d("ううううううううううううううううううう","ううううううううううううううううううううううううううう");
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone_uri); //Preferenceがあった場合の選択済み
                    }else if (uri != null) {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri); // 選択済みを選択する
                    }
                    startActivityForResult(intent, REQUEST_CODE_RINGTONE_PICKER);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        RingtoneManager mRingtone = new RingtoneManager(this);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
//        select_ringtone = (Button)findViewById(R.id.select_ringtone);
//        play_ringtone = (Button)findViewById(R.id.play_ringtone);
//        stop_ringtone = (Button)findViewById(R.id.stop_ringtone);
//        ringtone_text = (TextView)findViewById(R.id.ringtone_text);

        toolbar.setTitle(R.string.ringtone_setting_title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        play_ringtone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ringtone_String != null){
//                    try {
//                        Log.d("いいいいいいいいいいいいいいいいいいいいいいいいいいい", "いいいいいいいいいいいいいいいいいいいいいいいいい");
//                        playRingtone(ringtone_uri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }else if (uri != null) {
//                    try {
//                        Log.d("ええええええええええええええええええええええええええええ", "ええええええええええええええええええええええええええええええええええええええええ");
//                        playRingtone(uri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        stop_ringtone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mediaPlayer.stop();
//                mediaPlayer.reset();
//                mediaPlayer.release();
//            }
//        });

//        select_ringtone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "タイトル");
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false); // サイレントは見せない
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM); // アラーム音
//                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);// デフォルトは表示しない
//                if(ringtone_String != null){
//                    Log.d("ううううううううううううううううううう","ううううううううううううううううううううううううううう");
//                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone_uri); //Preferenceがあった場合の選択済み
//                }else if (uri != null) {
//                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri); // 選択済みを選択する
//                }
//                startActivityForResult(intent, REQUEST_CODE_RINGTONE_PICKER);
//            }
//        });
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void playRingtone(Uri uri) throws IOException {
        mediaPlayer.setDataSource(this, uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_RINGTONE_PICKER && data != null) {
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            ringtone = RingtoneManager.getRingtone(this, uri);
            //ringtone_text.setText(ringtone.getTitle(this));
            setting.setSubtitle(ringtone.getTitle(this));
            adapter.notifyDataSetChanged();
            editor.putString("uri", uri.toString());
            editor.apply();
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
