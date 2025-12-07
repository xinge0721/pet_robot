package com.example.myapplication.utils;

/**
 * 常量定义类
 */
public class Constants {

    // 服务器配置（预留，后续接入真实服务器时使用）
    public static final String SERVER_URL = "http://192.168.1.100:8080";
    public static final String WS_URL = "ws://192.168.1.100:8080/ws";

    // API端点
    public static final String API_LOGIN = "/api/login";
    public static final String API_REGISTER = "/api/register";

    // 本地存储文件名
    public static final String USERS_FILE = "users.json";

    // SharedPreferences键名
    public static final String PREF_NAME = "PetCollarPrefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USERNAME = "username";

    // 请求超时时间（毫秒）
    public static final int CONNECT_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 10000;

    // WebSocket相关
    public static final int WS_RECONNECT_DELAY = 3000;
    public static final int WS_MAX_RECONNECT_TIMES = 5;
}
