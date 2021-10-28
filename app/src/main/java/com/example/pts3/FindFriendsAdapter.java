package com.example.pts3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FindFriendsAdapter extends ArrayAdapter<User> {

    public FindFriendsAdapter(Context context, ArrayList<User> users){
        super(context, R.layout.activity_find_friends, R.id.seek_username,  users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        User user = getItem(position);

        if (view == null){
            view = LayoutInflater.from(this.getContext()).inflate(R.layout.friends_item, parent, false);
        }

        TextView friendUsername = view.findViewById(R.id.seek_username);
        ImageView friendImage = view.findViewById(R.id.friendImage);

        friendImage.setImageResource(R.drawable.ic_account);
        friendUsername.setText(user.getPseudo());

        return view;
    }
}
