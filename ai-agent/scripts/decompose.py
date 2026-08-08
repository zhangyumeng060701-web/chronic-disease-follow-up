import requests
import json
import sys
import os
import time

def call_agent_arts(requirement):
    # 1. 真实接口信息
    url = "https://defaultgw-mu4gmyrsgi.cn-southwest-2.huaweicloud-agentarts.com/runtimes/agent-arts-603436e9c91b445aa942cd45b429a614/invocations"
    api_key = "2c764d94714b43d4a2a0423f99085e8a"

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}",
        "x-hw-agentarts-session-id": "6cd40a7dcfb34b7bbc4adbe782f660b7"
    }

    # 2. 动态定位 Prompt 文件路径（核心修正）
    current_dir = os.path.dirname(os.path.abspath(__file__))
    prompt_path = os.path.join(current_dir, "../prompts/task-decompose.txt")
    
    # 3. 简单重试逻辑
    for attempt in range(2):
        try:
            response = requests.post(url, headers=headers, json={"query": requirement}, timeout=12)
            if response.status_code == 200:
                result = response.json()
                # 兼容 AgentArts 不同的返回字段
                answer = result.get("answer") or result.get("result") or json.dumps(result)
                print(answer)
                return
            time.sleep(1) # 失败重试等待
        except Exception as e:
            if attempt == 1:
                print(json.dumps({"code": 500, "message": f"Python连接异常: {str(e)}"}, ensure_ascii=False))

if __name__ == "__main__":
    user_input = sys.argv[1] if len(sys.argv) > 1 else "测试需求"
    call_agent_arts(user_input)