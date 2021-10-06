package com.example.pts3;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button validateButton;
    private Button searchButton;
    private Track track;
    private TextInputEditText description;
    private ImageView selectedCover;
    private TextView selectedArtist;
    private TextView selectedSong;
    private ImageView pausePlay;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                if(pausePlay.setImageResource()){
                    if(mediaPlayer == null){
                    mediaPlayer = new MediaPlayer();
                    playAudio(track.getPreview());
                } else {
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    } else {
                        mediaPlayer.start();
                    }
                }

            }
        });

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(track != null){
                    Post post = new Post(track, description.getText().toString());
                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("artist", post.getTrack().getArtistName());
                    postMap.put("cover", post.getTrack().getCover());
                    postMap.put("coverMax", post.getTrack().getCoverMax());
                    postMap.put("description", post.getDescription());
                    postMap.put("preview", post.getTrack().getPreview());
                    postMap.put("title", post.getTrack().getTitle());
                    db.collection("/Post")
                            .add(postMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Document snapshot added with ID : " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
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

    public void openSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
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