package com.ravi.enablepermission;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ActivityResultLauncher<String> requestPermissionLauncherBt, requestPermissionLauncherLoc;
    private ActivityResultLauncher<Intent> enableBtLauncher, enableLocLauncher;
    private Button enableBt, enableLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableBt = findViewById(R.id.btn_enable_bt);
        enableLoc = findViewById(R.id.btn_enable_location);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        // enables bluetooth
        enableBtLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() != RESULT_OK) {
                    Toast.makeText(MainActivity.this, "Bluetooth is required to use this feature", Toast.LENGTH_SHORT).show();
                    Intent openSettingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(openSettingsIntent);
                }
            }
        });

        // enables location
        enableLocLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() != RESULT_OK) {
                    Toast.makeText(MainActivity.this, "Location is required to use this feature", Toast.LENGTH_SHORT).show();
                    Intent openSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(openSettingsIntent);
                }
            }
        });

        // grant permission for bluetooth
        requestPermissionLauncherBt = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        enableBluetooth();
                    } else {
                        openApplicationDetailsSettings();
                    }
                });

        // grant permission for location
        requestPermissionLauncherLoc = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        enableLocation();
                    } else {
                        openApplicationDetailsSettings();
                    }
                });

        enableBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.BLUETOOTH_CONNECT")
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncherBt.launch("android.permission.BLUETOOTH_CONNECT");
                } else {
                    enableBluetooth();
                }
            }
        });

        enableLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.ACCESS_FINE_LOCATION")
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncherLoc.launch("android.permission.ACCESS_FINE_LOCATION");
                }
            }
        });
    }

    /**
     * triggers enable bluetooth
     */
    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtLauncher.launch(enableBluetooth);
        } else {
            Toast.makeText(this, "Bluetooth is enabled.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * triggers enable location
     */
    private void enableLocation() {
        Intent enableLocation = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        enableLocLauncher.launch(enableLocation);
    }

    /**
     * open application settings to give permission manually.
     */
    private void openApplicationDetailsSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}