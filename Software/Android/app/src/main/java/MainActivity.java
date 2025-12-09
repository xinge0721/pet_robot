package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import module.WebSockets.WebSocketManager;
import module.WebSockets.WebSocketMessage;

/**
 * 主Activity - 应用的主界面
 *
 * 这个类演示了如何使用新的WebSocket架构
 *
 * 新架构的特点：
 * 1. 使用WebSocketManager单例管理连接
 * 2. 使用EventBus接收消息（解耦）
 * 3. WebSocket连接不受Activity生命周期影响
 * 4. 代码更简洁，易于维护
 */
public class MainActivity extends AppCompatActivity {

    // 日志标签，用于在Logcat中查看日志
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 注册EventBus
        // EventBus是一个消息总线，用于接收WebSocket消息
        // 注册后，这个Activity就可以接收WebSocket发来的消息了
        EventBus.getDefault().register(this);

        // 初始化WebSocket连接
        initWebSocket();
    }

    /**
     * 初始化WebSocket连接
     *
     * 使用新架构后，初始化变得非常简单
     * 只需要调用WebSocketManager的connect方法即可
     */
    private void initWebSocket() {
        // 服务器地址
        // 注意：这里需要替换成你实际的服务器地址
        // 格式：ws://IP地址:端口号
        // 例如：ws://192.168.1.100:8080
        String serverUrl = "ws://192.168.1.100:8080";

        // 获取WebSocketManager单例并连接
        // 就这么简单！不需要写一大堆回调代码
        WebSocketManager.getInstance().connect(serverUrl);

        Log.d(TAG, "WebSocket初始化完成");
    }

    /**
     * EventBus消息接收方法
     *
     * 这个方法会接收所有通过EventBus发送的WebSocketMessage
     *
     * @Subscribe注解的作用：
     * - 告诉EventBus这是一个消息接收方法
     * - threadMode = ThreadMode.MAIN 表示在主线程执行（可以直接更新UI）
     *
     * 这个方法会在以下情况被调用：
     * - WebSocket连接成功
     * - 收到服务器消息
     * - WebSocket连接断开
     *
     * 根据消息的type字段，可以判断是什么类型的消息，然后做相应处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebSocketMessage(WebSocketMessage message) {
        // 打印日志
        Log.d(TAG, "收到WebSocket消息：" + message.toString());

        // 根据消息类型进行不同的处理
        switch (message.getType()) {
            case WebSocketMessage.TYPE_CONNECT:
                // 连接成功
                handleConnected(message);
                break;

            case WebSocketMessage.TYPE_DISCONNECT:
                // 连接断开
                handleDisconnected(message);
                break;

            case WebSocketMessage.TYPE_CHAT:
                // 聊天消息
                handleChatMessage(message);
                break;

            case WebSocketMessage.TYPE_LOCATION:
                // 位置消息
                handleLocationMessage(message);
                break;

            case WebSocketMessage.TYPE_AI_RESPONSE:
                // AI响应
                handleAIResponse(message);
                break;

            // 可以继续添加其他类型的消息处理...

            default:
                // 未知类型的消息
                Log.w(TAG, "未处理的消息类型：" + message.getType());
                break;
        }
    }

    /**
     * 处理连接成功事件
     */
    private void handleConnected(WebSocketMessage message) {
        Log.d(TAG, "WebSocket连接成功");
        Toast.makeText(this, "WebSocket连接成功", Toast.LENGTH_SHORT).show();

        // 连接成功后，可以发送一条测试消息
        WebSocketManager.getInstance().sendMessage(
                WebSocketMessage.TYPE_CHAT,
                "Hello Server! 我是Android客户端"
        );
    }

    /**
     * 处理连接断开事件
     */
    private void handleDisconnected(WebSocketMessage message) {
        Log.d(TAG, "WebSocket连接断开：" + message.getData());
        Toast.makeText(this, "连接断开：" + message.getData(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 处理聊天消息
     */
    private void handleChatMessage(WebSocketMessage message) {
        Log.d(TAG, "收到聊天消息：" + message.getData());
        Toast.makeText(this, "聊天消息：" + message.getData(), Toast.LENGTH_SHORT).show();

        // 这里可以更新聊天界面
        // 例如：把消息添加到聊天列表中
    }

    /**
     * 处理位置消息
     */
    private void handleLocationMessage(WebSocketMessage message) {
        Log.d(TAG, "收到位置消息：" + message.getData());

        // 这里可以解析位置数据并更新地图
        // 例如：
        // try {
        //     JSONObject json = new JSONObject(message.getData());
        //     double lat = json.getDouble("lat");
        //     double lng = json.getDouble("lng");
        //     // 更新地图上的机器人位置...
        // } catch (JSONException e) {
        //     e.printStackTrace();
        // }
    }

    /**
     * 处理AI响应
     */
    private void handleAIResponse(WebSocketMessage message) {
        Log.d(TAG, "收到AI响应：" + message.getData());
        Toast.makeText(this, "AI回复：" + message.getData(), Toast.LENGTH_LONG).show();

        // 这里可以显示AI的回复
    }

    /**
     * 当Activity被销毁时调用
     *
     * 这个方法会在以下情况被调用：
     * 1. 用户按返回键退出应用
     * 2. 系统因内存不足销毁Activity
     * 3. 屏幕旋转导致Activity重建
     *
     * 注意：使用新架构后，我们不需要在这里断开WebSocket连接
     * 因为WebSocket连接是全局的，由WebSocketManager管理
     * 只需要取消注册EventBus即可
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 取消注册EventBus
        // 如果不取消注册，可能会导致内存泄漏
        EventBus.getDefault().unregister(this);

        Log.d(TAG, "MainActivity销毁");

        // 注意：这里不要断开WebSocket连接！
        // 因为其他Activity可能还在使用
        // 只有在应用完全退出时才需要断开连接
    }

    // ========== 下面是一些工具方法，展示如何使用WebSocket ==========

    /**
     * 示例：发送聊天消息
     *
     * 你可以在按钮的点击事件中调用这个方法
     * 例如：
     * Button sendButton = findViewById(R.id.sendButton);
     * sendButton.setOnClickListener(v -> sendChatMessage("你好"));
     */
    private void sendChatMessage(String content) {
        // 检查是否已连接
        if (!WebSocketManager.getInstance().isConnected()) {
            Toast.makeText(this, "WebSocket未连接，无法发送消息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建消息对象
        WebSocketMessage message = new WebSocketMessage(
                WebSocketMessage.TYPE_CHAT,
                content
        );

        // 发送消息
        boolean success = WebSocketManager.getInstance().sendMessage(message);

        if (success) {
            Log.d(TAG, "聊天消息发送成功");
        } else {
            Log.e(TAG, "聊天消息发送失败");
            Toast.makeText(this, "消息发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 示例：发送位置信息
     *
     * @param latitude  纬度
     * @param longitude 经度
     */
    private void sendLocation(double latitude, double longitude) {
        if (!WebSocketManager.getInstance().isConnected()) {
            Toast.makeText(this, "WebSocket未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构造位置数据（JSON格式）
        String locationData = "{\"lat\":" + latitude + ",\"lng\":" + longitude + "}";

        // 发送位置消息
        WebSocketManager.getInstance().sendMessage(
                WebSocketMessage.TYPE_LOCATION,
                locationData
        );

        Log.d(TAG, "位置信息已发送");
    }

    /**
     * 示例：发送AI请求
     *
     * @param question 要问AI的问题
     */
    private void sendAIRequest(String question) {
        if (!WebSocketManager.getInstance().isConnected()) {
            Toast.makeText(this, "WebSocket未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        // 发送AI请求
        WebSocketManager.getInstance().sendMessage(
                WebSocketMessage.TYPE_AI_REQUEST,
                question
        );

        Log.d(TAG, "AI请求已发送：" + question);
    }

    /**
     * 示例：手动连接到服务器
     *
     * 通常不需要手动调用，因为在onCreate中已经自动连接了
     * 但如果需要重新连接，可以调用这个方法
     */
    private void connectToServer() {
        String serverUrl = "ws://192.168.1.100:8080";
        WebSocketManager.getInstance().connect(serverUrl);
        Log.d(TAG, "正在连接到服务器...");
    }

    /**
     * 示例：手动断开连接
     *
     * 注意：通常不建议在Activity中断开连接
     * 因为其他Activity可能还在使用
     * 只有在应用完全退出时才需要断开
     */
    private void disconnectFromServer() {
        WebSocketManager.getInstance().disconnect();
        Log.d(TAG, "已断开WebSocket连接");
    }

    /**
     * 检查连接状态
     *
     * @return true表示已连接，false表示未连接
     */
    private boolean isWebSocketConnected() {
        return WebSocketManager.getInstance().isConnected();
    }
}