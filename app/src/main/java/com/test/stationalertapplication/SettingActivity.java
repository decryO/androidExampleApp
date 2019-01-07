package com.test.stationalertapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class SettingActivity extends AppCompatActivity {

    protected final int REQUEST_CODE_RINGTONE_PICKER = 1;

    private Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    private Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SharedPreferences data;
    private SharedPreferences.Editor editor;

    private String ringtone_String;
    private Uri ringtone_uri;

    private Toolbar toolbar;
    private Button select_ringtone, play_ringtone, stop_ringtone;
    private TextView ringtone_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        editor = data.edit();

        ringtone_String = data.getString("uri", null);
        if(ringtone_String != null) {
            ringtone_uri = Uri.parse(ringtone_String);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RingtoneManager mRingtone = new RingtoneManager(this);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        select_ringtone = (Button)findViewById(R.id.select_ringtone);
        play_ringtone = (Button)findViewById(R.id.play_ringtone);
        stop_ringtone = (Button)findViewById(R.id.stop_ringtone);
        ringtone_text = (TextView)findViewById(R.id.ringtone_text);

        toolbar.setTitle(R.string.ringtone_setting);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(ringtone_String == null) {
            ringtone_text.setText("設定なし");
        }else{
            Log.d("あああああああああああああああああああああ", "ああああああああああああああああああ");
            ringtone = RingtoneManager.getRingtone(this, ringtone_uri);
            ringtone_text.setText(ringtone.getTitle(this));
        }

        play_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ringtone_String != null){
                    try {
                        Log.d("いいいいいいいいいいいいいいいいいいいいいいいいいいい", "いいいいいいいいいいいいいいいいいいいいいいいいい");
                        playRingtone(ringtone_uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if (uri != null) {
                    try {
                        Log.d("ええええええええええええええええええええええええええええ", "ええええええええええええええええええええええええええええええええええええええええ");
                        playRingtone(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        stop_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        });

        select_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
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
            ringtone_text.setText(ringtone.getTitle(this));
            editor.putString("uri", uri.toString());
            editor.apply();
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
