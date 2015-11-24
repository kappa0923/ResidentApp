package com.example.tomoyasu.residentapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by Tomoyasu on 2015/11/24.
 */
public class callReceiver extends BroadcastReceiver {
    private Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;

        try {
            // 電話の状態を取得するリスナー
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener phoneListener = new MyPhoneStateListener();
            tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            Log.e("LocalService", "Telephone Manager false");
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String callNumber) {
            // NotificationがON、入力された信号がCALLを含む
            if (NotificationChangeService.state_Notifi && NotificationChangeService.option.get(NotificationChangeService.morse).equals("CALL")) {
                // 電話が鳴っているか
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    // 受話ボタンを押す
                    Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                    ctx.sendOrderedBroadcast(intent, null);
                }
            }
        }
    }
}
