package com.example.tomoyasu.residentapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Tomoyasu on 2015/11/29.
 */
public class MorseView extends View {
    public MorseView(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawText("MyView Test", 20, 20, paint);
    }
}
