package com.example.pts3.controller;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pts3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ProfileManagerActivity extends AppCompatActivity {

    private EditText newPseudo, oldPassword, newPassword, newMail, emailPassword;
    private InternetCheckService internetCheckService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_managing);

        internetCheckService = new InternetCheckService();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
        Button sendNewMail = findViewById(R.id.sendNewMail);
        Button sendNewPassword = findViewById(R.id.sendNewPassword);
        Button sendNewPseudo = findViewById(R.id.sendNewPseudo);
        newMail = findViewById(R.id.newMail);
        newPseudo = findViewById(R.id.newPseudo);
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        emailPassword = findViewById(R.id.emailPassword);

        sendNewPseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pseudo = newPseudo.getText().toString().trim();

                if(pseudo.isEmpty()){
                    newPseudo.setError("Champ requis");
                    newPseudo.requestFocus();
                    return;
                }

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(pseudo)
                        .build();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Objects.requireNonNull(user).updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("pseudo")
                                            .setValue(pseudo);

                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("pseudo", pseudo);
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }
                            }
                        });

                newPseudo.getText().clear();
            }
        });

        sendNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        sendNewMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMail();
            }
        });
    }

    private void changePassword(){
        String oldPass = oldPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();

        if(oldPass.isEmpty()){
            oldPassword.setError("Champ requis");
            oldPassword.requestFocus();
            return;
        }

        if(oldPass.length() < 6){
            oldPassword.setError("Longueur minimum de 6 caractères");
            oldPassword.requestFocus();
            return;
        }

        if(newPass.isEmpty()){
            newPassword.setError("Champ requis");
            newPassword.requestFocus();
            return;
        }

        if(newPass.length() < 6){
            newPassword.setError("Longueur minimum de 6 caractères");
            newPassword.requestFocus();
            return;
        }

        if(oldPass.equals(newPass)){
            newPassword.setError("Les deux sont les mêmes");
            newPassword.requestFocus();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = Objects.requireNonNull(user).getEmail();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(email), oldPass);

        user.reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updatePassword(newPass)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(),"Le mot de passe a été changé",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Le mot de passe n'est pas le bon",Toast.LENGTH_LONG).show();
                }
            }
        });

        newPassword.getText().clear();
        oldPassword.getText().clear();

    }

    private void changeMail(){
        String email = newMail.getText().toString().trim();
        String password = emailPassword.getText().toString().trim();

        if(email.isEmpty()){
            newMail.setError("Champ requis");
            newMail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            newMail.setError("Email non valide");
            newMail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            newPassword.setError("Champ requis");
            newPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            newPassword.setError("Longueur minimum de 6 caractères");
            newPassword.requestFocus();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String oldMail = Objects.requireNonNull(user).getEmail();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(oldMail), password);
        user.reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updateEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(getApplicationContext(),"L'email a été changé",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(),"Le mot de passe n'est pas le bon",Toast.LENGTH_LONG).show();
                }
            }
        });

        newMail.getText().clear();
        emailPassword.getText().clear();
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
