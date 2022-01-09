package com.example.pts3.controller;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pts3.R;
import com.example.pts3.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    private ImageButton backComment;
    private EditText postAComment;
    private ImageButton sendComment;
    private String postID;
    private ArrayList<Comment> comments;
    private CommentAdapter commentAdapter;
    private ListView commentListView;
    private InternetCheckService internetCheckService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        postID = getIntent().getStringExtra("PostID").toString();

        comments = new ArrayList<>();
        backComment = (ImageButton) findViewById(R.id.backComment);
        postAComment = (EditText) findViewById(R.id.postAComment);
        sendComment = (ImageButton) findViewById(R.id.sendComment);
        commentListView = (ListView) findViewById(R.id.listViewComment);
        internetCheckService = new InternetCheckService();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);

        getData();

        backComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                finish();
            }
        });

        sendComment.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(!postAComment.getText().toString().isEmpty()){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("comment", postAComment.getText().toString());
                    comment.put("uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    comment.put("date", dtf.format(now));
                    FirebaseFirestore
                            .getInstance()
                            .collection("/Post")
                            .document(postID)
                            .collection("/comment")
                            .add(comment);

                    postAComment.getText().clear();
                    comments.clear();
                    getData();
                }
            }
        });

        postAComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO && !postAComment.getText().toString().isEmpty()){
                    Map<String, Object> comment = new HashMap<>();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    comment.put("comment", postAComment.getText().toString());
                    comment.put("uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    comment.put("date", dtf.format(now));
                    FirebaseFirestore
                            .getInstance()
                            .collection("/Post")
                            .document(postID)
                            .collection("/comment")
                            .add(comment);
                    comments.clear();
                    getData();

                    return true;
                }
                return false;
            }
        });

    }

    private void getData(){
        FirebaseFirestore.getInstance()
                .collection("/Post")
                .document(postID)
                .collection("/comment")
                .orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String uid = document.get("uid").toString();
                                System.out.println();
                                String comment = document.get("comment").toString();
                                String date = document.get("date").toString();
                                comments.add(new Comment(uid, comment, date));
                            }
                        }
                        display(comments);
                    }
                });
    }

    private void display(ArrayList<Comment> comments){
        commentAdapter = new CommentAdapter(this, comments);
        commentListView.setAdapter(commentAdapter);
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
}
