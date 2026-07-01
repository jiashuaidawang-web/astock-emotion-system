package com.astock.app.controller;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用健康检查接口。
 *
 * <p>用于 Docker、Nginx、运维脚本快速判断后端应用是否存活。
 */
@RestController
public class HealthController {

  /**
   * 返回后端应用存活状态。
   *
   * @return 存活状态、服务名和服务器当前时间
   */
  @GetMapping("/health")
  public Map<String, Object> health() {
    return Map.of(
      "status", "UP",
      "service", "astock-emotion-system",
      "time", LocalDateTime.now().toString()
    );
  }
}
