package com.example.pts3.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Root {
    @SerializedName("data")
    private final ArrayList<Search> searches;

    public Root(ArrayList<Search> searches) {
        this.searches = searches;
    }

    @NonNull
    @Override
    public String toString() {
        return "Root{" +
                "searches=" + searches +
                '}';
    }


    public ArrayList<Search> getSearches() {
        return searches;
    }
}
