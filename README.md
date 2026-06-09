# 📊 职数洞见 — IT行业岗位画像与职业数据透视平台

> **哈尔滨华德学院 · 大学生创新训练计划项目（A类）**
>
> Java Spring Boot + Python Flask AI 引擎 + ECharts 可视化大屏

---

## 🏗️ 项目架构

```
┌──────────────────────────────────────────────────────────┐
│                       浏览器                              │
│              http://localhost:8080 (ECharts大屏)          │
└──────────────┬──────────────────────────────┬────────────┘
               │                              │
        ┌──────▼──────┐               ┌──────▼──────────┐
        │ Spring Boot  │   HTTP调用    │  Python Flask   │
        │  后端服务     │◄────────────►│  AI分析引擎       │
        │  port:8080   │  JSON通信     │  port:5000       │
        │              │               │                  │
        │  业务逻辑     │               │  jieba分词        │
        │  数据CRUD    │               │  TF-IDF特征提取    │
        │  用户管理     │               │  薪资统计分析      │
        │  RESTful API │               │  技能词云数据      │
        └──────┬──────┘               └──────────────────┘
               │ MySQL
        ┌──────▼──────┐
        │   MySQL      │
        │   bosszp库    │
        │   t_job表     │
        └──────────────┘
```

**核心设计思想**：Java 负责业务逻辑和数据管理，Python 负责智能分析。两者通过 HTTP API 通信，各司其职，互不干扰。

---

## 📁 项目目录结构

```
bosszp/
│
├── bosszp-backend/                 # 【模块1】Java Spring Boot 后端
│   ├── pom.xml                     #   Maven 依赖配置
│   └── src/main/
│       ├── java/com/bosszp/
│       │   ├── BosszpApplication.java      #   启动类
│       │   ├── controller/
│       │   │   └── JobController.java      #   REST API 控制器
│       │   ├── service/
│       │   │   └── JobService.java         #   业务逻辑层
│       │   ├── model/
│       │   │   └── Job.java                #   岗位实体类
│       │   ├── mapper/
│       │   │   └── JobMapper.java          #   数据库操作层
│       │   └── config/
│       │       └── RestTemplateConfig.java #   HTTP客户端配置
│       └── resources/
│           ├── application.yml             #   应用配置文件
│           └── static/
│               └── index.html              #   ECharts数据大屏
│
├── bosszp-ai/                      # 【模块2】Python Flask AI引擎
│   ├── app.py                      #   Flask服务主程序（API接口）
│   ├── analyzer.py                 #   数据分析引擎（核心算法）
│   ├── requirements.txt            #   Python依赖清单
│   └── data/
│       └── sample_jobs.csv         #   示例数据（10条岗位信息）
│
├── sql/
│   └── init.sql                    # 【模块3】数据库初始化脚本
│
└── README.md                       #   你正在看的这个文件
```

---

## 🚀 启动步骤（按顺序执行）

### 第一步：安装环境

你需要先安装以下软件（如果还没装的话）：

| 软件 | 版本要求 | 下载地址 | 检查方法 |
|------|---------|---------|---------|
| **JDK** | 17+ | https://adoptium.net/ | `java -version` |
| **Maven** | 3.8+ | https://maven.apache.org/ | `mvn -version` |
| **MySQL** | 8.0+ | https://dev.mysql.com/ | `mysql -u root -p` |
| **Python** | 3.10+ | https://www.python.org/ | `python --version` |
| **IDEA** | 社区版 | https://www.jetbrains.com/idea/ | 开发Java用 |
| **VS Code** | 最新版 | https://code.visualstudio.com/ | 开发Python用（可选） |

### 第二步：初始化数据库

1. 打开 MySQL 客户端（命令行或 Navicat/DBeaver）
2. 执行 `sql/init.sql`：

```bash
# 命令行方式
mysql -u root -p < sql/init.sql

# 或者在 MySQL 客户端中
source D:/OneDrive/Desktop/bosszp/sql/init.sql;
```

3. 验证：`mysql -u root -p -e "SELECT * FROM bosszp.t_job;"` 应该能看到5条示例数据

### 第三步：启动 Python Flask AI 引擎

```bash
# 1. 进入 Python 模块目录
cd bosszp-ai

# 2. (推荐) 创建虚拟环境
python -m venv venv
venv\Scripts\activate          # Windows

# 3. 安装依赖（可能需要几分钟，首次安装）
pip install -r requirements.txt

# 4. 测试分析引擎（确认 analyzer.py 没问题）
python analyzer.py

# 5. 启动 Flask 服务
python app.py
```

看到以下输出表示成功：
```
🚀 bosszp-ai Flask 分析引擎启动中...
   地址：http://localhost:5000
   健康检查：http://localhost:5000/api/health
   全量分析：http://localhost:5000/api/analysis/full
```

6. 打开浏览器访问 `http://localhost:5000/api/health`，确认能看到 `{"status":"ok"}`

### 第四步：启动 Java Spring Boot 后端

1. **先修改配置文件**：打开 `bosszp-backend/src/main/resources/application.yml`
2. 找到这两行，改成你自己的 MySQL 密码：
```yaml
spring:
  datasource:
    username: root         # ← 改成你的 MySQL 用户名
    password: 123456       # ← 改成你的 MySQL 密码
```

3. 用 IDEA 打开 `bosszp-backend` 目录（Open → 选择 bosszp-backend 文件夹）
4. IDEA 会自动识别 Maven 项目，等待右下角加载完成
5. 找到 `BosszpApplication.java`，右键 → Run
6. 看到以下输出表示成功：
```
🚀 bosszp-backend 启动成功！
   数据大屏：http://localhost:8080
   岗位API：http://localhost:8080/api/jobs
   AI分析：http://localhost:8080/api/ai/analysis
```

### 第五步：打开数据大屏

浏览器访问：**`http://localhost:8080`**

你应该能看到完整的 ECharts 数据大屏：薪资分布图、城市岗位图、技能词云、经验要求饼图。

---

## 🔗 接口说明

### Java 后端接口（port 8080）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/jobs` | 查询岗位列表（支持 city/exp/keyword/page/size 参数） |
| GET | `/api/jobs/overview` | 数据概览（岗位总数等） |
| GET | `/api/jobs/by-skill` | 按技能搜索岗位 |
| GET | `/api/jobs/cities` | 获取城市列表 |
| GET | `/api/ai/analysis` | **核心：调用 Python AI 分析引擎** |
| GET | `/api/health` | Java 服务健康检查 |

### Python AI 接口（port 5000）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/health` | Flask 服务健康检查 |
| GET | `/api/analysis/full` | 执行全量分析（最常用） |
| GET | `/api/analysis/skills` | 单独执行技能分析 |
| GET | `/api/analysis/salary` | 单独执行薪资分析 |
| GET | `/api/analysis/city` | 单独执行城市分析 |
| POST | `/api/analysis/extract-skills` | 从JD文本实时提取技能关键词 |

---

## 🧪 开发调试技巧

### 测试 Java ↔ Python 通信

```bash
# 1. 确保 Flask 已启动（另一个终端窗口）
cd bosszp-ai && python app.py

# 2. 测试 Flask 直接返回
curl http://localhost:5000/api/analysis/full

# 3. 测试 Java 转发（Spring Boot 已启动）
curl http://localhost:8080/api/ai/analysis

# 4. 如果第3步失败，检查 Java 控制台的错误日志
```

### 常见问题

| 问题 | 原因 | 解决方法 |
|------|------|---------|
| Flask 启动后访问5000端口没反应 | 防火墙拦截 | 允许 Python 通过防火墙 |
| Java 报 "Connection refused" | Flask 没启动 | 先启动 `python app.py` |
| MySQL 连接失败 | 密码不对或数据库不存在 | 检查 `application.yml`，执行 `sql/init.sql` |
| Maven 依赖下载慢 | 默认下载源在国外 | IDEA设置里改阿里云镜像 |
| jieba 分词效果差 | 未加载自定义词典 | `analyzer.py` 里已预定义了IT术语词典 |

---

## 📈 项目发展阶段

### 当前版本 v1.0（你正在搭建的）
- ✅ Spring Boot + Flask 基础架构
- ✅ 示例数据 CSV + MySQL 存储
- ✅ ECharts 数据大屏
- ✅ jieba + TF-IDF 技能提取
- ✅ 薪资/城市/经验多维度分析

### 计划 v2.0（比赛前完成）
- 🔲 爬虫接入（从Boss直聘采集真实数据）
- 🔲 数据库表扩展（公司规模、福利待遇等维度）
- 🔲 更多AI功能（薪资回归预测、岗位分类）
- 🔲 用户系统（登录、收藏岗位）

### 计划 v3.0（iCan比赛后）
- 🔲 云服务器部署
- 🔲 GitHub Actions 定时采集
- 🔲 微信小程序端

---

## 📝 简历描述模板

项目完成后，你可以这样写在简历上：

> **职数洞见 — IT岗位画像与职业数据透视平台** | 项目负责人 | 2026.06-2026.09
> - 设计并实现 **Java Spring Boot + Python Flask** 异构技术栈架构，Java负责业务逻辑与数据管理，Python负责NLP智能分析，两个服务通过RESTful API解耦通信
> - 使用 **MyBatis-Plus + MySQL** 构建数据层，设计岗位信息表、技能统计表，实现多维度组合查询（城市+经验+技能）与分页接口
> - 集成 **jieba + TF-IDF** 实现技能关键词自动提取，支持从JD文本中识别IT技术栈标签；完成薪资分布、城市热度、技能共现等多维统计分析
> - 基于 **ECharts** 构建交互式数据大屏，包含薪资直方图、城市岗位图、技能词云、经验饼图等6类可视化图表
> - 项目获黑龙江省大学生创新训练计划立项（A类），参加iCan大学生创新创业大赛

---

**有问题随时找老师或来问我。加油！🚀**
