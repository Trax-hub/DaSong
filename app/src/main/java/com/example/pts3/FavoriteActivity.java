package com.example.pts3;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    private FavoriteAdapter favoriteAdapter;
    private TextView vide;
    private InternetCheckService internetCheckService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        vide = (TextView) findViewById(R.id.vide);
        internetCheckService = new InternetCheckService();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
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
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String title = document.get("title").toString();
                                String preview = document.get("preview").toString();
                                String artistName = document.get("artiste").toString();
                                String cover = document.get("cover").toString();
                                String coverMax = document.get("coverMax").toString();

                                tracks.add(new Track(title, preview, artistName, cover, coverMax));
                            }
                            display(tracks);
                            if(tracks.size() == 0){
                                vide.setVisibility(View.VISIBLE);
                            }else{
                                vide.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
    }

    private void display(ArrayList<Track> tracks){
        favoriteAdapter = new FavoriteAdapter(this, tracks);
        listView.setAdapter(favoriteAdapter);
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
        super.onStart();
    }

}
