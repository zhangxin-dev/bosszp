-- ============================================
-- bosszp 数据库初始化脚本
-- 作用：创建数据库和所有表结构
-- 使用方法：在 MySQL 客户端执行此文件
--   方式1: mysql -u root -p < init.sql
--   方式2: 复制到 Navicat/DBeaver 中执行
-- ============================================

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS bosszp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 使用这个数据库
USE bosszp;

-- 3. 创建岗位信息表（核心表）
-- 每一行代表一个招聘岗位
CREATE TABLE IF NOT EXISTS t_job (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    job_title   VARCHAR(200)  NOT NULL COMMENT '岗位名称，如：Java开发工程师',
    company     VARCHAR(200)  DEFAULT ''   COMMENT '公司名称',
    city        VARCHAR(50)   DEFAULT ''   COMMENT '工作城市，如：北京、上海',
    salary_low  INT           DEFAULT 0    COMMENT '薪资下限（单位：千/月），如15表示15K',
    salary_high INT           DEFAULT 0    COMMENT '薪资上限（单位：千/月），如25表示25K',
    exp         VARCHAR(50)   DEFAULT ''   COMMENT '经验要求，如：1-3年、应届生',
    education   VARCHAR(50)   DEFAULT ''   COMMENT '学历要求，如：本科、大专',
    skills      VARCHAR(500)  DEFAULT ''   COMMENT '技能标签（逗号分隔），如：Java,Spring Boot,MySQL',
    jd_text     TEXT                        COMMENT '岗位职责描述（原始文本，用于NLP分析）',
    source      VARCHAR(50)   DEFAULT 'boss' COMMENT '数据来源：boss/lagou/51job',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    INDEX idx_city (city),
    INDEX idx_exp (exp),
    INDEX idx_salary (salary_low, salary_high)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IT岗位信息表';

-- 4. 创建技能统计表（分析结果存储）
-- 由 Python AI 引擎分析后写入
CREATE TABLE IF NOT EXISTS t_skill_stat (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    skill_name  VARCHAR(100) NOT NULL COMMENT '技能名称，如：Spring Boot',
    count       INT          DEFAULT 0   COMMENT '出现次数',
    avg_salary  DECIMAL(10,2) DEFAULT 0 COMMENT '平均薪资（千/月）',
    city        VARCHAR(50)  DEFAULT ''  COMMENT '关联城市',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_skill_city (skill_name, city),
    INDEX idx_count (count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能统计表';

-- 5. 插入几条示例数据（方便你开发时测试）
INSERT INTO t_job (job_title, company, city, salary_low, salary_high, exp, education, skills, jd_text) VALUES
('Java开发工程师', '某科技公司', '北京', 15, 25, '1-3年', '本科', 'Java,Spring Boot,MySQL,Redis', '负责后端服务开发，参与系统架构设计，熟悉Spring Boot框架，有微服务经验优先。'),
('Python数据分析师', '某数据公司', '上海', 12, 20, '应届生', '本科', 'Python,Pandas,NumPy,SQL', '负责业务数据分析与报表开发，熟练使用Python进行数据处理，有机器学习基础优先。'),
('前端开发工程师', '某互联网公司', '深圳', 10, 18, '1-3年', '大专', 'JavaScript,Vue,React,CSS', '负责Web前端页面开发，熟悉Vue或React框架，能与后端良好协作。'),
('算法工程师', '某AI公司', '杭州', 20, 35, '3-5年', '硕士', 'Python,TensorFlow,PyTorch,NLP', '负责推荐算法研发与优化，熟悉深度学习框架，有NLP项目经验优先。'),
('Java开发实习生', '某软件公司', '北京', 4, 6, '应届生', '本科', 'Java,MySQL,Spring', '招聘Java开发实习生，有培训体系，要求计算机相关专业，基础扎实。');

-- 执行完毕！
-- 你的 MySQL 中现在有了 bosszp 数据库和示例数据
