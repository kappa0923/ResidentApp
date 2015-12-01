package com.example.tomoyasu.residentapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tomoyasu on 2015/11/29.
 */
public class MorseView extends View {
    private String TAG = "LocalService";
    private Paint paint;
    private int width, height;
    public static String errorMsg = "Let's hand on!";
    private Typeface tf;

    public MorseView(Context context) {
        super(context);
        paint = new Paint();
        setBackgroundColor(Color.parseColor("#ecf0f1"));
        tf = Typeface.createFromAsset(getContext().getAssets(), "mgenplus-2c-regular.ttf");
    }

    public MorseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setBackgroundColor(Color.parseColor("#ecf0f1"));
        tf = Typeface.createFromAsset(getContext().getAssets(), "mgenplus-2c-regular.ttf");
    }

    public MorseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        setBackgroundColor(Color.parseColor("#ecf0f1"));
        tf = Typeface.createFromAsset(getContext().getAssets(), "mgenplus-2c-regular.ttf");
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTypeface(tf);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        width = getWidth();
        height = getHeight();

        // 登録済みの信号だった場合
        paint.setTextSize(48);
        if (errorMsg.equals("登録済みの信号です")) paint.setColor(Color.RED);
        canvas.drawText(errorMsg, (width / 2) - (paint.measureText(errorMsg) / 2), height / 2, paint);

        // 円を描画
        for (int i = 0; i < MorseActivity.circleDataList.size(); i++) {
            MorseActivity.CircleData data = MorseActivity.circleDataList.get(i);
            if (data.drawable) {
                if (data.radius > 100) data.color = Color.parseColor("#29B6F6"); // Light Blue 400
                if (data.radius > 200) data.color = Color.parseColor("#4FC3F7");
                if (data.radius > 300) data.color = Color.parseColor("#81D4FA");
                if (data.radius > 400) data.color = Color.parseColor("#B3E5FC");
                paint.setStrokeWidth(5);
            } else {
                if (data.radius > 100) data.color = Color.parseColor("#FFA726"); // Orange 400
                if (data.radius > 200) data.color = Color.parseColor("#FFB74D");
                if (data.radius > 300) data.color = Color.parseColor("#FFCC80");
                if (data.radius > 400) data.color = Color.parseColor("#FFE0B2");
                paint.setStrokeWidth(15);
            }
            paint.setColor(data.color);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(width / 2, height / 2, data.radius, paint);
            data.radius += 5;
            if (data.radius > 500) MorseActivity.circleDataList.remove(data);
        }

//        invalidate();
    }
}
