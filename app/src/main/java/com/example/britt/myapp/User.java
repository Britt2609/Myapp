package com.example.britt.myapp;


import java.lang.reflect.Array;
import java.util.List;


public class User {
    public String name;
    public Integer score;
    public String password;
    public String email;

    public User() {}

    public User(Integer score, String name, String email, String password) {
        this.name = name;
        this.password = password;
        this.score = score;
        this.email = email;
    }
}
