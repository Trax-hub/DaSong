package com.example.pts3.controller;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pts3.R;
import com.example.pts3.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class AcceptFriendAdapter extends ArrayAdapter<User> {

    private final AcceptFriendAdapter adapter;
    private final ArrayList<User> users;

    public AcceptFriendAdapter(Context context, ArrayList<User> users){
        super(context, R.layout.friend_request_item, R.id.usernameRequest,  users);
        this.adapter = this;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        User user = getItem(position);

        if (view == null){
            view = LayoutInflater.from(this.getContext()).inflate(R.layout.friend_request_item, parent, false);
        }

        TextView friendUsername = view.findViewById(R.id.usernameRequest);
        ImageView profilePic = view.findViewById(R.id.profilePic);
        ImageView addFriend = view.findViewById(R.id.yesToRequest);
        ImageView declineFriend = view.findViewById(R.id.noToRequest);

        profilePic.setImageResource(R.drawable.ic_account);

        FirebaseStorage.getInstance().getReference()
                .child(user.getUid())
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

        friendUsername.setText(user.getPseudo());
        addFriend.setImageResource(R.drawable.ic_add);
        declineFriend.setImageResource(R.drawable.ic_close);


        DatabaseReference friendRequestDB = FirebaseDatabase.getInstance().getReference().child("friendReq");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference friendsDB = FirebaseDatabase.getInstance().getReference().child("Friends");

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendsDB.child(Objects.requireNonNull(currentUser).getUid())
                        .child(user.getUid())
                        .setValue("Friends")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                friendsDB.child(user.getUid())
                                        .child(currentUser.getUid())
                                        .setValue("Friends")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                friendRequestDB.child(currentUser.getUid())
                                                        .child(user.getUid())
                                                        .removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                friendRequestDB.child(user.getUid())
                                                                        .child(currentUser.getUid())
                                                                        .removeValue()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                users.remove(user);
                                                                                adapter.notifyDataSetChanged();
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
            }
        });

        declineFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendRequestDB.child(Objects.requireNonNull(currentUser).getUid())
                        .child(user.getUid())
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                friendRequestDB.child(user.getUid())
                                        .child(currentUser.getUid())
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                users.remove(user);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        });
            }
        });


        return view;
    }


}
