import json
import os
import sys
import time

try:
    import requests
except ImportError:
    requests = None


BASE_DIR = os.path.dirname(os.path.abspath(__file__))
PROMPT_PATH = os.environ.get("AGENT_ARTS_PROMPT_PATH", os.path.join(BASE_DIR, "..", "prompts", "task-decompose.txt"))
AGENT_ARTS_URL = os.environ.get("AGENT_ARTS_URL", "")
AGENT_ARTS_API_KEY = os.environ.get("AGENT_ARTS_API_KEY", "")
AGENT_ARTS_SESSION_ID = os.environ.get("AGENT_ARTS_SESSION_ID", "")


def read_prompt():
    try:
        with open(PROMPT_PATH, "r", encoding="utf-8") as f:
            return f.read()
    except OSError:
        return "你是一个慢病随访系统开发任务拆解助手。"


def mock_decompose(requirement):
    return {
        "code": 200,
        "data": {
            "summary": f"需求拆解：{requirement[:30]}",
            "tasks": [
                {
                    "type": "FRONTEND",
                    "title": "前端页面实现",
                    "description": "根据需求调整对应前端页面和交互。",
                    "filesToModify": ["frontend-target/src/views/"],
                    "apiEndpoint": "",
                    "acceptanceCriteria": "页面可交互且与后端联调通过。"
                },
                {
                    "type": "BACKEND",
                    "title": "后端接口实现",
                    "description": "根据需求实现或调整后端接口。",
                    "filesToModify": ["backend/src/main/java/com/example/followup/"],
                    "apiEndpoint": "POST /api/ai/decompose",
                    "acceptanceCriteria": "接口返回统一 Result 结构。"
                },
                {
                    "type": "DATABASE",
                    "title": "数据库变更评估",
                    "description": "评估是否需要新增字段、索引或表。",
                    "filesToModify": ["backend/src/main/resources/db/schema.sql"],
                    "apiEndpoint": "",
                    "acceptanceCriteria": "数据库变更脚本可执行。"
                },
                {
                    "type": "TEST",
                    "title": "自动化测试",
                    "description": "为本次需求补充测试用例。",
                    "filesToModify": ["backend/src/test/java/", "frontend-target/src/__tests__/"],
                    "apiEndpoint": "",
                    "acceptanceCriteria": "测试全部通过。"
                }
            ],
            "risk": "当前未配置真实 Agent Arts，请配置环境变量后切换真实链路。"
        },
        "message": "success"
    }


def call_agent_arts(requirement):
    if not AGENT_ARTS_URL or not AGENT_ARTS_API_KEY or requests is None:
        return mock_decompose(requirement)

    prompt = read_prompt()
    payload = {"query": prompt + "\n用户需求：" + requirement}
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {AGENT_ARTS_API_KEY}"
    }
    if AGENT_ARTS_SESSION_ID:
        headers["x-hw-agentarts-session-id"] = AGENT_ARTS_SESSION_ID

    try:
        response = requests.post(AGENT_ARTS_URL, headers=headers, json=payload, timeout=35, stream=True)
        full_answer = ""

        for line in response.iter_lines():
            if not line:
                continue
            line_data = line.decode("utf-8")
            if line_data.startswith("data: "):
                try:
                    data = json.loads(line_data[6:])
                    if data.get("event") == "message":
                        chunk = data.get("content") or data.get("reasoning_content") or ""
                        full_answer += chunk
                except json.JSONDecodeError:
                    continue

        if not full_answer and response.status_code == 200:
            try:
                result = response.json()
                full_answer = result.get("answer") or result.get("result") or response.text
            except json.JSONDecodeError:
                full_answer = response.text

        if not full_answer:
            return {"code": 500, "message": f"Agent Arts 未返回有效内容，HTTP {response.status_code}", "data": None}

        start = full_answer.find("{")
        end = full_answer.rfind("}")
        if start == -1 or end == -1:
            return {"code": 500, "message": "Agent Arts 输出不是有效 JSON", "data": None}
        parsed = json.loads(full_answer[start:end + 1])
        return {"code": 200, "data": parsed, "message": "success"}
    except Exception as exc:
        return {"code": 500, "message": f"Agent Arts 调用失败：{exc}", "data": None}


if __name__ == "__main__":
    requirement = sys.argv[1] if len(sys.argv) > 1 else "测试需求"
    print(json.dumps(call_agent_arts(requirement), ensure_ascii=False))
