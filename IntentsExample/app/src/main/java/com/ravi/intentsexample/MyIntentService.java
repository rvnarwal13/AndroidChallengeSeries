package com.ravi.intentsexample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyIntentService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform any initialization or processing here
        // This method will be called when the service is started
        String key = intent.getStringExtra("key");
        return START_STICKY; // or any other appropriate return value
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
