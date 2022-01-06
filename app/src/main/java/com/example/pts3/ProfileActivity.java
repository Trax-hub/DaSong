package com.example.pts3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity{

    static final int REQUEST_IMAGE_GET = 1;

    private ListView friendList;
    private ImageView signOut;
    private Button goToFav;
    private Button findFriends;
    private TextView pseudo;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendList = (ListView) findViewById(R.id.friendsInvite);
        findFriends = (Button) findViewById(R.id.findFriends);
        signOut = findViewById(R.id.signOut);
        goToFav = (Button) findViewById(R.id.goToFav);
        pseudo = findViewById(R.id.pseudo);
        profilePic = findViewById(R.id.profilePic);

        getData();

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


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        pseudo.setText(currentUser.getDisplayName());

        goToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ProfileActivity.this, FavoriteActivity.class));
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this, LobbyActivity.class));
            }
        });

        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ProfileActivity.this, FindFriendsActivity.class));
            }
        });
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), REQUEST_IMAGE_GET);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GET) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    profilePic.setImageURI(selectedImageUri);
                    FirebaseStorage.getInstance().getReference()
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .putFile(selectedImageUri);
                }
            }
        }
    }

    private void getData(){
        DatabaseReference friendRequestDB = FirebaseDatabase.getInstance().getReference().child("friendReq");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase.getInstance("https://android-app-7feb8-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users")
                .orderByChild("pseudo")
                .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<User> users = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(!dataSnapshot.child("uid").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    String pseudo = (String) dataSnapshot.child("pseudo").getValue();
                                    String uid = (String) dataSnapshot.child("uid").getValue();
                                    users.add(new User(pseudo, uid));
                                }
                            }
                            Log.d("AddFriends", String.valueOf(users.size()));
                            ArrayList<User> requestUser = new ArrayList<>();
                            friendRequestDB.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Log.d("AddFriends", String.valueOf(users.size()));
                                    for(User user : users) {
                                        if (snapshot.hasChild(user.getUid())) {
                                            String req_type = snapshot.child(user.getUid()).child("requestType").getValue().toString();
                                            if (req_type.equals("received")) {
                                                requestUser.add(user);
                                            }
                                        }
                                    }
                                    Log.d("AddFriends", String.valueOf(requestUser.size()) + " Request");
                                    display(requestUser);
                                }

                                @Override
                                public void onCancelled (@NonNull DatabaseError error){

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

    }

    private void display(ArrayList<User> users){
        AcceptFriendAdapter adapter = new AcceptFriendAdapter(this, users);
        friendList.setAdapter(adapter);
    }

}
