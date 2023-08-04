package com.ravi.djmusic;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String[] permissionsToGrant = {
            "android.permission.READ_MEDIA_AUDIO",
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.READ_MEDIA_VIDEO"
    };

    private ActivityResultLauncher<String> getMediaLauncher;
    private ActivityResultLauncher<String[]> grantPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatButton getMediaFile = findViewById(R.id.btn_get_song);

        grantPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    permissions -> {
                        boolean isGranted = true;
                        for(String permission : permissions.keySet()) {
                            if(!Boolean.TRUE.equals(permissions.get(permission))) {
                                isGranted = false;
                                break;
                            }
                        }
                        if(isGranted) {
                            Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "All permissions are not granted.", Toast.LENGTH_SHORT).show();
                        }
                    }
                );

        getMediaLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result != null) {
                            try {
                                retrieveAndDisplaySongDetails(result);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

        getMediaFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMediaLauncher.launch("*/*");
            }
        });
        grantPermissions();
    }

    private void grantPermissions() {
        boolean allPermissionsGranted = true;
        for(String permission: permissionsToGrant) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if(!allPermissionsGranted) {
            grantPermissions.launch(permissionsToGrant);
        }
    }

    private void retrieveAndDisplaySongDetails(Uri uri) throws IOException {
        MediaMetadataRetriever retriever = AudioMetadataHelper.getAudioMetadata(getApplicationContext(), uri);
        String uriData = AudioMetadataHelper.getMetadataString(retriever);
        showDetailsDialog(uriData);
    }

    private void showDetailsDialog(String details) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Song Details");
        builder.setMessage(details);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}