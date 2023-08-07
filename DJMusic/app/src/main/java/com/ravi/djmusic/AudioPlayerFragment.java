package com.ravi.djmusic;

import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioPlayerFragment extends Fragment {
    private final int REPEAT_ALL = 0;
    private final int REPEAT_ONE = 1;
    private final int SHUFFLE = 2;
    private List<MediaFile> audioFiles;
    private int position = 0;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private ImageButton stopMusic, reverse10Sec, playPrev, playPause, playNext, forward10Sec, repeatToggle;
    private ImageView mediaImage;
    private TextView mediaName, artistName, albumName, totalTime, timeElapsed;
    private static boolean isStop = false;
    private Handler handler;
    private AudioFileMetaData audioFileMetaData;
    private static int toggleMusic = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_player, container, false);

        mediaPlayer = new MediaPlayer();
        seekBar = view.findViewById(R.id.music_progress);
        stopMusic = view.findViewById(R.id.stop_music);
        reverse10Sec = view.findViewById(R.id.reverse_10sec);
        playPrev = view.findViewById(R.id.play_prev);
        playPause = view.findViewById(R.id.play_pause);
        playNext = view.findViewById(R.id.play_next);
        forward10Sec = view.findViewById(R.id.forward_10sec);
        repeatToggle = view.findViewById(R.id.repeat_toggle);
        mediaImage = view.findViewById(R.id.music_image);
        mediaName = view.findViewById(R.id.music_name);
        artistName = view.findViewById(R.id.artist_name);
        albumName = view.findViewById(R.id.album_name);
        timeElapsed = view.findViewById(R.id.time_elapsed);
        totalTime = view.findViewById(R.id.total_time);

        audioFiles = (List<MediaFile>) getArguments().getSerializable("list");
        position = getArguments().getInt("position");

        handler = new Handler();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    playNextAudio();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            playMusic();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPause.setBackground(requireContext().getDrawable(R.drawable.play_music));
                } else {
                    if (isStop) {
                        try {
                            playMusic();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        mediaPlayer.start();
                        playPause.setBackground(requireContext().getDrawable(R.drawable.pause_music));
                    }
                }
            }
        });

        reverse10Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
        });

        forward10Sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
            }
        });

        stopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                seekBar.setProgress(0);
                playPause.setBackground(requireContext().getDrawable(R.drawable.play_music));
                isStop = true;
                timeElapsed.setText("00:00");
            }
        });

        playPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playPrevAudio();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playNextAudio();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        repeatToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleMusic == REPEAT_ALL) {
                    toggleMusic = REPEAT_ONE;
                    repeatToggle.setBackground(requireContext().getDrawable(R.drawable.repeat_one));
                } else if(toggleMusic == REPEAT_ONE) {
                    toggleMusic = SHUFFLE;
                    repeatToggle.setBackground(requireContext().getDrawable(R.drawable.shuffle));
                } else if(toggleMusic == SHUFFLE) {
                    toggleMusic = REPEAT_ALL;
                    repeatToggle.setBackground(requireContext().getDrawable(R.drawable.repeat_all));
                }
            }
        });

        return view;
    }

    private void playPrevAudio() throws IOException {
        if (toggleMusic == REPEAT_ALL) {
            if (position == 0) {
                position = audioFiles.size() - 1;
            } else {
                position--;
            }
        } else if(toggleMusic == REPEAT_ONE) {
            // do nothing
        } else if(toggleMusic == SHUFFLE) {
            Random random = new Random();
            position = random.nextInt(audioFiles.size());
        }
        playMusic();
    }

    private void playNextAudio() throws IOException {
        if (toggleMusic == REPEAT_ALL) {
            if (position == audioFiles.size() - 1) {
                position = 0;
            } else {
                position++;
            }
        } else if(toggleMusic == REPEAT_ONE) {
            // do nothing
        } else if(toggleMusic == SHUFFLE) {
            Random random = new Random();
            position = random.nextInt(audioFiles.size());
        }
        playMusic();
    }

    private void playMusic() throws IOException {
        setMusicContent();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioFiles.get(position).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            playPause.setBackground(requireContext().getDrawable(R.drawable.pause_music));
            seekBar.setMax(mediaPlayer.getDuration());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                totalTime.setText(formatDuration(Duration.ofMillis(mediaPlayer.getDuration())));
            }
            handler.postDelayed(updateSeekBarProgress, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String formattedDuration = String.format("%02d:%02d", absSeconds / 60, absSeconds % 60);
        return seconds < 0 ? "-" + formattedDuration : formattedDuration;
    }

    private void setMusicContent() throws IOException {
        audioFileMetaData = MetadataHelper.getAudioMetadataString(audioFiles.get(position));
        mediaName.setText(audioFiles.get(position).getName());
        if (audioFileMetaData.getArtist() != null) {
            artistName.setText(audioFileMetaData.getArtist());
        }
        if (audioFileMetaData.getAlbum() != null) {
            albumName.setText(audioFileMetaData.getAlbum());
        }
        if (audioFileMetaData.getAlbumArt() != null) {
            mediaImage.setImageBitmap(audioFileMetaData.getAlbumArt());
        }
    }

    private Runnable updateSeekBarProgress = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 10);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    timeElapsed.setText(formatDuration(Duration.ofMillis(mediaPlayer.getCurrentPosition())));
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && updateSeekBarProgress != null) {
            handler.removeCallbacks(updateSeekBarProgress);
        }

        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}