package com.example.pts3;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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
        db.collection("Favorite")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("Tracks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                if(document.get("track") instanceof Track){
                                    Track track = (Track) document.get("track");
                                }
                                if(document.get("date") instanceof Date){
                                    Date date = (Date) document.get("date");
                                }

                            }
                        }
                    }
                });
    }

}
