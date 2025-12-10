package com.example.prt.module.WebSockets;

/**
 * WebSocket消息模型类
 *
 * 这个类定义了WebSocket消息的统一格式
 * 所有通过WebSocket发送和接收的消息都应该遵循这个格式
 *
 * 为什么需要统一的消息格式？
 * - 方便消息路由：根据type字段可以把消息分发给不同的模块
 * - 方便扩展：新增字段不影响现有代码
 * - 方便调试：消息格式统一，容易排查问题
 *
 * 消息格式示例：
 * {
 *   "type": "login",           // 消息类型
 *   "data": "加密后的数据",     // 实际数据
 *   "timestamp": 1234567890,   // 时间戳
 *   "token": "用户token"       // 用户认证token
 * }
 */
public class WebSocketMessage {

    /**
     * 消息类型常量定义
     *
     * 这些常量定义了系统中所有可能的消息类型
     * 使用常量而不是直接写字符串的好处：
     * 1. 避免拼写错误
     * 2. IDE可以自动提示
     * 3. 方便统一修改
     */

    // 系统相关消息类型
    public static final String TYPE_HEARTBEAT = "heartbeat";      // 心跳消息（用于保持连接）
    public static final String TYPE_CONNECT = "connect";          // 连接消息
    public static final String TYPE_DISCONNECT = "disconnect";    // 断开连接消息

    // 用户相关消息类型
    public static final String TYPE_LOGIN = "login";              // 登录消息
    public static final String TYPE_REGISTER = "register";        // 注册消息
    public static final String TYPE_LOGOUT = "logout";            // 登出消息

    // 聊天相关消息类型
    public static final String TYPE_CHAT = "chat";                // 聊天消息
    public static final String TYPE_CHAT_HISTORY = "chat_history"; // 聊天历史

    // AI交互相关消息类型
    public static final String TYPE_AI_REQUEST = "ai_request";    // AI请求
    public static final String TYPE_AI_RESPONSE = "ai_response";  // AI响应

    // 定位相关消息类型
    public static final String TYPE_LOCATION = "location";        // 位置信息
    public static final String TYPE_LOCATION_UPDATE = "location_update"; // 位置更新

    // 视频相关消息类型
    public static final String TYPE_VIDEO_START = "video_start";  // 开始视频传输
    public static final String TYPE_VIDEO_STOP = "video_stop";    // 停止视频传输
    public static final String TYPE_VIDEO_FRAME = "video_frame";  // 视频帧数据

    // 社区相关消息类型
    public static final String TYPE_COMMUNITY = "community";      // 社区消息
    public static final String TYPE_NOTIFICATION = "notification"; // 通知消息

    // ========== 消息字段 ==========

    /**
     * 消息类型
     *
     * 用于标识这是什么类型的消息
     * 应该使用上面定义的常量，例如：TYPE_LOGIN、TYPE_CHAT等
     */
    private String type;

    /**
     * 消息数据
     *
     * 实际的消息内容，可以是任意字符串
     * 通常是JSON格式的字符串，也可以是加密后的数据
     *
     * 例如：
     * - 登录消息：{"username":"user123","password":"pass123"}
     * - 聊天消息：{"content":"你好","toUser":"user456"}
     * - 位置消息：{"lat":39.9,"lng":116.4,"speed":10}
     */
    private String data;

    /**
     * 时间戳
     *
     * 消息发送的时间，使用Unix时间戳（毫秒）
     * 可以用于：
     * - 消息排序
     * - 判断消息是否过期
     * - 计算网络延迟
     */
    private long timestamp;

    /**
     * 用户认证token
     *
     * 用于验证用户身份
     * 登录成功后，服务器会返回一个token
     * 之后的所有请求都需要带上这个token
     *
     * 注意：登录和注册消息不需要token
     */
    private String token;

    /**
     * 消息ID（可选）
     *
     * 用于标识唯一的消息，可以用于：
     * - 消息去重
     * - 消息确认（ACK）
     * - 消息追踪
     */
    private String messageId;

    // ========== 构造函数 ==========

    /**
     * 无参构造函数
     *
     * 创建一个空的消息对象
     * 通常用于接收消息时，由Gson自动创建对象
     */
    public WebSocketMessage() {
    }

    /**
     * 基础构造函数
     *
     * @param type 消息类型
     * @param data 消息数据
     *
     * 创建一个基础的消息对象
     * 时间戳会自动设置为当前时间
     *
     * 使用示例：
     * WebSocketMessage msg = new WebSocketMessage(WebSocketMessage.TYPE_CHAT, "你好");
     */
    public WebSocketMessage(String type, String data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis(); // 自动设置当前时间
    }

    /**
     * 完整构造函数
     *
     * @param type      消息类型
     * @param data      消息数据
     * @param token     用户token
     *
     * 创建一个完整的消息对象
     *
     * 使用示例：
     * WebSocketMessage msg = new WebSocketMessage(
     *     WebSocketMessage.TYPE_CHAT,
     *     "你好",
     *     "user_token_123"
     * );
     */
    public WebSocketMessage(String type, String data, String token) {
        this.type = type;
        this.data = data;
        this.token = token;
        this.timestamp = System.currentTimeMillis();
    }

    // ========== Getter和Setter方法 ==========
    // 这些方法用于获取和设置消息的各个字段

    /**
     * 获取消息类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置消息类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取消息数据
     */
    public String getData() {
        return data;
    }

    /**
     * 设置消息数据
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 获取时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 获取用户token
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置用户token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取消息ID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * 设置消息ID
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    // ========== 工具方法 ==========

    /**
     * 转换为字符串（用于调试）
     *
     * 重写toString方法，方便打印日志时查看消息内容
     *
     * @return 消息的字符串表示
     */
    @Override
    public String toString() {
        return "WebSocketMessage{" +
                "type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", timestamp=" + timestamp +
                ", token='" + token + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }

    /**
     * 判断是否是心跳消息
     *
     * @return true表示是心跳消息，false表示不是
     */
    public boolean isHeartbeat() {
        return TYPE_HEARTBEAT.equals(type);
    }

    /**
     * 判断消息是否过期
     *
     * @param timeoutMillis 超时时间（毫秒）
     * @return true表示已过期，false表示未过期
     *
     * 使用示例：
     * if (message.isExpired(5000)) {
     *     // 消息超过5秒了，认为已过期
     *     Log.w(TAG, "消息已过期，忽略");
     * }
     */
    public boolean isExpired(long timeoutMillis) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - timestamp) > timeoutMillis;
    }
}
