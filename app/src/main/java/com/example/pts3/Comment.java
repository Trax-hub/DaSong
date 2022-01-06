package com.example.pts3;

public class Comment {

    private String uid;
    private String comment;

    public Comment(String uid, String comment){
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
