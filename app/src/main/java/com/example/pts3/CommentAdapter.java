package com.example.pts3;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {

    public CommentAdapter(Context context, ArrayList<Comment> commentArrayList){
        super(context, R.layout.comment_item,R.id.commentItem, commentArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        Comment comment = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
        }

        ImageView pictureItem = view.findViewById(R.id.pictureItem);
        TextView commentItem = view.findViewById(R.id.commentItem);

        FirebaseStorage.getInstance()
                .getReference()
                .child(comment.getUidComment())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(pictureItem);
                    }
                });

        commentItem.setText(comment.getComment());

        return view;
    }

}
