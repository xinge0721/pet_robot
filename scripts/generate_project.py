#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
基于Drogon框架的项目结构生成脚本
自动创建完整的C++服务器项目目录结构和基础文件
"""

import os
import sys
from pathlib import Path
from typing import List, Tuple

# 颜色输出
class Colors:
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BLUE = '\033[94m'
    END = '\033[0m'

def log_info(msg: str):
    print(f"{Colors.BLUE}[INFO]{Colors.END} {msg}")

def log_success(msg: str):
    print(f"{Colors.GREEN}[SUCCESS]{Colors.END} {msg}")

def log_warning(msg: str):
    print(f"{Colors.YELLOW}[WARNING]{Colors.END} {msg}")

def log_error(msg: str):
    print(f"{Colors.RED}[ERROR]{Colors.END} {msg}")

# 项目目录结构定义（基于Drogon）
PROJECT_STRUCTURE = {
    "Software/Server": {
        "files": ["main.cpp"],
        "subdirs": {
            "Core": {
                "subdirs": {
                    "Application": {"files": ["Application.h", "Application.cpp"]},
                    "Config": {"files": ["Config.h", "Config.cpp"]},
                    "DeviceConnectionManager": {"files": ["DeviceConnectionManager.h", "DeviceConnectionManager.cpp"]},
                }
            },
            "Controllers": {
                "subdirs": {
                    "Http": {
                        "subdirs": {
                            "UserController": {"files": ["UserController.h", "UserController.cpp"]},
                            "DeviceController": {"files": ["DeviceController.h", "DeviceController.cpp"]},
                            "AdminController": {"files": ["AdminController.h", "AdminController.cpp"]},
                            "TrafficController": {"files": ["TrafficController.h", "TrafficController.cpp"]},
                            "MonitorController": {"files": ["MonitorController.h", "MonitorController.cpp"]},
                        }
                    },
                    "WebSocket": {
                        "subdirs": {
                            "UserWsController": {"files": ["UserWsController.h", "UserWsController.cpp"]},
                            "DeviceWsController": {"files": ["DeviceWsController.h", "DeviceWsController.cpp"]},
                        }
                    },
                    "Tcp": {
                        "subdirs": {
                            "DeviceTcpServer": {"files": ["DeviceTcpServer.h", "DeviceTcpServer.cpp"]},
                            "Protocol": {
                                "subdirs": {
                                    "DeviceProtocol": {"files": ["DeviceProtocol.h", "DeviceProtocol.cpp"]},
                                    "VideoPacket": {"files": ["VideoPacket.h", "VideoPacket.cpp"]},
                                    "GpsPacket": {"files": ["GpsPacket.h", "GpsPacket.cpp"]},
                                }
                            }
                        }
                    }
                }
            },
            "Filters": {
                "subdirs": {
                    "AuthFilter": {"files": ["AuthFilter.h", "AuthFilter.cpp"]},
                    "AdminFilter": {"files": ["AdminFilter.h", "AdminFilter.cpp"]},
                }
            },
            "Services": {
                "subdirs": {
                    "Auth": {
                        "files": ["AuthService.h", "AuthService.cpp"],
                        "subdirs": {
                            "JwtHelper": {"files": ["JwtHelper.h", "JwtHelper.cpp"]},
                            "PasswordHelper": {"files": ["PasswordHelper.h", "PasswordHelper.cpp"]},
                        }
                    },
                    "Device": {
                        "files": ["DeviceService.h", "DeviceService.cpp"],
                        "subdirs": {
                            "DeviceManager": {"files": ["DeviceManager.h", "DeviceManager.cpp"]},
                        }
                    },
                    "User": {
                        "files": ["UserService.h", "UserService.cpp"],
                        "subdirs": {
                            "UserManager": {"files": ["UserManager.h", "UserManager.cpp"]},
                        }
                    },
                    "Stream": {
                        "files": ["StreamService.h", "StreamService.cpp"],
                        "subdirs": {
                            "VideoRouter": {"files": ["VideoRouter.h", "VideoRouter.cpp"]},
                            "VideoUdpServer": {"files": ["VideoUdpServer.h", "VideoUdpServer.cpp"]},
                        }
                    },
                    "Gps": {
                        "files": ["GpsService.h", "GpsService.cpp"],
                        "subdirs": {
                            "GpsDecryptor": {"files": ["GpsDecryptor.h", "GpsDecryptor.cpp"]},
                            "GpsBatchWriter": {"files": ["GpsBatchWriter.h", "GpsBatchWriter.cpp"]},
                        }
                    },
                    "Traffic": {
                        "files": ["TrafficService.h", "TrafficService.cpp"],
                        "subdirs": {
                            "TrafficCollector": {"files": ["TrafficCollector.h", "TrafficCollector.cpp"]},
                            "TrafficAlert": {"files": ["TrafficAlert.h", "TrafficAlert.cpp"]},
                        }
                    },
                    "Admin": {
                        "files": ["AdminService.h", "AdminService.cpp"],
                    },
                    "Ai": {
                        "files": ["AiService.h", "AiService.cpp"],
                        "subdirs": {
                            "AiClient": {"files": ["AiClient.h", "AiClient.cpp"]},
                        }
                    },
                }
            },
            "Models": {
                "files": ["User.h", "User.cc", "Device.h", "Device.cc",
                         "DeviceBinding.h", "DeviceBinding.cc",
                         "GpsHistory.h", "GpsHistory.cc",
                         "TrafficStats.h", "TrafficStats.cc",
                         "OperationLog.h", "OperationLog.cc"]
            },
            "Utils": {
                "subdirs": {
                    "Logger": {"files": ["Logger.h", "Logger.cpp"]},
                    "Crypto": {"files": ["Crypto.h", "Crypto.cpp"]},
                    "TimeUtil": {"files": ["TimeUtil.h", "TimeUtil.cpp"]},
                    "StringUtil": {"files": ["StringUtil.h", "StringUtil.cpp"]},
                    "RedisHelper": {"files": ["RedisHelper.h", "RedisHelper.cpp"]},
                }
            },
            "Common": {
                "files": ["Types.h", "Constants.h", "Errors.h", "Result.h"]
            },
            "test": {
                "subdirs": {
                    "unit": {"files": ["test_auth.cpp", "test_device.cpp", "test_gps.cpp", "test_traffic.cpp"]},
                    "integration": {"files": ["test_full_flow.cpp"]},
                },
                "files": ["CMakeLists.txt"]
            },
            "config": {
                "files": ["config.json", "model.json", "database.json"]
            },
            "views": {
                "files": ["index.csp", "dashboard.csp", "users.csp", "devices.csp", "traffic.csp"]
            },
            "static": {
                "subdirs": {
                    "css": {"files": ["style.css"]},
                    "js": {"files": ["main.js", "api.js"]},
                    "images": {"files": [".gitkeep"]},
                }
            },
            "scripts": {
                "files": ["init_db.sql", "start_server.sh", "stop_server.sh"]
            },
        },
        "root_files": ["CMakeLists.txt", ".gitignore"]
    },
    "Software/AI": {
        "files": ["main.py", "requirements.txt", "README.md"],
        "subdirs": {
            "api": {"files": ["__init__.py", "diet.py", "exercise.py", "health.py"]},
            "services": {"files": ["__init__.py", "diet_analyzer.py", "exercise_analyzer.py", "health_analyzer.py"]},
            "mcp": {
                "files": ["__init__.py", "mcp_client.py"],
                "subdirs": {"prompts": {"files": [".gitkeep"]}}
            },
            "utils": {"files": ["__init__.py", "logger.py"]},
            "test": {"files": ["test_api.py"]},
            "config": {"files": ["ai_config.json"]},
        }
    },
    "Hardware/ESP32": {
        "files": [".gitkeep"]
    },
    "docs": {
        "files": [".gitkeep"]
    }
}

def get_header_guard(file_path: str) -> str:
    """生成头文件保护宏"""
    parts = Path(file_path).parts
    if "Software" in parts:
        idx = parts.index("Software")
        relevant_parts = parts[idx+2:]
    else:
        relevant_parts = parts

    guard = "_".join(relevant_parts).upper().replace(".", "_")
    return guard

def generate_header_file(file_path: Path, module_name: str) -> str:
    """生成.h文件内容"""
    guard = get_header_guard(str(file_path))

    # 判断是否是Controller
    is_http_controller = "Controllers/Http" in str(file_path)
    is_ws_controller = "Controllers/WebSocket" in str(file_path)

    if is_http_controller:
        content = f"""#ifndef {guard}
#define {guard}

#include <drogon/HttpController.h>

using namespace drogon;

namespace PetRobot {{

// {module_name} HTTP控制器
class {module_name} : public drogon::HttpController<{module_name}> {{
public:
    METHOD_LIST_BEGIN
    // TODO: 添加路由映射
    // ADD_METHOD_TO({module_name}::methodName, "/api/v1/path", Post);
    METHOD_LIST_END

    // TODO: 添加处理方法
    // void methodName(const HttpRequestPtr& req, std::function<void(const HttpResponsePtr&)>&& callback);

private:
    // TODO: 添加私有成员
}};

}} // namespace PetRobot

#endif // {guard}
"""
    elif is_ws_controller:
        content = f"""#ifndef {guard}
#define {guard}

#include <drogon/WebSocketController.h>

using namespace drogon;

namespace PetRobot {{

// {module_name} WebSocket控制器
class {module_name} : public drogon::WebSocketController<{module_name}> {{
public:
    void handleNewMessage(const WebSocketConnectionPtr& conn,
                         std::string&& message,
                         const WebSocketMessageType& type) override;

    void handleNewConnection(const HttpRequestPtr& req,
                            const WebSocketConnectionPtr& conn) override;

    void handleConnectionClosed(const WebSocketConnectionPtr& conn) override;

    WS_PATH_LIST_BEGIN
    // TODO: 添加WebSocket路径
    // WS_PATH_ADD("/ws/path", "FilterName");
    WS_PATH_LIST_END

private:
    // TODO: 添加私有成员
}};

}} // namespace PetRobot

#endif // {guard}
"""
    else:
        content = f"""#ifndef {guard}
#define {guard}

// {module_name} 模块头文件

namespace PetRobot {{

class {module_name} {{
public:
    {module_name}();
    ~{module_name}();

    // TODO: 添加公共接口

private:
    // TODO: 添加私有成员
}};

}} // namespace PetRobot

#endif // {guard}
"""
    return content

def generate_cpp_file(file_path: Path, module_name: str, header_name: str) -> str:
    """生成.cpp文件内容"""
    is_http_controller = "Controllers/Http" in str(file_path)
    is_ws_controller = "Controllers/WebSocket" in str(file_path)

    if is_http_controller:
        content = f"""#include "{header_name}"
#include <drogon/drogon.h>

using namespace drogon;

namespace PetRobot {{

// TODO: 实现HTTP处理方法

}} // namespace PetRobot
"""
    elif is_ws_controller:
        content = f"""#include "{header_name}"
#include <drogon/drogon.h>

using namespace drogon;

namespace PetRobot {{

void {module_name}::handleNewMessage(const WebSocketConnectionPtr& conn,
                                     std::string&& message,
                                     const WebSocketMessageType& type) {{
    // TODO: 处理WebSocket消息
}}

void {module_name}::handleNewConnection(const HttpRequestPtr& req,
                                       const WebSocketConnectionPtr& conn) {{
    // TODO: 处理新连接
}}

void {module_name}::handleConnectionClosed(const WebSocketConnectionPtr& conn) {{
    // TODO: 处理连接关闭
}}

}} // namespace PetRobot
"""
    else:
        content = f"""#include "{header_name}"

// {module_name} 模块实现

namespace PetRobot {{

{module_name}::{module_name}() {{
    // TODO: 构造函数实现
}}

{module_name}::~{module_name}() {{
    // TODO: 析构函数实现
}}

}} // namespace PetRobot
"""
    return content

def generate_main_cpp() -> str:
    """生成main.cpp内容"""
    content = """#include <drogon/drogon.h>
#include "Core/Application/Application.h"

using namespace drogon;
using namespace PetRobot;

int main() {
    try {
        // 加载配置文件
        app().loadConfigFile("./config/config.json");

        // 初始化应用程序
        Application::getInstance().init();

        // 启动Drogon框架
        LOG_INFO << "Starting Pet Robot Server...";
        app().run();

    } catch (const std::exception& e) {
        LOG_ERROR << "Fatal error: " << e.what();
        return 1;
    }

    return 0;
}
"""
    return content

def generate_cmake() -> str:
    """生成CMakeLists.txt内容"""
    content = """cmake_minimum_required(VERSION 3.15)
project(PetRobotServer CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 编译选项
if(MSVC)
    add_compile_options(/W4 /utf-8)
else()
    add_compile_options(-Wall -Wextra -pedantic)
endif()

# 查找Drogon框架
find_package(Drogon CONFIG REQUIRED)

# 包含目录
include_directories(${PROJECT_SOURCE_DIR})

# 递归收集所有源文件
file(GLOB_RECURSE SOURCES
    Core/**/*.cpp
    Controllers/**/*.cpp
    Filters/**/*.cpp
    Services/**/*.cpp
    Models/*.cc
    Utils/**/*.cpp
)

# 可执行文件
add_executable(${PROJECT_NAME} main.cpp ${SOURCES})

# 链接Drogon
target_link_libraries(${PROJECT_NAME} PRIVATE Drogon::Drogon)

# 测试
enable_testing()
add_subdirectory(test)

# 安装
install(TARGETS ${PROJECT_NAME} DESTINATION bin)
install(DIRECTORY config/ DESTINATION etc/pet_robot)
install(DIRECTORY views/ DESTINATION share/pet_robot/views)
install(DIRECTORY static/ DESTINATION share/pet_robot/static)
"""
    return content

def generate_gitignore() -> str:
    """生成.gitignore内容"""
    content = """# 编译输出
build/
cmake-build-*/
*.o
*.obj
*.exe
*.out
*.app

# IDE
.vscode/
.idea/
*.swp
*.swo
*~

# 日志
logs/
*.log

# 上传文件
uploads/

# Drogon生成的文件
drogon_ctl/

# 配置文件（敏感信息）
config/*.local.json

# 依赖
third_party/

# 其他
.DS_Store
Thumbs.db
"""
    return content

def generate_drogon_config() -> str:
    """生成Drogon配置文件"""
    content = """{
  "app": {
    "threads_num": 16,
    "enable_session": true,
    "session_timeout": 3600,
    "document_root": "./static",
    "upload_path": "./uploads",
    "max_connections": 100000,
    "max_connections_per_ip": 0,
    "load_libs": [],
    "log": {
      "log_path": "./logs",
      "logfile_base_name": "server",
      "log_size_limit": 100000000,
      "log_level": "INFO"
    },
    "run_as_daemon": false,
    "relaunch_on_error": false,
    "use_sendfile": true,
    "use_gzip": true,
    "static_files_cache_time": 5,
    "idle_connection_timeout": 60,
    "server_header_field": "PetRobotServer",
    "keepalive_requests": 0,
    "pipelining_requests": 0,
    "enable_server_header": true,
    "enable_date_header": true
  },
  "listeners": [
    {
      "address": "0.0.0.0",
      "port": 8080,
      "https": false
    }
  ],
  "db_clients": [
    {
      "name": "default",
      "rdbms": "postgresql",
      "host": "127.0.0.1",
      "port": 5432,
      "dbname": "pet_robot",
      "user": "postgres",
      "passwd": "password",
      "is_fast": true,
      "connection_number": 10,
      "timeout": 10,
      "client_encoding": "utf8"
    }
  ],
  "redis_clients": [
    {
      "name": "default",
      "host": "127.0.0.1",
      "port": 6379,
      "passwd": "",
      "db": 0,
      "is_fast": true,
      "connection_number": 10,
      "timeout": 5
    }
  ],
  "plugins": []
}
"""
    return content

def generate_model_config() -> str:
    """生成Drogon ORM模型配置"""
    content = """{
  "rdbms": "postgresql",
  "host": "127.0.0.1",
  "port": 5432,
  "dbname": "pet_robot",
  "user": "postgres",
  "passwd": "password",
  "tables": [
    "users",
    "devices",
    "device_bindings",
    "gps_history",
    "traffic_stats",
    "operation_logs"
  ],
  "convert_method": "lower",
  "restful_api_controllers": {
    "enabled": false
  }
}
"""
    return content

def generate_database_config() -> str:
    """生成数据库配置"""
    content = """{
  "host": "localhost",
  "port": 5432,
  "dbname": "pet_robot",
  "user": "postgres",
  "password": "password",
  "pool_size": 10,
  "timeout": 30
}
"""
    return content

def create_directory(path: Path):
    """创建目录"""
    if not path.exists():
        path.mkdir(parents=True, exist_ok=True)
        log_success(f"Created directory: {path}")
    else:
        log_warning(f"Directory already exists: {path}")

def create_file(path: Path, content: str = ""):
    """创建文件"""
    if not path.exists():
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        log_success(f"Created file: {path}")
    else:
        log_warning(f"File already exists: {path}")

def process_structure(base_path: Path, structure: dict, current_path: str = ""):
    """递归处理目录结构"""
    # 处理当前目录的文件
    if "files" in structure:
        for file_name in structure["files"]:
            file_path = base_path / file_name

            # 生成文件内容
            content = ""
            if file_name == "main.cpp":
                content = generate_main_cpp()
            elif file_name == "CMakeLists.txt" and "test" not in str(base_path):
                content = generate_cmake()
            elif file_name == ".gitignore":
                content = generate_gitignore()
            elif file_name == "config.json":
                content = generate_drogon_config()
            elif file_name == "model.json":
                content = generate_model_config()
            elif file_name == "database.json":
                content = generate_database_config()
            elif file_name.endswith(".h"):
                module_name = file_name[:-2]
                content = generate_header_file(file_path, module_name)
            elif file_name.endswith(".cpp") and file_name != "main.cpp":
                module_name = file_name[:-4]
                header_name = f"{module_name}.h"
                content = generate_cpp_file(file_path, module_name, header_name)
            elif file_name.endswith(".cc"):
                # Drogon ORM生成的文件，暂时留空
                content = f"// {file_name} - Drogon ORM generated file\n// Use: drogon_ctl create model <model_name>\n"

            create_file(file_path, content)

    # 处理root_files
    if "root_files" in structure:
        for file_name in structure["root_files"]:
            file_path = base_path / file_name
            content = ""
            if file_name == "CMakeLists.txt":
                content = generate_cmake()
            elif file_name == ".gitignore":
                content = generate_gitignore()
            create_file(file_path, content)

    # 递归处理子目录
    if "subdirs" in structure:
        for subdir_name, subdir_structure in structure["subdirs"].items():
            subdir_path = base_path / subdir_name
            create_directory(subdir_path)
            process_structure(subdir_path, subdir_structure, f"{current_path}/{subdir_name}")

def generate_readme(root_path: Path):
    """生成README文档"""
    readme_content = """# Pet Robot Server (基于Drogon框架)

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
"""
    readme_path = root_path / "Software" / "Server" / "README.md"
    create_file(readme_path, readme_content)
    log_success(f"Generated README: {readme_path}")

def main():
    """主函数"""
    if len(sys.argv) > 1:
        root_path = Path(sys.argv[1])
    else:
        root_path = Path.cwd()

    log_info(f"Project root: {root_path}")
    log_info("Starting Drogon-based project structure generation...")

    # 创建项目结构
    for base_dir, structure in PROJECT_STRUCTURE.items():
        base_path = root_path / base_dir
        create_directory(base_path)
        process_structure(base_path, structure, base_dir)

    # 生成README
    generate_readme(root_path)

    log_success("Project structure generation completed!")
    log_info(f"Next steps:")
    log_info("  1. Install Drogon framework")
    log_info("  2. Configure database in config/config.json")
    log_info("  3. Run: mkdir build && cd build && cmake .. && make")
    log_info("  4. Generate ORM models: cd Models && drogon_ctl create model ../config/model.json")

if __name__ == "__main__":
    main()
