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

import java.util.HashMap;

public class MainActivity extends Activity {
    static final String TAG="LocalService";
    private Switch sw;
    public static HashMap<String, String> map = new HashMap<>();
    public static HashMap<String, String> option = new HashMap<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map.put("00", "uri,https://www.google.co.jp/");

        MapCreater.mapWrite(map);

        map = MapCreater.mapRead();

//        File file = new File("map.txt");
//        if (!file.exists()) {
//            try {
//                FileWriter fileWriter = new FileWriter(file, false);
//                fileWriter.write("00,uri,https://www.google.co.jp/\n");
//                fileWriter.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            FileInputStream fileInputStream = openFileInput("map.txt");
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
//            String inputString = bufferedReader.readLine();
//            while(inputString != null) {
//                String[] separate = inputString.split(",", 0);
//                map.put(separate[0], separate[1] + "," + separate[2]);
//                inputString = bufferedReader.readLine();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Log.i(TAG, map.get("00"));

//        //File file = new File(getDir("data", MODE_PRIVATE), "map");
//        // シリアライズ
//        SerializableData data = new SerializableData();
//        data.setMap(map);
//        try {
//            Log.i(TAG, "write map");
//            FileOutputStream fileOutputStream = openFileOutput("map.dat", MODE_PRIVATE);
//            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
//            outputStream.writeObject(data);
//            outputStream.close();
//        } catch (Exception e) {
//            Log.d(TAG, "Error");
//        }
//
//        try {
//            Log.i(TAG, "OK");
//            FileInputStream fileInputStream = openFileInput("map.dat");
//            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
//            SerializableData data1 = (SerializableData) inputStream.readObject();
//            testmap = data1.getMap();
//            Log.i(TAG, "intent:" + testmap.get("00"));
//            inputStream.close();
//        } catch (Exception e) {
//            Log.d(TAG, "Error1");
//        }

        // HashMapの作成
//        File map_file = new File("map.tmp");
//        File option_file = new File("option.tmp");
//        if (map_file.exists() && option_file.exists()) {
//            try {
//                Log.i(TAG, "File read");
//                InputStream is = openFileInput("map.tmp");
//                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//                String str,key,val;
//                String[] sep;
//                while((str = br.readLine()) != null) {
//                    sep = str.split(",",0 );
//                    map.put(sep[0], sep[1]);
//                }
//
//
//                ObjectInputStream inputStreamMap = new ObjectInputStream(new FileInputStream("map.tmp"));
//                map = (HashMap)inputStreamMap.readObject();
//                inputStreamMap.close();
//                ObjectInputStream inputStreamOption = new ObjectInputStream(new FileInputStream("option.tmp"));
//                option = (HashMap)inputStreamOption.readObject();
//                inputStreamOption.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                Log.i(TAG, "File write");
//                OutputStream os = openFileOutput("map.tmp", MODE_PRIVATE);
//                PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
//                pw.write("00," + (new Intent(Intent.ACTION_VIEW, Uri.parse("http://techbooster.org/"))) + "\n");
//                pw.close();
//                os = openFileOutput("option.tmp", MODE_PRIVATE);
//                pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
//                pw.write("01,HOME");
//                pw.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

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