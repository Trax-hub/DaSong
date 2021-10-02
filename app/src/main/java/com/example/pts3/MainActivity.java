package com.example.pts3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedSong = findViewById(R.id.track_title);
        selectedArtist = findViewById(R.id.track_artist);
        selectedCover = findViewById(R.id.selected_cover);
        description = findViewById(R.id.description);

        validateButton = (Button) findViewById(R.id.validate_button);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                    Log.d(Tag, "Document snapshot added with ID : " + documentReference.getId());
                                }
                            })
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

}