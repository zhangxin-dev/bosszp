"""
============================================
bosszp-ai Flask 服务
作用：把 analyzer.py 的分析功能包装成 HTTP API
  - 启动后运行在 http://localhost:5000
  - Java 端通过 RestTemplate 调用这里的接口
  - 所有接口返回 JSON 格式数据

使用方法：
  1. 安装依赖：pip install -r requirements.txt
  2. 启动服务：python app.py
  3. 测试接口：打开浏览器访问 http://localhost:5000/api/health
============================================
"""
from flask import Flask, jsonify, request
from flask_cors import CORS
import analyzer  # 导入我们刚才写的分析引擎

# 创建 Flask 应用
app = Flask(__name__)

# 允许跨域访问（这样前端和 Java 端都可以调用）
CORS(app)


# ========================================
# 接口1：健康检查
# 作用：确认 Flask 服务是否正常运行
# 访问：GET http://localhost:5000/api/health
# ========================================
@app.route("/api/health", methods=["GET"])
def health_check():
    return jsonify({
        "status": "ok",
        "service": "bosszp-ai 分析引擎",
        "version": "1.0.0"
    })


# ========================================
# 接口2：全量分析（核心接口）
# 作用：执行所有维度的分析，返回结果给 Java 后端
# 访问：GET http://localhost:5000/api/analysis/full
#
# Java 端调用示例：
#   RestTemplate rest = new RestTemplate();
#   String url = "http://localhost:5000/api/analysis/full";
#   Map<String, Object> result = rest.getForObject(url, Map.class);
# ========================================
@app.route("/api/analysis/full", methods=["GET"])
def full_analysis():
    """执行全量分析"""
    try:
        result = analyzer.full_analysis("data/sample_jobs.csv")
        return jsonify({"code": 200, "data": result, "message": "分析成功"})
    except Exception as e:
        return jsonify({"code": 500, "data": None, "message": f"分析失败: {str(e)}"}), 500


# ========================================
# 接口3：技能分析
# 访问：GET http://localhost:5000/api/analysis/skills
# ========================================
@app.route("/api/analysis/skills", methods=["GET"])
def skill_analysis():
    """单独执行技能分析"""
    try:
        df = analyzer.load_data("data/sample_jobs.csv")
        result = analyzer.extract_skills_batch(df)
        return jsonify({"code": 200, "data": result, "message": "技能分析成功"})
    except Exception as e:
        return jsonify({"code": 500, "data": None, "message": f"技能分析失败: {str(e)}"}), 500


# ========================================
# 接口4：薪资分析
# 访问：GET http://localhost:5000/api/analysis/salary
# ========================================
@app.route("/api/analysis/salary", methods=["GET"])
def salary_analysis():
    """单独执行薪资分析"""
    try:
        df = analyzer.load_data("data/sample_jobs.csv")
        result = analyzer.analyze_salary(df)
        return jsonify({"code": 200, "data": result, "message": "薪资分析成功"})
    except Exception as e:
        return jsonify({"code": 500, "data": None, "message": f"薪资分析失败: {str(e)}"}), 500


# ========================================
# 接口5：城市分析
# 访问：GET http://localhost:5000/api/analysis/city
# ========================================
@app.route("/api/analysis/city", methods=["GET"])
def city_analysis():
    """单独执行城市维度分析"""
    try:
        df = analyzer.load_data("data/sample_jobs.csv")
        result = analyzer.analyze_city(df)
        return jsonify({"code": 200, "data": result, "message": "城市分析成功"})
    except Exception as e:
        return jsonify({"code": 500, "data": None, "message": f"城市分析失败: {str(e)}"}), 500


# ========================================
# 接口6：技能提取（实时接口）
# 传入一段 JD 文本，实时提取技能关键词
# 访问：POST http://localhost:5000/api/analysis/extract-skills
# 请求体：{"text": "熟悉Java Spring Boot框架，有微服务开发经验..."}
# ========================================
@app.route("/api/analysis/extract-skills", methods=["POST"])
def extract_skills():
    """从传入的 JD 文本中实时提取技能关键词"""
    data = request.get_json()
    if not data or "text" not in data:
        return jsonify({"code": 400, "data": None, "message": "请传入 text 字段"}), 400

    text = data["text"]
    keywords = analyzer.extract_skills_from_text(text, top_n=10)
    return jsonify({
        "code": 200,
        "data": {"text": text[:100], "keywords": keywords},
        "message": "技能提取成功"
    })


# ========================================
# 启动服务
# ========================================
if __name__ == "__main__":
    print("=" * 60)
    print("🚀 bosszp-ai Flask 分析引擎启动中...")
    print(f"   地址：http://localhost:5000")
    print(f"   健康检查：http://localhost:5000/api/health")
    print(f"   全量分析：http://localhost:5000/api/analysis/full")
    print("=" * 60)

    # debug=True 表示修改代码后自动重启（开发环境用）
    # host="0.0.0.0" 允许局域网内其他设备访问
    app.run(debug=True, host="0.0.0.0", port=5000)
