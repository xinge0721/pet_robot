package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.prefs.UserPreferences;
import com.example.myapplication.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        userPrefs = new UserPreferences(this);

        new Handler().postDelayed(() -> {
            if (userPrefs.isLoggedIn()) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1000);
    }
}
