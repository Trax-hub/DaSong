package com.example.pts3;

import java.util.Date;
import java.util.List;

public class Post {

    private Track track;
    private String description;
    private String creatorUid;
    private List<String> uidWhoLiked;
    private String date;

    public Post(Track track, String description, String creatorUid, String date, List<String> uidWhoLiked) {
        this.track = track;
        this.description = description;
        this.creatorUid = creatorUid;
        this.uidWhoLiked = uidWhoLiked;
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

    public String getDate() {
        return date;
    }

    public List<String> getUidWhoLiked() {
        return uidWhoLiked;
    }
}
