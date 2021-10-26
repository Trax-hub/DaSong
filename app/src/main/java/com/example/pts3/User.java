package com.example.pts3;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String pseudo, mail;
    private String profilePicture;
    private ArrayList<User> friendList;


    public User(String pseudo, String mail) {
        this.mail = mail;
        this.pseudo = pseudo;
        this.profilePicture = "";
        this.friendList = new ArrayList<User>();
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getMail(){
        return mail;
    }

    public String getProfilePicture() { return profilePicture; }

    public ArrayList<User> getFriendList() { return friendList; }
}
