package com.example.tomoyasu.residentapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tomoyasu on 2015/11/22.
 */
public class NotificationChangeService extends Service {
    static final String TAG="LocalService";

    private static int NOTIFICATION_ID = R.layout.activity_main;

    @Override
    public void onCreate() {
        // 初回起動時のみ呼ばれる
        super.onCreate();
        // 画面内に通知
        Log.i(TAG, "onCreate");
        Toast.makeText(this, "MyService#onCreate", Toast.LENGTH_SHORT).show();

        // 通知バーへの登録
        sendNotification();
    }

    // 通知バーへの登録
    private void sendNotification() {
        // 登録するビルダーの初期設定
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.mono);
        builder.setContentTitle("Resident");
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
        Log.i(TAG, "onDestroy");
        Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
