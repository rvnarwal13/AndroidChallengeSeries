package com.ravi.torch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private CameraManager cameraManager;
    private String cameraId;
    private static boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // set torch to off if already on
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }

        imageView = findViewById(R.id.iv_light_bulb);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlash();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toggleFlash() {
        try {
            isFlashOn = !isFlashOn;
            cameraManager.setTorchMode(cameraId, isFlashOn);
            if(isFlashOn) {
                imageView.setImageDrawable(getDrawable(R.drawable.flashlight_on));
            } else {
                imageView.setImageDrawable(getDrawable(R.drawable.flashlight_off));
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}