慢病随访项目证据目录
======================

状态基线
--------
本目录以 2026-08-30（Asia/Shanghai）当前 main 分支提交 e353705 为最新主线基线。
latest-mainline/ 只存放基于该基线或其后明确提交实际执行的结果；historical/ 只存放仓库中已有的历史记录或历史声明，不能当作本次复验结果。

每条证据必须包含
----------------
1. 时间：使用 Asia/Shanghai 时区，精确到分钟或秒；
2. 命令/动作：写出完整命令，截图写明实际控制台或 IDE 插件动作；
3. 结果：写明通过、失败、警告或未执行，并保留关键输出；
4. commit：写出执行时的完整或短 SHA；
5. 来源：说明本地终端、真实 CodeArts 控制台、CodeArts IDE 插件或部署环境。

脱敏要求
--------
- API Key、Token、密码、Cookie、Authorization 等统一替换为 <REDACTED>，不得只做部分遮挡。
- 不记录真实患者姓名、手机号、身份证号、住址、病历号、联系方式或可组合识别患者的信息；演示数据必须是虚构数据，必要时使用 <SYNTHETIC_PATIENT>。
- 日志和截图提交前须人工检查 URL、请求头、环境变量、浏览器地址栏、终端历史和图片 OCR 可见内容。

CodeArts / AgentArts 截图规则
-----------------------------
截图必须来自真实华为云 CodeArts/AgentArts 控制台或 CodeArts IDE 插件，并在同目录的说明中标注时间、动作、结果和 commit。仓库代码、终端输出、网页静态图、模拟图或 AI 生成图片都不能替代 CodeArts 截图。

当前状态
--------
- latest-mainline/：已归档本机可执行的前端测试、lint 和构建日志。
- historical/：已登记仓库既有的历史 AgentArts、部署和构建声明；这些记录未在本次主线复验中重放。
- screenshots/：已归档 7 张真实 CodeArts/AgentArts 控制台或 IDE 截图；部署截图、CodeArts Snap 具体操作截图、AgentArts 单次调用日志/结构化返回截图仍为待补证据。
- 本机未提供 Java/Maven、Python 或 Docker，且未提供真实 AgentArts 凭据，因此不得把后端部署、真实 AgentArts 调用或线上接口写成本次已完成证明。

目录约定
--------
latest-mainline/  最新 main 主线复验日志
historical/      历史结果与历史声明索引
screenshots/     真实截图；未取得前保持空白并维护待补清单
