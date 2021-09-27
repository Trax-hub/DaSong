package com.example.pts3;

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

    public void display(){
        for(Search search : searches){
            System.out.println(search.toString());
        }
    }

    public ArrayList<Search> getSearches() {
        return searches;
    }
}
