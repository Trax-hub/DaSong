package com.example.pts3.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Root {
    @SerializedName("data")
    private ArrayList<Search> searches;

    public Root(ArrayList<Search> searches) {
        this.searches = searches;
    }

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
