package com.example.pts3.model;

public class Comment {

    private final String uid;
    private final String comment;

    public Comment(String uid, String comment, String date){
        this.uid = uid;
        this.comment = comment;
    }

    public String getUidComment() {
        return uid;
    }

    public String getComment() {
        return comment;
    }
}
