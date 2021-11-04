package com.example.pts3;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FindFriendsActivity extends AppCompatActivity {

    private ListView listView;
    private EditText seekingForFriends;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        listView = (ListView) findViewById(R.id.friends_view);
        seekingForFriends = (EditText) findViewById(R.id.seekingForFriends);

        seekingForFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getData(editable.toString());
            }
        });

    }

    private void getData(String str){
        FirebaseDatabase.getInstance("https://android-app-7feb8-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Users")
                .orderByChild("pseudo")
                .startAt(str)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<User> users = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String profilePicture = (String) dataSnapshot.child("profilePicture").getValue();
                            String pseudo = (String) dataSnapshot.child("pseudo").getValue();
                            String mail = (String) dataSnapshot.child("mail").getValue();
                            users.add(new User(pseudo, mail));
                        }

                        display(users);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void display(ArrayList<User> users){
        FindFriendsAdapter findFriendsAdapter = new FindFriendsAdapter(this, users);
        listView.setAdapter(findFriendsAdapter);
    }

}