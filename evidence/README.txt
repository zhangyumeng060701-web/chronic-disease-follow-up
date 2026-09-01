慢病随访项目证据目录
======================

状态基线
--------
本目录以 2026-08-30（Asia/Shanghai）当前 main 分支提交 e353705 为最新主线基线。

当前状态
--------
- `report_evidence.txt`：2026-08-31 的本地 Docker、测试、lint、构建和审计历史记录。
- `# AgentArts 真实联调证据.txt`、`$ ssh root@124.70.90.96.txt`：历史联调和 ECS 终端记录，不能替代当前时刻的在线证明。
- `screenshots/`：当前归档 2 张 AgentArts 控制台截图；它们支持智能体存在和调用观测，不等同于单次调用原始日志或结构化返回截图。
- 本次复验环境具备 Java/Maven 和 Node/npm，但未提供 Python 或 Docker；因此患者端构建和后端测试可重放，Docker、Python 脚本及真实 AgentArts 调用仍应按历史材料引用。

目录约定
--------
patient-h5-build.txt  患者端最新构建复验日志
screenshots/           AgentArts 控制台截图及说明

