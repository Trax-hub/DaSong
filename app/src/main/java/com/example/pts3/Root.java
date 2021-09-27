package com.example.pts3;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Root {
    @SerializedName("data")
    private ArrayList<com.example.bereal.Search> searches;

    public Root(ArrayList<com.example.bereal.Search> searches) {
        this.searches = searches;
    }

    @Override
    public String toString() {
        return "Root{" +
                "searches=" + searches +
                '}';
    }

    public void display(){
        for(com.example.bereal.Search search : searches){
            System.out.println(search.toString());
        }
    }

    public ArrayList<com.example.bereal.Search> getSearches() {
        return searches;
    }
}
