package com.example.pts3;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeAdapter extends ArrayAdapter<Post> {

    private MediaPlayer mediaPlayer;

    public HomeAdapter(Context context, ArrayList<Post> postArrayList){
        super(context, R.layout.post_item, R.id.home_track_title,  postArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        Post post = getItem(position);

        if(view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.post_item, parent, false);
        }

        ImageView profilePic = view.findViewById(R.id.profilePic);
        ImageView cover = view.findViewById(R.id.homeCover);
        ImageView pausePlay = view.findViewById(R.id.home_pause_play);
        TextView trackTitle = view.findViewById(R.id.home_track_title);
        TextView trackArtist = view.findViewById(R.id.home_track_artist);
        ImageView like = view.findViewById(R.id.like);
        TextView nbLike = view.findViewById(R.id.nbLike);
        ImageView add = view.findViewById(R.id.add);
        TextView creatorOfPost = view.findViewById(R.id.creatorOfPost);
        ImageView comment = view.findViewById(R.id.comment);
        TextView descriptionPost = view.findViewById(R.id.descriptionPost);

        Picasso.get().load(post.getTrack().getCoverMax()).fit().into(cover);
        trackArtist.setText(post.getTrack().getArtistName());
        trackTitle.setText(post.getTrack().getTitle());


        FirebaseStorage.getInstance()
                .getReference()
                .child(post.getCreatorUid())
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

        if(!post.getDescription().isEmpty()){
            descriptionPost.setText(post.getDescription());
        } else {
            descriptionPost.setVisibility(View.INVISIBLE);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference db = FirebaseFirestore.getInstance().collection("/Post");
        Query queryPost = db.whereEqualTo("date", post.getDate())
                .whereEqualTo("userID", post.getCreatorUid())
                .whereEqualTo("title", post.getTrack().getTitle())
                .whereEqualTo("artist", post.getTrack().getArtistName());

        queryPost.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        db.document(documentSnapshot.getId())
                                .collection("/uidWhoLiked").
                                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                ArrayList<String> uid = new ArrayList<>();
                                for(DocumentSnapshot document : task.getResult()){
                                    uid.add((String) document.get("liked"));
                                }
                                post.setUidLiked(uid);
                                post.setNbLikes(uid.size());
                                if (isLiked(post, currentUser)){
                                    like.setImageResource(R.drawable.ic_favorite_full);
                                }
                                nbLike.setText(post.getNbLikes() +" like(s)");
                            }
                        });
                    }
                }
            }
        });



        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(post.getCreatorUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        creatorOfPost.setText(dataSnapshot.child("pseudo").getValue().toString());
                    }
                });

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                queryPost.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                        intent.putExtra("PostID", documentSnapshot.getId());
                                        getContext().startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                CollectionReference db = FirebaseFirestore.getInstance().collection("/Favorite")
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .collection("/Tracks");

                Map<String, Object> map = new HashMap<>();
                map.put("title", post.getTrack().getTitle());
                map.put("preview", post.getTrack().getPreview());
                map.put("artiste", post.getTrack().getArtistName());
                map.put("cover", post.getTrack().getCover());
                map.put("coverMax", post.getTrack().getCoverMax());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                map.put("date", dtf.format(now));

                db.whereEqualTo("title", post.getTrack().getTitle())
                        .whereEqualTo("preview", post.getTrack().getPreview())
                        .whereEqualTo("artiste", post.getTrack().getArtistName())
                        .whereEqualTo("cover", post.getTrack().getCover())
                        .whereEqualTo("coverMax", post.getTrack().getCoverMax())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() == 0) {
                                        db.add(map);
                                    }
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        if (!queryDocumentSnapshot.exists()) {
                                            db.add(map);
                                        }
                                    }
                                }

                            }
                        });
            }
        });

        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post.getTrack() != null) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        playAudio(post.getTrack().getPreview());
                        pausePlay.setImageResource(R.drawable.ic_pause);
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            pausePlay.setImageResource(R.drawable.ic_play);
                        } else {
                            mediaPlayer.start();
                            pausePlay.setImageResource(R.drawable.ic_pause);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mp) {
                                    pausePlay.setImageResource(R.drawable.ic_play);
                                    mediaPlayer = null; // finish current activity
                                }
                            });
                        }
                    }
                }

            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLiked(post, currentUser)){
                    like.setEnabled(false);
                    queryPost.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                            db.document(documentSnapshot.getId())
                                                    .collection("/uidWhoLiked").document(currentUser.getUid())
                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            ArrayList<String> uid = post.getUidLiked();
                                                            uid.remove(currentUser.getUid());
                                                            post.setUidLiked(uid);
                                                            like.setEnabled(true);
                                                            like.setImageResource(R.drawable.ic_favorite_empty);
                                                            int nbLikes=post.getNbLikes()-1;
                                                            post.setNbLikes(nbLikes);
                                                            nbLike.setText(post.getNbLikes() +" like(s)");
                                                        }
                                                    });
                                        }

                                    }
                                }
                            });
                }else{
                    like.setEnabled(false);
                    queryPost.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                            HashMap<String, String> map = new HashMap<>();
                                            map.put("liked", FirebaseAuth.getInstance().getUid());
                                            db.document(documentSnapshot.getId())
                                                    .collection("/uidWhoLiked").document(currentUser.getUid())
                                                    .set(map).addOnSuccessListener(unused -> {
                                                        ArrayList<String> uid = post.getUidLiked();
                                                        uid.add(currentUser.getUid());
                                                        post.setUidLiked(uid);
                                                        like.setEnabled(true);
                                                        like.setImageResource(R.drawable.ic_favorite_full);
                                                        int nbLikes=post.getNbLikes()+1;
                                                        post.setNbLikes(nbLikes);
                                                        nbLike.setText(post.getNbLikes() +" like(s)");
                                                    });
                                        }

                                    }
                                }
                            });
                }
            }
        });

        return view;
    }

    private void playAudio(String preview){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(preview);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isLiked(Post post, FirebaseUser user){
        if (!post.getUidLiked().isEmpty()){
            for (String uid : post.getUidLiked()){
                if (uid.equals(user.getUid())) {
                    return true;
                }
            }
        }
        return false;
    }
}
