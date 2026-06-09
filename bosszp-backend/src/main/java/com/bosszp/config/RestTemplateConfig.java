package com.bosszp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * ============================================
 * RestTemplate 配置类
 *
 * RestTemplate 是什么？
 *   它是 Spring 提供的 HTTP 客户端工具，
 *   用来发送 HTTP 请求到其他服务（就像浏览器访问网站一样）。
 *
 * 在本项目中：
 *   Java (8080) → RestTemplate → HTTP GET/POST → Python Flask (5000)
 *
 * 示例：
 *   restTemplate.getForObject("http://localhost:5000/api/health", String.class);
 *   相当于用浏览器访问 http://localhost:5000/api/health
 * ============================================
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 创建并注册 RestTemplate Bean
     *
     * @Bean：Spring 会自动管理这个对象，在需要的地方自动注入
     *
     * 使用方式（在 Controller 或 Service 中）：
     *   @Autowired
     *   private RestTemplate restTemplate;
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
