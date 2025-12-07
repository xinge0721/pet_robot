#!/bin/bash
# Pet Robot Server 依赖安装脚本

set -e

echo "=========================================="
echo "开始安装 Pet Robot Server 依赖"
echo "=========================================="

# 更新包列表
echo "步骤 1/4: 更新apt包列表..."
sudo apt-get update

# 安装基础依赖
echo "步骤 2/4: 安装基础依赖包..."
sudo apt-get install -y \
    git \
    gcc \
    g++ \
    cmake \
    libjsoncpp-dev \
    uuid-dev \
    openssl \
    libssl-dev \
    zlib1g-dev \
    sqlite3 \
    libsqlite3-dev \
    redis-server

# 启动Redis服务
echo "步骤 3/4: 启动Redis服务..."
sudo systemctl start redis-server
sudo systemctl enable redis-server

# 验证Redis
echo "验证Redis安装..."
redis-cli ping

# 验证SQLite
echo "验证SQLite安装..."
sqlite3 --version

echo "步骤 4/4: 准备安装Drogon框架..."
echo "Drogon需要从源码编译，这可能需要10-20分钟"

# 检查是否已经下载了Drogon
if [ -d "/tmp/drogon" ]; then
    echo "发现已存在的Drogon源码目录，删除旧版本..."
    rm -rf /tmp/drogon
fi

# 克隆Drogon仓库
echo "克隆Drogon仓库..."
cd /tmp
git clone https://github.com/drogonframework/drogon
cd drogon
git submodule update --init

# 编译Drogon
echo "开始编译Drogon（这可能需要一些时间）..."
mkdir -p build
cd build
cmake ..
make -j$(nproc)

# 安装Drogon
echo "安装Drogon到系统..."
sudo make install

# 更新动态链接库缓存
sudo ldconfig

echo "=========================================="
echo "所有依赖安装完成！"
echo "=========================================="
echo ""
echo "已安装的组件："
echo "  - GCC/G++ 编译器"
echo "  - CMake 构建工具"
echo "  - SQLite3 数据库"
echo "  - Redis 缓存服务"
echo "  - Drogon Web框架"
echo "  - 其他必要的开发库"
echo ""
echo "下一步："
echo "  1. 返回项目目录: cd /home/xinge/warehouse/pet_robot/Software/Server"
echo "  2. 创建数据库目录: mkdir -p data"
echo "  3. 编译项目: mkdir -p build && cd build && cmake .. && cmake --build ."
echo ""
