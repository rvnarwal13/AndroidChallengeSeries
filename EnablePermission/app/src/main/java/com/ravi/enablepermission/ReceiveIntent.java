package com.ravi.enablepermission;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ReceiveIntent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("MESSAGE");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.i("MESSAGE", message);
    }
}
