package com.ravi.intentsexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, MyIntentService.class);
        startService(serviceIntent);

        Intent serviceIntentExtra = new Intent(this, MyIntentService.class);
        serviceIntent.putExtra("key", "value");
        startService(serviceIntent);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"));
        intent.setPackage("com.google.android.youtube");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // YouTube app is not installed, handle the error as needed
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("MESSAGE", "Hi, this is an intent from Ravi.");
        broadcastIntent.setAction("MESSAGE");
        sendBroadcast(broadcastIntent);
    }
}