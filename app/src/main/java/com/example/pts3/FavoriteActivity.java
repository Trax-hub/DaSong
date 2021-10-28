package com.example.pts3;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.core.utilities.Tree;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class FavoriteActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private ArrayList<Track> tracks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        db = FirebaseFirestore.getInstance();
        tracks = new ArrayList<>();
        listView = (ListView) findViewById(R.id.favList);
        getData();
    }

    private void getData(){
        db.collection("/Favorite")
                .document("/" + Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("/Tracks")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<Map<String, Object>> list = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String title = document.get("title").toString();
                                String preview = document.get("preview").toString();
                                String artistName = document.get("artiste").toString();
                                String cover = document.get("cover").toString();
                                String coverMax = document.get("coverMax").toString();
                                
                                tracks.add(new Track(title, preview, artistName, cover, coverMax));
                            }
                            display(tracks);
                        }
                    }
                });
    }

    private void display(ArrayList<Track> tracks){
        ListAdapter listAdapter = new ListAdapter(this, tracks);
        listView.setAdapter(listAdapter);
    }

}
