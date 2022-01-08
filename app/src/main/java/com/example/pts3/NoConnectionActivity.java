package com.example.pts3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NoConnectionActivity extends AppCompatActivity {

    private Button retryButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        retryButton = findViewById(R.id.retryButton);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtil.isConnectedToInternet(NoConnectionActivity.this)){
                    startActivity(new Intent(NoConnectionActivity.this, HomeActivity.class));
                }
            }
        });
    }
}
