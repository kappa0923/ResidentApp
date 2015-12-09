package com.example.tomoyasu.residentapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomoyasu on 2015/11/24.
 */
public class ListActivity extends AppCompatActivity {
    public final String TAG = "LocalService";
    public static Typeface typeface;
    private List<AppData> dataList = new ArrayList<>();
    private List<ResolveInfo> appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        typeface = Typeface.createFromAsset(getAssets(), "mgenplus-2c-regular.ttf");

        Log.i(TAG, "List Create");

        // ツールバー
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            TextView textView = (TextView)findViewById(R.id.toolbar_title);
            textView.setTypeface(typeface);
        }

        // アプリケーションの一覧表示
        // 端末にインストール済のアプリケーション一覧情報を取得
        PackageManager pm = getPackageManager();
        // アプリがランチャーかフィルタをかける
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        appInfo = pm.queryIntentActivities(intent, 0);

        // リストに一覧データを格納する
        for (ResolveInfo app : appInfo) {
            AppData data = new AppData();
            data.label = app.loadLabel(pm).toString();
            data.icon = app.loadIcon(pm);
            data.pname = app.activityInfo.packageName;
            dataList.add(data);
        }

        // リストビューにアプリケーションの一覧を表示する
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new RecyclerAdapter(this, dataList));

    }

    @Override
    public void onResume() {
        super.onResume();

        overridePendingTransition(R.anim.in_right, R.anim.out_left);
    }

    @Override
    public void onPause() {
        super.onPause();

        // アニメーションの設定
        overridePendingTransition(R.anim.in_left, R.anim.out_right);
    }

    // アプリケーションデータ格納クラス
    public static class AppData {
        String label;
        Drawable icon;
        String pname;
    }

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
            AppData data = dataList.get(viewHolder.getLayoutPosition());
            // フォント変更
            viewHolder.textLabel.setTypeface(typeface);
            viewHolder.packageName.setTypeface(typeface);
            // データ格納
            viewHolder.textLabel.setText(data.label);
            viewHolder.imageIcon.setImageDrawable(data.icon);
            viewHolder.packageName.setText(data.pname);

            // クリック処理
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // タップした位置のアプリの情報を取得
                    ResolveInfo resolveInfo = appInfo.get(viewHolder.getAdapterPosition());
                    PackageManager pManager = getPackageManager();
                    Intent intent = pManager.getLaunchIntentForPackage(resolveInfo.activityInfo.packageName);
                    // 一覧から取得したintentを元のactivityに返す
                    intent.putExtra("package", resolveInfo.activityInfo.packageName);
                    setResult(RESULT_OK, intent);
                    Log.i(TAG, "item clicked:" + intent);
                    finish();
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
}
