package com.example.pts3;

import java.io.Serializable;

public class User implements Serializable {

    public String pseudo, mail;

    public User(String pseudo, String mail) {
        this.mail = mail;
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getMail(){
        return mail;
    }
}
