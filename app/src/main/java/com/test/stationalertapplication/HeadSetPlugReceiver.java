package com.test.stationalertapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

public class HeadSetPlugReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_HEADSET_PLUG)){
            int state = intent.getIntExtra("state", -1);
            if(state == 0){
                Intent PLUG_intent = new Intent(context, AlertDialogActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, PLUG_intent, 0);
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }else if(state == 1){
                Log.d("\n\n\n\n\nイヤホン状態\n\n\n\n\n", "\n\n\n\n\n刺さりました\n\n\n\n\n");
            }
        }
    }
}
