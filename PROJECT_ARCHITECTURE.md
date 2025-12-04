# Pet Robot 项目架构文档

## 项目概述

这是一个三端互通的IoT系统，包括ESP32设备端、C++服务器端和Android手机端。系统实现了实时视频流转发、GPS数据传输、用户管理和AI数据分析等功能。

## 整体架构

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────┐
│  ESP32设备  │────────>│   C++服务器      │<────────│  Android手机 │
│  (C++)      │  UDP视频 │                  │ WebSocket│   (Java)    │
│  + 4G模块   │  TCP控制 │  消息队列架构    │  HTTPS  │             │
│  + 摄像头   │  TCP GPS │                  │         │             │
│  + GPS      │         │                  │         │             │
└─────────────┘         └────────┬─────────┘         └─────────────┘
                                 │
                        ┌────────▼─────────┐
                        │ Python AI服务    │
                        │ (FastAPI)        │
                        │ + MCP集成        │
                        └──────────────────┘
```

## 核心设计理念

### 1. 消息队列 + 工作线程组架构

**传统方案的问题**：
- 一个连接一个线程 → 10000用户 = 10000线程 → 资源爆炸
- 线程切换开销大，性能不可控

**我们的方案**：
```
固定数量的工作线程（如16个）+ 对应数量的消息队列

网络接收 → 消息分发器（按ID hash） → 队列[N] → 工作线程[N] → Handler处理
```

**优势**：
- 资源可控：固定16个线程，不会因用户增多而线程爆炸
- 性能可预测：队列深度可监控，处理速度可统计
- 负载均衡：hash分配相对均衡
- 顺序保证：同一用户/设备的消息进入同一队列，顺序处理

### 2. 分组策略

```
设备消息：queue_index = hash(device_id) % 16
用户消息：queue_index = hash(user_id) % 16
```

同一实体的所有消息进入同一队列，避免竞争条件。

### 3. 视频流快速通道

视频流对延迟敏感，不经过消息队列：
```
UDP接收 → 查询路由表 → 直接发送到WebSocket
```

路由表在内存中维护，由其他消息更新。

## 技术栈

### C++服务器
- **框架**: Drogon（异步HTTP/WebSocket框架）
- **数据库**: PostgreSQL（用户、设备、GPS历史）
- **缓存**: Redis（会话、在线状态、消息队列）
- **加密**: OpenSSL（TLS、AES、RSA）
- **构建**: CMake

### Python AI服务
- **框架**: FastAPI
- **AI集成**: MCP服务器（支持国内外大模型）
- **功能**: 食谱分析、运动分析、健康分析

### 通信协议
- **ESP32 → 服务器**: UDP（视频）+ TCP（GPS、控制）
- **手机 → 服务器**: WebSocket（实时）+ HTTPS（API）
- **服务器 → AI**: HTTP REST

## 服务器架构分层

### 第一层：网络接收层
- **TcpReceiver**: 接收设备TCP连接（GPS、心跳、抓拍照片）
- **UdpReceiver**: 接收视频流
- **WebSocketReceiver**: 接收用户连接
- **HttpServer**: REST API + 管理后台Web界面

### 第二层：消息分发层
- **MessageDispatcher**: 根据ID hash分配消息到队列
- **MessageQueue**: 线程安全的无锁队列（16个）

### 第三层：工作线程层
- **WorkerThread**: 从队列取消息并处理（16个线程）
- 每个线程绑定一个队列

### 第四层：消息处理层
- **GpsDataHandler**: 处理GPS数据（解密、存储、推送）
- **VideoPacketHandler**: 处理视频包（重组、转发）
- **UserRequestHandler**: 处理用户请求（观看设备、停止观看）
- **DeviceControlHandler**: 处理设备控制（推流/抓拍切换）
- **HeartbeatHandler**: 处理心跳（更新在线状态）
- **TimerHandler**: 处理定时任务（批量写入、流量统计）

### 第五层：业务模块层
- **Auth**: 认证授权（JWT、密码加密）
- **Device**: 设备管理（连接池、绑定关系）
- **User**: 用户管理（会话管理、权限验证）
- **Stream**: 流媒体路由（视频转发、按需推流）
- **Gps**: GPS处理（解密、批量写入）
- **Traffic**: 流量统计（实时统计、预警）
- **Admin**: 管理后台（用户管理、设备管理、监控）
- **Ai**: AI网关（调用Python AI服务）

### 第六层：数据访问层
- **DatabaseManager**: PostgreSQL连接池
- **RedisManager**: Redis连接池
- **Models**: 数据模型（User、Device、GpsHistory等）
- **Repositories**: 数据访问对象（CRUD操作）

## 关键业务流程

### 流程1：用户观看设备视频

```
1. 手机WebSocket发送: {"action": "start_watch", "device_id": "xxx"}
   ↓
2. MessageDispatcher分发到用户对应的队列
   ↓
3. WorkerThread取出消息
   ↓
4. UserRequestHandler处理:
   - 验证设备绑定关系
   - 检查设备在线状态
   ↓
5. 生成ControlCommandMsg，分发到设备对应的队列
   ↓
6. DeviceControlHandler处理:
   - 通过TCP发送推流指令给ESP32
   ↓
7. ESP32开始UDP推流
   ↓
8. UdpReceiver接收视频包 → 直接查路由表 → 转发到用户WebSocket
```

### 流程2：GPS数据处理

```
1. ESP32每100ms通过TCP发送加密GPS数据
   ↓
2. TcpReceiver接收 → 创建GpsDataMsg → 分发到设备队列
   ↓
3. GpsDataHandler处理:
   - 解密GPS数据
   - 放入Redis缓冲队列
   - 如果用户在线，实时推送
   ↓
4. TimerHandler定时触发（每秒）:
   - 从Redis批量取出GPS数据
   - 批量INSERT到PostgreSQL
```

### 流程3：流量监控

```
1. 在关键位置埋点统计流量:
   - UDP接收视频流
   - TCP接收GPS数据
   - WebSocket发送数据
   ↓
2. TrafficCollector实时累加流量到Redis
   ↓
3. TimerHandler定时触发（每分钟）:
   - 聚合流量数据
   - 检查预警阈值
   - 每小时写入PostgreSQL
   ↓
4. 管理后台查询流量统计
```

## 数据存储设计

### Redis（热数据）
```
session:{user_id}              - 用户会话
device:online:{device_id}      - 设备在线状态
device:binding:{device_id}     - 设备绑定关系缓存
user:online:{user_id}          - 用户在线状态
gps:buffer:{device_id}         - GPS数据缓冲队列
traffic:device:{device_id}     - 实时流量统计
traffic:user:{user_id}         - 用户流量统计
```

### PostgreSQL（冷数据）
```
users                - 用户信息
devices              - 设备信息
device_bindings      - 设备绑定关系
gps_history_YYYYMM   - GPS历史轨迹（按月分表）
snapshots            - 抓拍照片
traffic_stats        - 流量历史统计（按小时聚合）
ai_analysis          - AI分析结果
operation_logs       - 操作日志
```

## 管理后台功能

### 1. 实时监控大屏
- 在线用户数、在线设备数
- 实时流量曲线
- 系统资源使用率
- 异常事件列表

### 2. 用户管理
- 用户列表（搜索、筛选、分页）
- 用户详情（设备、流量、登录历史）
- 操作：封禁、解封、删除、重置密码

### 3. 设备管理
- 设备列表（在线状态、流量）
- 设备详情（GPS轨迹、抓拍照片）
- 操作：强制下线、远程重启、解绑

### 4. 流量分析
- 流量趋势图
- 设备流量排行榜
- 用户流量排行榜
- 流量预警配置

### 5. 数据管理
- GPS轨迹查看器（地图展示）
- 抓拍照片浏览器
- AI分析结果查看
- 批量数据导出

### 6. 系统日志
- 操作日志
- 错误日志
- 访问日志

## 安全设计

### 1. 传输安全
- 所有外部通信使用TLS/SSL
- GPS数据AES-256加密
- 视频流可选加密

### 2. 认证授权
- 用户：JWT Token认证
- 设备：设备Token认证
- 管理员：权限分级（超级管理员/运维/客服）

### 3. 数据安全
- 密码bcrypt哈希存储
- GPS加密密钥安全协商
- 敏感操作记录日志

### 4. 防护措施
- API限流防DDoS
- 设备白名单机制
- 流量预警和自动断开

## 性能优化

### 1. 消息队列优化
- 使用无锁队列（boost::lockfree::queue）
- 固定线程数，避免线程爆炸
- 按ID hash分组，负载均衡

### 2. 视频流优化
- 快速通道，不经过队列
- 零拷贝转发
- UDP容忍少量丢包

### 3. 数据库优化
- GPS批量写入（每秒10条 → 批量INSERT）
- 连接池复用
- 按月分表（GPS历史）
- 索引优化

### 4. 缓存优化
- Redis缓存热数据
- 路由表内存缓存
- 减少数据库查询

## 监控指标

### 1. 队列监控
- 每个队列的深度
- 消息等待时间
- 消息处理速度

### 2. 性能监控
- CPU、内存使用率
- 数据库连接数
- Redis内存使用
- 网络带宽占用

### 3. 业务监控
- 在线用户数、设备数
- 推流设备数
- 流量统计
- 错误率

### 4. 告警
- 队列深度超阈值
- 流量超阈值
- 设备异常掉线
- 系统资源告警

## 部署架构

### 单机部署（初期）
```
一台服务器：
- C++主服务
- Python AI服务
- PostgreSQL
- Redis
```

### 扩展部署（后期）
```
负载均衡器（Nginx）
    ↓
多个C++服务实例（无状态）
    ↓
Redis集群（共享状态）
    ↓
PostgreSQL主从复制
    ↓
Python AI服务独立扩展
```

## 开发顺序建议

### 阶段1：基础框架（1-2周）
1. Core框架（Application、Config、Logger）
2. 数据访问层（DatabaseManager、RedisManager）
3. 数据模型和Repository

### 阶段2：消息队列系统（1周）
1. MessageQueue实现
2. MessageDispatcher实现
3. WorkerThread实现
4. Message类型定义

### 阶段3：网络层（1-2周）
1. TcpReceiver（设备连接）
2. UdpReceiver（视频流）
3. WebSocketReceiver（用户连接）
4. HttpServer（REST API）
5. 协议解析（DeviceProtocol、VideoPacket、GpsPacket）

### 阶段4：业务模块（2-3周）
1. Auth模块（认证授权）
2. Device模块（设备管理）
3. User模块（用户管理）
4. Stream模块（流媒体路由）
5. Gps模块（GPS处理）
6. Traffic模块（流量统计）

### 阶段5：消息处理器（1周）
1. 各种Handler实现
2. 与业务模块集成

### 阶段6：管理后台（1-2周）
1. Admin模块
2. Controllers实现
3. 前端页面开发

### 阶段7：AI服务（1周）
1. Python AI服务搭建
2. MCP集成
3. AiGateway实现

### 阶段8：测试和优化（1-2周）
1. 单元测试
2. 集成测试
3. 性能测试
4. 优化和调试

## 使用脚本生成项目结构

```bash
# 进入项目根目录
cd pet_robot

# 运行生成脚本
python scripts/generate_project.py

# 或指定目标目录
python scripts/generate_project.py /path/to/project
```

脚本会自动创建：
- 所有目录结构
- 所有.h和.cpp文件（带基本框架）
- CMakeLists.txt
- 配置文件模板
- .gitignore
- 目录结构文档

## 总结

这个架构的核心优势：
1. **消息队列架构**：资源可控，性能可预测
2. **模块化设计**：职责清晰，易于维护
3. **分层架构**：解耦合，易于扩展
4. **性能优化**：批量操作、零拷贝、快速通道
5. **完善监控**：实时监控、流量统计、告警机制
6. **安全可靠**：加密传输、权限控制、日志记录

这个架构可以支撑从小规模（几十个用户）到中等规模（几千个用户）的应用场景，并且有清晰的扩展路径。
