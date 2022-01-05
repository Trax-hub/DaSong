package com.example.pts3;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private Button validateButton;
    private Button searchButton;
    private Track track;
    private TextInputEditText description;
    private ImageView selectedCover;
    private TextView selectedArtist;
    private TextView selectedSong;
    private ImageView pausePlay;
    private MediaPlayer mediaPlayer;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_create);
        selectedSong = (TextView) findViewById(R.id.track_title);
        selectedArtist = (TextView) findViewById(R.id.track_artist);
        selectedCover = (ImageView) findViewById(R.id.selected_cover);
        description = (TextInputEditText) findViewById(R.id.description);
        pausePlay = (ImageView) findViewById(R.id.pause_play);
        validateButton = (Button) findViewById(R.id.validate_button);
        selectedCover.setImageResource(R.drawable.general_cover);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(track != null) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        playAudio(track.getPreview());
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

        validateButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(track != null){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    Post post = new Post(track, description.getText().toString(), firebaseAuth.getCurrentUser().getUid(), dtf.format(now), new ArrayList<>());
                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("artist", post.getTrack().getArtistName());
                    postMap.put("cover", post.getTrack().getCover());
                    postMap.put("coverMax", post.getTrack().getCoverMax());
                    postMap.put("description", post.getDescription());
                    postMap.put("preview", post.getTrack().getPreview());
                    postMap.put("title", post.getTrack().getTitle());
                    postMap.put("userID", firebaseAuth.getCurrentUser().getUid());
                    postMap.put("date", dtf.format(now));

                    db.collection("/Post")
                            .whereEqualTo("userID", firebaseAuth.getCurrentUser().getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if(task.getResult().size() == 0){
                                            db.collection("/Post").add(postMap);
                                        }
                                    }
                                }
                            });
                    
                    openHomeActivity();
                }
            }
        });

        searchButton = (Button) findViewById(R.id.search_button);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });

        Intent i = getIntent();
        if(i.getSerializableExtra("Track") != null){
            track = (Track) i.getSerializableExtra("Track");
            selectedArtist.setText(track.getArtistName());
            selectedSong.setText(track.getTitle());
            Picasso.get().load(track.getCoverMax()).fit().into(selectedCover);
        }
    }

    private void openSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity(){
        startActivity(new Intent(this, HomeActivity.class));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseMediaPlayer();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                mediaPlayer.release();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer != null){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                mediaPlayer.release();
            }
        }
    }

    private void releaseMediaPlayer(){
        Log.d("MediaPlayer", "Dans releaseMediaPlayer");
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
