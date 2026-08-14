# CodeArts API 调研笔记

## 1. 可用能力
- CodeArts Snap：代码生成、代码续写、代码解释、代码翻译
- CodeArts Pipeline：流水线编排
- CodeArts Check：代码检查
- CodeArts Artifact：编译后的软件包仓库。

## 2. API 调用方式
- 认证方式：主要使用 AK/SK（就是你刚才下载的那个文件）进行签名认证。
- 请求格式：JSON 
- 响应格式：JSON

## 3. 代码生成 API 详情
- 接口 URL：https://pangu.cn-east-3.myhuaweicloud.com/v1/{project_id}/deployments/{deployment_id}/chat/completions
- 请求参数格式："https://{endpoint}/v1/{project_id}/deployments/{deployment_id}/chat/completions"
{"messages":[
    {
        "content":"hello"
    },
    {
        "content":"Hello! How can I assist you today?"
    }
    ],
    "max_tokens":50,
    "n":1
    }
- 输入示例（自然语言需求 → API 输入）：{"messages":[
    {
        "content":"hello"
        },
        {
            "content":"Hello! How can I assist you today?"
            }
            ],
            "max_tokens":50,
            "n":1
            }
- 输出示例（API 返回的代码）：{"id":"8efcd203-4cd9-4452-923f-00439205c6cb","created":1696759072,"choices":[{"index":0,"message":{"role":null,"content":"My name is AI Assistant. How can I help you today?"}}],"usage":{"completion_tokens":9,"prompt_tokens":3,"total_tokens":12}}
- 单次调用限制（字数/Token 上限）：单次输入的 Prompt 通常不能超过 2000-4000 个字符
-补充说明：经过调研，目前 CodeArts Snap 主要通过 VS Code/IntelliJ 插件集成使用。若需通过代码调用其背后的 AI 能力，需申请盘古大模型 (Pangu-Coder) 的 API 权限。其调用逻辑遵循华为云标准 AK/SK 签名认证，接口位于 API Explorer 的 Pangu 模块下。

## 4. 支持的编程语言
- Java: 是
- JavaScript/Vue: 是
- SQL: 是
- Python: 是

## 5. 当前限制与问题
- QPS 限制：普通用户可能每秒只能调 1-5 次
- 单次最长响应时间：单次输入的 Prompt 通常不能超过 2000-4000 个字符
- 已知的不稳定情况：暂无