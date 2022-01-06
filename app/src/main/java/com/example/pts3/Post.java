package com.example.pts3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {

    private Track track;
    private String description;
    private String creatorUid;
    private String date;
    private int nbLikes = 0;
    private ArrayList<String> uidLiked;

    public Post(Track track, String description, String creatorUid, String date) {
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

    public String getDate() {
        return date;
    }

    public int getNbLikes() {
        return nbLikes;
    }

    public void setNbLikes(int nbLikes) {
        this.nbLikes = nbLikes;
    }

    public ArrayList<String> getUidLiked() {
        return uidLiked;
    }

    public void setUidLiked(ArrayList<String> uidLiked) {
        this.uidLiked = uidLiked;
    }
}
