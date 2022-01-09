package com.example.pts3.controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pts3.R;
import com.example.pts3.model.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<Track> {

    private final MediaPlayer mediaPlayer;
    private boolean mediaPlayerReady=true;
    private String currentPreviewPlayed;

    public SearchAdapter(Context context, ArrayList<Track> searchArrayList, MediaPlayer mediaPlayer){
        super(context, R.layout.list_item,R.id.trackName, searchArrayList);
        this.mediaPlayer = mediaPlayer;
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

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying() && tracks.getPreview().equals(currentPreviewPlayed) && !mediaPlayerReady){
                    mediaPlayer.pause();
                    mediaPlayerReady = true;
                }
                else if(!mediaPlayer.isPlaying() && tracks.getPreview().equals(currentPreviewPlayed) && mediaPlayerReady){
                    mediaPlayer.start();
                    mediaPlayerReady = false;
                }
                else{
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(tracks.getPreview());
                        mediaPlayer.setVolume(0.5f, 0.5f);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.setLooping(false);
                                mediaPlayer.start();
                                currentPreviewPlayed=tracks.getPreview();
                                mediaPlayerReady=false;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
}