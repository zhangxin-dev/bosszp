# 职数洞见 - IT行业岗位画像与职业数据透视平台

大创项目，目前还在开发中。目标是做一个IT岗位数据分析平台，帮助求职者了解市场真实需求。

## 技术栈

- **后端**：Java Spring Boot + MyBatis-Plus（业务层）
- **AI分析**：Python Flask（jieba分词 + TF-IDF技能提取）
- **数据库**：MySQL
- **前端**：ECharts 数据大屏（一个 index.html）
- **通信方式**：Java 通过 RestTemplate 调 Python Flask API

## 怎么跑起来

### 1. 数据库
执行 `sql/init.sql`，会建库建表+插入示例数据

### 2. Python AI 引擎
```bash
cd bosszp-ai
pip install -r requirements.txt   # 装依赖
python analyzer.py                 # 可以先测试分析引擎
python app.py                      # 启动 Flask，跑在 5000 端口
```

### 3. Spring Boot 后端
用 IDEA 打开 `bosszp-backend`，改 `application.yml` 里的数据库密码，然后 Run

### 4. 看效果
浏览器打开 `http://localhost:8080`

## 当前进度

- [x] 项目骨架搭建（Java + Python 双模块）
- [x] 示例数据跑通（CSV里塞了10条数据）
- [x] 数据大屏能看（ECharts 四个图表）
- [x] Java 调 Python 的链路打通
- [ ] 爬虫还没写（现在用的是假数据）
- [ ] 还没部署（只能在本地跑）

## 项目结构

```
bosszp/
├── bosszp-backend/    # Java后端
│   └── src/main/java/com/bosszp/
│       ├── controller/JobController.java    # API接口
│       ├── service/JobService.java          # 业务逻辑
│       ├── mapper/JobMapper.java            # 数据库操作
│       └── model/Job.java                   # 岗位实体
├── bosszp-ai/         # Python分析引擎
│   ├── app.py         # Flask服务
│   ├── analyzer.py    # 分析逻辑（分词、统计）
│   └── data/          # 示例数据
├── sql/init.sql       # 建表脚本
└── index.html         # 大屏页面
```

## 遇到的问题

- Python 3.14 和 scikit-learn 兼容问题，换到最新版解决
- Maven settings.xml 配置出错，排查了半天
- 一开始 Java 调 Python 一直 404，后来发现是端口写错了

## TODO

- [ ] 写爬虫，采集真实招聘数据
- [ ] 补充 Python AI 模块（薪资预测之类的）
- [ ] 前端页面改好看点
- [ ] 部署到服务器上
