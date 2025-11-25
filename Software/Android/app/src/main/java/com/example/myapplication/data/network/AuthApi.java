package com.example.myapplication.data.network;

import android.os.Handler;
import android.os.Looper;
import com.example.myapplication.data.model.LoginRequest;
import com.example.myapplication.data.model.LoginResponse;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthApi {
    private String serverUrl;
    private Gson gson;

    public interface AuthCallback {
        void onSuccess(LoginResponse response);
        void onError(String error);
    }

    public AuthApi(String serverUrl) {
        this.serverUrl = serverUrl;
        this.gson = new Gson();
    }

    public void login(String username, String password, AuthCallback callback) {
        LoginRequest request = new LoginRequest(username, password);
        String json = gson.toJson(request);

        new Thread(() -> {
            try {
                URL url = new URL(serverUrl + "/api/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    LoginResponse loginResponse = gson.fromJson(
                        response.toString(), LoginResponse.class);

                    new Handler(Looper.getMainLooper()).post(() ->
                        callback.onSuccess(loginResponse));
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("登录失败：" + responseCode));
                }
                conn.disconnect();

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                    callback.onError("网络错误：" + e.getMessage()));
            }
        }).start();
    }

    public void register(String username, String password, AuthCallback callback) {
        // 类似login实现
    }
}
