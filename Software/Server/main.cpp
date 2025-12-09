  #include <spdlog/spdlog.h>
  #include <spdlog/sinks/basic_file_sink.h>
  #include <string.h>
  #include <iostream>
  #include "./utils/logger/logger.h"
  int main()
  {
    Log::init("my_app.log"); // 初始化日志

    LOG_INFO("程序启动");
    int x = 42;
    // LOG_DEBUG("x 的值为: {}", x);
    LOG_ERROR("错误示例");

    return 0;

  }
