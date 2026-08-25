import requests
import json
import sys
import os

def call_agent_arts(requirement):
    # 1. 严格安全准则：仅从环境变量读取，不留明文 Fallback
    url = "https://defaultgw-mu4gmyrsgi.cn-southwest-2.huaweicloud-agentarts.com/runtimes/agent-arts-603436e9c91b445aa942cd45b429a614/invocations"
    api_key = os.getenv("AGENT_API_KEY")
    session_id = os.getenv("AGENT_SESSION_ID")

    # 安全检查：若无密钥则主动报错，不尝试发起请求
    if not api_key:
        print(json.dumps({"code": 401, "message": "AGENT_API_KEY environment variable is missing"}, ensure_ascii=False))
        return

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}",
        "x-hw-agentarts-session-id": session_id if session_id else ""
    }

    # 关闭流式输出以简化返回处理，若服务端强制流式，下方 SSE 逻辑依然有效
    payload = {
        "query": requirement,
        "stream": False 
    }

    try:
        # 2. 发送请求
        response = requests.post(url, headers=headers, json=payload, timeout=30, stream=True)

        full_answer = ""
        
        # 3. 处理流式数据拼接 (SSE 格式)
        for line in response.iter_lines():
            if line:
                line_data = line.decode('utf-8')
                if line_data.startswith("data: "):
                    try:
                        json_str = line_data[6:]
                        data = json.loads(json_str)
                        if data.get("event") == "message":
                            chunk = data.get("content") or data.get("reasoning_content") or ""
                            full_answer += chunk
                    except:
                        continue

        # 4. 兜底处理：若非流式返回则直接解析
        if not full_answer and response.status_code == 200:
            try:
                res_json = response.json()
                full_answer = res_json.get("answer") or res_json.get("result") or response.text
            except:
                full_answer = response.text

        # 5. 核心逻辑：精准提取 JSON 块，过滤 AI 开场白和 Markdown 标签
        final_result = full_answer
        if "{" in final_result:
            # 找到第一个 { 和最后一个 }
            start = final_result.find("{")
            end = final_result.rfind("}")
            if start != -1 and end != -1:
                final_result = final_result[start:end+1]

        # 6. 最终输出给 Java 的纯净字符串
        if final_result and final_result.strip().startswith("{"):
            print(final_result)
        else:
            print(json.dumps({"code": 500, "message": "AI Output is not a valid JSON"}, ensure_ascii=False))

    except Exception as e:
        print(json.dumps({"code": 500, "message": str(e)}, ensure_ascii=False))

if __name__ == "__main__":
    # 获取命令行参数，默认为演示需求
    user_input = sys.argv[1] if len(sys.argv) > 1 else "在随访记录页面增加按随访日期范围筛选的功能"
    call_agent_arts(user_input)