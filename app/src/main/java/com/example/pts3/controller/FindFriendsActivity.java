package com.example.pts3.controller;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pts3.R;
import com.example.pts3.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFriendsActivity extends AppCompatActivity {

    private ListView listView;
    private EditText seekingForFriends;
    private InternetCheckService internetCheckService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        listView = (ListView) findViewById(R.id.friends_view);
        seekingForFriends = (EditText) findViewById(R.id.seekingForFriends);
        internetCheckService = new InternetCheckService();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
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
                .endAt(str + "\uf8ff")
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

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
        super.onStart();
    }

}
