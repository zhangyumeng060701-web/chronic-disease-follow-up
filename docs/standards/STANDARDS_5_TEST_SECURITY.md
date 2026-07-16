# 5号 第一周标准 — 自动化测试 + 安全检查

## 本周产出物

| 产出 | 文件名 | 完成标志 |
|---|---|---|
| 测试框架搭建 | 后端 `src/test/` + 前端 `__tests__/` | 至少一个示例用例通过 |
| 慢病异常规则清单 | `docs/alert-rules.md` | 1号评审通过 |
| 异常规则表设计 | `t_alert_rule` 建表语句 | 合并到1号的 schema.sql |
| 数据脱敏规则文档 | `docs/desensitization-rules.md` | 全组评审通过 |

---

## 一、测试框架搭建

### 后端测试（JUnit 5 + MockMvc）

目录结构：
```
backend/src/test/java/com/example/followup/
├── controller/
│   └── PatientControllerTest.java     # 本周示例
└── service/
    └── PatientServiceTest.java        # 本周示例（后续周补充）
```

本周最小可跑示例（`PatientControllerTest.java`）：

```java
@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("健康检查接口应返回200")
    void healthCheck_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
```

### 前端测试（Vitest）

目录结构：
```
frontend-target/src/__tests__/
└── example.test.js                    # 本周示例
```

```js
import { describe, it, expect } from 'vitest'

describe('示例测试', () => {
  it('基本断言通过', () => {
    expect(1 + 1).toBe(2)
  })
})
```

---

## 二、慢病异常规则清单（`docs/alert-rules.md`）

### 规则来源

基于《中国高血压防治指南》和《中国2型糖尿病防治指南》的临床标准。

### 高血压异常规则

| 编号 | 规则名称 | 指标 | 条件 | 阈值 | 等级 |
|---|---|---|---|---|---|
| BP-01 | 收缩压高危 | systolic_bp | ≥ | 180 | RED |
| BP-02 | 收缩压警告 | systolic_bp | ≥ | 160 | YELLOW |
| BP-03 | 收缩压关注 | systolic_bp | ≥ | 140 | YELLOW |
| BP-04 | 舒张压高危 | diastolic_bp | ≥ | 110 | RED |
| BP-05 | 舒张压警告 | diastolic_bp | ≥ | 100 | YELLOW |
| BP-06 | 舒张压关注 | diastolic_bp | ≥ | 90 | YELLOW |

### 糖尿病异常规则

| 编号 | 规则名称 | 指标 | 条件 | 阈值 | 等级 |
|---|---|---|---|---|---|
| GLU-01 | 空腹血糖高危 | fasting_glucose | ≥ | 11.1 | RED |
| GLU-02 | 空腹血糖警告 | fasting_glucose | ≥ | 7.0 | YELLOW |
| GLU-03 | 餐后血糖高危 | postprandial_glucose | ≥ | 16.7 | RED |
| GLU-04 | 餐后血糖警告 | postprandial_glucose | ≥ | 11.1 | YELLOW |

### 随访管理规则

| 编号 | 规则名称 | 条件 | 等级 |
|---|---|---|---|
| FU-01 | 失访预警 | 超过 `next_follow_up_date` 7 天仍未随访 | YELLOW |
| FU-02 | 严重失访 | 超过 `next_follow_up_date` 30 天仍未随访 | RED |
| FU-03 | 连续异常 | 最近连续2次随访同一指标均触发 YELLOW 或 RED | RED |

---

## 三、异常规则表设计 — 给 1 号的建表语句

```sql
-- 初始化数据（在 schema.sql 末尾追加）
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

**规则**：
- 1号建表时你把这 10 条初始数据给他
- 表中 `is_active` 字段支持后续在系统管理页面动态启用/禁用规则
- 预警触发逻辑由 1 号在 Service 层实现：新增随访记录后，遍历 `t_alert_rule` 逐条匹配

---

## 四、数据脱敏规则（`docs/desensitization-rules.md`）

### 规则表

| 数据字段 | 脱敏规则 | 原始 → 脱敏后 | 生效角色 |
|---|---|---|---|
| 患者姓名 | 保留首字，其余星号 | 张三丰 → 张** | 非管理员 |
| 身份证号 | 保留前6后4，中间8位星号 | 320102199001011234 → 320102********1234 | 非管理员 |
| 手机号 | 保留前3后4，中间4位星号 | 13812345678 → 138****5678 | 非管理员 |
| 详细住址 | 只显示到区/县，其余隐藏 | 南京市鼓楼区汉口路22号 → 南京市鼓楼区**** | 非管理员 |

### 脱敏规则应用位置

- **后端**：在 Service 层返回数据前统一脱敏（推荐）
- **前端**：用 `Desensitize.vue` 组件包裹敏感字段（兜底）

### 前端脱敏组件

```vue
<template>
  <span>{{ displayText }}</span>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const props = defineProps({
  text: { type: String, default: '' },
  type: { type: String, required: true }  // name | idCard | phone | address
})

const userStore = useUserStore()

const displayText = computed(() => {
  if (userStore.isAdmin || !props.text) return props.text

  const rules = {
    name:    () => props.text[0] + '*'.repeat(props.text.length - 1),
    idCard:  () => props.text.replace(/^(.{6})(.{8})(.{4})$/, '$1********$3'),
    phone:   () => props.text.replace(/^(.{3})(.{4})(.{4})$/, '$1****$3'),
    address: () => {
      const match = props.text.match(/^(.+?[区县])/)
      return match ? match[1] + '****' : '****'
    }
  }
  return (rules[props.type] || (() => props.text))()
})
</script>
```

---

## 五、本周禁止事项

- ❌ 不要一口气写所有测试用例——本周先搭框架，下周再补
- ❌ 不要自己编阈值——上面表格里的数值来自临床指南，不要随意改
- ❌ 不要等 1 号的接口写完了再测——HealthController 的 `/api/health` 这周就能测
- ❌ 脱敏规则不要只写文档不写代码——前端组件和后端拦截器都要实现

---

## 六、与 1 号的协作点

- 本周三前把 `t_alert_rule` 建表语句和初始化数据给 1 号
- 本周五前把脱敏规则文档给 1 号，1 号在后端 Service 层实现脱敏

## 与 6 号的协作点

- 6 号的 CI/CD 流水线需要跑你的测试用例，约定测试报告格式为 JUnit XML
- GitHub Actions 中测试步骤：`mvn test` 或 `npm run test`
