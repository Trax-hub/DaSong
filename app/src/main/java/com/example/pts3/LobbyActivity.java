package com.example.pts3;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LobbyActivity extends AppCompatActivity {

    private Button signUpButton;
    private Button logInButton;
    private InternetCheckService internetCheckService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        signUpButton = (Button) findViewById(R.id.signUp);
        logInButton = (Button) findViewById(R.id.logIn);
        internetCheckService = new InternetCheckService();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LobbyActivity.this, SignUpActivity.class));
                finish();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LobbyActivity.this, LogInActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(internetCheckService);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(internetCheckService);
        super.onDestroy();
    }

}
