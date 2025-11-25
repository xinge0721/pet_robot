package com.example.myapplication.data.model;

public class User {
    private String userId;
    private String username;
    private String token;

    public User(String userId, String username, String token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getToken() { return token; }
}
