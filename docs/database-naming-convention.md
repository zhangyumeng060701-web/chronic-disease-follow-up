# 数据库命名规范速查

> 全组统一 | 后端D+E必须遵守 | 组长Code Review依据

---

## 一、表名

| 规则 | 示例 |
|---|---|
| `t_` 前缀 | `t_patient` `t_follow_up` |
| 全小写 + 下划线 | `t_operation_log`（不是 t_OperationLog） |
| 单数形式 | `t_patient`（不是 t_patients） |

---

## 二、主键 ID

**所有表统一：`id BIGINT PRIMARY KEY AUTO_INCREMENT`**

| 表 | 主键 | Java 实体对应 |
|---|---|---|
| t_user | id | `@TableId(type = IdType.AUTO) private Long id;` |
| t_patient | id | 同上 |
| t_follow_up | id | 同上 |
| t_alert | id | 同上 |
| t_alert_rule | id | 同上 |
| t_operation_log | id | 同上 |

**禁止**：不要用 `patient_id` 做主键、不要用 `user_id` 做主键、不要用 UUID 或 String 类型

---

## 三、字段命名

| 规则 | 数据库（下划线） | Java 实体（小驼峰） |
|---|---|---|
| 普通字段 | `disease_type` | `diseaseType` |
| 外键 | `patient_id` | `patientId` |
| 时间 | `create_time` | `createTime` |
| 状态标记 | `status` / `is_resolved` | `status` / `isResolved` |
| 日期 | `follow_up_date` | `followUpDate` |

**MyBatis-Plus 自动做转换**，数据库写 `follow_up_date`，Java 写 `followUpDate`，不用手动映射。

---

## 四、必须存在的字段

### 所有表都要有

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | BIGINT AUTO_INCREMENT | 主键 |
| `create_time` | DATETIME DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 需要软删除的表要有

| 字段 | 类型 | 说明 |
|---|---|---|
| `status` | TINYINT DEFAULT 1 | 1正常 / 0已删除 |

适用：`t_patient` `t_user`

---

## 五、Java 实体 ID 规范

6个实体类的 ID 统一写法：

```java
@Data
@TableName("t_patient")
public class Patient {
    @TableId(type = IdType.AUTO)
    private Long id;              // ← 统一叫 id，类型 Long
    // ...其他字段
}
```

**禁止**：
- ❌ 不要用 `private String id;`
- ❌ 不要用 `private Integer id;`
- ❌ 不要用 `private Long patientId;`（主键就叫 id）

---

## 六、索引命名

| 规则 | 示例 |
|---|---|
| 格式 `idx_表名_字段名` | `idx_follow_up_patient_id` |
| 外键字段要建索引 | `INDEX idx_fu_patient_id (patient_id)` |
| 常用查询字段要建索引 | `INDEX idx_fu_date (follow_up_date)` |
| 复合索引 | `INDEX idx_alert_patient (patient_id, alert_type)` |

---

## 七、一页速查：6张表的所有字段

### t_patient
```
id, name, gender, age, phone, id_card, address,
disease_type, medical_history, medication_info,
doctor_id, status, create_time, update_time
```

### t_follow_up
```
id, patient_id, follow_up_date, follow_up_type,
systolic_bp, diastolic_bp, fasting_glucose,
postprandial_glucose, medication_adherence,
symptoms, advice, next_follow_up_date,
doctor_id, create_time
```

### t_alert
```
id, patient_id, alert_type, alert_level,
alert_reason, is_resolved, resolve_time, create_time
```

### t_alert_rule
```
id, rule_name, indicator, operator, threshold,
alert_level, is_active, create_time
```

### t_user
```
id, username, password, real_name, role,
phone, status, create_time
```

### t_operation_log
```
id, user_id, username, operation,
target_type, target_id, ip_address, create_time
```

---

## 八、绝对禁止

- ❌ 表名不加 `t_` 前缀
- ❌ 主键用 VARCHAR 或 UUID
- ❌ 字段名用驼峰在数据库里（数据库一律下划线）
- ❌ 不写 `create_time` 字段
- ❌ 物理 DELETE（全部用 status=0 软删除）
- ❌ 前端 JSON 字段名用下划线（前端 JSON 必须驼峰，和后端 Java 一致）
- ❌ 自创字段名——上面表格里没有的字段不能自己加
