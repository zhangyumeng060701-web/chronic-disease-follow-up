# 失访预警生命周期

系统按患者最新一条有效随访记录的 `next_follow_up_date` 计算逾期天数。

| 逾期天数 | 处理 |
|---:|---|
| 0～6 天 | 不生成预警 |
| 7～29 天 | 生成 `LOST_FOLLOW_UP / YELLOW` |
| 30 天及以上 | 关闭同周期黄色预警并生成 `LOST_FOLLOW_UP / RED` |

同一患者、预警类型、等级和来源到期日构成唯一业务周期。调度任务重复或并发执行时，不得产生重复预警。`next_follow_up_date` 为空、患者停用或不是患者最新随访记录时不参与扫描。

调度任务默认按 `Asia/Shanghai` 时区每天 02:00 执行，可通过 `follow-up.lost-alert.enabled`、`cron` 和 `zone` 配置覆盖。
