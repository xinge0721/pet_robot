package com.example.prt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prt.module.Auth.AuthManager;
import com.example.prt.module.WebSockets.WebSocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.example.prt.module.WebSockets.WebSocketMessage;

/**
 * 登录注册Activity - 应用的启动界面
 *
 * 这个类负责处理用户的登录和注册功能
 *
 * 主要功能：
 * 1. 连接到WebSocket服务器
 * 2. 提供登录界面，让用户输入用户名和密码
 * 3. 提供注册界面，让用户创建新账号
 * 4. 登录或注册成功后跳转到主界面
 *
 * 工作流程：
 * 1. 启动Activity时自动连接WebSocket服务器
 * 2. 用户输入用户名和密码
 * 3. 点击登录或注册按钮
 * 4. 调用AuthManager进行认证
 * 5. 等待服务器响应
 * 6. 认证成功后跳转到MainActivity
 */
public class startActivity extends AppCompatActivity {

    // 日志标签
    private static final String TAG = "startActivity";

    // ========== UI组件 ==========

    // 用户名输入框
    private EditText etUsername;

    // 密码输入框
    private EditText etPassword;

    // 登录按钮
    private Button btnLogin;

    // 注册按钮
    private Button btnRegister;

    // 加载进度条（显示正在登录或注册）
    private ProgressBar progressBar;

    // 连接状态文本
    private TextView tvConnectionStatus;

    // ========== 成员变量 ==========

    // WebSocket管理器
    private WebSocketManager webSocketManager;

    // 认证管理器
    private AuthManager authManager;

    // 是否正在进行认证操作
    private boolean isAuthenticating = false;

    // ========== Activity生命周期 ==========

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // 初始化UI组件
        initViews();

        // 初始化管理器
        initManagers();

        // 注册EventBus，用于接收WebSocket连接状态
        EventBus.getDefault().register(this);

        // 连接到WebSocket服务器
        connectToServer();
    }

    /**
     * 初始化UI组件
     *
     * 这个方法会找到布局文件中的所有UI组件，并保存到成员变量中
     * 这样后续就可以通过成员变量来操作这些UI组件了
     */
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
        tvConnectionStatus = findViewById(R.id.tv_connection_status);

        // 设置按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        // 初始状态：隐藏进度条
        progressBar.setVisibility(View.GONE);

        Log.d(TAG, "UI组件初始化完成");
    }

    /**
     * 初始化管理器
     *
     * 获取WebSocketManager和AuthManager的单例实例
     */
    private void initManagers() {
        // 获取WebSocketManager单例
        webSocketManager = WebSocketManager.getInstance();

        // 获取AuthManager单例
        authManager = AuthManager.getInstance();

        Log.d(TAG, "管理器初始化完成");
    }

    /**
     * 连接到WebSocket服务器
     *
     * 在登录之前，必须先连接到服务器
     */
    private void connectToServer() {
        Log.d(TAG, "开始连接到WebSocket服务器");

        // 服务器地址
        // 注意：在 Android 模拟器中，10.0.2.2 代表宿主机（您的电脑）的 localhost
        // 如果使用真机，需要改为电脑的局域网 IP 地址（如 192.168.1.x）
        String serverUrl = "ws://10.0.2.2:8080";

        // 更新连接状态文本
        updateConnectionStatus("正在连接到服务器...");

        // 连接到服务器
        webSocketManager.connect(serverUrl);
    }

    /**
     * 更新连接状态文本
     *
     * @param status 状态文本
     */
    private void updateConnectionStatus(String status) {
        Log.d(TAG, "连接状态：" + status);

        // 如果UI组件已初始化，更新文本
        if (tvConnectionStatus != null) {
            tvConnectionStatus.setText(status);
        }
    }

    // ========== WebSocket消息处理 ==========

    /**
     * 接收WebSocket消息（通过EventBus）
     *
     * 这个方法主要用于监听WebSocket连接状态
     * 只有连接成功后，才能进行登录或注册操作
     *
     * @param message WebSocket消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebSocketMessage(WebSocketMessage message) {
        String type = message.getType();

        if (WebSocketMessage.TYPE_CONNECT.equals(type)) {
            // WebSocket连接成功
            handleWebSocketConnected();
        } else if (WebSocketMessage.TYPE_DISCONNECT.equals(type)) {
            // WebSocket连接断开
            handleWebSocketDisconnected(message.getData());
        }
    }

    /**
     * 处理WebSocket连接成功
     */
    private void handleWebSocketConnected() {
        Log.d(TAG, "WebSocket连接成功");

        // 更新连接状态
        updateConnectionStatus("已连接到服务器");

        // 显示提示
        Toast.makeText(this, "服务器连接成功，可以登录了", Toast.LENGTH_SHORT).show();

        // 启用登录和注册按钮
        if (btnLogin != null) {
            btnLogin.setEnabled(true);
        }
        if (btnRegister != null) {
            btnRegister.setEnabled(true);
        }
    }

    /**
     * 处理WebSocket连接断开
     *
     * @param reason 断开原因
     */
    private void handleWebSocketDisconnected(String reason) {
        Log.d(TAG, "WebSocket连接断开：" + reason);

        // 更新连接状态
        updateConnectionStatus("连接断开：" + reason);

        // 显示提示
        Toast.makeText(this, "服务器连接断开", Toast.LENGTH_SHORT).show();

        // 禁用登录和注册按钮
        if (btnLogin != null) {
            btnLogin.setEnabled(false);
        }
        if (btnRegister != null) {
            btnRegister.setEnabled(false);
        }

        // 如果正在认证，取消认证状态
        if (isAuthenticating) {
            hideLoading();
            isAuthenticating = false;
        }
    }

    // ========== 登录功能 ==========

    /**
     * 处理登录按钮点击
     *
     * 这个方法会在用户点击登录按钮时被调用
     */
    private void handleLogin() {
        Log.d(TAG, "用户点击登录按钮");

        // 检查是否正在进行认证操作
        if (isAuthenticating) {
            Toast.makeText(this, "正在处理中，请稍候", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取用户输入的用户名和密码
        String username = etUsername != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString().trim() : "";

        // 验证输入
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载状态
        showLoading("正在登录...");

        // 标记正在认证
        isAuthenticating = true;

        // 调用AuthManager进行登录
        authManager.login(username, password, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(String username, String message) {
                // 登录成功
                handleLoginSuccess(username, message);
            }

            @Override
            public void onFailure(String errorMessage) {
                // 登录失败
                handleLoginFailure(errorMessage);
            }
        });
    }

    /**
     * 处理登录成功
     *
     * @param username 用户名
     * @param message  成功消息
     */
    private void handleLoginSuccess(String username, String message) {
        Log.d(TAG, "登录成功：" + message);

        // 隐藏加载状态
        hideLoading();

        // 重置认证状态
        isAuthenticating = false;

        // 显示成功提示
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // 跳转到主界面
        navigateToMainActivity();
    }

    /**
     * 处理登录失败
     *
     * @param errorMessage 错误消息
     */
    private void handleLoginFailure(String errorMessage) {
        Log.e(TAG, "登录失败：" + errorMessage);

        // 隐藏加载状态
        hideLoading();

        // 重置认证状态
        isAuthenticating = false;

        // 显示错误提示
        Toast.makeText(this, "登录失败：" + errorMessage, Toast.LENGTH_LONG).show();
    }

    // ========== 注册功能 ==========

    /**
     * 处理注册按钮点击
     *
     * 这个方法会在用户点击注册按钮时被调用
     */
    private void handleRegister() {
        Log.d(TAG, "用户点击注册按钮");

        // 检查是否正在进行认证操作
        if (isAuthenticating) {
            Toast.makeText(this, "正在处理中，请稍候", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取用户输入的用户名和密码
        String username = etUsername != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString().trim() : "";

        // 验证输入
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 密码长度检查（可选）
        if (password.length() < 6) {
            Toast.makeText(this, "密码长度至少6位", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载状态
        showLoading("正在注册...");

        // 标记正在认证
        isAuthenticating = true;

        // 调用AuthManager进行注册
        authManager.register(username, password, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess(String username, String message) {
                // 注册成功
                handleRegisterSuccess(username, message);
            }

            @Override
            public void onFailure(String errorMessage) {
                // 注册失败
                handleRegisterFailure(errorMessage);
            }
        });
    }

    /**
     * 处理注册成功
     *
     * @param username 用户名
     * @param message  成功消息
     */
    private void handleRegisterSuccess(String username, String message) {
        Log.d(TAG, "注册成功：" + message);

        // 隐藏加载状态
        hideLoading();

        // 重置认证状态
        isAuthenticating = false;

        // 显示成功提示
        Toast.makeText(this, message + "，请登录", Toast.LENGTH_SHORT).show();

        // 注册成功后，可以选择：
        // 方案1：自动跳转到主界面
        // navigateToMainActivity();

        // 方案2：提示用户登录（当前采用这个方案）
        // 用户需要再次点击登录按钮
    }

    /**
     * 处理注册失败
     *
     * @param errorMessage 错误消息
     */
    private void handleRegisterFailure(String errorMessage) {
        Log.e(TAG, "注册失败：" + errorMessage);

        // 隐藏加载状态
        hideLoading();

        // 重置认证状态
        isAuthenticating = false;

        // 显示错误提示
        Toast.makeText(this, "注册失败：" + errorMessage, Toast.LENGTH_LONG).show();
    }

    // ========== UI辅助方法 ==========

    /**
     * 显示加载状态
     *
     * @param message 加载提示文本
     */
    private void showLoading(String message) {
        Log.d(TAG, message);

        // 显示进度条
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // 禁用按钮，防止重复点击
        if (btnLogin != null) {
            btnLogin.setEnabled(false);
        }
        if (btnRegister != null) {
            btnRegister.setEnabled(false);
        }

        // 更新状态文本
        updateConnectionStatus(message);
    }

    /**
     * 隐藏加载状态
     */
    private void hideLoading() {
        Log.d(TAG, "隐藏加载状态");

        // 隐藏进度条
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // 启用按钮
        if (btnLogin != null) {
            btnLogin.setEnabled(true);
        }
        if (btnRegister != null) {
            btnRegister.setEnabled(true);
        }

        // 恢复状态文本
        updateConnectionStatus("已连接到服务器");
    }

    /**
     * 跳转到主界面
     *
     * 登录或注册成功后调用这个方法跳转到MainActivity
     */
    private void navigateToMainActivity() {
        Log.d(TAG, "跳转到主界面");

        // 创建Intent，指定要跳转的Activity
        Intent intent = new Intent(startActivity.this, MainActivity.class);

        // 启动MainActivity
        startActivity(intent);

        // 结束当前Activity
        // 这样用户按返回键时不会回到登录界面
        finish();
    }

    // ========== Activity生命周期 ==========

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 取消注册EventBus
        EventBus.getDefault().unregister(this);

        Log.d(TAG, "startActivity销毁");

        // 注意：这里不要断开WebSocket连接
        // 因为登录成功后会跳转到MainActivity，MainActivity还需要使用WebSocket
    }
}
