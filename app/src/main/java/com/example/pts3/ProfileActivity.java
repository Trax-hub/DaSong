package com.example.pts3;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity{
    ListView friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendList = (ListView) findViewById(R.id.friendList);

        List friendList = new ArrayList<User>();
    }

}
