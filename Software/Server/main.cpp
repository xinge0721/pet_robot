  #include <spdlog/spdlog.h>
  #include <spdlog/sinks/basic_file_sink.h>
  #include <string.h>
  #include <iostream>
  #include "./utils/logger/logger.h"
  int main()
  {
    // 初始化全局logger（只需要在程序启动时调用一次）
    // 参数：日志配置文件的路径
    initLogger("/home/xinge/warehouse/pet_robot/Software/Server/data/config/logger.json");

    // 使用全局logger输出日志
    // Logger是一个全局指针，可以在程序的任何地方使用
    Logger->info("nihao");
    Logger->warn("cuowu");
    return 0;
  }
