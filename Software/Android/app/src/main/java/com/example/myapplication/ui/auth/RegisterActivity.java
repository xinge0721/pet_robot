package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.model.LoginResponse;
import com.example.myapplication.data.model.User;
import com.example.myapplication.data.network.MockAuthService;
import com.example.myapplication.data.prefs.UserPreferences;
import com.example.myapplication.ui.main.MainActivity;

/**
 * 注册界面
 * 负责用户注册功能，创建新用户账号
 */
public class RegisterActivity extends AppCompatActivity {
    // UI组件
    private EditText etUsername, etPassword, etPasswordConfirm;
    private Button btnRegister;

    // 业务组件
    private MockAuthService authService;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化业务组件
        authService = new MockAuthService(this);
        userPreferences = new UserPreferences(this);

        // 初始化UI组件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        btnRegister = findViewById(R.id.btn_register);

        // 设置注册按钮点击事件
        btnRegister.setOnClickListener(v -> register());
    }

    /**
     * 处理注册逻辑
     * 1. 验证输入
     * 2. 调用注册服务
     * 3. 注册成功后自动登录并跳转
     */
    private void register() {
        // 获取输入的用户名和密码
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();

        // 验证输入是否为空
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证密码长度
        if (password.length() < 6) {
            Toast.makeText(this, "密码长度至少6位", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证两次密码是否一致
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 禁用按钮，防止重复点击
        btnRegister.setEnabled(false);

        // 在后台线程执行注册操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用注册服务
                LoginResponse response = authService.register(username, password);

                // 切换回主线程处理结果
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnRegister.setEnabled(true);
                        handleRegisterResponse(response, username);
                    }
                });
            }
        }).start();
    }

    /**
     * 处理注册响应结果
     * @param response 注册响应对象
     * @param username 用户名
     */
    private void handleRegisterResponse(LoginResponse response, String username) {
        if (response.getCode() == 200) {
            // 注册成功
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();

            // 保存用户信息到本地
            User user = new User(response.getUserId(), username, response.getToken());
            userPreferences.saveUser(user);

            // 跳转到主界面
            navigateToMain();
        } else {
            // 注册失败，显示错误信息
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到主界面
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 结束当前Activity和登录Activity

        // 同时结束登录Activity
        if (getParent() != null) {
            getParent().finish();
        }
    }
}
