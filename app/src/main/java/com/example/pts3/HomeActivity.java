package com.example.pts3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private InternetCheckService internetCheckService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        internetCheckService = new InternetCheckService();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(HomeActivity.this, LogInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);

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

        pullToRefresh.setRefreshing(false);

        getData();
        verifPost();

        doAPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    } else if(task.getResult().size() == 0){
                                        startActivity(new Intent(HomeActivity.this, CreateActivity.class));
                                    }
                                }
                            }
                        });
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
                unregisterReceiver(internetCheckService);
                IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver(internetCheckService,intentFilter);
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
            if (post.getCreatorUid().equals(creatorUid)) {
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
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(internetCheckService);
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(internetCheckService);
        super.onDestroy();
    }
}
