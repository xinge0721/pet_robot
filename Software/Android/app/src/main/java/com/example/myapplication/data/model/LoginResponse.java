package com.example.myapplication.data.model;

public class LoginResponse {
    private int code;
    private String message;
    private String token;
    private String userId;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getUserId() { return userId; }
}
