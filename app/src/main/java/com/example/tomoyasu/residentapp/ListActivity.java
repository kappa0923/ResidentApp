package com.example.tomoyasu.residentapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomoyasu on 2015/11/24.
 */
public class ListActivity extends Activity {
    public final String TAG = "LocalService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Log.i(TAG, "List Create");

        // 端末にインストール済のアプリケーション一覧情報を取得
        final PackageManager pm = getPackageManager();
        // アプリがランチャーかフィルタをかける
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> appInfo = pm.queryIntentActivities(intent, 0);
//        final int flags = PackageManager.GET_UNINSTALLED_PACKAGES;
//        final List<ApplicationInfo> installedAppList = pm.getInstalledApplications(flags);

        // リストに一覧データを格納する
        final List<AppData> dataList = new ArrayList<>();
        for (ResolveInfo app : appInfo) {
            AppData data = new AppData();
            data.label = app.loadLabel(pm).toString();
            data.icon = app.loadIcon(pm);
            data.pname = app.activityInfo.packageName;
            dataList.add(data);
        }

//        for (ApplicationInfo app : installedAppList) {
//            // プリインアプリだったら飛ばす
//            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) continue;
//            AppData data = new AppData();
//            data.label = app.loadLabel(pm).toString();
//            data.icon = app.loadIcon(pm);
//            data.pname = app.packageName;
//            dataList.add(data);
//        }

        // リストビューにアプリケーションの一覧を表示する
        final ListView listView = new ListView(this);
        listView.setAdapter(new AppListAdapter(this, dataList));
//        listView.setBackgroundColor(Color.parseColor("#55ACEE"));
        //クリック処理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResolveInfo item = appInfo.get(position);
                PackageManager pManager = getPackageManager();
                Intent intent = pManager.getLaunchIntentForPackage(item.activityInfo.packageName);
                // 一覧から取得したintentを元のactivityに返す
                intent.putExtra("package", item.activityInfo.packageName);
                setResult(RESULT_OK, intent);
                Log.i(TAG, "item clicked:" + intent);
                finish();
//                startActivity(intent);
            }
        });
        setContentView(listView);
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
            holder.textLabel.setText(data.label);
            holder.imageIcon.setImageDrawable(data.icon);
            holder.packageName.setText(data.pname);

//            int listColor;
//            switch (position%3) {
//                case 0:
//                    listColor = Color.parseColor("#e67e22");
//                    break;
//                case 1:
//                    listColor = Color.parseColor("#2ecc71");
//                    break;
//                default:
//                    listColor = Color.parseColor("#e74c3c");
//                    break;
//            }
//
//            convertView.setBackgroundColor(listColor);

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
