package com.example.myapplication.utils;

/**
 * 8ÏšI{
 */
public class Constants {

    // ¡hMn„Yí¥ež¡hö(	
    public static final String SERVER_URL = "http://192.168.1.100:8080";
    public static final String WS_URL = "ws://192.168.1.100:8080/ws";

    // APIï¹
    public static final String API_LOGIN = "/api/login";
    public static final String API_REGISTER = "/api/register";

    // ,0X¨‡ö
    public static final String USERS_FILE = "users.json";

    // SharedPreferences.
    public static final String PREF_NAME = "PetCollarPrefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USERNAME = "username";

    // ÷B…ööôëÒ	
    public static final int CONNECT_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;

    // WebSocketøs
    public static final int WS_RECONNECT_DELAY = 3000;
    public static final int WS_MAX_RECONNECT_TIMES = 5;
}
