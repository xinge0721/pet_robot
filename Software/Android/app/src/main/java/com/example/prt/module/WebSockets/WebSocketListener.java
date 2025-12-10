package com.example.prt.module.WebSockets;

/**
 * WebSocket消息监听接口
 *
 * 这个接口定义了WebSocket通信过程中的各种事件回调
 * 当你使用WebSocket客户端时，需要实现这个接口来处理各种事件
 *
 * 简单理解：就像你在等快递，这个接口告诉你：
 * - 快递什么时候开始派送（onConnected）
 * - 快递送到了（onMessageReceived）
 * - 快递出问题了（onConnectFailed）
 * - 快递断了联系（onDisconnected）
 */
public interface WebSocketListener {

    /**
     * 当WebSocket连接成功建立时调用
     *
     * 这个方法会在你的手机成功连接到服务器时被调用
     * 连接成功后，你就可以开始发送和接收消息了
     *
     * 使用场景：
     * - 可以在这里更新UI，显示"已连接"状态
     * - 可以在这里发送一些初始化消息给服务器
     */
    void onConnected();

    /**
     * 当收到服务器发来的文本消息时调用
     *
     * @param message 服务器发来的文本消息内容
     *
     * 这个方法会在服务器给你发消息时被调用
     * message参数就是服务器发来的内容（比如一段文字、JSON数据等）
     *
     * 使用场景：
     * - 接收服务器的指令
     * - 接收聊天消息
     * - 接收机器人的状态数据（比如GPS位置、电量等）
     *
     * 例如：服务器发来 "{"type":"gps","lat":39.9,"lng":116.4}"
     * 你就可以在这个方法里解析这个JSON，然后更新地图显示
     */
    void onMessageReceived(String message);

    /**
     * 当WebSocket连接失败时调用
     *
     * @param error 失败的原因描述
     *
     * 这个方法会在连接服务器失败时被调用
     * 可能的失败原因：
     * - 服务器地址错误
     * - 网络不通
     * - 服务器没有启动
     * - 防火墙阻止了连接
     *
     * 使用场景：
     * - 显示错误提示给用户
     * - 记录错误日志
     * - 决定是否要重新尝试连接
     */
    void onConnectFailed(String error);

    /**
     * 当WebSocket连接断开时调用
     *
     * @param reason 断开的原因描述
     *
     * 这个方法会在连接断开时被调用
     * 可能的断开原因：
     * - 网络信号不好
     * - 服务器主动断开连接
     * - 手机切换了网络（比如从WiFi切换到4G）
     * - 应用进入后台太久
     *
     * 使用场景：
     * - 更新UI显示"已断开"状态
     * - 尝试重新连接
     * - 提示用户检查网络
     */
    void onDisconnected(String reason);
}
