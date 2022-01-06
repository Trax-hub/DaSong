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

public class AcceptFriendAdapter extends ArrayAdapter<User> {

    public AcceptFriendAdapter(Context context, ArrayList<User> users){
        super(context, R.layout.friends_item, R.id.usernameSeeked,  users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        User user = getItem(position);

        if (view == null){
            view = LayoutInflater.from(this.getContext()).inflate(R.layout.friends_item, parent, false);
        }

        TextView friendUsername = view.findViewById(R.id.usernameSeeked);
        ImageView profilePic = view.findViewById(R.id.profilePic);
        ImageView addFriend = view.findViewById(R.id.addFriend);
        ImageView declineFriend = view.findViewById(R.id.declineFriend);

        profilePic.setImageResource(R.drawable.ic_account);
        friendUsername.setText(user.getPseudo());



        return view;
    }
}
