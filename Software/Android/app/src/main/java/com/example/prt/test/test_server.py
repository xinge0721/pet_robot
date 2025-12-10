# -*- coding: utf-8 -*-
"""
WebSocket 测试服务器

这是一个简单的 WebSocket 服务器，用于测试 Android 客户端的连接和消息收发功能

功能说明：
1. 监听 8080 端口，等待客户端连接
2. 接收客户端发送的 JSON 格式消息
3. 根据消息类型自动回复相应的测试消息
4. 打印所有收发的消息，方便调试

使用方法：
1. 安装依赖：pip install websockets
2. 运行服务器：python test_server.py
3. 修改 Android 客户端的服务器地址为你的电脑 IP（例如：ws://192.168.1.100:8080）
4. 运行 Android 应用，即可看到连接和消息交互

注意事项：
- 确保防火墙允许 8080 端口
- 确保手机和电脑在同一个局域网内
- 如果连接失败，检查 IP 地址是否正确
"""

import asyncio
import websockets
import json
import time

# 服务器配置
HOST = "127.0.0.1"
PORT = 8080


def create_message(msg_type, data, token=None):
    """
    创建符合 Android 客户端格式的消息

    参数说明：
    - msg_type: 消息类型（例如：chat、ai_response、location 等）
    - data: 消息数据（字符串）
    - token: 用户认证 token（可选）

    返回值：
    - JSON 格式的字符串
    """
    message = {
        "type": msg_type,
        "data": data,
        "timestamp": int(time.time() * 1000)  # 毫秒级时间戳
    }

    # 如果提供了 token，则添加到消息中
    if token:
        message["token"] = token

    return json.dumps(message, ensure_ascii=False)


async def handle_client(websocket):
    """
    处理客户端连接

    这个函数会在每次有新客户端连接时被调用
    它会持续运行，直到客户端断开连接

    参数说明：
    - websocket: WebSocket 连接对象
    """
    # 获取客户端的 IP 地址
    client_ip = websocket.remote_address[0]
    print(f"\n{'='*60}")
    print(f"[连接] 新客户端已连接：{client_ip}")
    print(f"{'='*60}\n")

    try:
        # 发送欢迎消息
        welcome_msg = create_message("connect", "欢迎连接到测试服务器！")
        await websocket.send(welcome_msg)
        print(f"[发送] {welcome_msg}\n")

        # 持续接收客户端消息
        async for message in websocket:
            print(f"[接收] {message}")

            try:
                # 解析 JSON 消息
                msg_data = json.loads(message)
                msg_type = msg_data.get("type", "unknown")
                msg_content = msg_data.get("data", "")
                msg_token = msg_data.get("token", None)

                print(f"  类型: {msg_type}")
                print(f"  内容: {msg_content}")
                if msg_token:
                    print(f"  Token: {msg_token}")
                print()

                # 根据消息类型进行不同的处理
                response = None

                if msg_type == "chat":
                    # 聊天消息：回复一个简单的确认消息
                    response = create_message(
                        "chat",
                        f"服务器收到你的消息：{msg_content}",
                        msg_token
                    )

                elif msg_type == "ai_request":
                    # AI 请求：模拟 AI 回复
                    response = create_message(
                        "ai_response",
                        f"这是 AI 对「{msg_content}」的模拟回复：你好！我是测试服务器的模拟 AI。",
                        msg_token
                    )

                elif msg_type == "location":
                    # 位置消息：确认收到位置信息
                    response = create_message(
                        "location",
                        f"服务器已收到位置信息：{msg_content}",
                        msg_token
                    )

                elif msg_type == "heartbeat":
                    # 心跳消息：回复心跳
                    response = create_message("heartbeat", "pong", msg_token)

                elif msg_type == "login":
                    # 登录消息：模拟登录成功
                    response = create_message(
                        "login",
                        "登录成功！",
                        "test_token_123456"  # 返回一个测试 token
                    )

                else:
                    # 未知类型的消息：返回通用确认
                    response = create_message(
                        msg_type,
                        f"服务器收到 {msg_type} 类型的消息",
                        msg_token
                    )

                # 发送回复消息
                if response:
                    await websocket.send(response)
                    print(f"[发送] {response}\n")

            except json.JSONDecodeError:
                # 如果消息不是有效的 JSON，打印错误信息
                print(f"[错误] 无法解析 JSON 消息：{message}\n")
                error_msg = create_message("error", "消息格式错误，请发送 JSON 格式的消息")
                await websocket.send(error_msg)

            except Exception as e:
                # 处理其他异常
                print(f"[错误] 处理消息时出错：{str(e)}\n")

    except websockets.exceptions.ConnectionClosed:
        # 客户端正常断开连接
        print(f"\n{'='*60}")
        print(f"[断开] 客户端已断开连接：{client_ip}")
        print(f"{'='*60}\n")

    except Exception as e:
        # 其他异常
        print(f"\n[错误] 连接异常：{str(e)}\n")


async def main():
    """
    主函数：启动 WebSocket 服务器
    """
    print("\n" + "="*60)
    print("WebSocket 测试服务器")
    print("="*60)
    print(f"服务器地址：ws://{HOST}:{PORT}")
    print(f"启动时间：{time.strftime('%Y-%m-%d %H:%M:%S')}")
    print("="*60)
    print("\n等待客户端连接...\n")

    # 启动 WebSocket 服务器
    # 这个服务器会一直运行，直到手动停止（Ctrl+C）
    async with websockets.serve(handle_client, HOST, PORT):
        # 保持服务器运行
        await asyncio.Future()  # 永远等待（直到程序被终止）


if __name__ == "__main__":
    """
    程序入口

    当直接运行这个文件时，会执行这里的代码
    """
    try:
        # 运行主函数
        asyncio.run(main())
    except KeyboardInterrupt:
        # 用户按下 Ctrl+C 时，优雅地退出
        print("\n\n" + "="*60)
        print("服务器已停止")
        print("="*60 + "\n")
