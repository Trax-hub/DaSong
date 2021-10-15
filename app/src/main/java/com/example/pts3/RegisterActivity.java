package com.example.pts3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText pseudo, mail, password;
    private Button button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        firebaseAuth = FirebaseAuth.getInstance();

        mail = (EditText) findViewById(R.id.mail);
        password = (EditText) findViewById(R.id.password);
        pseudo = (EditText) findViewById(R.id.pseudo);
        button = (Button) findViewById(R.id.signIn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


    }

    private void registerUser(){
        String mailS = mail.getText().toString().trim();
        String passwordS = password.getText().toString().trim();
        String pseudoS = pseudo.getText().toString().trim();

        if(mailS.isEmpty()){
            mail.setError("Champ requis");
            mail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(mailS).matches()){
            mail.setError("Email non valide");
            mail.requestFocus();
            return;
        }

        if(passwordS.isEmpty()){
            password.setError("Champ requis");
            password.requestFocus();
            return;
        }

        if(passwordS.length() < 6){
            password.setError("Longueur minimum de 6 caractères");
            password.requestFocus();
            return;
        }

        if(pseudoS.isEmpty()){
            pseudo.setError("Champ requis");
            pseudo.requestFocus();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(mailS, passwordS)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(pseudoS, mailS);

                            FirebaseDatabase.getInstance("https://android-app-7feb8-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Nouvel utilisateur ajouté", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Ajout non réussi", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }
}
