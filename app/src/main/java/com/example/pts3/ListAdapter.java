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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Search> {

    public ListAdapter(Context context, ArrayList<Search> searchArrayList){
        super(context, R.layout.list_item,R.id.trackName,searchArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        Search search = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ImageView cover = view.findViewById(R.id.cover);
        TextView track = view.findViewById(R.id.trackName);
        TextView artist = view.findViewById(R.id.artist);

        Picasso.get().load(search.getAlbum().getCoverSmall()).fit().into(cover);
        track.setText(search.getTitle());
        artist.setText(search.getArtist().getName());

        return view;
    }
}
