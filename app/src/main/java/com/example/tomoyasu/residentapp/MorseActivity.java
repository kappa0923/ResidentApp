package com.example.tomoyasu.residentapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomoyasu on 2015/11/30.
 */
public class MorseActivity extends Activity implements SensorEventListener {
    private String TAG = "LocalService";

    Handler countHandler;
    public float proximity;
    public static List<CircleData> circleDataList = new ArrayList<>();
    private long startTime, endTime;
    private boolean onsw = false;
    public static String morse = "";
    private int borderTime = 400;
    private Typeface tf;
    private View view;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_morse);

        // フォントの読み込み
        tf = Typeface.createFromAsset(getAssets(), "mgenplus-2c-regular.ttf");

        TextView textView = (TextView)findViewById(R.id.morseText);
        textView.setTypeface(tf);

        // アプリ追加ボタン
        Button button = (Button)findViewById(R.id.appButton);
        button.setTypeface(tf);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // webショートカット追加ボタン
        button = (Button)findViewById(R.id.webButton);
        button.setTypeface(tf);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // ホームボタンの追加
        button = (Button)findViewById(R.id.homeButton);
        button.setTypeface(tf);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 通話ボタンの追加
        button = (Button)findViewById(R.id.phoneButton);
        button.setTypeface(tf);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 波形表示ビューを取得
        view = findViewById(R.id.waveView);

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

    @Override
    public void onDestroy(){
        super.onDestroy();

        // 非同期処理の終了
        countHandler.removeCallbacks(runnable);

        // センサーの開放
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximity = event.values[0];
            if (proximity == 0) {
                // 近接時
                Log.i(TAG, "onSensorChanged");

                if (!onsw) morse = "";
                onsw = true;
                startTime = endTime = System.currentTimeMillis();

            } else if (onsw) {
                // 非近接時

                // 手が離れた時の時間を格納
                endTime = System.currentTimeMillis();

                // 円の追加
                CircleData data = new CircleData();
                data.radius = 0;
                // モールス信号の判別
                if ( (endTime - startTime) <= borderTime) {
                    morse = morse + "・";
                    data.color = Color.parseColor("#03A9F4"); // Light Blue 500
                    data.drawable = true;
                } else {
                    morse = morse + "－";
                    data.color = Color.parseColor("#FF9800"); // Orange 500
                    data.drawable = false;
                }
                circleDataList.add(data);

                // 表示テキストの更新
                TextView textView = (TextView)findViewById(R.id.morseText);
                textView.setText(morse);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
                if (MainActivity.map.containsKey(morse)) {
                    // モールス信号が登録済み
                    MorseView.errorMsg = "登録済みの信号です";
                } else {
                    MorseView.errorMsg = "登録可能の信号です";
                }
            }

            view.invalidate();
            countHandler.postDelayed(runnable, 10); // 呼び出し間隔(ミリ秒)
        }
    };

    public static class CircleData {
        boolean drawable;
        float radius;
        int color;
    }
}
