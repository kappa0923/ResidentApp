package com.example.tomoyasu.residentapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Tomoyasu on 2015/11/30.
 */
public class MorseActivity extends Activity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_morse);
//        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.morseWave);
        //linearLayout.addView(new MorseView(this));

    }
}
