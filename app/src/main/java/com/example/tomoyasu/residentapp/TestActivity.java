package com.example.tomoyasu.residentapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tomoyasu on 2015/12/08.
 */
public class TestActivity extends Activity {
    private RecyclerView recyclerView;
    private ArrayList<String> mDataset;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.test_list);

        recyclerView = (RecyclerView)findViewById(R.id.test_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // 適当にデータ作成
        mDataset = new ArrayList<>();
        mDataset.add("A");
        mDataset.add("B");
        mDataset.add("C");
        mDataset.add("D");
        mDataset.add("E");
        mDataset.add("F");
        mDataset.add("G");
        mDataset.add("H");

        // この辺りはListViewと同じ
        recyclerView.setAdapter(new RecyclerAdapter(this, mDataset));
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        private LayoutInflater mInflater;

        public RecyclerAdapter(Context context, ArrayList<String> data) {
            super();
            mInflater = LayoutInflater.from(context);
        }

        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mInflater.inflate(R.layout.test_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            // データ表示
            String data = mDataset.get(viewHolder.getLayoutPosition());
            viewHolder.textView.setText(data);

            // クリック処理
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 長押しするとリストから削除
                    Log.i("LocalService", "position:" + viewHolder.getAdapterPosition());
                    mDataset.remove(viewHolder.getAdapterPosition());
                    notifyItemRemoved(viewHolder.getAdapterPosition());
                    return true;
                }
            });
        }

        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView)itemView.findViewById(R.id.test_list_item_text);
            }
        }

    }
}
