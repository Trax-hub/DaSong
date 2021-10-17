package com.example.pts3;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Post> posts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        posts = new ArrayList<>();
        listView = (ListView) findViewById(R.id.homeSong);
        getData();
        display();
    }

    private void getData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("/Post")
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
                        } else {
                            Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void display(){
        HomeAdapter homeAdapter = new HomeAdapter(this, posts);
        listView.setAdapter(homeAdapter);
        homeAdapter.notifyDataSetChanged();
    }

}
