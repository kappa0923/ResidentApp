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
                    Log.i(TAG, "Start Button");
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, 123);
                    break;
                case R.id.StopButton:
                    stopService(new Intent(MainActivity.this, NotificationChangeService.class));
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 123 && resultCode == RESULT_OK) {
            Log.i(TAG, "Return:" + intent);
            // リストから帰ってきたintentをmapに登録
            NotificationChangeService.map.put("001", intent);

//        Button btn = (Button)findViewById(R.id.TestButton);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(intent);
//            }
//        });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sw.setChecked(NotificationChangeService.state_Notifi);
    }

}