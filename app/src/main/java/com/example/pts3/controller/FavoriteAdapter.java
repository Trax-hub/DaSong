package com.example.pts3.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pts3.R;
import com.example.pts3.model.Track;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class FavoriteAdapter extends ArrayAdapter<Track>{

        private MediaPlayer mediaPlayer;
        private FavoriteAdapter adapter;
        private ArrayList<Track> trackArrayList;

        public FavoriteAdapter(Context context, ArrayList<Track> trackArrayList){
            super(context, R.layout.favorite_item,R.id.trackName, trackArrayList);
            this.adapter = this;
            this.trackArrayList = trackArrayList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = super.getView(position, convertView, parent);

            Track tracks = getItem(position);

            if (view == null){
                view = LayoutInflater.from(getContext()).inflate(R.layout.favorite_item, parent, false);
            }

            ImageView cover = view.findViewById(R.id.cover);
            TextView track = view.findViewById(R.id.trackName);
            TextView artist = view.findViewById(R.id.artist);
            ImageButton delete = view.findViewById(R.id.deleteFavorite);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CollectionReference collecRef = FirebaseFirestore.getInstance()
                            .collection("/Favorite")
                            .document("/" + Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                            .collection("/Tracks");


                    collecRef.whereEqualTo("artiste", tracks.getArtistName())
                            .whereEqualTo("title", tracks.getTitle())
                            .whereEqualTo("cover", tracks.getCover())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                                            collecRef.document(documentSnapshot.getId()).delete();
                                        }
                                        trackArrayList.remove(tracks);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                }
            });

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

