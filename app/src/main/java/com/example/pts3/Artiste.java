package com.example.pts3;

import java.net.URL;

public class Artiste {
    public int id;
    public String name;
    public URL link;
    public URL picture;
    public URL picture_small;
    public URL picture_medium;
    public URL picture_big;
    public URL picture_xl;

    public Artiste(int id, String name, URL link, URL picture, URL picture_small, URL picture_medium, URL picture_big, URL picture_xl) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.picture = picture;
        this.picture_small = picture_small;
        this.picture_medium = picture_medium;
        this.picture_big = picture_big;
        this.picture_xl = picture_xl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public URL getLink() {
        return link;
    }

    public URL getPicture() {
        return picture;
    }

    public URL getPicture_small() {
        return picture_small;
    }

    public URL getPicture_medium() {
        return picture_medium;
    }

    public URL getPicture_big() {
        return picture_big;
    }

    public URL getPicture_xl() {
        return picture_xl;
    }
}
