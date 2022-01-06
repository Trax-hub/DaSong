package com.example.pts3;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private ArrayList<Post> posts;
    private ImageButton doAPost;
    private ImageView profilePic;
    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(this, LobbyActivity.class));
            finish();
        }

        FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePic);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilePic.setImageResource(R.drawable.ic_account);
            }
        });

        //TODO Si pas d'ami, faire bouton chercher des amis

        db = FirebaseFirestore.getInstance();
        posts = new ArrayList<>();
        doAPost = (ImageButton) findViewById(R.id.doAPost);
        profilePic = (ImageView) findViewById(R.id.profilePic);
        listView = (ListView) findViewById(R.id.homeSong);
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        getData();
        verifPost();

        doAPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CreateActivity.class));
                finish();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                verifPost();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void getData(){
        db.collection("Post")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FirebaseFirestore", document.getId() + " => " + document.getData());


                                if(FirebaseAuth.getInstance().getCurrentUser() != null){

                                    FirebaseDatabase.getInstance()
                                            .getReference("Friends")
                                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                            .child(Objects.requireNonNull(document.get("userID").toString()))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DataSnapshot> other) {
                                                    if(other.isSuccessful()){
                                                        if(other.getResult() != null && other.getResult().getValue() != null){
                                                            if(other.getResult().getValue().toString().equals("Friends")){
                                                                addPost(document);
                                                                display(posts);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                    if (Objects.requireNonNull(document.get("userID")).toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        addPost(document);
                                    }

                                }

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

    private void addPost(QueryDocumentSnapshot document){
        String title = document.get("title").toString();
        String preview = document.get("preview").toString();
        String artistName = document.get("artist").toString();
        String cover = document.get("cover").toString();
        String coverMax = document.get("coverMax").toString();

        Track track = new Track(title, preview, artistName, cover, coverMax);
        String description = document.get("description").toString();
        String creatorUid = document.get("userID").toString();
        String date = document.get("date").toString();

        boolean same = false;

        for(Post post : posts){
            if (post.getTrack().getTitle().equals(title) &&
                    post.getTrack().getPreview().equals(preview) &&
                    post.getTrack().getArtistName().equals(artistName) &&
                    post.getTrack().getCover().equals(cover) &&
                    post.getTrack().getCoverMax().equals(coverMax) &&
                    post.getDescription().equals(description) &&
                    post.getCreatorUid().equals(creatorUid) &&
                    post.getDate().equals(date)) {

                same = true;
                break;
            }
        }

        if (!same){
            posts.add(new Post(track, description, creatorUid, date));
        }

    }

    private void verifPost(){
        db.collection("/Post")
                .whereEqualTo("userID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if(task.getResult().size() != 0){
                                doAPost.setEnabled(false);
                                doAPost.setColorFilter(Color.GRAY);
                            }
                        }
                    }
                });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
