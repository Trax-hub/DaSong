package com.example.pts3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Log.d("Comment", getIntent().getStringExtra("PostID").toString());
        postID = getIntent().getStringExtra("PostID").toString();

        comments = new ArrayList<>();
        backComment = (ImageButton) findViewById(R.id.backComment);
        postAComment = (EditText) findViewById(R.id.postAComment);
        sendComment = (ImageButton) findViewById(R.id.sendComment);
        commentListView = (ListView) findViewById(R.id.listViewComment);

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
            @Override
            public void onClick(View view) {
                if(!postAComment.getText().toString().isEmpty()){
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("comment", postAComment.getText().toString());
                    comment.put("uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    FirebaseFirestore
                            .getInstance()
                            .collection("/Post")
                            .document(postID)
                            .collection("/comment")
                            .add(comment);

                    postAComment.getText().clear();
                }
            }
        });

        postAComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO && !postAComment.getText().toString().isEmpty()){
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("comment", postAComment.getText().toString());
                    comment.put("uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    FirebaseFirestore
                            .getInstance()
                            .collection("/Post")
                            .document(postID)
                            .collection("/comment")
                            .add(comment);

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
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String uid = document.get("uid").toString();
                                System.out.println();
                                String comment = document.get("comment").toString();
                                comments.add(new Comment(uid, comment));
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

}
