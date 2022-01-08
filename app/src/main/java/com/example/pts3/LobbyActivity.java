package com.example.pts3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LobbyActivity extends AppCompatActivity {

    private Button signUpButton;
    private Button logInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        signUpButton = (Button) findViewById(R.id.signUp);
        logInButton = (Button) findViewById(R.id.logIn);

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


}
