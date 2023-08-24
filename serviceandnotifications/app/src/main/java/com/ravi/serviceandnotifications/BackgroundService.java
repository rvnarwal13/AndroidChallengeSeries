package com.ravi.serviceandnotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class BackgroundService extends Service {
    private NotificationHelper notificationHelper;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize your service here.
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationHelper = new NotificationHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = notificationHelper.createNotification(
                "Foreground Service",
                "Your service is running in the foreground."
        );

        startForeground(1, notification);

        // Start your service tasks here.

        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up and release resources here.
        stopForeground(true);
        stopSelf();
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
    }
}
