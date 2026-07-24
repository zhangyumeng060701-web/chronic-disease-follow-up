# scripts/decompose.py
import sys
import json
import requests
from huaweicloudsdkcore.auth.credentials import BasicCredentials
# 注意：你需要安装 huaweicloudsdkcore

def call_pangu_ai(requirement):
    # 1. 读取 Prompt 模板
    with open('prompts/task-decompose.txt', 'r', encoding='utf-8') as f:
        prompt_template = f.read()

    # 2. 构造请求体 (根据华为云 API 文档)
    url = "https://{endpoint}/v1/{project_id}/pangu/chat" # 替换为真实 API 地址
    payload = {
        "messages": [
            {"role": "system", "content": prompt_template},
            {"role": "user", "content": requirement}
        ],
        "temperature": 0.7
    }

    # 3. 鉴权与发送 (这里建议使用华为云 SDK 或标准签名算法)
    # response = requests.post(url, json=payload, headers=headers)
    
    # 假设此时已获取 AI 返回的字符串
    ai_raw_content = response.json()['choices'][0]['message']['content']
    
    # 4. 强制格式化并输出
    try:
        data = json.loads(ai_raw_content)
        return {"code": 200, "data": data, "message": "success"}
    except:
        return {"code": 500, "message": "AI 返回格式非标准 JSON"}

if __name__ == "__main__":
    user_input = sys.argv[1]
    result = call_pangu_ai(user_input)
    print(json.dumps(result, ensure_ascii=False))

    #由于需要调用的盘古大模型需要付费，目前正在申请代金券中，购买部署后会补全SDK以及deployment_id