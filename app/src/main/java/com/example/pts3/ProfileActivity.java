package com.example.pts3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_GET = 1;
    static final int REQUEST_MANAGE_PROFILE = 2;

    private ListView friendList;
    private ImageButton signOut, back, findFriends, goToFav;
    private Button managingProfile;
    private TextView pseudo;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendList = (ListView) findViewById(R.id.friendsInvite);
        findFriends = (ImageButton) findViewById(R.id.findFriends);
        signOut = (ImageButton) findViewById(R.id.signOut);
        back = (ImageButton) findViewById(R.id.back);
        goToFav = (ImageButton) findViewById(R.id.goToFav);
        pseudo = (TextView) findViewById(R.id.pseudo);
        profilePic = findViewById(R.id.profilePic);
        managingProfile = findViewById(R.id.managingProfile);

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

        managingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ProfileActivity.this, ProfileManagerActivity.class), REQUEST_MANAGE_PROFILE);
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        pseudo.setText(currentUser.getDisplayName());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        goToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            } else if (requestCode == REQUEST_MANAGE_PROFILE) {
                String result = data.getStringExtra("pseudo");
                pseudo.setText(result);
            }
        }
    }

    private void getData() {
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
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (!dataSnapshot.child("uid").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                String pseudo = (String) dataSnapshot.child("pseudo").getValue();
                                String uid = (String) dataSnapshot.child("uid").getValue();
                                users.add(new User(pseudo, uid));
                            }
                        }
                        ArrayList<User> requestUser = new ArrayList<>();
                        friendRequestDB.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (User user : users) {
                                    if (snapshot.hasChild(user.getUid())) {
                                        String req_type = snapshot.child(user.getUid()).child("requestType").getValue().toString();
                                        if (req_type.equals("received")) {
                                            requestUser.add(user);
                                        }
                                    }
                                }
                                display(requestUser);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void display(ArrayList<User> users) {
        AcceptFriendAdapter adapter = new AcceptFriendAdapter(this, users);
        friendList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
