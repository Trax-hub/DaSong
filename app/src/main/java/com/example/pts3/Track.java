package com.example.pts3;

import java.io.Serializable;
import java.net.URL;

public class Track implements Serializable {

    private String title;
    private String preview;
    private String artistName;
    private String cover;
    private String coverMax;

    public Track(String title, String preview, String artistName, String cover, String coverMax) {
        this.title = title;
        this.preview = preview;
        this.artistName = artistName;
        this.cover = cover;
        this.coverMax = coverMax;
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

    public String getCoverMax() {
        return coverMax;
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
