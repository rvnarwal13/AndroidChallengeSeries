package com.ravi.backgroundapp;

import android.app.Application;
import android.widget.Toast;

public class MyApplication extends Application {
    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "toast", Toast.LENGTH_SHORT).show();
    }
}
