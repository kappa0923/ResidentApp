package com.example.tomoyasu.residentapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends Activity {
    static final String TAG="LocalService";
    private Switch sw;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // フォント取得
        Typeface tf = Typeface.createFromAsset(getAssets(), "mgenplus-2c-regular.ttf");

        // トグルスイッチ
        sw = (Switch)findViewById(R.id.SwitchButton);
        sw.setChecked(NotificationChangeService.state_Notifi);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "Switch On");
                    startService(new Intent(MainActivity.this, NotificationChangeService.class));
                } else {
                    Log.i(TAG, "Switch Off");
                    stopService(new Intent(MainActivity.this, NotificationChangeService.class));
                }
            }
        });
        sw.setTypeface(tf);

        // 各ボタンの設定
        Button btn = (Button)findViewById(R.id.StartButton);
        btn.setOnClickListener(btnListener);
        btn.setTypeface(tf);

        btn = (Button)findViewById(R.id.StopButton);
        btn.setOnClickListener(btnListener);
        btn.setTypeface(tf);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.StartButton:
                    startService(new Intent(MainActivity.this, NotificationChangeService.class));
                    break;
                case R.id.StopButton:
                    stopService(new Intent(MainActivity.this, NotificationChangeService.class));
                    break;
            }
        }
    };

}