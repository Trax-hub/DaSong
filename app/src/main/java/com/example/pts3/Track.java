package com.example.pts3;

import java.net.URL;

public class Track {

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


}
