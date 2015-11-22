package com.example.tomoyasu.residentapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById(R.id.StartButton);
        btn.setOnClickListener(btnListener);

        btn = (Button)findViewById(R.id.StopButton);
        btn.setOnClickListener(btnListener);
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.StartButton:
                    startService(new Intent(MainActivity.this, NotificationChangeService.class));
                    break;
                case R.id.StopButton:
                    stopService(new Intent(MainActivity.this, NotificationChangeService.class));
                    break;
            }
        }
    };

}