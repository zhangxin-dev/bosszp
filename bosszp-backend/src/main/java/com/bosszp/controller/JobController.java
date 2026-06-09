package com.bosszp.controller;

import com.bosszp.model.Job;
import com.bosszp.service.JobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================
 * 岗位数据 REST API 控制器
 *
 * 所有以 /api 开头的请求都由这个类处理
 *
 * 注解说明：
 *   @RestController = @Controller + @ResponseBody
 *     → 所有方法的返回值自动转为 JSON
 *   @RequestMapping("/api") → 类级别的路径前缀
 *   @GetMapping → 处理 HTTP GET 请求
 *   @RequestParam → 获取 URL 参数（?key=value）
 *   @CrossOrigin → 允许前端跨域请求
 * ============================================
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // 允许任何来源的前端页面调用
public class JobController {

    private final JobService jobService;
    private final RestTemplate restTemplate;

    // application.yml 中的配置值
    @Value("${bosszp.ai.url}")
    private String aiUrl;  // = "http://localhost:5000/api"

    // 构造方法注入（推荐做法，比 @Autowired 更清晰）
    public JobController(JobService jobService, RestTemplate restTemplate) {
        this.jobService = jobService;
        this.restTemplate = restTemplate;
    }

    /**
     * ============================================
     * 接口1：查询岗位列表
     * 请求：GET /api/jobs?city=北京&exp=应届生&keyword=Java&page=1&size=10
     * 返回：{"total": 100, "page": 1, "list": [...]}
     * ============================================
     */
    @GetMapping("/jobs")
    public Map<String, Object> getJobs(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String exp,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return jobService.searchJobs(city, exp, keyword, page, size);
    }

    /**
     * ============================================
     * 接口2：获取数据概览（大屏顶部卡片）
     * 请求：GET /api/jobs/overview
     * ============================================
     */
    @GetMapping("/jobs/overview")
    public Map<String, Object> getOverview() {
        return jobService.getOverview();
    }

    /**
     * ============================================
     * 接口3：按技能搜索岗位
     * 请求：GET /api/jobs/by-skill?skill=Spring Boot
     * ============================================
     */
    @GetMapping("/jobs/by-skill")
    public List<Job> getBySkill(@RequestParam String skill) {
        return jobService.findBySkill(skill);
    }

    /**
     * ============================================
     * 接口4：获取城市列表（前端下拉框用）
     * 请求：GET /api/jobs/cities
     * 返回：["北京","上海","深圳","杭州"]
     * ============================================
     */
    @GetMapping("/jobs/cities")
    public List<String> getCities() {
        return jobService.getAllCities();
    }

    /**
     * ============================================
     * 接口5：【核心接口】调用 Python AI 分析引擎
     *
     * 这是 Java ↔ Python 通信的关键接口！
     *
     * 流程：
     *   1. 前端/用户请求 GET /api/ai/analysis
     *   2. Java 通过 RestTemplate 发 HTTP 请求给 Python Flask
     *   3. Python 执行分析，返回 JSON 结果
     *   4. Java 把结果返回给前端
     *
     * 请求：GET /api/ai/analysis
     * 返回：Python 分析引擎的完整分析结果（JSON）
     * ============================================
     */
    @GetMapping("/ai/analysis")
    public Map<String, Object> getAiAnalysis() {
        String pythonApiUrl = aiUrl + "/analysis/full";
        System.out.println("[INFO] 正在调用 Python AI 引擎: " + pythonApiUrl);

        try {
            // 核心：Java 调用 Python 的关键代码
            // restTemplate.getForObject(url, 返回值类型.class)
            // 这行代码会发出 HTTP GET 请求到 Flask，然后把 JSON 响应转成 Map
            Map<String, Object> result = restTemplate.getForObject(pythonApiUrl, Map.class);
            System.out.println("[INFO] Python AI 引擎返回成功");
            return result;
        } catch (Exception e) {
            System.err.println("[ERROR] Python AI 引擎调用失败: " + e.getMessage());
            System.err.println("[TIP] 请确保 Flask 服务已启动: python app.py");

            // 如果 Python 没启动，返回友好的错误提示
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 500);
            errorResult.put("message", "Python AI 引擎未启动，请先运行 bosszp-ai/app.py");
            errorResult.put("detail", e.getMessage());
            return errorResult;
        }
    }

    /**
     * ============================================
     * 接口6：健康检查（确认 Java 服务正常）
     * 请求：GET /api/health
     * ============================================
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "ok");
        result.put("service", "bosszp-backend");
        return result;
    }
}
