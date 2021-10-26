package com.example.pts3;

import java.util.Date;

public class Post {

    private Track track;
    private String description;
    private String creatorUid;
    private Date date;

    public Post(Track track, String description, String creatorUid) {
        this.track = track;
        this.description = description;
        this.creatorUid = creatorUid;
    }

    public Post(Track track, String description, String creatorUid, Date date) {
        this.track = track;
        this.description = description;
        this.creatorUid = creatorUid;
        this.date = date;
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

    public Date getDate() {
        return date;
    }
}
