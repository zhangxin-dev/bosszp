package com.bosszp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * ============================================
 * 岗位实体类
 * 对应数据库中的 t_job 表
 *
 * MyBatis-Plus 会自动映射：
 *   类名 Job → 表名 t_job（因为 application.yml 中配置了 prefix: t_）
 *   字段名 jobTitle → 列名 job_title（驼峰自动转下划线）
 *
 * Lombok @Data 自动生成：getter、setter、toString、equals、hashCode
 * ============================================
 */
@TableName("t_job")    // 映射表名
public class Job {

    @TableId(type = IdType.AUTO)  // 主键自增
    private Long id;

    private String jobTitle;       // 岗位名称
    private String company;        // 公司名称
    private String city;           // 工作城市
    private Integer salaryLow;     // 薪资下限（千/月）
    private Integer salaryHigh;    // 薪资上限（千/月）
    private String exp;            // 经验要求
    private String education;      // 学历要求
    private String skills;         // 技能标签（逗号分隔）
    private String jdText;         // 岗位描述文本
    private String source;         // 数据来源
    private LocalDateTime createTime;  // 入库时间

    // ===== 手动写 getter/setter（不用 Lombok 的话） =====
    // 如果你 IDEA 安装了 Lombok 插件，可以用 @Data 替代这些
    // 没装的话这里就是完整的 getter/setter

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Integer getSalaryLow() { return salaryLow; }
    public void setSalaryLow(Integer salaryLow) { this.salaryLow = salaryLow; }

    public Integer getSalaryHigh() { return salaryHigh; }
    public void setSalaryHigh(Integer salaryHigh) { this.salaryHigh = salaryHigh; }

    public String getExp() { return exp; }
    public void setExp(String exp) { this.exp = exp; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getJdText() { return jdText; }
    public void setJdText(String jdText) { this.jdText = jdText; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
