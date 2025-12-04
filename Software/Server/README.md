# Pet Robot Server (基于Drogon框架)

## 项目简介

这是一个基于Drogon框架的三端互通IoT系统，包括ESP32设备端、C++服务器端和Android手机端。

## 技术栈

- **框架**: Drogon (C++异步Web框架)
- **数据库**: PostgreSQL
- **缓存**: Redis
- **构建**: CMake
- **C++标准**: C++17

## 依赖安装

### Ubuntu/Debian
```bash
sudo apt-get install git gcc g++ cmake libjsoncpp-dev uuid-dev openssl libssl-dev zlib1g-dev postgresql-all
```

### 安装Drogon
```bash
git clone https://github.com/drogonframework/drogon
cd drogon
git submodule update --init
mkdir build
cd build
cmake ..
make
sudo make install
```

## 编译

```bash
mkdir build
cd build
cmake ..
cmake --build .
```

## 配置

编辑 `config/config.json` 配置文件，修改数据库连接信息。

## 运行

```bash
./PetRobotServer
```

服务器将在 http://localhost:8080 启动。

## 生成数据库模型

使用Drogon命令行工具生成ORM模型：

```bash
cd Models
drogon_ctl create model ../config/model.json
```

## 项目结构

- `Core/` - 核心框架
- `Controllers/` - HTTP/WebSocket/TCP控制器
- `Filters/` - 过滤器（认证、权限）
- `Services/` - 业务逻辑层
- `Models/` - 数据模型（Drogon ORM）
- `Utils/` - 工具类
- `config/` - 配置文件
- `views/` - CSP模板
- `static/` - 静态资源

## 开发文档

详见 `docs/` 目录。

## License

MIT
