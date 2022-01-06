package com.example.pts3;

public class Comment {

    private String uid;
    private String comment;
    private String date;

    public Comment(String uid, String comment, String date){
        this.uid = uid;
        this.comment = comment;
        this.date = date;
    }

    public String getUidComment() {
        return uid;
    }

    public String getComment() {
        return comment;
    }
}
