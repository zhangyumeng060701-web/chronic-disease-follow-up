# 慢病随访管理系统 — API 接口文档

> 版本: v1.0 | 最后更新: 2026-07-16
> 前后端协作唯一依据 | 字段名必须严格遵守，不准自己发明

---

## 通用规范

| 项目 | 规定 |
|---|---|
| Base URL | `http://localhost:8080/api` |
| 认证方式 | 请求头 `Authorization: Bearer <token>` |
| 统一返回 | `{"code": 200, "data": {...}, "message": "success"}` |
| 分页请求 | `?page=1&size=20` |
| 日期格式 | `YYYY-MM-DD`（如 `2026-07-16`） |
| 空值表示 | 字段值为 null，不省略字段 |
| 手机号 | 脱敏后 `138****5678` |
| 身份证 | 脱敏后 `320102********1234` |

---

## 一、认证模块（2个接口）

### 1. 登录

```
POST /api/auth/login
```

**请求体：**
```json
{
  "username": "admin",
  "password": "123456"
}
```

**返回：**
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  },
  "message": "success"
}
```

### 2. 登出

```
POST /api/auth/logout
Authorization: Bearer <token>
```

**返回：**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

---

## 二、患者管理（5个接口）

### 3. 分页查询患者列表

```
GET /api/patients?page=1&size=20&name=张三&diseaseType=HYPERTENSION
Authorization: Bearer <token>
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| page | Integer | 是 | 页码，默认1 |
| size | Integer | 是 | 每页条数，默认20 |
| name | String | 否 | 姓名（模糊查询） |
| diseaseType | String | 否 | HYPERTENSION / DIABETES / BOTH |

**返回：**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "name": "张三",
        "gender": "男",
        "age": 65,
        "phone": "138****5678",
        "idCard": "320102********1234",
        "address": "南京市鼓楼区****",
        "diseaseType": "HYPERTENSION",
        "medicalHistory": "高血压病史10年",
        "medicationInfo": "硝苯地平 30mg qd",
        "doctorId": 1,
        "status": 1,
        "createTime": "2026-01-15T10:30:00",
        "updateTime": "2026-07-10T14:20:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20
  },
  "message": "success"
}
```

### 4. 获取患者详情

```
GET /api/patients/{id}
Authorization: Bearer <token>
```

**返回：** 同列表中的单条记录格式

### 5. 新增患者

```
POST /api/patients
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体：**
```json
{
  "name": "张三",
  "gender": "男",
  "age": 65,
  "phone": "13812345678",
  "idCard": "320102199001011234",
  "address": "南京市鼓楼区汉口路22号",
  "diseaseType": "HYPERTENSION",
  "medicalHistory": "高血压病史10年",
  "medicationInfo": "硝苯地平 30mg qd",
  "doctorId": 1
}
```

**字段说明：**

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| name | String | 是 | 姓名 |
| gender | String | 是 | 男 / 女 |
| age | Integer | 否 | 年龄 |
| phone | String | 否 | 手机号，11位 |
| idCard | String | 否 | 身份证号，18位 |
| address | String | 否 | 住址 |
| diseaseType | String | 是 | HYPERTENSION / DIABETES / BOTH |
| medicalHistory | String | 否 | 病史描述 |
| medicationInfo | String | 否 | 用药信息 |
| doctorId | Long | 否 | 责任医生ID |

**返回：**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

### 6. 编辑患者

```
PUT /api/patients/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体：** 同新增患者，所有字段均可选（只传需要修改的字段）

### 7. 删除患者（软删除）

```
DELETE /api/patients/{id}
Authorization: Bearer <token>
```

**返回：**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

> 注意：删除操作将 status 设为 0，不执行物理 DELETE

---

## 三、随访记录（5个接口）

### 8. 分页查询随访记录

```
GET /api/follow-ups?page=1&size=20&patientId=1&startDate=2026-07-01&endDate=2026-07-31
Authorization: Bearer <token>
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| page | Integer | 是 | 页码 |
| size | Integer | 是 | 每页条数 |
| patientId | Long | 否 | 患者ID |
| startDate | String | 否 | 随访日期起始 |
| endDate | String | 否 | 随访日期截止 |

**返回：**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "patientId": 1,
        "patientName": "张三",
        "followUpDate": "2026-07-15",
        "followUpType": "门诊",
        "systolicBp": 135,
        "diastolicBp": 85,
        "fastingGlucose": 6.2,
        "postprandialGlucose": 8.5,
        "medicationAdherence": "规律",
        "symptoms": "偶有头晕",
        "advice": "继续用药，注意低盐饮食",
        "nextFollowUpDate": "2026-08-15",
        "doctorId": 1,
        "doctorName": "李医生",
        "createTime": "2026-07-15T09:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 20
  },
  "message": "success"
}
```

### 9. 获取随访详情

```
GET /api/follow-ups/{id}
Authorization: Bearer <token>
```

### 10. 新增随访记录

```
POST /api/follow-ups
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体：**
```json
{
  "patientId": 1,
  "followUpDate": "2026-07-15",
  "followUpType": "门诊",
  "systolicBp": 135,
  "diastolicBp": 85,
  "fastingGlucose": 6.2,
  "postprandialGlucose": 8.5,
  "medicationAdherence": "规律",
  "symptoms": "偶有头晕",
  "advice": "继续用药，注意低盐饮食",
  "nextFollowUpDate": "2026-08-15",
  "doctorId": 1
}
```

**字段说明：**

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| patientId | Long | 是 | 患者ID |
| followUpDate | String | 是 | 随访日期 YYYY-MM-DD |
| followUpType | String | 是 | 门诊 / 电话 / 上门 |
| systolicBp | Integer | 否 | 收缩压 mmHg |
| diastolicBp | Integer | 否 | 舒张压 mmHg |
| fastingGlucose | Float | 否 | 空腹血糖 mmol/L |
| postprandialGlucose | Float | 否 | 餐后血糖 mmol/L |
| medicationAdherence | String | 否 | 规律 / 间断 / 不服药 |
| symptoms | String | 否 | 症状描述 |
| advice | String | 否 | 随访建议 |
| nextFollowUpDate | String | 否 | 下次随访日期 |
| doctorId | Long | 否 | 随访医生ID |

> **触发逻辑**：新增随访后，后端自动检查该患者最近2次随访是否触发预警规则。若触发，自动插入 t_alert 表。

### 11. 编辑随访记录

```
PUT /api/follow-ups/{id}
Authorization: Bearer <token>
```

请求体同新增。

### 12. 逾期随访查询

```
GET /api/follow-ups/overdue?days=7
Authorization: Bearer <token>
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| days | Integer | 否 | 逾期天数阈值，默认7 |

**返回：**
```json
{
  "code": 200,
  "data": [
    {
      "patientId": 3,
      "patientName": "王五",
      "lastFollowUpDate": "2026-06-30",
      "nextFollowUpDate": "2026-07-08",
      "overdueDays": 8,
      "diseaseType": "BOTH",
      "doctorName": "李医生"
    }
  ],
  "message": "success"
}
```

---

## 四、风险预警（3个接口）

### 13. 预警列表

```
GET /api/alerts?page=1&size=20&alertType=HIGH_RISK&alertLevel=RED&isResolved=0
Authorization: Bearer <token>
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| page | Integer | 是 | 页码 |
| size | Integer | 是 | 每页条数 |
| alertType | String | 否 | HIGH_RISK / LOST_FOLLOW_UP |
| alertLevel | String | 否 | RED / YELLOW |
| isResolved | Integer | 否 | 0未处理 / 1已处理 |

**返回：**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "patientId": 2,
        "patientName": "李四",
        "alertType": "HIGH_RISK",
        "alertLevel": "RED",
        "alertReason": "连续2次随访收缩压>=180mmHg",
        "isResolved": 0,
        "resolveTime": null,
        "createTime": "2026-07-10T10:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 20
  },
  "message": "success"
}
```

### 14. 处理预警

```
PUT /api/alerts/{id}/resolve
Authorization: Bearer <token>
```

**返回：**
```json
{
  "code": 200,
  "data": null,
  "message": "success"
}
```

### 15. 预警统计

```
GET /api/alerts/stats
Authorization: Bearer <token>
```

**返回：**
```json
{
  "code": 200,
  "data": {
    "unresolvedHighRisk": 3,
    "unresolvedLostFollowUp": 2,
    "totalUnresolved": 5
  },
  "message": "success"
}
```

---

## 五、统计看板（3个接口）

### 16. 数据总览

```
GET /api/stats/overview
Authorization: Bearer <token>
```

**返回：**
```json
{
  "code": 200,
  "data": {
    "totalPatients": 156,
    "hypertensionCount": 89,
    "diabetesCount": 45,
    "bothCount": 22,
    "thisMonthFollowUpTotal": 32,
    "thisMonthFollowUpCompleted": 28,
    "followUpCompletionRate": 87.5,
    "highRiskPatientCount": 8
  },
  "message": "success"
}
```

### 17. 血压控制率趋势

```
GET /api/stats/blood-pressure?months=6
Authorization: Bearer <token>
```

**返回：**
```json
{
  "code": 200,
  "data": [
    { "month": "2026-02", "controlledRate": 72.5, "uncontrolledRate": 27.5 },
    { "month": "2026-03", "controlledRate": 74.1, "uncontrolledRate": 25.9 },
    { "month": "2026-04", "controlledRate": 71.8, "uncontrolledRate": 28.2 },
    { "month": "2026-05", "controlledRate": 76.3, "uncontrolledRate": 23.7 },
    { "month": "2026-06", "controlledRate": 78.0, "uncontrolledRate": 22.0 },
    { "month": "2026-07", "controlledRate": 75.5, "uncontrolledRate": 24.5 }
  ],
  "message": "success"
}
```

> 控制标准：收缩压 < 140 且 舒张压 < 90

### 18. 血糖控制率趋势

```
GET /api/stats/blood-sugar?months=6
Authorization: Bearer <token>
```

**返回：** 格式同血压，字段名 `controlledRate` / `uncontrolledRate`

> 控制标准：空腹血糖 < 7.0

---

## 六、系统管理（5个接口）

### 19. 用户列表

```
GET /api/users?page=1&size=20
Authorization: Bearer <token>
```

**返回：**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "role": "ADMIN",
        "phone": "138****5678",
        "status": 1,
        "createTime": "2026-01-01T00:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 20
  },
  "message": "success"
}
```

> 注意：不返回 password 字段

### 20. 新增用户

```
POST /api/users
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体：**
```json
{
  "username": "doctor1",
  "password": "123456",
  "realName": "李医生",
  "role": "DOCTOR",
  "phone": "13812345678"
}
```

### 21. 编辑用户

```
PUT /api/users/{id}
Authorization: Bearer <token>
```

请求体同新增（password 可选，不传则不修改密码）。

### 22. 删除用户

```
DELETE /api/users/{id}
Authorization: Bearer <token>
```

> 软删除：将 status 设为 0

### 23. 操作日志

```
GET /api/logs?page=1&size=20&username=admin
Authorization: Bearer <token>
```

**返回：**
```json
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "username": "admin",
        "operation": "新增患者",
        "targetType": "PATIENT",
        "targetId": 5,
        "ipAddress": "192.168.1.100",
        "createTime": "2026-07-16T15:30:00"
      }
    ],
    "total": 200,
    "page": 1,
    "size": 20
  },
  "message": "success"
}
```

---

## 七、AI 智能体接口（1个接口）

### 24. 需求拆解

```
POST /api/ai/decompose
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体：**
```json
{
  "requirement": "在随访记录页面增加按随访日期范围筛选的功能"
}
```

**返回：**
```json
{
  "code": 200,
  "data": {
    "summary": "随访记录页面增加日期范围筛选",
    "tasks": [
      {
        "type": "FRONTEND",
        "title": "随访列表页增加日期范围选择器",
        "description": "在搜索栏增加两个 DatePicker",
        "filesToModify": ["frontend-target/src/views/followUp/FollowUpList.vue"],
        "apiEndpoint": "GET /api/follow-ups?startDate=xxx&endDate=xxx",
        "acceptanceCriteria": "选择日期范围后点查询，表格只显示该范围内的记录"
      },
      {
        "type": "BACKEND",
        "title": "随访查询接口支持日期范围参数",
        "description": "Controller 增加 startDate/endDate 参数",
        "filesToModify": ["backend/.../FollowUpController.java"],
        "apiEndpoint": "GET /api/follow-ups",
        "acceptanceCriteria": "Postman 传日期参数能正确过滤"
      }
    ],
    "risk": "日期格式需要前后端统一为 YYYY-MM-DD"
  },
  "message": "success"
}
```

---

## 八、接口汇总速查表

| 编号 | 方法 | URL | 说明 |
|---|---|---|---|
| 1 | POST | /api/auth/login | 登录 |
| 2 | POST | /api/auth/logout | 登出 |
| 3 | GET | /api/patients | 患者列表（分页+筛选） |
| 4 | GET | /api/patients/{id} | 患者详情 |
| 5 | POST | /api/patients | 新增患者 |
| 6 | PUT | /api/patients/{id} | 编辑患者 |
| 7 | DELETE | /api/patients/{id} | 删除患者（软删除） |
| 8 | GET | /api/follow-ups | 随访记录列表 |
| 9 | GET | /api/follow-ups/{id} | 随访详情 |
| 10 | POST | /api/follow-ups | 新增随访 |
| 11 | PUT | /api/follow-ups/{id} | 编辑随访 |
| 12 | GET | /api/follow-ups/overdue | 逾期随访查询 |
| 13 | GET | /api/alerts | 预警列表 |
| 14 | PUT | /api/alerts/{id}/resolve | 处理预警 |
| 15 | GET | /api/alerts/stats | 预警统计 |
| 16 | GET | /api/stats/overview | 数据总览 |
| 17 | GET | /api/stats/blood-pressure | 血压趋势 |
| 18 | GET | /api/stats/blood-sugar | 血糖趋势 |
| 19 | GET | /api/users | 用户列表 |
| 20 | POST | /api/users | 新增用户 |
| 21 | PUT | /api/users/{id} | 编辑用户 |
| 22 | DELETE | /api/users/{id} | 删除用户 |
| 23 | GET | /api/logs | 操作日志 |
| 24 | POST | /api/ai/decompose | 需求拆解（AI） |

---

## 九、错误码

| code | 说明 |
|---|---|
| 200 | 成功 |
| 400 | 参数校验失败 |
| 401 | 未登录或 token 过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 十、给所有开发者的铁律

1. **字段名必须一模一样**——前端发 `diseaseType`，后端也必须收 `diseaseType`，不能写成 `disease_type`
2. **返回格式必须用 Result 包装**——不准直接返回字符串或数组
3. **分页返回必须用 `records` + `total`**——前端分页组件依赖这两个字段名
4. **删除全部软删除**——不准用 DELETE 语句
5. **新增随访必须自动触发预警检查**——这是核心业务逻辑
6. **接口有任何改动必须同步更新本文档**——并在群里通知全组
