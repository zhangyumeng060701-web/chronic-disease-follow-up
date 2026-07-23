"""
CodeArts 智能体最小 Demo
输入：一句自然语言需求
输出：CodeArts Snap 生成的代码片段
运行：python demo.py
"""
import requests
import json
import os

# ===== 配置（替换为你的实际值）=====
API_KEY = os.environ.get("CODEARTS_API_KEY", "你的API Key")
ENDPOINT = os.environ.get("CODEARTS_ENDPOINT", "https://codearts-api.cn-north-4.myhuaweicloud.com/v1/snap/generate")
PROJECT_ID = os.environ.get("CODEARTS_PROJECT_ID", "你的项目ID")

# ===== 请求 =====
headers = {
    "Content-Type": "application/json",
    "X-Auth-Token": API_KEY
}

requirement = """
在 Spring Boot 项目中新增一个 GET 接口 /api/patients，
支持分页查询患者列表，参数有 page、size、name（模糊查询），
返回 {code, data: {records, total}, message} 格式。
"""

payload = {
    "prompt": requirement,
    "language": "java",
    "max_tokens": 1000
}

# ===== 调用 =====
try:
    response = requests.post(ENDPOINT, headers=headers, json=payload, timeout=30)
    result = response.json()
    print("=== 返回的代码 ===")
    print(result.get("generated_code", "无返回内容"))
    print("\n=== 完整响应 ===")
    print(json.dumps(result, indent=2, ensure_ascii=False))
except Exception as e:
    print(f"调用失败: {e}")
    print("请确认 API_KEY 和 ENDPOINT 配置正确")
