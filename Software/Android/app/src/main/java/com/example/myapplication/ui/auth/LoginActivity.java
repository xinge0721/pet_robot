package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.model.LoginResponse;
import com.example.myapplication.data.model.User;
import com.example.myapplication.data.network.MockAuthService;
import com.example.myapplication.data.prefs.UserPreferences;
import com.example.myapplication.ui.main.MainActivity;

/**
 * 登录界面
 * 负责用户登录功能，验证用户名和密码
 */
public class LoginActivity extends AppCompatActivity {

    // UI组件
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvRegister;

    // 业务组件
    private MockAuthService authService;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化业务组件
        authService = new MockAuthService(this);
        userPreferences = new UserPreferences(this);

        // 初始化UI组件
        initViews();

        // 设置监听器
        setupListeners();
    }

    /**
     * 初始化UI组件
     * 绑定布局文件中的各个控件
     */
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvRegister = findViewById(R.id.tv_register);
    }

    /**
     * 设置各个控件的点击监听器
     */
    private void setupListeners() {
        // 登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // 注册文本点击事件
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });
    }

    /**
     * 处理登录逻辑
     * 1. 验证输入
     * 2. 调用认证服务
     * 3. 处理登录结果
     */
    private void handleLogin() {
        // 获取输入的用户名和密码
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 验证输入是否为空
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载状态
        showLoading(true);

        // 在后台线程执行登录操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用认证服务进行登录
                LoginResponse response = authService.login(username, password);

                // 切换回主线程处理结果
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        handleLoginResponse(response, username);
                    }
                });
            }
        }).start();
    }

    /**
     * 处理登录响应结果
     * @param response 登录响应对象
     * @param username 用户名
     */
    private void handleLoginResponse(LoginResponse response, String username) {
        if (response.getCode() == 200) {
            // 登录成功
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();

            // 保存用户信息到本地
            User user = new User(response.getUserId(), username, response.getToken());
            userPreferences.saveUser(user);

            // 跳转到主界面
            navigateToMain();
        } else {
            // 登录失败，显示错误信息
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示或隐藏加载状态
     * @param show 是否显示加载状态
     */
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
        }
    }

    /**
     * 跳转到主界面
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 结束当前Activity，防止用户按返回键回到登录页
    }

    /**
     * 跳转到注册界面
     */
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
