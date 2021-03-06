package com.example.tomoyasu.residentapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.rey.material.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomoyasu on 2015/11/30.
 */
public class MorseActivity extends AppCompatActivity implements SensorEventListener {
    private String TAG = "LocalService";

    Handler countHandler;
    public float proximity;
    public static List<CircleData> circleDataList = new ArrayList<>();
    private long startTime, endTime;
    private boolean onsw = false;
    public static String morse = "";
    public static String uri = "", number = "";
    private int borderTime = 400;
    private Typeface tf;
    private View view;
    private Dialog dialog;
    private int view_width;
    private Intent intent;
    private Button webButton, appButton, homeButton, phoneButton, callButton, okButton;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_morse);

        intent = getIntent();

        // 画面幅を取得
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        view_width = metrics.widthPixels;

        // フォントの読み込み
        tf = Typeface.createFromAsset(getAssets(), "mgenplus-2c-regular.ttf");

        // ツールバー
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            TextView textView = (TextView)findViewById(R.id.toolbar_title);
            textView.setTypeface(tf);
        }

        // モールス信号表示ビュー
        TextView textView = (TextView)findViewById(R.id.morseText);
        textView.setTypeface(tf);

        // アプリ追加ボタン
        appButton = (Button)findViewById(R.id.appButton);
        appButton.setTypeface(tf);
        appButton.setEnabled(false);
        appButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MorseActivity.this, ListActivity.class);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
                // アニメーションの設定
                overridePendingTransition(R.anim.in_right, R.anim.out_left);
            }
        });

        // webショートカット追加ボタン
        webButton = (Button)findViewById(R.id.webButton);
        webButton.setTypeface(tf);
        webButton.setEnabled(false);
        webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(MorseActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog);

                // windowサイズを取得
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = view_width;
                dialog.getWindow().setAttributes(lp);

                // dialogの要素
                TextView textView1 = (TextView) dialog.findViewById(R.id.dialog_title);
                textView1.setTypeface(tf);

                textView1 = (TextView) dialog.findViewById(R.id.dialog_text);
                textView1.setTypeface(tf);

                textView1 = (TextView) dialog.findViewById(R.id.dialog_morse);
                textView1.setText(morse);
                textView1.setTypeface(tf);

                // URLを入力するためのフィールド
                final EditText editText = (EditText) dialog.findViewById(R.id.editText);
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                editText.setTypeface(tf);
                // ダイアログの内容が変更されるたびに呼ばれる
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                        if ( s.toString().startsWith("http://") || s.toString().startsWith("https://")) {
                            okButton.setEnabled(true);
                            editText.setError(null);
                            editText.setHelper("Enter URL");
                        } else {
                            okButton.setEnabled(false);
                            editText.setError("Enter URL only: start with 'http://' or 'https://'");
                            editText.setHelper(null);
                        }
                    }
                });

                // Dialog cancel button
                Button button = (Button) dialog.findViewById(R.id.button_cancel);
                button.setTypeface(tf);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "Dialog CANCEL");

                        dialog.dismiss();
                    }
                });

                // Dialog OK button
                okButton = (Button) dialog.findViewById(R.id.button_ok);
                okButton.setTypeface(tf);
                okButton.setEnabled(false);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "Dialog OK");
                        if (!editText.getText().toString().equals("")) {
                            // OKタップ時の返すintent
                            uri = editText.getText().toString();
                            intent.putExtra("morse", morse);
                            intent.putExtra("package", "uri," + uri);
                            setResult(RESULT_OK, intent);

                            dialog.dismiss();
                            finish();
                        }
                    }
                });

                dialog.show();
            }
        });

        // ホームボタンの追加
        homeButton = (Button)findViewById(R.id.homeButton);
        homeButton.setTypeface(tf);
        homeButton.setEnabled(false);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("morse", morse);
                intent.putExtra("package", "HOME,HOME");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // 電話を受けるボタンの追加
        phoneButton = (Button)findViewById(R.id.phoneButton);
        phoneButton.setTypeface(tf);
        phoneButton.setEnabled(false);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("morse", morse);
                intent.putExtra("package", "CALL,CALL");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // 電話をかけるボタンの追加
        callButton = (Button)findViewById(R.id.callButton);
        callButton.setTypeface(tf);
        callButton.setEnabled(false);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(MorseActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog);

                // windowサイズを取得
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = view_width;
                dialog.getWindow().setAttributes(lp);

                // dialogの要素
                TextView textView1 = (TextView) dialog.findViewById(R.id.dialog_title);
                textView1.setText("make call shortcut");
                textView1.setTypeface(tf);

                textView1 = (TextView) dialog.findViewById(R.id.dialog_text);
                textView1.setTypeface(tf);

                textView1 = (TextView) dialog.findViewById(R.id.dialog_morse);
                textView1.setText(morse);
                textView1.setTypeface(tf);

                // URLを入力するためのフィールド
                final EditText editText = (EditText) dialog.findViewById(R.id.editText);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                editText.setTypeface(tf);

                // Dialog cancel button
                Button button = (Button) dialog.findViewById(R.id.button_cancel);
                button.setTypeface(tf);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "Dialog CANCEL");

                        dialog.dismiss();
                    }
                });

                // Dialog OK button
                okButton = (Button) dialog.findViewById(R.id.button_ok);
                okButton.setTypeface(tf);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "Dialog OK");
                        if (!editText.getText().toString().equals("")) {
                            // OKタップ時の返すintent
                            number = editText.getText().toString();
                            intent.putExtra("morse", morse);
                            intent.putExtra("package", "TEL," + number);
                            setResult(RESULT_OK, intent);

                            dialog.dismiss();
                            finish();
                        }
                    }
                });

                dialog.show();
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        // アニメーションの設定
        overridePendingTransition(R.anim.in_left, R.anim.out_right);
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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.i(TAG, "MorseActivity:" + intent);
            String hoge = intent.getStringExtra("package");
            intent.putExtra("morse", morse);
            intent.putExtra("package", "app," + hoge);
            setResult(RESULT_OK, intent);
            finish();
        }
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

                if (morse.length() <= 1) {
                    // モールス信号が短すぎる
                    MorseView.errorMsg = "信号が短すぎます";
                    // 各ボタンを無効化
                    appButton.setEnabled(false);
                    webButton.setEnabled(false);
                    homeButton.setEnabled(false);
                    phoneButton.setEnabled(false);
                } else {

                    // 入力されたモールスで振り分け
                    if (MainActivity.map.containsKey(morse)) {
                        // モールス信号が登録済み
                        MorseView.errorMsg = "登録済みの信号です";
                        // 各ボタンを無効化
                        appButton.setEnabled(false);
                        webButton.setEnabled(false);
                        homeButton.setEnabled(false);
                        phoneButton.setEnabled(false);
                    } else {
                        // モールス信号が登録可
                        MorseView.errorMsg = "登録可能の信号です";
                        // 各ボタンを有効化
                        appButton.setEnabled(true);
                        webButton.setEnabled(true);
                        homeButton.setEnabled(true);
                        phoneButton.setEnabled(true);
                    }
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
