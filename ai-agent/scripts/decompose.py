import requests
import json
import sys
import os

def call_agent_arts(requirement):
    # 1. 严格安全审计：彻底移除硬编码，仅从系统环境变量读取
    # 提交到 GitHub 的源码中不再包含任何 Key 字符串
    url = "https://defaultgw-mu4gmyrsgi.cn-southwest-2.huaweicloud-agentarts.com/runtimes/agent-arts-603436e9c91b445aa942cd45b429a614/invocations"
    api_key = os.getenv("AGENT_API_KEY")
    session_id = os.getenv("AGENT_SESSION_ID")

    # 安全红线检查：若服务器未注入环境变量，则直接返回错误并退出
    if not api_key:
        print(json.dumps({
            "code": 401, 
            "message": "Security Error: AGENT_API_KEY is not configured on the server."
        }, ensure_ascii=False))
        sys.exit(1)

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}",
        "x-hw-agentarts-session-id": session_id if session_id else ""
    }

    # 构造请求，强制关闭流式输出以减少数据解析碎片（由 Agent 端处理）
    payload = {
        "query": requirement,
        "stream": False 
    }

    try:
        # 2. 发送请求（保留流式兼容逻辑，防止服务端配置变更）
        response = requests.post(url, headers=headers, json=payload, timeout=35, stream=True)
        full_answer = ""
        
        for line in response.iter_lines():
            if line:
                line_data = line.decode('utf-8')
                if line_data.startswith("data: "):
                    try:
                        data = json.loads(line_data[6:])
                        if data.get("event") == "message":
                            chunk = data.get("content") or data.get("reasoning_content") or ""
                            full_answer += chunk
                    except:
                        continue

        # 3. 兜底处理：处理非流式返回
        if not full_answer and response.status_code == 200:
            try:
                res_json = response.json()
                full_answer = res_json.get("answer") or res_json.get("result") or response.text
            except:
                full_answer = response.text

        # 4. 【核心质量改进】精准提取 JSON，解决 Java 反序列化报错
        # 该逻辑自动剥离 AI 产生的中文前言、Markdown 标签（```json）等杂质
        final_json_str = ""
        if "{" in full_answer:
            start_index = full_answer.find("{")
            end_index = full_answer.rfind("}")
            if start_index != -1 and end_index != -1:
                final_json_str = full_answer[start_index:end_index+1]

        # 5. 最终验证与输出
        if final_json_str and final_json_str.strip().startswith("{"):
            # 只打印纯净的 JSON 字符串，确保 Java 这一端可以直接 ObjectMapper.readValue()
            print(final_json_str)
        else:
            # 返回一个标准的错误 JSON，防止 Java 解析 null 崩溃
            fallback_error = {
                "summary": "AI Parsing Failed",
                "tasks": [],
                "risk": "AI returned non-JSON content. Full response: " + full_answer[:50]
            }
            print(json.dumps(fallback_error, ensure_ascii=False))

    except Exception as e:
        # 系统级异常捕获
        print(json.dumps({"code": 500, "message": str(e)}, ensure_ascii=False))

if __name__ == "__main__":
    # 获取命令行参数，首选用户输入的需求内容
    user_input = sys.argv[1] if len(sys.argv) > 1 else "测试高血压随访页面导出功能"
    call_agent_arts(user_input)