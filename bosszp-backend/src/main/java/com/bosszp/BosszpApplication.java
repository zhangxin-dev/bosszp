package com.bosszp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================
 * bosszp-backend 启动类
 * 这是整个 Spring Boot 程序的入口
 *
 * 启动方法：
 *   方式1（IDEA）：右键此类 → Run 'BosszpApplication'
 *   方式2（命令行）：mvn spring-boot:run
 *   方式3（命令行）：mvn package → java -jar target/bosszp-backend-1.0.0.jar
 *
 * 启动后访问：
 *   http://localhost:8080          → 数据大屏（index.html）
 *   http://localhost:8080/api/jobs → 岗位数据 API
 *   http://localhost:8080/api/ai/analysis → 调用 Python AI 分析
 * ============================================
 */
@SpringBootApplication
public class BosszpApplication {

    public static void main(String[] args) {
        SpringApplication.run(BosszpApplication.class, args);
        System.out.println("========================================");
        System.out.println("🚀 bosszp-backend 启动成功！");
        System.out.println("   数据大屏：http://localhost:8080");
        System.out.println("   岗位API：http://localhost:8080/api/jobs");
        System.out.println("   AI分析：http://localhost:8080/api/ai/analysis");
        System.out.println("========================================");
    }
}
