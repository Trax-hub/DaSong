package com.example.pts3;

import java.io.Serializable;
import java.net.URL;

public class Track implements Serializable {

    private String title;
    private String preview;
    private String artistName;
    private String cover;

    public Track(String title, String preview, String artistName, String cover) {
        this.title = title;
        this.preview = preview;
        this.artistName = artistName;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public String getPreview() {
        return preview;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getCover() {
        return cover;
    }

    @Override
    public String toString() {
        return "Track{" +
                "title='" + title + '\'' +
                ", preview='" + preview + '\'' +
                ", artistName='" + artistName + '\'' +
                ", cover='" + cover + '\'' +
                '}';
    }
}
