package com.example.pts3;

import java.util.Date;

public class Post {

    private Track track;
    private String description;
    private String creatorUid;

    public Post(Track track, String description, String creatorUid) {
        this.track = track;
        this.description = description;
        this.creatorUid = creatorUid;
    }

    public String getDescription() {
        return description;
    }

    public Track getTrack() {
        return track;
    }

    public String getCreatorUid() {
        return creatorUid;
    }
}
