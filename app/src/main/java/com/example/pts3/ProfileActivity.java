package com.example.pts3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity{
    private ListView friendList;
    private ImageView signOut;
    private Button goToFav;
    private Button findFriends;
    private TextView pseudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendList = (ListView) findViewById(R.id.friendsInvite);
        findFriends = (Button) findViewById(R.id.findFriends);
        signOut = findViewById(R.id.signOut);
        goToFav = (Button) findViewById(R.id.goToFav);
        pseudo = findViewById(R.id.textViewPseudo);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        pseudo.setText(currentUser.getDisplayName());

        goToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(ProfileActivity.this, FavoriteActivity.class));
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

}
