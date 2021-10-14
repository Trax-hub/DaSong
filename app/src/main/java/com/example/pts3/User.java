package com.example.pts3;

import java.io.Serializable;

public class User implements Serializable {

    public String name, surname,  age, email, pseudo;

    public User(String name, String surname, String age, String email, String pseudo) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.email = email;
        this.pseudo = pseudo;
    }
}
