package com.test.stationalertapplication;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StationDetailActivity extends AppCompatActivity {

    private Button testButton;

//    private AudioManager mAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startFoundDevice();
            }
        });
    }

//    public void startFoundDevice() {
//        AudioDeviceInfo[] audioDevices = mAudioManager.getDevices(AudioManager.GET_DEVICES_ALL);
//        Log.d("\n\n\n\nおおおおおおおおおおおおおおおおおおおおおおおおおおおお", "おおおおおおおおおおおおああああああああああああ");
//        for (AudioDeviceInfo audio_device : audioDevices) {
//            String temp = myAudioTypeToReadableString(audio_device.getType());
//            if(temp.equals("TYPE_WIRED_HEADSET") || temp.equals("TYPE_WIRED_HEADPHONE")) {
//                Log.d("アラーム音再生許可", "許可します");
//            }
//            Log.d("\n\n\n\nおおおおおおおおおおおおおおおおおおおおおおおおおおおお", temp + "\n\n");
//        }
//    }
//
//
//    private String myAudioTypeToReadableString(int type) {
//        //https://developer.android.com/reference/android/media/AudioDeviceInfo.html
//        switch (type) {
//            case 1:
//                return "TYPE_BUILTIN_EARPIECE";
//            case 2:
//                return "TYPE_BUILTIN_SPEAKER";
//            case 3:
//                return "TYPE_WIRED_HEADSET";
//            case 4:
//                return "TYPE_WIRED_HEADPHONES";
//            case 5:
//                return "TYPE_LINE_ANALOG";
//            case 6:
//                return "TYPE_LINE_DIGITAL";
//            case 7:
//                return "TYPE_BLUETOOTH_SCO";
//            case 8:
//                return "TYPE_BLUETOOTH_A2DP";
//            //～省略～
//            case 15:
//                return "TYPE_BUILTIN_MIC";
//            case 16:
//                return "TYPE_FM_TUNER";
//            case 17:
//                return "TYPE_TV_TUNER";
//            case 18:
//                return "TYPE_TELEPHONY";
//            //～省略～
//            default:    //0
//                return "TYPE_UNKNOWN";
//        }
//    }
}
