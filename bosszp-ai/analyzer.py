"""
============================================
bosszp-ai 数据分析引擎
作用：负责所有 Python AI 相关的分析工作
  - 技能关键词提取（jieba分词 + TF-IDF）
  - 薪资分布统计
  - 技能共现分析
  - 热门城市统计

这个文件是纯 Python 模块，不依赖 Flask。
Flask 只负责把这里的函数包装成 HTTP API。

使用方法（测试）：
  python analyzer.py
============================================
"""
import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

import pandas as pd
import numpy as np
import jieba
import re
from collections import Counter
from sklearn.feature_extraction.text import TfidfVectorizer


# ========================================
# 1. 数据加载
# ========================================
def load_data(filepath="data/sample_jobs.csv"):
    try:
        df = pd.read_csv(filepath, encoding="utf-8")
        print(f"[INFO] 成功加载数据：{len(df)} 条岗位记录")
        return df
    except FileNotFoundError:
        print(f"[ERROR] 文件未找到：{filepath}")
        return pd.DataFrame()


# ========================================
# 2. 技能关键词提取
# ========================================
IT_SKILL_DICT = {
    "Java", "Python", "Go", "C++", "JavaScript", "TypeScript", "Swift", "Kotlin", "Rust",
    "Spring", "Spring Boot", "Spring Cloud", "MyBatis", "Hibernate", "Django", "Flask",
    "Vue", "React", "Angular", "Node.js", "HTML", "CSS",
    "MySQL", "Redis", "MongoDB", "PostgreSQL", "Oracle", "Elasticsearch",
    "Docker", "Kubernetes", "Linux", "Git", "Jenkins", "Nginx",
    "Spark", "Hadoop", "Flink", "Kafka",
    "TensorFlow", "PyTorch", "NLP", "OpenCV", "Scikit-learn",
}

for word in IT_SKILL_DICT:
    jieba.add_word(word)


def extract_skills_from_text(text, top_n=15):
    if not text or not isinstance(text, str):
        return []

    text = re.sub(r'[^一-龥a-zA-Z+#.\-]+', ' ', text)
    words = list(jieba.cut(text))

    stop_words = {"熟悉", "熟练", "掌握", "了解", "具有", "一定", "以上", "优先",
                  "负责", "参与", "进行", "相关", "经验", "能力", "工作", "任职",
                  "要求", "岗位", "职责", "描述", "公司", "团队", "项目", "技术",
                  "的", "和", "与", "等", "及", "或", "有", "在", "是", "了"}
    filtered = [w.strip() for w in words
                if w.strip() and len(w.strip()) > 1
                and w.strip() not in stop_words
                and not w.strip().isdigit()]

    if len(filtered) < 2:
        return []

    try:
        doc = ' '.join(filtered)
        vectorizer = TfidfVectorizer(max_features=top_n, token_pattern=r'(?u)\b\w+\b')
        tfidf_matrix = vectorizer.fit_transform([doc])
        feature_names = vectorizer.get_feature_names_out()
        scores = tfidf_matrix.toarray()[0]

        result = [{"word": w, "score": round(float(s), 3)}
                  for w, s in sorted(zip(feature_names, scores),
                                    key=lambda x: x[1], reverse=True)[:top_n]]
        return result
    except Exception as e:
        counter = Counter(filtered)
        result = [{"word": w, "score": round(float(c) / len(filtered), 3)}
                  for w, c in counter.most_common(top_n)]
        return result


def extract_skills_batch(df, job_title_col="job_title", text_col="jd_text"):
    all_skills = []

    if "skills" in df.columns:
        for skills_str in df["skills"].dropna():
            for skill in str(skills_str).split(","):
                s = skill.strip()
                if s:
                    all_skills.append(s)

    if len(set(all_skills)) < 5:
        for title in df[job_title_col].dropna():
            keywords = extract_skills_from_text(str(title), top_n=5)
            for kw in keywords:
                all_skills.append(kw["word"])

    counter = Counter(all_skills)
    top_skills = [{"name": name, "count": count}
                  for name, count in counter.most_common(30)]

    return {
        "total_skills_count": len(all_skills),
        "unique_skills_count": len(counter),
        "top_skills": top_skills
    }


# ========================================
# 3. 薪资分析
# ========================================
def analyze_salary(df):
    if df.empty:
        return {"avg_salary": 0, "salary_distribution": []}

    df_copy = df.copy()
    df_copy["avg_salary"] = (df_copy["salary_low"] + df_copy["salary_high"]) / 2

    result = {
        "avg_salary_high": round(float(df_copy["salary_high"].mean()), 1),
        "avg_salary_low": round(float(df_copy["salary_low"].mean()), 1),
        "avg_salary_mid": round(float(df_copy["avg_salary"].mean()), 1),
        "max_salary": int(df_copy["salary_high"].max()),
        "min_salary": int(df_copy["salary_low"].min()),
    }

    bins = [(0, 5), (5, 10), (10, 15), (15, 20), (20, 25), (25, 30), (30, 50)]
    distribution = []
    for low, high in bins:
        count = len(df_copy[(df_copy["avg_salary"] >= low) & (df_copy["avg_salary"] < high)])
        distribution.append({"range": f"{low}-{high}K", "count": count})
    result["salary_distribution"] = distribution

    return result


# ========================================
# 4. 城市维度分析
# ========================================
def analyze_city(df):
    if df.empty:
        return {"city_stats": []}

    city_groups = df.groupby("city").agg(
        job_count=("id", "count") if "id" in df.columns else ("job_title", "count"),
        avg_salary_low=("salary_low", "mean"),
        avg_salary_high=("salary_high", "mean"),
    ).reset_index()

    result = []
    for _, row in city_groups.iterrows():
        result.append({
            "city": row["city"],
            "job_count": int(row["job_count"]),
            "avg_salary": round(float((row["avg_salary_low"] + row["avg_salary_high"]) / 2), 1)
        })

    result.sort(key=lambda x: x["job_count"], reverse=True)
    return {"city_stats": result}


# ========================================
# 5. 经验要求分析
# ========================================
def analyze_experience(df):
    if df.empty or "exp" not in df.columns:
        return {"exp_stats": []}

    exp_groups = df.groupby("exp").agg(
        count=("job_title", "count"),
        avg_salary=("salary_low", "mean"),
    ).reset_index()

    result = []
    for _, row in exp_groups.iterrows():
        result.append({
            "exp": row["exp"],
            "count": int(row["count"]),
            "avg_salary_low": round(float(row["avg_salary"]), 1)
        })

    return {"exp_stats": result}


# ========================================
# 6. 综合分析接口（给 Java 调用的主入口）
# ========================================
def full_analysis(filepath="data/sample_jobs.csv"):
    df = load_data(filepath)

    if df.empty:
        return {"error": "无数据", "message": "请先采集数据后再进行分析"}

    return {
        "data_overview": {
            "total_jobs": len(df),
            "cities": df["city"].nunique() if "city" in df.columns else 0,
            "companies": df["company"].nunique() if "company" in df.columns else 0,
        },
        "skill_analysis": extract_skills_batch(df),
        "salary_analysis": analyze_salary(df),
        "city_analysis": analyze_city(df),
        "exp_analysis": analyze_experience(df),
    }


# ========================================
# 本地测试
# ========================================
if __name__ == "__main__":
    print("=" * 60)
    print("bosszp-ai 分析引擎 - 本地测试")
    print("=" * 60)

    df = load_data("data/sample_jobs.csv")
    print("\n[数据预览:]")
    print(df.head())

    print("\n[技能分析:]")
    skills = extract_skills_batch(df)
    print(f"  唯一技能数: {skills['unique_skills_count']}")
    print(f"  Top 10: {skills['top_skills'][:10]}")

    print("\n[薪资分析:]")
    salary = analyze_salary(df)
    print(f"  平均薪资范围: {salary['avg_salary_low']}K - {salary['avg_salary_high']}K")

    print("\n[全量分析结果:]")
    result = full_analysis("data/sample_jobs.csv")
    import json
    print(json.dumps(result, ensure_ascii=False, indent=2)[:500] + "...")
    print("\n[OK] 分析引擎测试完成！")
