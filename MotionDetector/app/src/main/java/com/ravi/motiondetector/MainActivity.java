package com.ravi.motiondetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView messageTextView;
    private Vibrator vibrator;
    private long lastUpdateTime = 0;
    private float lastX = 0, lastY = 0, lastZ = 0;
    private static final int SHAKE_THRESHOLD = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        messageTextView = findViewById(R.id.sensor_message);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > 100) {
                long timeDiff = currentTime - lastUpdateTime;
                lastUpdateTime = currentTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / timeDiff * 10000;
                if(speed > SHAKE_THRESHOLD) {
                    messageTextView.setText("Zig-Zag motion detected");
                    vibrate();
                }
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private void vibrate() {
        long[] pattern = {0, 500};
        VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
        vibrator.vibrate(vibrationEffect);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}