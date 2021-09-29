package com.example.pts3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.text.Editable;
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

public class SearchActivity extends AppCompatActivity {
    private Button validateButton;

    private TextView mGreetingTextView;
    private EditText mNameEditText;

    private static HttpURLConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.Activity_search);

        mGreetingTextView = findViewById(R.id.main_textview_greeting);
        mNameEditText = findViewById(R.id.main_editText_name);


        mNameEditText.addTextChangedListener(new TextWatcher() {
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

                final Root[] root = new Root[1];

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        root[0] = getListTrack(s);
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                display(root[0]);
            }
        });

    }


    public Root getListTrack(String s){

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
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
            for (Search search : root.getSearches()) {
                System.out.println(search.getPreview());
            }

            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return root;
    }

    public void display(Root root){
        ListAdapter listAdapter = new ListAdapter(SearchActivity.this , root.getSearches());
        ListView listView = (ListView) findViewById(R.id.list_item);
        listView.setAdapter(listAdapter);
    }
}
