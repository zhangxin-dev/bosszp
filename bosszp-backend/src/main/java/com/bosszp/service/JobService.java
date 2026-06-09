package com.bosszp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bosszp.mapper.JobMapper;
import com.bosszp.model.Job;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================
 * 岗位业务逻辑层
 * 作用：
 *   1. 处理业务逻辑（查询条件组合、数据转换等）
 *   2. 调用 JobMapper 操作数据库
 *   3. 返回处理后的数据给 Controller
 *
 * Controller → Service → Mapper → Database
 * 这样分层的好处：Controller 只管接收请求和返回响应，
 * Service 只管业务逻辑，Mapper 只管数据库操作。
 * 每层职责清晰，改一处不会影响其他层。
 * ============================================
 */
@Service
public class JobService {

    // Spring 自动注入 JobMapper（因为加了 @Mapper 注解）
    private final JobMapper jobMapper;

    public JobService(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    /**
     * 条件查询岗位列表
     *
     * @param city   城市（可选），传 null 表示不限
     * @param exp    经验要求（可选），传 null 表示不限
     * @param keyword 关键词（可选），按岗位名称模糊搜索
     * @param page   页码，从1开始
     * @param size   每页条数
     * @return 分页结果
     */
    public Map<String, Object> searchJobs(String city, String exp, String keyword, int page, int size) {
        // 构建查询条件
        QueryWrapper<Job> wrapper = new QueryWrapper<>();

        // 如果传了城市参数，加上城市条件
        if (city != null && !city.isEmpty()) {
            wrapper.eq("city", city);
        }
        // 如果传了经验参数，加上经验条件
        if (exp != null && !exp.isEmpty()) {
            wrapper.eq("exp", exp);
        }
        // 如果传了关键词，按岗位名称模糊搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("job_title", keyword);
        }

        // 按薪资降序排列
        wrapper.orderByDesc("salary_high");

        // 查询总记录数
        long total = jobMapper.selectCount(wrapper);

        // 分页查询：计算偏移量
        int offset = (page - 1) * size;
        wrapper.last("LIMIT " + offset + ", " + size);
        List<Job> list = jobMapper.selectList(wrapper);

        // 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("list", list);
        return result;
    }

    /**
     * 获取所有不重复的城市列表（前端下拉框用）
     */
    public List<String> getAllCities() {
        return jobMapper.countByCity().stream()
                .map(row -> (String) row.get("city"))
                .toList();
    }

    /**
     * 获取数据概览（大屏顶部卡片用）
     * 返回：岗位总数、平均薪资、城市数等
     */
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        long totalJobs = jobMapper.countAll();
        overview.put("totalJobs", totalJobs);

        // 查询平均薪资
        QueryWrapper<Job> wrapper = new QueryWrapper<>();
        wrapper.select("AVG(salary_low) as avg_low, AVG(salary_high) as avg_high");
        // 简化处理：用所有数据的平均薪资
        List<Job> topJobs = jobMapper.findTopSalary(1);
        if (!topJobs.isEmpty()) {
            overview.put("highestSalary", topJobs.get(0).getSalaryHigh());
        }

        // 城市数量
        long cityCount = jobMapper.countByCity().size();
        overview.put("cityCount", cityCount);

        return overview;
    }

    /**
     * 按技能搜索（调用 Mapper 的自定义 SQL）
     */
    public List<Job> findBySkill(String skill) {
        return jobMapper.findBySkill(skill);
    }
}
