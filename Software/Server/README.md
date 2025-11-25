# 宠物项圈服务器

## 编译
```bash
mkdir build
cd build
cmake ..
make
```

## 运行
```bash
./pet_collar_server
```

## 架构
- core/: 核心模块（WebSocket、客户端管理、健康检查）
- message/: 消息处理（路由、认证、GPS、视频、控制）
- database/: 数据库（用户管理、Token管理）
- utils/: 工具（日志、异常处理、通用工具）
- data/: 数据文件（数据库、配置、日志）
