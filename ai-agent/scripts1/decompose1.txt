import urllib3
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
import os
import json
import sys
from huaweicloudsdkcore.auth.credentials import BasicCredentials
from huaweicloudsdkcore.exceptions import exceptions
from huaweicloudsdkpangulargemodels.v1.region.pangulargemodels_region import PanguLargeModelsRegion
from huaweicloudsdkpangulargemodels.v1 import *
from huaweicloudsdkcore.http.http_config import HttpConfig

def decompose_task(requirement):
    # 1. 填入你的凭证信息 (建议从环境变量读取或手动填入)
    # 请务必确认这里的 AK/SK 是正确的
    ak = "HPUA5OC9ICCNLXJUEUS1" 
    sk = "5CTmL02OX27KL2HfX3hw1xRYwaDRJi4TXr2NKaC3"
    project_id = "019fb332fa1f7ea8897b3b17cc47195a" # 你贵阳二的项目ID

    try:
        # 2. 初始化客户端
        credentials = BasicCredentials(ak, sk, project_id)
        client = PanguLargeModelsClient.new_builder() \
            .with_credentials(credentials) \
            .with_region(PanguLargeModelsRegion.value_of("cn-southwest-2")) \
            .with_http_config(HttpConfig.get_default_config().with_ignore_ssl_verification(True)) \
            .with_endpoint("https://pangu.cn-southwest-2.myhuaweicloud.com") \
            .build()

        # 3. 读取 Prompt 模板
        # 假设你的工作目录在项目根目录，路径如下：
        prompt_path = os.path.join(os.path.dirname(__file__), "../prompts/task-decompose.txt")
        with open(prompt_path, "r", encoding="utf-8") as f:
            system_prompt = f.read()

        # 4. 构造请求体
        request = ExecuteChatCompletionRequest()
        request.deployment_id = "2aa2f47c440e41789e527d5af9ed7cf3" # 比赛提供的ID
        
        body = ChatCompletionReq(
            messages=[
                # 系统设置：人设和规则
                {"role": "system", "content": system_prompt},
                # 用户输入：真实需求
                {"role": "user", "content": requirement}
            ],
            temperature=0.7
        )
        request.body = body

        # 5. 执行调用
        response = client.execute_chat_completion(request)
        
        # 6. 处理返回结果并打印为标准 JSON (供 Java 后端读取)
        # 假设 AI 返回的是一个字符串，我们直接输出
        ai_reply = response.choices[0].message.content
        print(ai_reply)

    except exceptions.ClientRequestException as e:
        print(json.dumps({"code": e.status_code, "message": str(e.error_msg)}))
    except Exception as e:
        print(json.dumps({"code": 500, "message": str(e)}))

if __name__ == "__main__":
    # 从命令行接收参数，例如：python decompose.py "增加日期筛选"
    if len(sys.argv) > 1:
        user_requirement = sys.argv[1]
        decompose_task(user_requirement)
    else:
        # 默认测试用例
        decompose_task("在随访记录页面增加按随访日期范围筛选的功能")