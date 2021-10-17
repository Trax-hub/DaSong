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

public class HomeAdapter extends ArrayAdapter<Post> {

    private MediaPlayer mediaPlayer;

    public HomeAdapter(Context context, ArrayList<Post> postArrayList){
        super(context, R.layout.home, R.id.home_track_title,  postArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        Post post = getItem(position);

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.home, parent, false);
        }

        ImageView cover = view.findViewById(R.id.homeCover);
        ImageView playPause = view.findViewById(R.id.home_pause_play);
        TextView trackTitle = view.findViewById(R.id.home_track_title);
        TextView trackArtist = view.findViewById(R.id.home_track_artist);
        ImageView like = view.findViewById(R.id.like);
        ImageView add = view.findViewById(R.id.add);

        Picasso.get().load(post.getTrack().getCoverMax()).fit().into(cover);
        trackArtist.setText(post.getTrack().getArtistName());
        trackTitle.setText(post.getTrack().getTitle());

        mediaPlayer = new MediaPlayer();

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                    mediaPlayer.reset();
                else
                    playAudio(post.getTrack().getPreview());
            }
        });

        return view;
    }

    private void playAudio(String preview){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(preview);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
