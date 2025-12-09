# Server 模块完整目录结构

```
Software/Server/
├── CMakeLists.txt
├── main.cpp
├── README.md
├── .gitignore
├── install_dependencies.sh
├── Common/
│   ├── Constants.h
│   ├── Errors.h
│   ├── Result.h
│   └── Types.h
├── Controllers/
│   ├── Http/
│   │   ├── AdminController/
│   │   │   ├── AdminController.cpp
│   │   │   └── AdminController.h
│   │   ├── DeviceController/
│   │   │   ├── DeviceController.cpp
│   │   │   └── DeviceController.h
│   │   ├── MonitorController/
│   │   │   ├── MonitorController.cpp
│   │   │   └── MonitorController.h
│   │   ├── TrafficController/
│   │   │   ├── TrafficController.cpp
│   │   │   └── TrafficController.h
│   │   └── UserController/
│   │       ├── UserController.cpp
│   │       └── UserController.h
│   ├── Tcp/
│   │   ├── DeviceTcpServer/
│   │   │   ├── DeviceTcpServer.cpp
│   │   │   └── DeviceTcpServer.h
│   │   └── Protocol/
│   │       ├── DeviceProtocol/
│   │       │   ├── DeviceProtocol.cpp
│   │       │   └── DeviceProtocol.h
│   │       ├── GpsPacket/
│   │       │   ├── GpsPacket.cpp
│   │       │   └── GpsPacket.h
│   │       └── VideoPacket/
│   │           ├── VideoPacket.cpp
│   │           └── VideoPacket.h
│   └── WebSocket/
│       ├── DeviceWsController/
│       │   ├── DeviceWsController.cpp
│       │   └── DeviceWsController.h
│       └── UserWsController/
│           ├── UserWsController.cpp
│           └── UserWsController.h
├── Core/
│   ├── Application/
│   │   ├── Application.cpp
│   │   └── Application.h
│   ├── Config/
│   │   ├── Config.cpp
│   │   └── Config.h
│   └── DeviceConnectionManager/
│       ├── DeviceConnectionManager.cpp
│       └── DeviceConnectionManager.h
├── data/
│   ├── config/
│   │   ├── config.json
│   │   ├── database.json
│   │   ├── logger.json
│   │   └── model.json
│   ├── HistoryLog/
│   └── HistoryLog_2025-12-09/
├── Filters/
│   ├── AdminFilter/
│   │   ├── AdminFilter.cpp
│   │   └── AdminFilter.h
│   └── AuthFilter/
│       ├── AuthFilter.cpp
│       └── AuthFilter.h
├── Models/
│   ├── Device.cc
│   ├── Device.h
│   ├── DeviceBinding.cc
│   ├── DeviceBinding.h
│   ├── GpsHistory.cc
│   ├── GpsHistory.h
│   ├── OperationLog.cc
│   ├── OperationLog.h
│   ├── TrafficStats.cc
│   ├── TrafficStats.h
│   ├── User.cc
│   └── User.h
├── scripts/
│   ├── init_db.sql
│   ├── start_server.sh
│   └── stop_server.sh
├── Services/
│   ├── Admin/
│   │   ├── AdminService.cpp
│   │   └── AdminService.h
│   ├── Ai/
│   │   ├── AiClient/
│   │   │   ├── AiClient.cpp
│   │   │   └── AiClient.h
│   │   ├── AiService.cpp
│   │   └── AiService.h
│   ├── Auth/
│   │   ├── JwtHelper/
│   │   │   ├── JwtHelper.cpp
│   │   │   └── JwtHelper.h
│   │   ├── PasswordHelper/
│   │   │   ├── PasswordHelper.cpp
│   │   │   └── PasswordHelper.h
│   │   ├── AuthService.cpp
│   │   └── AuthService.h
│   ├── Device/
│   │   ├── DeviceManager/
│   │   │   ├── DeviceManager.cpp
│   │   │   └── DeviceManager.h
│   │   ├── DeviceService.cpp
│   │   └── DeviceService.h
│   ├── Gps/
│   │   ├── GpsBatchWriter/
│   │   │   ├── GpsBatchWriter.cpp
│   │   │   └── GpsBatchWriter.h
│   │   ├── GpsDecryptor/
│   │   │   ├── GpsDecryptor.cpp
│   │   │   └── GpsDecryptor.h
│   │   ├── GpsService.cpp
│   │   └── GpsService.h
│   ├── Stream/
│   │   ├── VideoRouter/
│   │   │   ├── VideoRouter.cpp
│   │   │   └── VideoRouter.h
│   │   ├── VideoUdpServer/
│   │   │   ├── VideoUdpServer.cpp
│   │   │   └── VideoUdpServer.h
│   │   ├── StreamService.cpp
│   │   └── StreamService.h
│   ├── Traffic/
│   │   ├── TrafficAlert/
│   │   │   ├── TrafficAlert.cpp
│   │   │   └── TrafficAlert.h
│   │   ├── TrafficCollector/
│   │   │   ├── TrafficCollector.cpp
│   │   │   └── TrafficCollector.h
│   │   ├── TrafficService.cpp
│   │   └── TrafficService.h
│   └── User/
│       ├── UserManager/
│       │   ├── UserManager.cpp
│       │   └── UserManager.h
│       ├── UserService.cpp
│       └── UserService.h
├── static/
│   ├── css/
│   │   └── style.css
│   ├── images/
│   │   └── .gitkeep
│   └── js/
│       ├── api.js
│       └── main.js
├── test/
│   ├── CMakeLists.txt
│   ├── integration/
│   │   └── test_full_flow.cpp
│   └── unit/
│       ├── test_auth.cpp
│       ├── test_device.cpp
│       ├── test_gps.cpp
│       └── test_traffic.cpp
├── utils/
│   ├── Crypto/
│   │   ├── Crypto.cpp
│   │   └── Crypto.h
│   ├── logger/
│   │   ├── logger.cpp
│   │   └── logger.h
│   ├── RedisHelper/
│   │   ├── RedisHelper.cpp
│   │   └── RedisHelper.h
│   ├── StringUtil/
│   │   ├── StringUtil.cpp
│   │   └── StringUtil.h
│   └── TimeUtil/
│       ├── TimeUtil.cpp
│       └── TimeUtil.h
├── views/
│   ├── dashboard.csp
│   ├── devices.csp
│   ├── index.csp
│   ├── traffic.csp
│   └── users.csp
├── .vscode/
│   ├── launch.json
│   ├── settings.json
│   └── tasks.json
└── build/
    └── (编译生成的文件)
```

## 目录说明

### 根目录文件
- **CMakeLists.txt** - CMake构建配置文件
- **main.cpp** - 程序入口文件
- **README.md** - 项目说明文档
- **.gitignore** - Git忽略文件配置
- **install_dependencies.sh** - 依赖安装脚本

### Common/ - 公共定义目录
- **Constants.h** - 常量定义
- **Errors.h** - 错误码定义
- **Result.h** - 结果类型定义
- **Types.h** - 通用类型定义

### Controllers/ - 控制器目录
#### Http/ - HTTP控制器
- **AdminController/** - 管理员HTTP控制器
  - **AdminController.cpp** - 管理员控制器实现
  - **AdminController.h** - 管理员控制器接口

- **DeviceController/** - 设备HTTP控制器
  - **DeviceController.cpp** - 设备控制器实现
  - **DeviceController.h** - 设备控制器接口

- **MonitorController/** - 监控HTTP控制器
  - **MonitorController.cpp** - 监控控制器实现
  - **MonitorController.h** - 监控控制器接口

- **TrafficController/** - 流量HTTP控制器
  - **TrafficController.cpp** - 流量控制器实现
  - **TrafficController.h** - 流量控制器接口

- **UserController/** - 用户HTTP控制器
  - **UserController.cpp** - 用户控制器实现
  - **UserController.h** - 用户控制器接口

#### Tcp/ - TCP控制器
- **DeviceTcpServer/** - 设备TCP服务器
  - **DeviceTcpServer.cpp** - TCP服务器实现
  - **DeviceTcpServer.h** - TCP服务器接口

- **Protocol/** - 协议处理
  - **DeviceProtocol/** - 设备协议
    - **DeviceProtocol.cpp** - 设备协议实现
    - **DeviceProtocol.h** - 设备协议接口
  - **GpsPacket/** - GPS数据包
    - **GpsPacket.cpp** - GPS数据包实现
    - **GpsPacket.h** - GPS数据包接口
  - **VideoPacket/** - 视频数据包
    - **VideoPacket.cpp** - 视频数据包实现
    - **VideoPacket.h** - 视频数据包接口

#### WebSocket/ - WebSocket控制器
- **DeviceWsController/** - 设备WebSocket控制器
  - **DeviceWsController.cpp** - 设备WS控制器实现
  - **DeviceWsController.h** - 设备WS控制器接口
- **UserWsController/** - 用户WebSocket控制器
  - **UserWsController.cpp** - 用户WS控制器实现
  - **UserWsController.h** - 用户WS控制器接口

### Core/ - 核心模块目录
- **Application/** - 应用程序核心
  - **Application.cpp** - 应用程序实现
  - **Application.h** - 应用程序接口
- **Config/** - 配置管理
  - **Config.cpp** - 配置管理实现
  - **Config.h** - 配置管理接口
- **DeviceConnectionManager/** - 设备连接管理器
  - **DeviceConnectionManager.cpp** - 连接管理器实现
  - **DeviceConnectionManager.h** - 连接管理器接口

### data/ - 数据目录
- **config/** - 配置文件目录
  - **config.json** - 主配置文件
  - **database.json** - 数据库配置文件
  - **logger.json** - 日志配置文件
  - **model.json** - 模型配置文件
- **HistoryLog/** - 历史日志目录
- **HistoryLog_2025-12-09/** - 按日期分类的日志目录
#### Models/ - 数据模型目录
- **Device.cc** - 设备模型实现
- **Device.h** - 设备模型定义

- **DeviceBinding.cc** - 设备绑定模型实现
- **DeviceBinding.h** - 设备绑定模型定义

- **GpsHistory.cc** - GPS历史模型实现
- **GpsHistory.h** - GPS历史模型定义

- **OperationLog.cc** - 操作日志模型实现
- **OperationLog.h** - 操作日志模型定义

- **TrafficStats.cc** - 流量统计模型实现
- **TrafficStats.h** - 流量统计模型定义

- **User.cc** - 用户模型实现
- **User.h** - 用户模型定义

### Filters/ - 过滤器目录
- **AdminFilter/** - 管理员过滤器
  - **AdminFilter.cpp** - 管理员过滤器实现
  - **AdminFilter.h** - 管理员过滤器接口
  
- **AuthFilter/** - 认证过滤器
  - **AuthFilter.cpp** - 认证过滤器实现
  - **AuthFilter.h** - 认证过滤器接口

### scripts/ - 脚本目录
- **init_db.sql** - 数据库初始化脚本
- **start_server.sh** - 服务器启动脚本
- **stop_server.sh** - 服务器停止脚本

### Services/ - 服务层目录
#### Admin/ - 管理员服务
- **AdminService.cpp** - 管理员服务实现
- **AdminService.h** - 管理员服务接口

#### Ai/ - AI服务
- **AiClient/** - AI客户端
  - **AiClient.cpp** - AI客户端实现
  - **AiClient.h** - AI客户端接口
- **AiService.cpp** - AI服务实现
- **AiService.h** - AI服务接口

#### Auth/ - 认证服务
- **JwtHelper/** - JWT工具
  - **JwtHelper.cpp** - JWT工具实现
  - **JwtHelper.h** - JWT工具接口
- **PasswordHelper/** - 密码工具
  - **PasswordHelper.cpp** - 密码工具实现
  - **PasswordHelper.h** - 密码工具接口
- **AuthService.cpp** - 认证服务实现
- **AuthService.h** - 认证服务接口

#### Device/ - 设备服务
- **DeviceManager/** - 设备管理器
  - **DeviceManager.cpp** - 设备管理器实现
  - **DeviceManager.h** - 设备管理器接口
- **DeviceService.cpp** - 设备服务实现
- **DeviceService.h** - 设备服务接口

#### Gps/ - GPS服务
- **GpsBatchWriter/** - GPS批量写入器
  - **GpsBatchWriter.cpp** - 批量写入器实现
  - **GpsBatchWriter.h** - 批量写入器接口
- **GpsDecryptor/** - GPS解密器
  - **GpsDecryptor.cpp** - 解密器实现
  - **GpsDecryptor.h** - 解密器接口
- **GpsService.cpp** - GPS服务实现
- **GpsService.h** - GPS服务接口

#### Stream/ - 视频流服务
- **VideoRouter/** - 视频路由器
  - **VideoRouter.cpp** - 视频路由器实现
  - **VideoRouter.h** - 视频路由器接口
- **VideoUdpServer/** - 视频UDP服务器
  - **VideoUdpServer.cpp** - UDP服务器实现
  - **VideoUdpServer.h** - UDP服务器接口
- **StreamService.cpp** - 视频流服务实现
- **StreamService.h** - 视频流服务接口

#### Traffic/ - 流量统计服务
- **TrafficAlert/** - 流量告警
  - **TrafficAlert.cpp** - 流量告警实现
  - **TrafficAlert.h** - 流量告警接口
- **TrafficCollector/** - 流量收集器
  - **TrafficCollector.cpp** - 流量收集器实现
  - **TrafficCollector.h** - 流量收集器接口
- **TrafficService.cpp** - 流量服务实现
- **TrafficService.h** - 流量服务接口

#### User/ - 用户服务
- **UserManager/** - 用户管理器
  - **UserManager.cpp** - 用户管理器实现
  - **UserManager.h** - 用户管理器接口
- **UserService.cpp** - 用户服务实现
- **UserService.h** - - 用户服务接口

### static/ - 静态资源目录
- **css/** - 样式表目录
  - **style.css** - 主样式表
- **images/** - 图片目录
  - **.gitkeep** - Git占位文件
- **js/** - JavaScript目录
  - **api.js** - API接口脚本
  - **main.js** - 主脚本文件

### test/ - 测试目录
- **CMakeLists.txt** - 测试构建配置
- **integration/** - 集成测试
  - **test_full_flow.cpp** - 完整流程测试
- **unit/** - 单元测试
  - **test_auth.cpp** - 认证测试
  - **test_device.cpp** - 设备测试
  - **test_gps.cpp** - GPS测试
  - **test_traffic.cpp** - 流量测试

### utils/ - 工具类目录
- **Crypto/** - 加密工具
  - **Crypto.cpp** - 加密工具实现
  - **Crypto.h** - 加密工具接口

- **logger/** - 日志工具
  - **logger.cpp** - 日志工具实现
  - **logger.h** - 日志工具接口

- **RedisHelper/** - Redis助手
  - **RedisHelper.cpp** - Redis助手实现
  - **RedisHelper.h** - Redis助手接口

- **StringUtil/** - 字符串工具
  - **StringUtil.cpp** - 字符串工具实现
  - **StringUtil.h** - 字符串工具接口

- **TimeUtil/** - 时间工具
  - **TimeUtil.cpp** - 时间工具实现
  - **TimeUtil.h** - 时间工具接口

### views/ - 视图模板目录
- **dashboard.csp** - 仪表盘页面模板
- **devices.csp** - 设备页面模板
- **index.csp** - 首页模板
- **traffic.csp** - 流量页面模板
- **users.csp** - 用户页面模板

### .vscode/ - VSCode配置目录
- **launch.json** - 调试配置
- **settings.json** - 编辑器设置
- **tasks.json** - 任务配置

### build/ - 构建输出目录
编译生成的中间文件和可执行文件
