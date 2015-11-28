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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;

public class MainActivity extends Activity {
    static final String TAG="LocalService";
    private Switch sw;
    public static HashMap<String, String> map = new HashMap<>(); // <key_code, 実行形式, URI|パッケージ名>

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = this.getFileStreamPath("map.txt");
        if (!file.exists()) {
            // ファイルが存在しなかったら生成
            map.put("00", "uri,https://www.google.co.jp/");
            map.put("000", "HOME");
            mapWrite(map);
        } else {
            // ファイルが存在したらロード
            map = mapRead();
        }

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
            String str = intent.getStringExtra("package");
            map = mapRead();
            map.put("01", "app," + str);

            for (HashMap.Entry<String,String> entry : map.entrySet()) {
                Log.i(TAG, entry.getKey() + ":" + entry.getValue());
            }

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

    public void mapWrite(HashMap<String,String> map) {
        try {
            Log.i(TAG,"test1");
            // ファイルにmapを保存
            OutputStream outputStream = openFileOutput("map.txt", MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            for (HashMap.Entry<String,String> entry : map.entrySet()) {
                printWriter.println(entry.getKey() + "," + entry.getValue());
            }
            printWriter.close();
        } catch (Exception e) {
            Log.i(TAG,"Error1");
        }
    }

    public HashMap<String,String> mapRead() {
        HashMap<String,String> temp_map = new HashMap<>();
        try {
            Log.i(TAG,"test2");
            // ファイルからmapを読みだし
            InputStream inputStream = openFileInput("map.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String inputString = bufferedReader.readLine();
            while(inputString != null) {
                String[] separate = inputString.split(",", 0);
                temp_map.put(separate[0], separate[1] + "," + separate[2]);
                inputString = bufferedReader.readLine();
            }
        } catch (Exception e) {
            Log.i(TAG,"Error2");
        }

        return temp_map;
    }

}