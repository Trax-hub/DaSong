package com.example.pts3.controller;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pts3.R;
import com.example.pts3.model.Post;
import com.example.pts3.model.Track;
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
    private AlertDialog alertDialog;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            Intent intent = new Intent(HomeActivity.this, LobbyActivity.class);
            startActivity(intent);
            finish();
        }


        mediaPlayer = new MediaPlayer();

        if(user != null){
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
        }

        firebaseAuth = FirebaseAuth.getInstance();
        internetCheckService = new InternetCheckService();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(HomeActivity.this, LobbyActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);

        noFriends();

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
                                            .child(Objects.requireNonNull(Objects.requireNonNull(document.get("userID")).toString()))
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
        HomeAdapter homeAdapter = new HomeAdapter(this, postArrayList, mediaPlayer);
        listView.setAdapter(homeAdapter);
    }

    private void addPost(QueryDocumentSnapshot document){
        String title = Objects.requireNonNull(document.get("title")).toString();
        String preview = Objects.requireNonNull(document.get("preview")).toString();
        String artistName = Objects.requireNonNull(document.get("artist")).toString();
        String cover = Objects.requireNonNull(document.get("cover")).toString();
        String coverMax = Objects.requireNonNull(document.get("coverMax")).toString();

        Track track = new Track(title, preview, artistName, cover, coverMax);
        String description = Objects.requireNonNull(document.get("description")).toString();
        String creatorUid = Objects.requireNonNull(document.get("userID")).toString();
        String date = Objects.requireNonNull(document.get("date")).toString();

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
                .whereEqualTo("userID", Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
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

    private void noFriends(){
        FirebaseDatabase.getInstance()
                .getReference("Friends")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> other) {
                        if(other.isSuccessful() && other.getResult().getValue() == null){
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.my_dialog);
                            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_no_friends, null);
                            dialogBuilder.setView(dialogView);
                            Button goToAddFriends = dialogView.findViewById(R.id.goToAddFriends);
                            goToAddFriends.setOnClickListener(view -> {
                                startActivity(new Intent(HomeActivity.this, FindFriendsActivity.class));
                                finish();
                            });
                            alertDialog = dialogBuilder.create();
                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            alertDialog.show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        firebaseAuth.addAuthStateListener(authStateListener);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
        display(posts);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(internetCheckService);
        super.onStop();
        mediaPlayer.reset();
        firebaseAuth.removeAuthStateListener(authStateListener);
        if(alertDialog != null){
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.reset();
        System.out.println("onback    jejfj");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.reset();
    }
}
