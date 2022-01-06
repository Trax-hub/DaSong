package com.example.pts3;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String pseudo;
    private String uid;


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
