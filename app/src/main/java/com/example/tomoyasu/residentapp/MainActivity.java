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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.app.Dialog;
import com.rey.material.widget.FloatingActionButton;

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
    public List<AppData> dataList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tf = Typeface.createFromAsset(getAssets(), "mgenplus-2c-regular.ttf");

        // ファイルのロード&セーブ
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

        FloatingActionButton mButton = (FloatingActionButton)findViewById(R.id.fab);
        mButton.setOnClickListener(btnListener);

        sw.setChecked(NotificationChangeService.state_Notifi);

        // リストに一覧データを格納する
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
                case "CALL":
                    data.label = app.getKey();
                    data.pname = "Catch phone";
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.phone);
                    data.icon = new BitmapDrawable(getResources(), bitmap);
                    break;
                case "TEL":
                    data.label = app.getKey();
                    data.pname = "TEL:" + tmp[1];
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.phone);
                    data.icon = new BitmapDrawable(getResources(), bitmap);
                    break;
            }
            dataList.add(data);
        }

        // リストビューにアプリケーションの一覧を表示する
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerAdapter(this, dataList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.fab:
                    Log.i(TAG, "Fab Button");
                    Intent intent = new Intent(MainActivity.this, MorseActivity.class);
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, 2);
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

            String[] tmp = fuga.split(",");

            // リストを更新
            AppData data = new AppData();
            data.label = hoge;
            data.pname = tmp[1];
            switch (tmp[0]) {
                case "uri":
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.net);
                    data.icon = new BitmapDrawable(getResources(), bitmap);
                    break;
                case "app":
                    PackageManager packageManager = getPackageManager();
                    try {
                        data.icon = packageManager.getApplicationIcon(tmp[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "HOME":
                    data.pname = "HOME KEY";
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home);
                    data.icon = new BitmapDrawable(getResources(), bitmap);
                    break;
                case "CALL":
                    data.pname = "Catch phone";
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.phone);
                    data.icon = new BitmapDrawable(getResources(), bitmap);
                    break;
                case "TEL":
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.phone);
                    data.icon = new BitmapDrawable(getResources(), bitmap);
                    break;
                default:
                    data.pname = "empty";
                    data.label = "";
            }
            dataList.add(data);
            Log.i(TAG, "Insert:" + data.pname);
            mAdapter.notifyDataSetChanged();

            mapWrite(map);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sw.setChecked(NotificationChangeService.state_Notifi);
    }

    // mapの保存
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

    // mapの読み込み
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

    // アダプタークラス
    public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
        private LayoutInflater mInflater;

        public RecyclerAdapter(Context context, List<AppData> data) {
            super();
            mInflater = LayoutInflater.from(context);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mInflater.inflate(R.layout.activity_list_item, parent, false);
            return new ViewHolder(v);
        }

        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            // データ表示
            Log.i(TAG, Integer.toString(position));
            AppData data = dataList.get(viewHolder.getLayoutPosition());
            // フォント変更
            viewHolder.textLabel.setTypeface(tf);
            viewHolder.packageName.setTypeface(tf);
            // データ格納
            viewHolder.textLabel.setText(data.label);
            viewHolder.imageIcon.setImageDrawable(data.icon);
            viewHolder.packageName.setText(data.pname);

            // クリック処理
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // ロングタップしたものを削除する
                    final Dialog mDialog = new Dialog(MainActivity.this);
                    mDialog.title("Delete this shortcut?")
                            .positiveAction("Delete")
                            .positiveActionClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.i(TAG, "Dialog OK");
                                    dataList.remove(viewHolder.getAdapterPosition());
                                    notifyItemRemoved(viewHolder.getAdapterPosition());

                                    map.remove(viewHolder.textLabel.getText().toString());
                                    mapWrite(map);
                                    mDialog.dismiss();
                                }
                            })
                            .negativeAction("CANCEL")
                            .negativeActionClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                }
                            })
                            .cancelable(true)
                            .show();

//                    dataList.remove(viewHolder.getAdapterPosition());
//                    notifyItemRemoved(viewHolder.getAdapterPosition());
//3
//                    Log.i(TAG, viewHolder.textLabel.getText().toString());
//                    map.remove(viewHolder.textLabel.getText().toString());
//                    mapWrite(map);
                    return true;
                }
            });
        }

        public int getItemCount() {
            return dataList.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textLabel;
        public ImageView imageIcon;
        public TextView packageName;

        public ViewHolder(View itemView) {
            super(itemView);
            textLabel = (TextView)itemView.findViewById(R.id.label);
            imageIcon = (ImageView)itemView.findViewById(R.id.icon);
            packageName = (TextView)itemView.findViewById(R.id.pname);
        }
    }

    // アプリケーションのラベルとアイコンを表示するためのアダプタークラス
//    private static class AppListAdapter extends ArrayAdapter<AppData> {
//
//        private final LayoutInflater mInflater;
//
//        public AppListAdapter(Context context, List<AppData> dataList) {
//            super(context, R.layout.activity_list_item);
//            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            addAll(dataList);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ViewHolder holder = new ViewHolder();
//
//            if (convertView == null) {
//                convertView = mInflater.inflate(R.layout.activity_list_item, parent, false);
//                holder.textLabel = (TextView) convertView.findViewById(R.id.label);
//                holder.imageIcon = (ImageView) convertView.findViewById(R.id.icon);
//                holder.packageName = (TextView) convertView.findViewById(R.id.pname);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            // 表示データを取得
//            final AppData data = getItem(position);
//            // ラベルとアイコンをリストビューに設定
//            holder.textLabel.setTypeface(tf);
//            holder.packageName.setTypeface(tf);
//            holder.textLabel.setText(data.label);
//            holder.imageIcon.setImageDrawable(data.icon);
//            holder.packageName.setText(data.pname);
//
//            return convertView;
//        }
//    }
//
//    // ビューホルダー
//    private static class ViewHolder {
//        TextView textLabel;
//        ImageView imageIcon;
//        TextView packageName;
//    }

}