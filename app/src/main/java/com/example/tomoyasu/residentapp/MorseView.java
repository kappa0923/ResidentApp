package com.example.tomoyasu.residentapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tomoyasu on 2015/11/29.
 */
public class MorseView extends View {
    private Paint paint;

    public MorseView(Context context) {
        super(context);
        paint = new Paint();
        setBackgroundColor(Color.parseColor("#ecf0f1"));
    }

    public MorseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setBackgroundColor(Color.parseColor("#ecf0f1"));
    }

    public MorseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        setBackgroundColor(Color.parseColor("#ecf0f1"));
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        canvas.drawText("MyView Test", 20, 20, paint);

        invalidate();
    }
}
