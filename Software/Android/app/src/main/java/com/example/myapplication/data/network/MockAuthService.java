package com.example.myapplication.data.network;

import android.content.Context;
import com.example.myapplication.data.model.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 模拟认证服务类
 * 用于在服务器未开发时进行本地用户管理
 * 后续接入服务器后可直接替换为 AuthApi
 */
public class MockAuthService {
    private Context context;
    private Gson gson;
    private static final String FILE_NAME = "users.json";

    // 用户数据模型
    private static class UserData {
        String username;
        String password;
        String userId;

        UserData(String username, String password, String userId) {
            this.username = username;
            this.password = password;
            this.userId = userId;
        }
    }

    // 用户列表包装类
    private static class UsersWrapper {
        List<UserData> users;

        UsersWrapper() {
            this.users = new ArrayList<>();
        }
    }

    public MockAuthService(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    /**
     * 注册新用户
     * @param username 用户名
     * @param password 密码
     * @return LoginResponse 注册结果
     */
    public LoginResponse register(String username, String password) {
        LoginResponse response = new LoginResponse();

        try {
            // 读取现有用户数据
            UsersWrapper wrapper = readUsersFromFile();

            // 检查用户名是否已存在
            for (UserData user : wrapper.users) {
                if (user.username.equals(username)) {
                    return createResponse(400, "用户名已存在", null, null);
                }
            }

            // 创建新用户
            String userId = UUID.randomUUID().toString();
            UserData newUser = new UserData(username, password, userId);
            wrapper.users.add(newUser);

            // 保存到文件
            writeUsersToFile(wrapper);

            // 生成token
            String token = UUID.randomUUID().toString();

            return createResponse(200, "注册成功", token, userId);

        } catch (Exception e) {
            return createResponse(500, "注册失败: " + e.getMessage(), null, null);
        }
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return LoginResponse 登录结果
     */
    public LoginResponse login(String username, String password) {
        try {
            // 读取用户数据
            UsersWrapper wrapper = readUsersFromFile();

            // 验证用户名和密码
            for (UserData user : wrapper.users) {
                if (user.username.equals(username) && user.password.equals(password)) {
                    // 登录成功，生成token
                    String token = UUID.randomUUID().toString();
                    return createResponse(200, "登录成功", token, user.userId);
                }
            }

            // 用户名或密码错误
            return createResponse(401, "用户名或密码错误", null, null);

        } catch (Exception e) {
            return createResponse(500, "登录失败: " + e.getMessage(), null, null);
        }
    }

    /**
     * 从文件读取用户数据
     */
    private UsersWrapper readUsersFromFile() {
        File file = new File(context.getFilesDir(), FILE_NAME);

        if (!file.exists()) {
            return new UsersWrapper();
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<UsersWrapper>(){}.getType();
            UsersWrapper wrapper = gson.fromJson(reader, type);
            return wrapper != null ? wrapper : new UsersWrapper();
        } catch (Exception e) {
            e.printStackTrace();
            return new UsersWrapper();
        }
    }

    /**
     * 将用户数据写入文件
     */
    private void writeUsersToFile(UsersWrapper wrapper) throws Exception {
        File file = new File(context.getFilesDir(), FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(wrapper, writer);
        }
    }

    /**
     * 创建响应对象
     */
    private LoginResponse createResponse(int code, String message, String token, String userId) {
        // 由于 LoginResponse 没有 setter 方法，我们需要使用反射或者修改模型
        // 这里使用 Gson 来创建对象
        String json = String.format(
            "{\"code\":%d,\"message\":\"%s\",\"token\":\"%s\",\"userId\":\"%s\"}",
            code, message,
            token != null ? token : "",
            userId != null ? userId : ""
        );
        return gson.fromJson(json, LoginResponse.class);
    }
}
