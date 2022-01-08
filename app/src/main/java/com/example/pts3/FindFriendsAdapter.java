package com.example.pts3;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class FindFriendsAdapter extends ArrayAdapter<User> {

    String currentState;

    public FindFriendsAdapter(Context context, ArrayList<User> users){
        super(context, R.layout.friends_item, R.id.usernameSeeked,  users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        User user = getItem(position);

        if (view == null){
            view = LayoutInflater.from(this.getContext()).inflate(R.layout.friends_item, parent, false);
        }

        TextView friendUsername = view.findViewById(R.id.usernameSeeked);
        ImageView profilePic = view.findViewById(R.id.profilePic);
        ImageButton addFriend = view.findViewById(R.id.addFriend);
        ImageButton declineFriend = view.findViewById(R.id.declineFriend);

        profilePic.setImageResource(R.drawable.ic_account);
        friendUsername.setText(user.getPseudo());

        FirebaseStorage.getInstance()
                .getReference()
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

        currentState = "not_friends";
        DatabaseReference friendRequestDB = FirebaseDatabase.getInstance().getReference().child("friendReq");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference friendsDB = FirebaseDatabase.getInstance().getReference().child("Friends");

        friendRequestDB.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(user.getUid())){
                    String req_type = snapshot.child(user.getUid()).child("requestType").getValue().toString();
                    if(req_type.equals("received")){
                        currentState = "req_received";
                        addFriend.setImageResource(R.drawable.ic_accept);
                        declineFriend.setImageResource(R.drawable.ic_close);
                    } else if (req_type.equals("sent")){
                        currentState = "req_sent";
                        addFriend.setImageResource(R.drawable.ic_close);
                    }
                } else {
                    friendsDB.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(user.getUid())){
                                currentState = "friends";
                                addFriend.setImageResource(R.drawable.ic_friends);
                                declineFriend.setImageResource(R.drawable.ic_close);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(currentState.equals("not_friends")){
            addFriend.setImageResource(R.drawable.ic_add);
        }

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend.setEnabled(false);
                switch (currentState) {
                    case "not_friends":
                        HashMap<String, String> data = new HashMap<>();
                        data.put("receiver", user.getUid());
                        data.put("sender", currentUser.getUid());
                        data.put("requestType", "sent");
                        friendRequestDB.child(currentUser.getUid())
                                .child(user.getUid())
                                .setValue(data)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            friendRequestDB.child(user.getUid())
                                                    .child(currentUser.getUid())
                                                    .child("requestType")
                                                    .setValue("received")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(view.getContext(), "Request sent", Toast.LENGTH_LONG);
                                                            currentState = "req_sent";
                                                            addFriend.setImageResource(R.drawable.ic_close);
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(view.getContext(), "Failed sending request", Toast.LENGTH_LONG).show();
                                        }
                                        addFriend.setEnabled(true);
                                    }
                                });
                        break;
                    case "req_sent":
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
                                                        addFriend.setEnabled(true);
                                                        currentState = "not_friends";
                                                        addFriend.setImageResource(R.drawable.ic_add);
                                                    }
                                                });
                                    }
                                });
                        break;
                    case "req_received":
                        friendsDB.child(currentUser.getUid())
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
                                                                                        addFriend.setEnabled(true);
                                                                                        currentState = "friends";
                                                                                        addFriend.setImageResource(R.drawable.ic_friends);
                                                                                        declineFriend.setImageResource(R.drawable.ic_close);
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                        break;
                }
            }
        });

        declineFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentState.equals("req_received")){
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
                                                    addFriend.setEnabled(true);
                                                    currentState = "not_friends";
                                                    addFriend.setImageResource(R.drawable.ic_add);
                                                    declineFriend.setImageResource(android.R.color.transparent);
                                                }
                                            });
                                }
                            });
                } else if(currentState.equals("friends")){
                    friendsDB.child(currentUser.getUid())
                            .child(user.getUid())
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    friendsDB.child(user.getUid())
                                            .child(currentUser.getUid())
                                            .removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    addFriend.setEnabled(true);
                                                    currentState = "not_friends";
                                                    addFriend.setImageResource(R.drawable.ic_add);
                                                    declineFriend.setImageResource(android.R.color.transparent);
                                                }
                                            });
                                }
                            });
                }
            }
        });

        return view;
    }
}