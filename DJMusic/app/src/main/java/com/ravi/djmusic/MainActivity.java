package com.ravi.djmusic;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeviceEvent {

    private static final String[] permissionsToGrant = {
            "android.permission.READ_MEDIA_AUDIO",
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.READ_MEDIA_VIDEO"
    };
    private ActivityResultLauncher<String[]> grantPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grantPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean isGranted = true;
                    for (String permission : permissions.keySet()) {
                        if (!Boolean.TRUE.equals(permissions.get(permission))) {
                            isGranted = false;
                            break;
                        }
                    }
                    if (isGranted) {
                        loadAudioFragmentFolders();
                    } else {
                        grantPermissions();
                    }
                }
        );

        if (savedInstanceState == null) {
            grantPermissions();
        }
    }

    public void loadAudioFragmentFolders() {
        FolderFragment folderFragment = new FolderFragment();
        loadFragment(folderFragment, false);
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.fragment_loader, fragment);
        transaction.commit();
    }

    @Override
    public void createAudioFragment(String folderPath) {
        AudioFragment audioListFragment = new AudioFragment();
        Bundle bundle = new Bundle();
        bundle.putString("folderPath", folderPath);
        audioListFragment.setArguments(bundle);
        loadFragment(audioListFragment, true);
    }

    @Override
    public void createAudioPlayerFragment(List<MediaFile> audioFiles, int position) {
        AudioPlayerFragment audioPlayerFragment = new AudioPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) audioFiles);
        bundle.putInt("position", position);
        audioPlayerFragment.setArguments(bundle);
        loadFragment(audioPlayerFragment, true);
    }

    private void grantPermissions() {
        boolean allPermissionsGranted = true;
        for (String permission : permissionsToGrant) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (!allPermissionsGranted) {
            grantPermissions.launch(permissionsToGrant);
        } else {
            loadAudioFragmentFolders();
        }
    }
}