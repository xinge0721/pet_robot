package com.example.prt.module.WebSockets;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

import java.util.concurrent.TimeUnit;

/**
 * WebSocket客户端工具类
 *
 * 这个类封装了WebSocket的所有操作，让你可以很方便地：
 * 1. 连接到服务器
 * 2. 发送消息
 * 3. 接收消息
 * 4. 断开连接
 *
 * 使用方法很简单：
 * 1. 创建一个WebSocketClient对象
 * 2. 调用connect()方法连接服务器
 * 3. 调用sendMessage()发送消息
 * 4. 通过WebSocketListener接口接收消息
 * 5. 不用时调用disconnect()断开连接
 */
public class WebSocketClient {

    // 日志标签，用于在Android Studio的Logcat中查看日志
    // 你可以在Logcat中搜索"WebSocketClient"来查看所有相关日志
    private static final String TAG = "WebSocketClient";

    // OkHttpClient对象 - 这是OkHttp库提供的HTTP客户端
    // 我们用它来创建WebSocket连接
    private OkHttpClient okHttpClient;

    // WebSocket对象 - 代表一个WebSocket连接
    // 通过这个对象可以发送消息、关闭连接等
    private WebSocket webSocket;

    // 服务器地址，例如："ws://192.168.1.100:8080"
    // ws:// 表示WebSocket协议（类似http://）
    // wss:// 表示加密的WebSocket协议（类似https://）
    private String serverUrl;

    // 消息监听器 - 用于接收各种事件通知
    // 当连接成功、收到消息、连接断开等事件发生时，会通过这个监听器通知你
    private WebSocketListener listener;

    // Handler对象 - 用于在主线程中执行代码
    // Android规定：只能在主线程中更新UI（比如修改TextView的文字）
    // WebSocket的回调是在子线程中执行的，所以我们需要用Handler切换到主线程
    private Handler mainHandler;

    // 连接状态标志
    // true表示已连接，false表示未连接
    // 用这个变量可以判断当前是否可以发送消息
    private boolean isConnected = false;

    /**
     * 构造函数 - 创建WebSocketClient对象时会调用这个方法
     *
     * @param serverUrl 服务器地址，例如："ws://192.168.1.100:8080"
     * @param listener  消息监听器，用于接收各种事件通知
     *
     * 使用示例：
     * WebSocketClient client = new WebSocketClient("ws://192.168.1.100:8080", new WebSocketListener() {
     *     // 实现各种回调方法...
     * });
     */
    public WebSocketClient(String serverUrl, WebSocketListener listener) {
        // 保存服务器地址
        this.serverUrl = serverUrl;
        // 保存监听器
        this.listener = listener;

        // 创建Handler对象，绑定到主线程的Looper
        // Looper是Android中的消息循环机制，主线程有一个默认的Looper
        this.mainHandler = new Handler(Looper.getMainLooper());

        // 创建OkHttpClient对象，并配置一些参数
        this.okHttpClient = new OkHttpClient.Builder()
                // 设置连接超时时间为10秒
                // 如果10秒内无法连接到服务器，就会触发连接失败
                .connectTimeout(10, TimeUnit.SECONDS)
                // 设置读取超时时间为10秒
                // 如果10秒内没有收到服务器的数据，就会触发超时
                .readTimeout(10, TimeUnit.SECONDS)
                // 设置写入超时时间为10秒
                // 如果10秒内无法发送数据到服务器，就会触发超时
                .writeTimeout(10, TimeUnit.SECONDS)
                // 构建OkHttpClient对象
                .build();

        // 打印日志，表示WebSocketClient对象创建成功
        Log.d(TAG, "WebSocketClient创建成功，服务器地址：" + serverUrl);
    }

    /**
     * 连接到WebSocket服务器
     *
     * 调用这个方法后，会开始尝试连接到服务器
     * 连接结果会通过WebSocketListener的回调方法通知你：
     * - 连接成功：onConnected()
     * - 连接失败：onConnectFailed()
     *
     * 使用示例：
     * client.connect();
     */
    public void connect() {
        // 打印日志，表示开始连接
        Log.d(TAG, "开始连接到服务器：" + serverUrl);

        // 创建一个Request对象，指定要连接的服务器地址
        // Request就像一个"请求单"，告诉OkHttp要连接到哪里
        Request request = new Request.Builder()
                .url(serverUrl)  // 设置服务器地址
                .build();        // 构建Request对象

        // 使用OkHttpClient创建WebSocket连接
        // 第一个参数是Request对象（包含服务器地址）
        // 第二个参数是WebSocketListener对象（处理各种事件）
        webSocket = okHttpClient.newWebSocket(request, new okhttp3.WebSocketListener() {

            /**
             * 当WebSocket连接成功打开时，OkHttp会调用这个方法
             *
             * @param webSocket WebSocket对象
             * @param response  服务器的响应信息
             *
             * 注意：这个方法是在子线程中调用的，不能直接更新UI
             * 所以我们需要用Handler切换到主线程，然后再调用我们自己的监听器
             */
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // 打印日志
                Log.d(TAG, "WebSocket连接成功");

                // 设置连接状态为已连接
                isConnected = true;

                // 使用Handler在主线程中执行代码
                // mainHandler.post()会把这段代码放到主线程的消息队列中
                // 主线程会在合适的时候执行这段代码
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 检查监听器是否存在（防止空指针异常）
                        if (listener != null) {
                            // 调用监听器的onConnected方法，通知外部连接成功
                            listener.onConnected();
                        }
                    }
                });
            }

            /**
             * 当收到服务器发来的文本消息时，OkHttp会调用这个方法
             *
             * @param webSocket WebSocket对象
             * @param text      服务器发来的文本消息
             *
             * 注意：这个方法也是在子线程中调用的
             */
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 打印日志，显示收到的消息内容
                Log.d(TAG, "收到消息：" + text);

                // 切换到主线程，然后调用监听器
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            // 把收到的消息传递给监听器
                            listener.onMessageReceived(text);
                        }
                    }
                });
            }

            /**
             * 当WebSocket连接关闭时，OkHttp会调用这个方法
             *
             * @param webSocket WebSocket对象
             * @param code      关闭代码（1000表示正常关闭）
             * @param reason    关闭原因描述
             */
            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                // 打印日志
                Log.d(TAG, "WebSocket连接正在关闭，原因：" + reason);

                // 设置连接状态为未连接
                isConnected = false;

                // 切换到主线程，通知监听器
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onDisconnected(reason);
                        }
                    }
                });
            }

            /**
             * 当WebSocket连接失败时，OkHttp会调用这个方法
             *
             * @param webSocket WebSocket对象
             * @param t         异常对象，包含失败的详细信息
             * @param response  服务器的响应（可能为null）
             */
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // 打印错误日志
                Log.e(TAG, "WebSocket连接失败：" + t.getMessage());

                // 设置连接状态为未连接
                isConnected = false;

                // 获取错误信息
                // 如果异常对象的getMessage()返回null，就使用"未知错误"
                final String errorMsg = t.getMessage() != null ? t.getMessage() : "未知错误";

                // 切换到主线程，通知监听器
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onConnectFailed(errorMsg);
                        }
                    }
                });
            }
        });
    }

    /**
     * 发送文本消息到服务器
     *
     * @param message 要发送的文本消息
     * @return true表示发送成功，false表示发送失败
     *
     * 使用示例：
     * boolean success = client.sendMessage("Hello Server!");
     * if (success) {
     *     Log.d(TAG, "消息发送成功");
     * } else {
     *     Log.e(TAG, "消息发送失败");
     * }
     */
    public boolean sendMessage(String message) {
        // 检查是否已连接
        if (!isConnected || webSocket == null) {
            // 如果未连接，打印错误日志并返回false
            Log.e(TAG, "发送消息失败：WebSocket未连接");
            return false;
        }

        // 使用WebSocket对象发送消息
        // send()方法返回true表示消息已加入发送队列，false表示发送失败
        boolean result = webSocket.send(message);

        if (result) {
            // 发送成功，打印日志
            Log.d(TAG, "消息已发送：" + message);
        } else {
            // 发送失败，打印错误日志
            Log.e(TAG, "消息发送失败：" + message);
        }

        return result;
    }

    /**
     * 断开WebSocket连接
     *
     * 调用这个方法会主动关闭与服务器的连接
     * 关闭后，会触发onDisconnected回调
     *
     * 使用示例：
     * client.disconnect();
     */
    public void disconnect() {
        // 检查WebSocket对象是否存在
        if (webSocket != null) {
            // 打印日志
            Log.d(TAG, "正在断开WebSocket连接");

            // 关闭WebSocket连接
            // 第一个参数是关闭代码：1000表示正常关闭
            // 第二个参数是关闭原因：可以是任意字符串
            webSocket.close(1000, "客户端主动断开连接");

            // 设置连接状态为未连接
            isConnected = false;
        }
    }

    /**
     * 检查是否已连接到服务器
     *
     * @return true表示已连接，false表示未连接
     *
     * 使用示例：
     * if (client.isConnected()) {
     *     // 已连接，可以发送消息
     *     client.sendMessage("Hello");
     * } else {
     *     // 未连接，需要先连接
     *     client.connect();
     * }
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 获取服务器地址
     *
     * @return 服务器地址字符串
     */
    public String getServerUrl() {
        return serverUrl;
    }
}
