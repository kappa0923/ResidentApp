package com.example.tomoyasu.residentapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String TAG="LocalService";
    private SwitchCompat sw;
    public static HashMap<String, String> map = new HashMap<>(); // <key_code, 実行形式, URI|パッケージ名>
    public static Typeface tf;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        File file = this.getFileStreamPath("map.txt");
        if (!file.exists()) {
            // ファイルが存在しなかったら生成
            map.put("・・", "uri,https://www.google.co.jp/");
            map.put("・・・", "HOME,HOME");
            mapWrite(map);
        } else {
            // ファイルが存在したらロード
            map = mapRead();
        }

        tf = Typeface.createFromAsset(getAssets(), "mgenplus-2c-regular.ttf");

        createMain();

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.StartButton:
                    Log.i(TAG, "Start Button");
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, 1);
                    // アニメーションの設定
                    overridePendingTransition(R.anim.in_right, R.anim.out_left);
                    break;
                case R.id.StopButton:
                    Log.i(TAG, "Stop Button");
                    Intent intent1 = new Intent(MainActivity.this, MorseActivity.class);
                    intent1.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent1, 2);
                    // アニメーションの設定
                    overridePendingTransition(R.anim.in_right, R.anim.out_left);

                    // サービスの停止
                    stopService(new Intent(MainActivity.this, NotificationChangeService.class));
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Intentで呼び出したアプリから返ってきたら呼ばれる
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.i(TAG, "Return:" + intent);
            // リストから帰ってきたintentをmapに登録
            String str = intent.getStringExtra("package");
            map.put("・－", "app," + str);

            for (HashMap.Entry<String,String> entry : map.entrySet()) {
                Log.i(TAG, entry.getKey() + ":" + entry.getValue());
            }
            mapWrite(map);
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            String hoge = intent.getStringExtra("morse");
            String fuga = intent.getStringExtra("package");
            Log.i(TAG, "Return:" + hoge + "," + fuga);
            // リストから返ってきたintentをmapに登録
            map.put(hoge, fuga);

            mapWrite(map);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        createMain();
//        sw.setChecked(NotificationChangeService.state_Notifi);
//
//        // リストに一覧データを格納する
//        final List<AppData> dataList = new ArrayList<>();
//        for (HashMap.Entry<String,String> app : map.entrySet()) {
//            AppData data = new AppData();
//            String[] tmp = app.getValue().split(",");
//            Bitmap bitmap;
//            switch (tmp[0]) {
//                case "uri":
//                    data.label = app.getKey();
//                    data.pname = "link:" + tmp[1];
//                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.net);
//                    data.icon = new BitmapDrawable(getResources(), bitmap);
//                    break;
//                case "app":
//                    PackageManager packageManager = getPackageManager();
//                    try {
//                        data.label = app.getKey();
//                        data.pname = "app:" + tmp[1];
//                        data.icon = packageManager.getApplicationIcon(tmp[1]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case "HOME":
//                    data.label = app.getKey();
//                    data.pname = "HOME KEY";
//                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home);
//                    data.icon = new BitmapDrawable(getResources(), bitmap);
//                    break;
//            }
//            dataList.add(data);
//        }
//
//        // リストビューを生成
//        final ListView listView = (ListView)findViewById(R.id.listView);
//        listView.setAdapter(new AppListAdapter(this, dataList));
//
//        //クリック処理
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
    }

    public void createMain() {
        setContentView(R.layout.activity_main);

        // ツールバー
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
            TextView textView = (TextView)findViewById(R.id.toolbar_title);
            textView.setTypeface(tf);
        }

        // トグルスイッチ
        TextView textView = (TextView)findViewById(R.id.switch_text);
        textView.setTypeface(tf);
        sw = (SwitchCompat)findViewById(R.id.SwitchButton);
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

        // 各ボタンの設定
        Button btn = (Button)findViewById(R.id.StartButton);
        btn.setOnClickListener(btnListener);
        btn.setTypeface(tf);

        btn = (Button)findViewById(R.id.StopButton);
        btn.setOnClickListener(btnListener);
        btn.setTypeface(tf);

        sw.setChecked(NotificationChangeService.state_Notifi);

        // リストに一覧データを格納する
        final List<AppData> dataList = new ArrayList<>();
        for (HashMap.Entry<String,String> app : map.entrySet()) {
            AppData data = new AppData();
            String[] tmp = app.getValue().split(",");
            Bitmap bitmap;
            switch (tmp[0]) {
                case "uri":
                    data.label = app.getKey();
                    data.pname = "link:" + tmp[1];
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.net);
                    data.icon = new BitmapDrawable(getResources(), bitmap);
                    break;
                case "app":
                    PackageManager packageManager = getPackageManager();
                    try {
                        data.label = app.getKey();
                        data.pname = "app:" + tmp[1];
                        data.icon = packageManager.getApplicationIcon(tmp[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "HOME":
                    data.label = app.getKey();
                    data.pname = "HOME KEY";
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home);
                    data.icon = new BitmapDrawable(getResources(), bitmap);
                    break;
            }
            dataList.add(data);
        }

        // リストビューを生成
        final ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(new AppListAdapter(this, dataList));

        //クリック処理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    public void mapWrite(HashMap<String,String> map) {
        try {
            Log.i(TAG,"map write");
            // ファイルにmapを保存
            OutputStream outputStream = openFileOutput("map.txt", MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            for (HashMap.Entry<String,String> entry : map.entrySet()) {
                printWriter.println(entry.getKey() + "," + entry.getValue());
            }
            printWriter.close();
        } catch (Exception e) {
            Log.i(TAG,"map write error");
        }
    }

    public HashMap<String,String> mapRead() {
        HashMap<String,String> temp_map = new HashMap<>();
        try {
            Log.i(TAG,"map load");
            // ファイルからmapを読みだし
            InputStream inputStream = openFileInput("map.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String inputString = bufferedReader.readLine();
            while(inputString != null) {
                String[] separate = inputString.split(",", 0);
                temp_map.put(separate[0], separate[1] + "," + separate[2]);
                inputString = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception e) {
            Log.i(TAG,"map load error");
        }

        return temp_map;
    }

    // アプリケーションデータ格納クラス
    private static class AppData {
        String label;
        Drawable icon;
        String pname;
    }

    // アプリケーションのラベルとアイコンを表示するためのアダプタークラス
    private static class AppListAdapter extends ArrayAdapter<AppData> {

        private final LayoutInflater mInflater;

        public AppListAdapter(Context context, List<AppData> dataList) {
            super(context, R.layout.activity_list);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            addAll(dataList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = new ViewHolder();

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_list, parent, false);
                holder.textLabel = (TextView) convertView.findViewById(R.id.label);
                holder.imageIcon = (ImageView) convertView.findViewById(R.id.icon);
                holder.packageName = (TextView) convertView.findViewById(R.id.pname);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 表示データを取得
            final AppData data = getItem(position);
            // ラベルとアイコンをリストビューに設定
            holder.textLabel.setTypeface(tf);
            holder.packageName.setTypeface(tf);
            holder.textLabel.setText(data.label);
            holder.imageIcon.setImageDrawable(data.icon);
            holder.packageName.setText(data.pname);

            return convertView;
        }
    }

    // ビューホルダー
    private static class ViewHolder {
        TextView textLabel;
        ImageView imageIcon;
        TextView packageName;
    }

}