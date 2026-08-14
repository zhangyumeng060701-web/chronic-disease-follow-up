import requests
import json
import sys
import os

def call_agent_arts(requirement):
    # 1. 凭证信息
    url = "https://defaultgw-mu4gmyrsgi.cn-southwest-2.huaweicloud-agentarts.com/runtimes/agent-arts-603436e9c91b445aa942cd45b429a614/invocations"
    api_key = os.getenv("AGENT_API_KEY") or "2c764d94714b43d4a2a0423f99085e8a"
    session_id = os.getenv("AGENT_SESSION_ID") or "6cd40a7dcfb34b7bbc4adbe782f660b7"

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}",
        "x-hw-agentarts-session-id": session_id
    }

    # 强制关闭流式输出（如果接口支持），如果不支持，下方逻辑也会处理
    payload = {
        "query": requirement,
        "stream": False 
    }

    try:
        # 2. 发送请求，设置 stream=True 来处理碎片数据
        response = requests.post(url, headers=headers, json=payload, timeout=30, stream=True)

        full_answer = ""
        
        # 3. 核心：处理流式返回 (SSE 格式)
        for line in response.iter_lines():
            if line:
                line_data = line.decode('utf-8')
                if line_data.startswith("data: "):
                    try:
                        # 去掉开头的 "data: " 
                        json_str = line_data[6:]
                        data = json.loads(json_str)
                        
                        # 抓取 message 事件中的内容
                        if data.get("event") == "message":
                            # 优先抓取正式回答 content，如果没有则抓取推理过程 reasoning_content
                            chunk = data.get("content") or data.get("reasoning_content") or ""
                            full_answer += chunk
                    except:
                        continue

        # 4. 如果不是流式返回，直接尝试正常解析
        if not full_answer and response.status_code == 200:
            try:
                res_json = response.json()
                full_answer = res_json.get("answer") or res_json.get("result") or response.text
            except:
                full_answer = response.text

        # 5. 清洗 Markdown 标签（确保 Java 拿到的是纯 JSON）
        final_result = full_answer
        if "```json" in final_result:
            final_result = final_result.split("```json")[1].split("```")[0].strip()
        elif "```" in final_result:
            final_result = final_result.split("```")[1].split("```")[0].strip()

        # 6. 最终输出
        if final_result:
            print(final_result)
        else:
            print(json.dumps({"code": 500, "message": "AI未返回有效内容"}, ensure_ascii=False))

    except Exception as e:
        print(json.dumps({"code": 500, "message": str(e)}, ensure_ascii=False))

if __name__ == "__main__":
    user_input = sys.argv[1] if len(sys.argv) > 1 else "测试内容"
    call_agent_arts(user_input)