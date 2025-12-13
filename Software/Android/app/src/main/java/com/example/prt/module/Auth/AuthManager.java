package com.example.prt.module.Auth;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.example.prt.module.WebSockets.WebSocketManager;
import com.example.prt.module.WebSockets.WebSocketMessage;

/**
 * 认证管理器（单例模式）
 *
 * 这个类负责处理用户的登录和注册功能
 * 采用单例模式，保证全局只有一个实例
 *
 * 主要职责：
 * 1. 处理用户登录请求
 * 2. 处理用户注册请求
 * 3. 接收服务器的认证响应
 * 4. 通知界面认证结果
 *
 * 工作流程：
 * 用户输入 → AuthManager.login() → WebSocketManager发送消息
 * → 服务器处理 → WebSocketManager接收响应 → EventBus分发
 * → AuthManager接收 → 回调通知界面
 */
public class AuthManager {

    // 日志标签
    private static final String TAG = "AuthManager";

    // ========== 单例相关 ==========

    /**
     * 单例实例
     * volatile关键字保证多线程环境下的可见性
     */
    private static volatile AuthManager instance;

    /**
     * 获取单例实例（双重检查锁定）
     *
     * @return AuthManager的唯一实例
     *
     * 使用示例：
     * AuthManager.getInstance().login("user123", "pass123", callback);
     */
    public static AuthManager getInstance() {
        if (instance == null) {
            synchronized (AuthManager.class) {
                if (instance == null) {
                    instance = new AuthManager();
                }
            }
        }
        return instance;
    }

    // ========== 成员变量 ==========

    // WebSocket管理器 - 用于发送消息
    private WebSocketManager webSocketManager;

    // Gson对象 - 用于JSON的序列化和反序列化
    private Gson gson;

    // 主线程Handler - 用于在主线程执行回调
    private Handler mainHandler;

    // 当前的认证回调 - 保存当前正在进行的认证操作的回调
    private AuthCallback currentCallback;

    // 当前用户名 - 登录成功后保存
    private String currentUsername;

    // ========== 构造函数 ==========

    /**
     * 私有构造函数
     *
     * 设置为private，防止外部直接new对象
     * 只能通过getInstance()方法获取实例
     */
    private AuthManager() {
        // 获取WebSocketManager实例
        webSocketManager = WebSocketManager.getInstance();

        // 初始化Gson对象
        gson = new Gson();

        // 初始化主线程Handler
        mainHandler = new Handler(Looper.getMainLooper());

        // 注册EventBus，用于接收WebSocket消息
        // EventBus是一个消息总线，可以在不同组件之间传递消息
        EventBus.getDefault().register(this);

        Log.d(TAG, "AuthManager初始化完成");
    }

    // ========== 认证回调接口 ==========

    /**
     * 认证回调接口
     *
     * 用于通知界面认证结果
     * 界面需要实现这个接口来接收认证结果
     */
    public interface AuthCallback {
        /**
         * 认证成功时调用
         *
         * @param username 用户名
         * @param message  成功消息
         */
        void onSuccess(String username, String message);

        /**
         * 认证失败时调用
         *
         * @param errorMessage 错误消息
         */
        void onFailure(String errorMessage);
    }

    // ========== 登录功能 ==========

    /**
     * 执行登录操作
     *
     * @param username 用户名（明文）
     * @param password 密码（明文）
     * @param callback 认证结果回调
     *
     * 使用示例：
     * AuthManager.getInstance().login("user123", "pass123", new AuthCallback() {
     *     @Override
     *     public void onSuccess(String username, String message) {
     *         // 登录成功，跳转到主界面
     *         Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
     *     }
     *
     *     @Override
     *     public void onFailure(String errorMessage) {
     *         // 登录失败，显示错误信息
     *         Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
     *     }
     * });
     */
    public void login(String username, String password, AuthCallback callback) {
        Log.d(TAG, "开始登录：用户名=" + username);

        // 检查WebSocket是否已连接
        if (!webSocketManager.isConnected()) {
            Log.e(TAG, "登录失败：WebSocket未连接");
            notifyFailure(callback, "网络未连接，请先连接服务器");
            return;
        }

        // 检查参数是否为空
        if (username == null || username.trim().isEmpty()) {
            Log.e(TAG, "登录失败：用户名为空");
            notifyFailure(callback, "用户名不能为空");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            Log.e(TAG, "登录失败：密码为空");
            notifyFailure(callback, "密码不能为空");
            return;
        }

        // 保存回调，用于接收服务器响应后通知界面
        this.currentCallback = callback;

        // 构造登录数据（JSON格式）
        // 格式：{"username":"user123","password":"pass123"}
        LoginData loginData = new LoginData(username, password);
        String jsonData = gson.toJson(loginData);

        // 创建WebSocket消息
        WebSocketMessage message = new WebSocketMessage(
                WebSocketMessage.TYPE_LOGIN,
                jsonData
        );

        // 发送登录消息
        boolean sent = webSocketManager.sendMessage(message);

        if (sent) {
            Log.d(TAG, "登录消息已发送");
        } else {
            Log.e(TAG, "登录消息发送失败");
            notifyFailure(callback, "发送登录请求失败");
            this.currentCallback = null;
        }
    }

    // ========== 注册功能 ==========

    /**
     * 执行注册操作
     *
     * @param username 用户名（明文）
     * @param password 密码（明文）
     * @param callback 认证结果回调
     *
     * 使用示例：
     * AuthManager.getInstance().register("user123", "pass123", new AuthCallback() {
     *     @Override
     *     public void onSuccess(String username, String message) {
     *         // 注册成功
     *         Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
     *     }
     *
     *     @Override
     *     public void onFailure(String errorMessage) {
     *         // 注册失败
     *         Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
     *     }
     * });
     */
    public void register(String username, String password, AuthCallback callback) {
        Log.d(TAG, "开始注册：用户名=" + username);

        // 检查WebSocket是否已连接
        if (!webSocketManager.isConnected()) {
            Log.e(TAG, "注册失败：WebSocket未连接");
            notifyFailure(callback, "网络未连接，请先连接服务器");
            return;
        }

        // 检查参数是否为空
        if (username == null || username.trim().isEmpty()) {
            Log.e(TAG, "注册失败：用户名为空");
            notifyFailure(callback, "用户名不能为空");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            Log.e(TAG, "注册失败：密码为空");
            notifyFailure(callback, "密码不能为空");
            return;
        }

        // 保存回调
        this.currentCallback = callback;

        // 构造注册数据（JSON格式）
        // 格式：{"username":"user123","password":"pass123"}
        LoginData registerData = new LoginData(username, password);
        String jsonData = gson.toJson(registerData);

        // 创建WebSocket消息
        WebSocketMessage message = new WebSocketMessage(
                WebSocketMessage.TYPE_REGISTER,
                jsonData
        );

        // 发送注册消息
        boolean sent = webSocketManager.sendMessage(message);

        if (sent) {
            Log.d(TAG, "注册消息已发送");
        } else {
            Log.e(TAG, "注册消息发送失败");
            notifyFailure(callback, "发送注册请求失败");
            this.currentCallback = null;
        }
    }

    // ========== 接收服务器响应 ==========

    /**
     * 接收WebSocket消息（通过EventBus）
     *
     * 这个方法会在WebSocketManager收到消息后被自动调用
     * @Subscribe注解表示这是一个EventBus订阅方法
     * ThreadMode.MAIN表示在主线程执行
     *
     * @param message WebSocket消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebSocketMessage(WebSocketMessage message) {
        // 只处理登录和注册响应
        String type = message.getType();

        if (WebSocketMessage.TYPE_LOGIN.equals(type)) {
            // 处理登录响应
            handleLoginResponse(message);
        } else if (WebSocketMessage.TYPE_REGISTER.equals(type)) {
            // 处理注册响应
            handleRegisterResponse(message);
        }
    }

    /**
     * 处理登录响应
     *
     * @param message 服务器返回的消息
     */
    private void handleLoginResponse(WebSocketMessage message) {
        Log.d(TAG, "收到登录响应：" + message.getData());

        if (currentCallback == null) {
            Log.w(TAG, "没有回调，忽略登录响应");
            return;
        }

        // 获取响应数据
        String data = message.getData();

        // 简单判断：如果包含"成功"就认为登录成功
        if (data != null && data.contains("成功")) {
            // 登录成功
            Log.d(TAG, "登录成功");

            // 保存当前用户名（从之前发送的数据中获取）
            // 注意：这里简化处理，实际应该从响应中获取
            // 但由于我们的简化版本没有返回用户名，所以这里暂时不保存

            // 通知界面登录成功
            notifySuccess(currentCallback, "", data);
        } else {
            // 登录失败
            Log.e(TAG, "登录失败：" + data);
            notifyFailure(currentCallback, data != null ? data : "登录失败");
        }

        // 清空回调
        currentCallback = null;
    }

    /**
     * 处理注册响应
     *
     * @param message 服务器返回的消息
     */
    private void handleRegisterResponse(WebSocketMessage message) {
        Log.d(TAG, "收到注册响应：" + message.getData());

        if (currentCallback == null) {
            Log.w(TAG, "没有回调，忽略注册响应");
            return;
        }

        // 获取响应数据
        String data = message.getData();

        // 简单判断：如果包含"成功"就认为注册成功
        if (data != null && data.contains("成功")) {
            // 注册成功
            Log.d(TAG, "注册成功");
            notifySuccess(currentCallback, "", data);
        } else {
            // 注册失败
            Log.e(TAG, "注册失败：" + data);
            notifyFailure(currentCallback, data != null ? data : "注册失败");
        }

        // 清空回调
        currentCallback = null;
    }

    // ========== 回调通知 ==========

    /**
     * 通知认证成功
     *
     * @param callback 回调接口
     * @param username 用户名
     * @param message  成功消息
     */
    private void notifySuccess(AuthCallback callback, String username, String message) {
        if (callback != null) {
            // 在主线程执行回调
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(username, message);
                }
            });
        }
    }

    /**
     * 通知认证失败
     *
     * @param callback     回调接口
     * @param errorMessage 错误消息
     */
    private void notifyFailure(AuthCallback callback, String errorMessage) {
        if (callback != null) {
            // 在主线程执行回调
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(errorMessage);
                }
            });
        }
    }

    // ========== 用户信息 ==========

    /**
     * 获取当前登录的用户名
     *
     * @return 用户名，如果未登录返回null
     */
    public String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * 检查是否已登录
     *
     * @return true表示已登录，false表示未登录
     */
    public boolean isLoggedIn() {
        return currentUsername != null && !currentUsername.isEmpty();
    }

    /**
     * 登出
     *
     * 清除当前用户信息
     */
    public void logout() {
        Log.d(TAG, "用户登出");
        currentUsername = null;
    }

    // ========== 数据模型类 ==========

    /**
     * 登录/注册数据模型
     *
     * 用于构造JSON格式的登录和注册数据
     */
    private static class LoginData {
        // 用户名
        private String username;

        // 密码
        private String password;

        /**
         * 构造函数
         *
         * @param username 用户名
         * @param password 密码
         */
        public LoginData(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // Gson需要getter方法来序列化对象
        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    // ========== 清理资源 ==========

    /**
     * 销毁AuthManager
     *
     * 取消EventBus注册，释放资源
     * 注意：一般不需要调用这个方法，因为AuthManager是单例
     */
    public void destroy() {
        Log.d(TAG, "销毁AuthManager");

        // 取消EventBus注册
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        // 清空回调
        currentCallback = null;
        currentUsername = null;
    }
}
