package com.example.pts3;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        ImageView pausePlay = view.findViewById(R.id.home_pause_play);
        TextView trackTitle = view.findViewById(R.id.home_track_title);
        TextView trackArtist = view.findViewById(R.id.home_track_artist);
        ImageView like = view.findViewById(R.id.like);
        ImageView add = view.findViewById(R.id.add);

        Picasso.get().load(post.getTrack().getCoverMax()).fit().into(cover);
        trackArtist.setText(post.getTrack().getArtistName());
        trackTitle.setText(post.getTrack().getTitle());

        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<>();
                map.put("title", post.getTrack().getTitle());
                map.put("preview", post.getTrack().getPreview());
                map.put("artiste", post.getTrack().getArtistName());
                map.put("cover", post.getTrack().getCover());
                map.put("coverMax", post.getTrack().getCoverMax());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                map.put("date", dtf.format(now));
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("/Favorite")
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .collection("/Tracks")
                        .add(map)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Firestore", "Document snapshot added with ID : " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Firestore", "Error adding document", e);
                            }
                        });
            }
        });

        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post.getTrack() != null) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        playAudio(post.getTrack().getPreview());
                        pausePlay.setImageResource(R.drawable.ic_pause);
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            pausePlay.setImageResource(R.drawable.ic_play);
                        } else {
                            mediaPlayer.start();
                            pausePlay.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mp) {
                                    pausePlay.setImageResource(R.drawable.ic_play);
                                    mediaPlayer = null; // finish current activity
                                }
                            });
                        }
                    }
                }

            }
        });

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
}
