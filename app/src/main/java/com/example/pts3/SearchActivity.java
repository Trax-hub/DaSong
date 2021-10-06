package com.example.pts3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    private EditText editText;

    private static HttpURLConnection connection;
    private ListView listView;
    private Track selectedTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editText = findViewById(R.id.editText);

        listView = (ListView) findViewById(R.id.list_item);
        editText = findViewById(R.id.editText);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedTrack = (Track) listView.getItemAtPosition(position);
                ListAdapter listAdapter = (ListAdapter) listView.getAdapter();
                listAdapter.getMediaPlayer().stop();
                System.out.println(selectedTrack.toString());
                view.setSelected(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Sélection :");
                builder.setMessage("Êtes-vous sûrs de votre choix ?");
                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
            tracks.add( new Track(search.getTitle(), search.getPreview(), search.getArtist().getName(), search.getAlbum().getCover(), search.getAlbum().getCover_big()));
        }

        return tracks;
    }

    @Override
    public void onBackPressed() {
        ListAdapter listAdapter = (ListAdapter) listView.getAdapter();
        listAdapter.getMediaPlayer().stop();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        ListAdapter listAdapter = (ListAdapter) listView.getAdapter();
        listAdapter.getMediaPlayer().stop();
        super.onDestroy();
    }

    private void display(ArrayList<Track> trackList){
        ListAdapter listAdapter = new ListAdapter(this, trackList);
        listView.setAdapter(listAdapter);
    }

}
