package com.example.pts3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private ArrayList<Post> posts;
    private Button doAPost, signOut;
    //TODO REMOVE

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = FirebaseFirestore.getInstance();
        posts = new ArrayList<>();
        doAPost = (Button) findViewById(R.id.doAPost);
        signOut = (Button) findViewById(R.id.signOut);
        listView = (ListView) findViewById(R.id.homeSong);
        getData();

        doAPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(HomeActivity.this, LobbyActivity.class));
            }
        });

    }

    private void getData(){

        db.collection("Post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FirebaseFirestore", document.getId() + " => " + document.getData());

                                String title = document.get("title").toString();
                                String preview = document.get("preview").toString();
                                String artistName = document.get("artist").toString();
                                String cover = document.get("cover").toString();
                                String coverMax = document.get("coverMax").toString();

                                Track track = new Track(title, preview, artistName, cover, coverMax);
                                String description = document.get("description").toString();
                                String creatorUid = document.get("userID").toString();

                                posts.add(new Post(track, description, creatorUid));

                            }

                            display(posts);

                        } else {
                            Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void display(ArrayList<Post> postArrayList){
        HomeAdapter homeAdapter = new HomeAdapter(this, postArrayList);
        listView.setAdapter(homeAdapter);
    }

}
