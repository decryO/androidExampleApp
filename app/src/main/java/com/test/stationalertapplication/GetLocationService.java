package com.test.stationalertapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

public class GetLocationService extends Service {
    private FusedLocationProviderClient mLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Context context;

    private Location location;

    // 現在地から設定位置までの距離を入れる
    private float resultRadius;

    //設定位置の座標と半径を受け取る
    private double Lat, Lng;
    private double alertLine;
    private String goalStation, ringtone_String;
    private int counter = 0;

    //距離をまずここに入れる
    private float[] results = new float[1];

    private HeadSetPlugReceiver headSetPlugReceiver;

    //アラーム関係
    private SharedPreferences data;
    private SharedPreferences.Editor editor;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Uri ringtone_uri;
    private Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    private Ringtone ringtone = RingtoneManager.getRingtone(this, uri);


    @Override
    public void onCreate() {
        super.onCreate();
        context = GetLocationService.this;
        Log.d("debug", "サービスonCreate");

        // パーミッション
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "許可されています。", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "許可されていません。", Toast.LENGTH_SHORT).show();
        }

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();

        data = getSharedPreferences("DataSave", Context.MODE_PRIVATE);
        ringtone_String = data.getString("uri", null);
        if(ringtone_String != null) {
            ringtone_uri = Uri.parse(ringtone_String);
        }
        if(ringtone_String == null) {

        }else {
            ringtone = RingtoneManager.getRingtone(this, ringtone_uri);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = "default";
        String title = context.getString(R.string.app_name);

        headSetPlugReceiver = new HeadSetPlugReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headSetPlugReceiver, filter);

        Lat = intent.getDoubleExtra("Lat", 0);
        Lng = intent.getDoubleExtra("Lng", 0);
        goalStation = intent.getStringExtra("goalStation");
        alertLine = intent.getDoubleExtra("alertLine", 0.3);
        Log.d("MainActivityからの値", "\n緯度" + Lat + "\n経度" + Lng + "\nアラートライン" + alertLine);

        Intent returnIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(returnIntent);

        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // ForegroundにするためNotificationが必要、Contextを設定
        NotificationManager notificationManager =
                (NotificationManager) context.
                        getSystemService(Context.NOTIFICATION_SERVICE);


        // Notification　Channel 設定
        NotificationChannel channel = new NotificationChannel(
                channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Silent Notification");
        channel.setSound(null, null);
        channel.enableLights(false);
        channel.setLightColor(Color.BLUE);
        channel.enableVibration(false);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.baseline_alarm_white_18dp)
                    .setContentText("目的地：" + goalStation + "アラームをセット中です")
                    //.setContentText("アラームタイトル:"+ringtone.getTitle(getApplicationContext()))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();

            // startForeground
            startForeground(1, notification);
        }
        createLocationRequest();
        createLocationCallback();
        startLocationUpdates();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headSetPlugReceiver);
        stopLocationUpdates();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void createLocationRequest() {
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) {
                    return;
                }
                location = locationResult.getLastLocation();
                // 2点間の距離を求めるメソッドdistanceBetween
                // しかし、直線距離での計測なので誤差あり
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), Lat, Lng, results);
                resultRadius = results[0] / 1000;
                Log.d("FusedLocationの値", "方式"+ location.getProvider() +"\nLat = " + location.getLatitude() + "\nLng = " + location.getLongitude());
                Log.d("設定距離", "\n" + alertLine + "m");
                Log.d("2点間の距離", "\n" + String.valueOf(resultRadius) + "Km");

                // AlertDialogが出まくるのを防ぐため、一度出たらcounterをインクリメントして出さなくする。
                if (resultRadius < alertLine) {   //本番用
                //if(counter == 1){                   //テスト用
                    if(ringtone_String != null){
                        try {
                            playRingtone(ringtone_uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if (uri != null) {
                        try {
                            playRingtone(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
                    // ブロードキャストレシーバーが反応する言葉みたいなやつ
                    // とりあえず適当な言葉を入れているだけなので変更可能
                    // Manifestの書き換えaも忘れず行う。
                    intent.setAction("Location_change");
                    sendBroadcast(intent);
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
                    //counter++;
                }
                counter++;
            }
        };
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

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mLocationClient.removeLocationUpdates(mLocationCallback);
    }

}