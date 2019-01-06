package com.test.stationalertapplication;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    private int REQUEST_CODE_RINGTONE_PICKER = 1;
    private Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    private Ringtone ringtone = RingtoneManager.getRingtone(this, uri);

    private Button select_ringtone;
    private TextView ringtone_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RingtoneManager mRingtone = new RingtoneManager(this);

        select_ringtone = (Button)findViewById(R.id.select_ringtone);
        ringtone_text = (TextView)findViewById(R.id.ringtone_text);

        select_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "タイトル");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false); // サイレントは見せない
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM); // アラーム音
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);// デフォルトは表示しない
                if (uri != null) {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri); // 選択済みを選択する
                }
                startActivityForResult(intent, REQUEST_CODE_RINGTONE_PICKER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_RINGTONE_PICKER && data != null) {
            uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            ringtone = RingtoneManager.getRingtone(this, uri);
            ringtone_text.setText(ringtone.getTitle(this));
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
