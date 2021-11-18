package com.example.pts3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private Button backComment;
    private EditText postAComment;
    private Button sendComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Log.d("Comment", getIntent().getStringExtra("PostID").toString());
        String postID = getIntent().getStringExtra("PostID").toString();

        backComment = (Button) findViewById(R.id.backComment);
        postAComment = (EditText) findViewById(R.id.postAComment);
        sendComment = (Button) findViewById(R.id.sendComment);


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
                    comment.put("Comment", postAComment.getText().toString());
                    FirebaseFirestore
                            .getInstance()
                            .collection("/Post")
                            .document(postID)
                            .collection("/comment")
                            .document(FirebaseAuth.getInstance().getUid())
                            .set(comment);

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
                    FirebaseFirestore
                            .getInstance()
                            .collection("/Post")
                            .document(postID)
                            .collection("/comment")
                            .document(FirebaseAuth.getInstance().getUid())
                            .update(comment);

                    return true;
                }
                return false;
            }
        });

    }

}
