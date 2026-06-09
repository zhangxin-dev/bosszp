package com.bosszp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bosszp.model.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * ============================================
 * 岗位数据访问层（Mapper）
 * 负责和数据库打交道：增删改查
 *
 * 继承 BaseMapper<Job> 后自带以下方法（不用写 SQL）：
 *   - insert(Job)     插入一条记录
 *   - selectById(id)  根据ID查询
 *   - selectList(...) 条件查询
 *   - updateById(Job) 根据ID更新
 *   - deleteById(id)  根据ID删除
 *
 * 自定义 SQL 写在下面（用 @Select 注解）
 * ============================================
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 按城市统计岗位数量（自定义 SQL）
     * 返回示例：[{"city":"北京","count":150}, {"city":"上海","count":120}]
     */
    @Select("SELECT city, COUNT(*) AS count FROM t_job GROUP BY city ORDER BY count DESC")
    List<Map<String, Object>> countByCity();

    /**
     * 按技能关键词搜索岗位
     * 参数：skill → 比如 "Spring Boot"
     * SQL 解释：skills LIKE '%Spring Boot%' 模糊匹配
     */
    @Select("SELECT * FROM t_job WHERE skills LIKE CONCAT('%', #{skill}, '%') ORDER BY salary_high DESC LIMIT 20")
    List<Job> findBySkill(String skill);

    /**
     * 查询薪资最高的前 N 个岗位
     */
    @Select("SELECT * FROM t_job ORDER BY salary_high DESC LIMIT #{limit}")
    List<Job> findTopSalary(int limit);

    /**
     * 统计数据库中的总记录数
     */
    @Select("SELECT COUNT(*) FROM t_job")
    long countAll();
}
