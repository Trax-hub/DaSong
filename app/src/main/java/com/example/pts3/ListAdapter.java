package com.example.pts3;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Track> {

    private MediaPlayer mediaPlayer;

    public ListAdapter(Context context, ArrayList<Track> searchArrayList){
        super(context, R.layout.list_item,R.id.trackName, searchArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        Track tracks = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ImageView cover = view.findViewById(R.id.cover);
        TextView track = view.findViewById(R.id.trackName);
        TextView artist = view.findViewById(R.id.artist);

        mediaPlayer = new MediaPlayer();

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                    mediaPlayer.reset();
               else
                   playAudio(tracks.getPreview());
            }
        });

        if(tracks.getCover().isEmpty()){
            tracks.setCover("https://drive.google.com/drive/u/0/folders/1KrHiDVmSoHVJDUHZ3l6Uvk949CeHYfp2");
            tracks.setCoverMax("https://drive.google.com/drive/u/0/folders/1KrHiDVmSoHVJDUHZ3l6Uvk949CeHYfp2");
        }

        Picasso.get().load(tracks.getCover()).fit().into(cover);
        track.setText(tracks.getTitle());
        artist.setText(tracks.getArtistName());

        return view;
    }

    private void playAudio(String preview){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(preview);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}
