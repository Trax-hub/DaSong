package com.example.pts3.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pts3.R;
import com.example.pts3.model.Root;
import com.example.pts3.model.Search;
import com.example.pts3.model.Track;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText editText;
    private static HttpURLConnection connection;
    private ListView listView;
    private Track selectedTrack;
    private MediaPlayer mediaPlayer;
    private InternetCheckService internetCheckService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        internetCheckService = new InternetCheckService();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
        editText = findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.list_item);

        mediaPlayer = new MediaPlayer();
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

                if(s.isEmpty()){
                    display(new ArrayList<Track>());
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedTrack = (Track) listView.getItemAtPosition(position);
                view.setSelected(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Sélection :");
                builder.setMessage("Êtes-vous sûr de votre choix ?");
                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
                        intent.putExtra("Track", selectedTrack);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    private ArrayList<Track> getListTrack(String s){

        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();
        Root root = new Root(new ArrayList<Search>());

        try {
            s = StringUtils.stripAccents(s);
            s = s.replace(" ", "_");
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


            Gson gson = new Gson();
            root = gson.fromJson(responseContent.toString(), Root.class);

            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Track> tracks = new ArrayList<>();

        for(Search search : root.getSearches()){
            tracks.add( new Track(search.getTitle(), search.getPreview(), search.getArtist().getName(), search.getAlbum().getCover(), search.getAlbum().getCover_big()));
        }
            return tracks;
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetCheckService,intentFilter);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.reset();
        finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(internetCheckService);
        super.onDestroy();
        mediaPlayer.reset();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.reset();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(internetCheckService);
        super.onStop();
        mediaPlayer.reset();
        finish();
    }

    private void display(ArrayList<Track> trackList){
        SearchAdapter searchAdapter = new SearchAdapter(this, trackList, mediaPlayer);
        listView.setAdapter(searchAdapter);
    }

}
