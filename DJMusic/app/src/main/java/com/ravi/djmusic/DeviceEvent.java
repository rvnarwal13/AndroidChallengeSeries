package com.ravi.djmusic;

import java.util.List;

public interface DeviceEvent {
    void createAudioFragment(String folderPath);
    void createAudioPlayerFragment(List<MediaFile> audioFiles, int position);
}
