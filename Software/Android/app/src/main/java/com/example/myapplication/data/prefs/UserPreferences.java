package com.example.myapplication.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.myapplication.data.model.User;

public class UserPreferences {
    private static final String PREFS_NAME = "PetCollarPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";

    private SharedPreferences prefs;

    public UserPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        prefs.edit()
            .putString(KEY_TOKEN, user.getToken())
            .putString(KEY_USER_ID, user.getUserId())
            .putString(KEY_USERNAME, user.getUsername())
            .apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }
}
