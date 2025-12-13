package com.example.prt.module.WebSockets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

import java.util.concurrent.TimeUnit;

/**
 * WebSocket管理器（单例模式）
 *
 * 这是整个WebSocket通信的核心管理类
 * 采用单例模式，保证全局只有一个实例
 *
 * 主要职责：
 * 1. 管理WebSocket连接的生命周期（连接、断开、重连）
 * 2. 消息的发送和接收
 * 3. 消息路由（把不同类型的消息分发给不同模块）
 * 4. 心跳保活
 * 5. 断线重连
 *
 * 什么是单例模式？
 * 单例模式确保一个类只有一个实例，并提供全局访问点
 * 就像一个公司只有一个CEO，所有人都通过这个CEO来管理公司
 *
 * 为什么要用单例？
 * - WebSocket连接是全局唯一的，不应该有多个连接
 * - 方便在任何地方访问（不需要传递对象）
 * - 节省资源（只创建一次）
 */
public class WebSocketManager {

    // 日志标签
    private static final String TAG = "WebSocketManager";

    // ========== 单例相关 ==========

    /**
     * 单例实例
     *
     * volatile关键字的作用：
     * - 保证多线程环境下的可见性
     * - 防止指令重排序
     * 简单理解：确保在多线程环境下，这个变量的值是最新的
     */
    private static volatile WebSocketManager instance;

    /**
     * 获取单例实例（双重检查锁定）
     *
     * 这是一个经典的单例模式实现方式
     *
     * @return WebSocketManager的唯一实例
     *
     * 使用示例：
     * WebSocketManager manager = WebSocketManager.getInstance();
     * manager.connect("ws://192.168.1.100:8080");
     */
    public static WebSocketManager getInstance() {
        // 第一次检查：如果实例已存在，直接返回（提高性能）
        if (instance == null) {
            // 加锁：防止多个线程同时创建实例
            synchronized (WebSocketManager.class) {
                // 第二次检查：防止多个线程都通过了第一次检查
                if (instance == null) {
                    instance = new WebSocketManager();
                }
            }
        }
        return instance;
    }

    // ========== 成员变量 ==========

    // OkHttpClient对象 - 用于创建WebSocket连接
    private OkHttpClient okHttpClient;

    // WebSocket对象 - 代表当前的WebSocket连接
    private WebSocket webSocket;

    // 服务器地址
    private String serverUrl;

    // 连接状态
    private boolean isConnected = false;

    // 主线程Handler - 用于在主线程执行代码
    private Handler mainHandler;

    // Gson对象 - 用于JSON的序列化和反序列化
    private Gson gson;

    // 用户token - 登录后保存，后续请求都需要带上
    private String userToken;

    // 心跳相关
    private Handler heartbeatHandler;           // 心跳Handler
    private long heartbeatInterval = 30000;     // 心跳间隔：默认30秒
    private Runnable heartbeatRunnable;         // 心跳任务

    // 重连相关
    private int reconnectCount = 0;             // 当前重连次数
    private int maxReconnectCount = 999;        // 最大重连次数：默认999
    private long reconnectDelay = 3000;         // 重连延迟：默认3秒
    private Handler reconnectHandler;           // 重连Handler
    private Runnable reconnectRunnable;         // 重连任务

    // 配置相关
    private static class Config {
        String serverUrl;
        int connectTimeout;
        int readTimeout;
        int writeTimeout;
        int pingInterval;
        long heartbeatInterval;
        int maxReconnectCount;
        long reconnectDelay;
    }

    // ========== 构造函数 ==========

    /**
     * 私有构造函数
     *
     * 设置为private，防止外部直接new对象
     * 只能通过getInstance()方法获取实例
     */
    private WebSocketManager() {
        // 初始化主线程Handler
        mainHandler = new Handler(Looper.getMainLooper());

        // 初始化Gson对象
        gson = new Gson();

        // 加载配置文件
        loadConfig();

        // 初始化OkHttpClient（使用配置文件中的参数）
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                // 设置ping间隔，OkHttp会自动发送ping帧保持连接
                .pingInterval(20, TimeUnit.SECONDS)
                .build();

        // 初始化心跳Handler
        heartbeatHandler = new Handler(Looper.getMainLooper());

        // 初始化重连Handler
        reconnectHandler = new Handler(Looper.getMainLooper());

        // 初始化心跳任务
        heartbeatRunnable = new Runnable() {
            @Override
            public void run() {
                // 发送心跳消息
                sendHeartbeat();
                // 根据配置的间隔时间再次执行
                heartbeatHandler.postDelayed(this, heartbeatInterval);
            }
        };

        // 初始化重连任务
        reconnectRunnable = new Runnable() {
            @Override
            public void run() {
                // 尝试重新连接
                reconnect();
            }
        };

        Log.d(TAG, "WebSocketManager初始化完成");
    }

    // ========== 连接管理 ==========

    /**
     * 连接到WebSocket服务器
     *
     * @param url 服务器地址，例如："ws://192.168.1.100:8080"
     *
     * 使用示例：
     * WebSocketManager.getInstance().connect("ws://192.168.1.100:8080");
     */
    public void connect(String url) {
        // 保存服务器地址（用于重连）
        this.serverUrl = url;

        // 如果已经连接，先断开
        if (isConnected && webSocket != null) {
            Log.w(TAG, "已存在连接，先断开旧连接");
            disconnect();
        }

        Log.d(TAG, "开始连接到服务器：" + url);

        // 创建Request对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 创建WebSocket连接
        webSocket = okHttpClient.newWebSocket(request, new okhttp3.WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket连接成功");
                isConnected = true;
                reconnectCount = 0; // 重置重连次数

                // 启动心跳
                startHeartbeat();

                // 通过EventBus发送连接成功事件
                // EventBus会把这个事件发送给所有订阅了这个事件的地方
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WebSocketMessage message = new WebSocketMessage(
                                WebSocketMessage.TYPE_CONNECT,
                                "连接成功"
                        );
                        EventBus.getDefault().post(message);
                    }
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "收到消息：" + text);

                // 解析消息
                try {
                    // 使用Gson把JSON字符串转换成WebSocketMessage对象
                    WebSocketMessage message = gson.fromJson(text, WebSocketMessage.class);

                    // 如果是心跳消息，不需要分发
                    if (message.isHeartbeat()) {
                        Log.d(TAG, "收到心跳响应");
                        return;
                    }

                    // 通过EventBus分发消息
                    // 各个模块可以订阅EventBus来接收消息
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(message);
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "解析消息失败：" + e.getMessage());
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket连接正在关闭：" + reason);
                isConnected = false;

                // 停止心跳
                stopHeartbeat();

                // 发送断开连接事件
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WebSocketMessage message = new WebSocketMessage(
                                WebSocketMessage.TYPE_DISCONNECT,
                                reason
                        );
                        EventBus.getDefault().post(message);
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket连接失败：" + t.getMessage());
                isConnected = false;

                // 停止心跳
                stopHeartbeat();

                // 尝试重连
                scheduleReconnect();

                // 发送连接失败事件
                final String errorMsg = t.getMessage() != null ? t.getMessage() : "未知错误";
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WebSocketMessage message = new WebSocketMessage(
                                WebSocketMessage.TYPE_DISCONNECT,
                                "连接失败：" + errorMsg
                        );
                        EventBus.getDefault().post(message);
                    }
                });
            }
        });
    }

    /**
     * 断开WebSocket连接
     */
    public void disconnect() {
        Log.d(TAG, "断开WebSocket连接");

        // 停止心跳
        stopHeartbeat();

        // 取消重连任务
        cancelReconnect();

        // 关闭WebSocket连接
        if (webSocket != null) {
            webSocket.close(1000, "客户端主动断开");
            webSocket = null;
        }

        isConnected = false;
    }

    // ========== 消息发送 ==========

    /**
     * 发送消息
     *
     * @param message WebSocketMessage对象
     * @return true表示发送成功，false表示发送失败
     *
     * 使用示例：
     * WebSocketMessage msg = new WebSocketMessage(
     *     WebSocketMessage.TYPE_CHAT,
     *     "你好"
     * );
     * WebSocketManager.getInstance().sendMessage(msg);
     */
    public boolean sendMessage(WebSocketMessage message) {
        if (!isConnected || webSocket == null) {
            Log.e(TAG, "发送消息失败：WebSocket未连接");
            return false;
        }

        // 如果有token，自动添加到消息中
        if (userToken != null && message.getToken() == null) {
            message.setToken(userToken);
        }

        // 使用Gson把WebSocketMessage对象转换成JSON字符串
        String jsonString = gson.toJson(message);

        // 发送消息
        boolean result = webSocket.send(jsonString);

        if (result) {
            Log.d(TAG, "消息已发送：" + jsonString);
        } else {
            Log.e(TAG, "消息发送失败：" + jsonString);
        }

        return result;
    }

    /**
     * 发送文本消息（简化版）
     *
     * @param type 消息类型
     * @param data 消息数据
     * @return true表示发送成功，false表示发送失败
     *
     * 使用示例：
     * WebSocketManager.getInstance().sendMessage(
     *     WebSocketMessage.TYPE_CHAT,
     *     "你好"
     * );
     */
    public boolean sendMessage(String type, String data) {
        WebSocketMessage message = new WebSocketMessage(type, data);
        return sendMessage(message);
    }

    // ========== 心跳保活 ==========

    /**
     * 启动心跳
     *
     * 心跳的作用：
     * - 保持连接活跃，防止被服务器断开
     * - 及时发现连接断开
     */
    private void startHeartbeat() {
        Log.d(TAG, "启动心跳");
        // 先停止之前的心跳（如果有）
        stopHeartbeat();
        // 根据配置的间隔时间开始第一次心跳
        heartbeatHandler.postDelayed(heartbeatRunnable, heartbeatInterval);
    }

    /**
     * 停止心跳
     */
    private void stopHeartbeat() {
        Log.d(TAG, "停止心跳");
        heartbeatHandler.removeCallbacks(heartbeatRunnable);
    }

    /**
     * 发送心跳消息
     */
    private void sendHeartbeat() {
        if (isConnected) {
            Log.d(TAG, "发送心跳");
            WebSocketMessage heartbeat = new WebSocketMessage(
                    WebSocketMessage.TYPE_HEARTBEAT,
                    "ping"
            );
            sendMessage(heartbeat);
        }
    }

    // ========== 断线重连 ==========

    /**
     * 安排重连任务
     */
    private void scheduleReconnect() {
        // 如果已经达到最大重连次数，不再重连
        if (reconnectCount >= maxReconnectCount) {
            Log.e(TAG, "已达到最大重连次数，停止重连");
            return;
        }

        Log.d(TAG, "安排重连任务，" + reconnectDelay + "毫秒后执行");
        reconnectHandler.postDelayed(reconnectRunnable, reconnectDelay);
    }

    /**
     * 取消重连任务
     */
    private void cancelReconnect() {
        Log.d(TAG, "取消重连任务");
        reconnectHandler.removeCallbacks(reconnectRunnable);
        reconnectCount = 0;
    }

    /**
     * 执行重连
     */
    private void reconnect() {
        reconnectCount++;
        Log.d(TAG, "尝试重连，第" + reconnectCount + "次");

        if (serverUrl != null) {
            connect(serverUrl);
        }
    }

    // ========== Token管理 ==========

    /**
     * 设置用户token
     *
     * 登录成功后调用这个方法保存token
     * 之后发送的所有消息都会自动带上这个token
     *
     * @param token 用户token
     */
    public void setUserToken(String token) {
        this.userToken = token;
        Log.d(TAG, "设置用户token：" + token);
    }

    /**
     * 获取用户token
     */
    public String getUserToken() {
        return userToken;
    }

    /**
     * 清除用户token
     *
     * 登出时调用
     */
    public void clearUserToken() {
        this.userToken = null;
        Log.d(TAG, "清除用户token");
    }

    // ========== 状态查询 ==========

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 获取服务器地址
     */
    public String getServerUrl() {
        return serverUrl;
    }

    // ========== 配置加载 ==========

    /**
     * 加载配置文件
     *
     * 从JSON文件中读取WebSocket配置参数
     * 如果读取失败，使用默认值
     */
    private void loadConfig() {
        try {
            // 读取JSON配置文件（使用相对路径）
            java.io.File configFile = new java.io.File(
                "app/src/main/java/com/example/prt/data/config/websocket_config.json"
            );

            // 如果相对路径找不到，尝试从类路径读取
            if (!configFile.exists()) {
                configFile = new java.io.File(
                    "Software/Android/app/src/main/java/com/example/prt/data/config/websocket_config.json"
                );
            }

            java.io.InputStream is = new java.io.FileInputStream(configFile);
            java.io.InputStreamReader reader = new java.io.InputStreamReader(is, "UTF-8");

            // 使用Gson解析JSON
            Config config = gson.fromJson(reader, Config.class);

            // 应用配置
            if (config != null) {
                if (config.heartbeatInterval > 0) {
                    this.heartbeatInterval = config.heartbeatInterval;
                }
                if (config.maxReconnectCount > 0) {
                    this.maxReconnectCount = config.maxReconnectCount;
                }
                if (config.reconnectDelay > 0) {
                    this.reconnectDelay = config.reconnectDelay;
                }
                Log.d(TAG, "配置加载成功：心跳间隔=" + heartbeatInterval +
                          "ms, 最大重连次数=" + maxReconnectCount +
                          ", 重连延迟=" + reconnectDelay + "ms");
            }

            reader.close();
            is.close();

        } catch (Exception e) {
            Log.w(TAG, "配置文件加载失败，使用默认配置：" + e.getMessage());
            // 使用默认值（已在变量声明时设置）
        }
    }
}
