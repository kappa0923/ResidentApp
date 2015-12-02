package com.example.tomoyasu.residentapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomoyasu on 2015/11/22.
 */
public class NotificationChangeService extends Service implements SensorEventListener {
    static final String TAG="LocalService";

    Handler countHandler;
    public static boolean state_Notifi; // Notificationの状態を監視
    public static Map<String, String> map = new HashMap<>();
    public static Map<String, String> option;
    private static int NOTIFICATION_ID = R.layout.activity_main;
    private float proximity;
    private long startTime, endTime;
    private boolean onsw = false;
    public static String morse = "";
    private int borderTime = 400;

    @Override
    public void onCreate() {
        // 初回起動時のみ呼ばれる
        super.onCreate();

        // mapへ読み込み
        try {
            Log.i(TAG,"map load");
            // ファイルからmapを読みだし
            InputStream inputStream = openFileInput("map.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String inputString = bufferedReader.readLine();
            while(inputString != null) {
                String[] separate = inputString.split(",", 0);
                map.put(separate[0], separate[1] + "," + separate[2]);
                inputString = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception e) {
            Log.i(TAG,"map load error");
        }

        // 画面内に通知
        Log.i(TAG, "onCreate");
        Toast.makeText(this, "MyService#onCreate", Toast.LENGTH_SHORT).show();
        state_Notifi = true;

        // 通知バーへの登録
        sendNotification();

        // センサーマネージャの登録
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
            Log.i(TAG, "sensorListener");
        }

        // 非同期処理の開始
        countHandler = new Handler();
        countHandler.post(runnable);
    }

    // 通知バーへの登録
    private void sendNotification() {
        // 登録するビルダーの初期設定
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.mono);
        builder.setLargeIcon(largeIcon);
        builder.setContentTitle("Resident");
        builder.setContentText("Try put hand");
        builder.setTicker("Switch on!");
        builder.setOngoing(true);

        // 起動したいActivityのIntent
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pi);

        // 対象バージョンによってコード分け
        final Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        startForeground(NOTIFICATION_ID, notification);
    }

    //裏で走らせてるハンドラ
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (onsw && (System.currentTimeMillis() - startTime) > borderTime*4 && proximity == 0) {
                onsw =false;
                morse = "";
            }

            if (onsw && (System.currentTimeMillis() - endTime) > borderTime*2 && proximity != 0) {
                onsw = false;
                Log.i(TAG, "morse:" + morse);

                // 入力されたモールスで振り分け
                String[] tmp = map.get(morse).split(",");
                Intent intent;
                switch (tmp[0]) {
                    case "uri":
                        // URL
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tmp[1]));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    case "app":
                        // アプリケーション
                        break;
                    case "HOME":
                         // HOMEボタンの呼び出し
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        break;
                }


//                if (map.containsKey(morse)) {
//                    Intent intent = map.get(morse);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                } else if (option.get(morse).equals("HOME")) {
//                    // HOMEボタンの呼び出し
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    startActivity(intent);
//                }

//                if (morse.equals("00")) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://techbooster.org/"));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                } else if (morse.equals("01")) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com/"));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }

                morse = "";
            }

            countHandler.postDelayed(runnable, 10); // 呼び出し間隔(ミリ秒)
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 毎回呼ばれる
        Log.i(TAG, "onStartCommand Recieved start id " + startId + ": " + intent);
        Toast.makeText(this, "MyService#onStartCommand", Toast.LENGTH_SHORT).show();
        // 明示的にサービスの起動、停止が決められる場合の返り値
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // 死ぬときに呼ばれる
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
        state_Notifi = false;

        // ハンドラの開放
        countHandler.removeCallbacks(runnable);

        // センサーの開放
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximity = event.values[0];
            if (proximity == 0) {
                // 近接時
                Log.i(TAG, "onSensorChanged");

                onsw = true;
                startTime = endTime = System.currentTimeMillis();

                // 暗黙的インテント
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://techbooster.org/"));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            } else if (onsw) {
                // 非近接時
                endTime = System.currentTimeMillis();

                // モールス信号の判別
                if ( (endTime - startTime) <= borderTime) morse = morse + "・";
                else morse = morse + "－";
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
