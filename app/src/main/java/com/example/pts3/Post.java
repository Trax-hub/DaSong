package com.example.pts3;

public class Post {

    private Track track;
    private String description;

    public Post(Track track, String description) {
        this.track = track;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Track getTrack() {
        return track;
    }
}
