package com.example.pts3;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MusicHandler {

    private final Context context;
    private MediaPlayer mediaPlayer;

    public MusicHandler(Context context){
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
    }

    public void playMusic(String preview){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(preview);
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    start();
                    mediaPlayer.setLooping(false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void start(){
        mediaPlayer.start();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
}
