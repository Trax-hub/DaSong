package com.example.pts3;

public class Album {
    public int id;
    public String title;
    public String cover;
    public String cover_small;
    public String cover_medium;
    public String cover_big;
    public String cover_xl;

    public Album(int id, String title, String cover, String cover_small, String cover_medium, String cover_big, String cover_xl) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.cover_small = cover_small;
        this.cover_medium = cover_medium;
        this.cover_big = cover_big;
        this.cover_xl = cover_xl;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public String getCoverSmall() {
        return cover_small;
    }

    public String getCover_medium() {
        return cover_medium;
    }

    public String getCover_big() {
        return cover_big;
    }

    public String getCover_xl() {
        return cover_xl;
    }
}
