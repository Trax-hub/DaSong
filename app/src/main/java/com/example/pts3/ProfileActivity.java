package com.example.pts3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.net.URI;
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

        FirebaseStorage.getInstance().getReference()
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePic);
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

}
