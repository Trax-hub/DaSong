package com.example.pts3.model;

import java.io.Serializable;

public class User implements Serializable {

    private final String pseudo;
    private final String uid;


    public User(String pseudo, String uid) {
        this.pseudo = pseudo;
        this.uid = uid;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getUid() {
        return uid;
    }
}
