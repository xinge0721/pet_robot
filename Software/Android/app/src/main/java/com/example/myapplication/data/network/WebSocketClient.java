//package com.example.myapplication.data.network;
//
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.WebSocket;
//import okhttp3.WebSocketListener;
//import org.json.JSONObject;
//import java.util.concurrent.TimeUnit;
//
//public class WebSocketClient {
//    private static final String TAG = "WebSocketClient";
//    private OkHttpClient okHttpClient;
//    private WebSocket webSocket;
//    private String token;
//    private Handler mainHandler;
//
//    public interface WebSocketCallback {
//        void onGpsDataReceived(double lat, double lng);
//        void onVideoFrameReceived(String frameBase64);
//        void onConnected();
//        void onDisconnected();
//        void onError(Exception e);
//    }
//
//    private WebSocketCallback callback;
//
//    public WebSocketClient(String token, WebSocketCallback callback) {
//        this.token = token;
//        this.callback = callback;
//        this.mainHandler = new Handler(Looper.getMainLooper());
//        this.okHttpClient = new OkHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .build();
//    }
//
//    public void connect(String serverUrl) {
//        String wsUrl = serverUrl + "?token=" + token;
//
//        Request request = new Request.Builder()
//            .url(wsUrl)
//            .build();
//
//        webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
//            @Override
//            public void onOpen(WebSocket webSocket, Response response) {
//                mainHandler.post(() -> callback.onConnected());
//            }
//
//            @Override
//            public void onMessage(WebSocket webSocket, String text) {
//                handleMessage(text);
//            }
//
//            @Override
//            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
//                mainHandler.post(() -> callback.onError(new Exception(t)));
//            }
//
//            @Override
//            public void onClosed(WebSocket webSocket, int code, String reason) {
//                mainHandler.post(() -> callback.onDisconnected());
//            }
//        });
//    }
//
//    private void handleMessage(String json) {
//        try {
//            JSONObject obj = new JSONObject(json);
//            String type = obj.getString("type");
//
//            if ("gps".equals(type)) {
//                double lat = obj.getDouble("lat");
//                double lng = obj.getDouble("lng");
//                mainHandler.post(() -> callback.onGpsDataReceived(lat, lng));
//            } else if ("video".equals(type)) {
//                String frame = obj.getString("frame");
//                mainHandler.post(() -> callback.onVideoFrameReceived(frame));
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "解析消息失败", e);
//        }
//    }
//
//    public void sendCommand(String command) {
//        if (webSocket != null) {
//            try {
//                JSONObject json = new JSONObject();
//                json.put("type", "control");
//                json.put("command", command);
//                webSocket.send(json.toString());
//            } catch (Exception e) {
//                Log.e(TAG, "发送指令失败", e);
//            }
//        }
//    }
//
//    public void disconnect() {
//        if (webSocket != null) {
//            webSocket.close(1000, "Client disconnect");
//        }
//    }
//}
