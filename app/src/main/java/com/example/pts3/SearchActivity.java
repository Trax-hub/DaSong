package com.example.pts3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private Button validateButton;

    private EditText editText;

    private static HttpURLConnection connection;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editText = findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.list_item);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                System.out.println(s);

                if(s.isEmpty()){
                    return;
                }

                final ArrayList<Track>[] tracks = (ArrayList<Track>[]) new ArrayList<?>[1];

                Thread thread = new Thread(() -> tracks[0] = getListTrack(s));
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                display(tracks[0]);
            }
        });

    }

    private ArrayList<Track> getListTrack(String s){

        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();
        Root root = new Root(new ArrayList<Search>());


        try {
            s = s.replace(" ", "");
            s = "https://api.deezer.com/search?q=" + s;
            URL url = new URL(s);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            System.out.println(status);

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }

            System.out.println(responseContent.toString());

            Gson gson = new Gson();
            root = gson.fromJson(responseContent.toString(), Root.class);
            System.out.println(root.toString());
            root.display();

            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Track> tracks = new ArrayList<>();

        for(Search search : root.getSearches()){
            tracks.add( new Track(search.getTitle(), search.getPreview(), search.getArtist().getName(), search.getAlbum().getCover()));
        }

        return tracks;
    }

    private void display(ArrayList<Track> trackList){
        ListAdapter listAdapter = new ListAdapter(this, trackList);
        listView.setAdapter(listAdapter);
    }



}
