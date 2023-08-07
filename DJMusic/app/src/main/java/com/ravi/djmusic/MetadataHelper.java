package com.ravi.djmusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.IOException;

public class MetadataHelper {
    public static AudioFileMetaData getAudioMetadataString(MediaFile mediaFile) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        AudioFileMetaData audioFileMetaData = null;
        try {
            retriever.setDataSource(mediaFile.getPath());
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            byte[] albumArtBytes = retriever.getEmbeddedPicture();

            Bitmap albumArt = null;
            if (albumArtBytes != null) {
                albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
            }

            audioFileMetaData = new AudioFileMetaData(title,artist, album, genre, duration, albumArt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return audioFileMetaData;
    }
}