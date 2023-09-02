package com.ravi.backgroundapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Objects;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            Data inputData = new Data.Builder()
                    .putString("notificationMessage", "notificationMessage")
                    .build();

            OneTimeWorkRequest backgroundWork =
                    new OneTimeWorkRequest.Builder(BackgroundWorker.class)
                            .setInputData(inputData)
                            .build();

            WorkManager.getInstance(context).enqueue(backgroundWork);
        }
    }
}
