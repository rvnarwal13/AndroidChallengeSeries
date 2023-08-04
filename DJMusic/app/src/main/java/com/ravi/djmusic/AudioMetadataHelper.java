package com.ravi.djmusic;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.IOException;

public class AudioMetadataHelper {
    public static MediaMetadataRetriever getAudioMetadata(Context context, Uri audioUri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try{
            retriever.setDataSource(context, audioUri);
        } catch (Exception e) {
            e.printStackTrace();
            retriever = null;
        }
        return retriever;
    }

    public static String getMetadataString(MediaMetadataRetriever retriever) throws IOException {
        String details = "";
        try {
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            // Display the retrieved details in the dialog.
            details = "Title: " + title + "\n"
                    + "Artist: " + artist + "\n"
                    + "Album: " + album + "\n"
                    + "Genre: " + genre + "\n"
                    + "Duration: " + duration + " ms";
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions that may occur during metadata retrieval.
        } finally {
            retriever.release();
        }
        return details;
    }
}
