# 慢病异常规则清单

## 1. 规则来源

本规则清单用于慢病随访系统的风险预警模块，参考高血压和 2 型糖尿病相关临床管理标准，并结合项目需求文档定义。最终系统实现以 `t_alert_rule` 表中的动态规则为准。

## 2. 高血压异常规则

| 编号 | 规则名称 | 指标 | 条件 | 阈值 | 等级 |
|---|---|---|---|---|---|
| BP-01 | 收缩压高危 | `systolic_bp` | `>=` | 180 | RED |
| BP-02 | 收缩压警告 | `systolic_bp` | `>=` | 160 | YELLOW |
| BP-03 | 收缩压关注 | `systolic_bp` | `>=` | 140 | YELLOW |
| BP-04 | 舒张压高危 | `diastolic_bp` | `>=` | 110 | RED |
| BP-05 | 舒张压警告 | `diastolic_bp` | `>=` | 100 | YELLOW |
| BP-06 | 舒张压关注 | `diastolic_bp` | `>=` | 90 | YELLOW |

## 3. 糖尿病异常规则

| 编号 | 规则名称 | 指标 | 条件 | 阈值 | 等级 |
|---|---|---|---|---|---|
| GLU-01 | 空腹血糖高危 | `fasting_glucose` | `>=` | 11.1 | RED |
| GLU-02 | 空腹血糖警告 | `fasting_glucose` | `>=` | 7.0 | YELLOW |
| GLU-03 | 餐后血糖高危 | `postprandial_glucose` | `>=` | 16.7 | RED |
| GLU-04 | 餐后血糖警告 | `postprandial_glucose` | `>=` | 11.1 | YELLOW |

## 4. 随访管理规则

| 编号 | 规则名称 | 条件 | 等级 |
|---|---|---|---|
| FU-01 | 失访预警 | 超过 `next_follow_up_date` 7 天仍未随访 | YELLOW |
| FU-02 | 严重失访 | 超过 `next_follow_up_date` 30 天仍未随访 | RED |
| FU-03 | 连续异常 | 最近连续 2 次随访同一指标均触发 YELLOW 或 RED | RED |

## 5. 数据库初始化语句

当前 `schema.sql` 已包含以下初始化规则：

```sql
INSERT INTO t_alert_rule (rule_name, indicator, operator, threshold, alert_level) VALUES
('收缩压≥180高危',    'systolic_bp',          '>=', 180,   'RED'),
('收缩压≥160警告',    'systolic_bp',          '>=', 160,   'YELLOW'),
('收缩压≥140关注',    'systolic_bp',          '>=', 140,   'YELLOW'),
('舒张压≥110高危',    'diastolic_bp',         '>=', 110,   'RED'),
('舒张压≥100警告',    'diastolic_bp',         '>=', 100,   'YELLOW'),
('舒张压≥90关注',     'diastolic_bp',         '>=', 90,    'YELLOW'),
('空腹血糖≥11.1高危',  'fasting_glucose',      '>=', 11.1,  'RED'),
('空腹血糖≥7.0警告',   'fasting_glucose',      '>=', 7.0,   'YELLOW'),
('餐后血糖≥16.7高危',  'postprandial_glucose', '>=', 16.7,  'RED'),
('餐后血糖≥11.1警告',  'postprandial_glucose', '>=', 11.1,  'YELLOW');
```

## 6. 测试要点

- 阈值等于边界值时应触发规则，例如收缩压 `180` 应触发 RED。
- 阈值低于边界值时不应触发对应规则，例如收缩压 `179` 不应触发 BP-01。
- 同一指标最近连续 2 次异常时，应生成高危预警。
- 逾期 7 天和 30 天应分别触发 YELLOW 和 RED 失访预警。

