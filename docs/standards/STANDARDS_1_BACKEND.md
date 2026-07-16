# 1号 第一周标准 — 靶系统后端 + 数据库

## 本周产出物

| 产出 | 文件名 | 完成标志 |
|---|---|---|
| 数据库建表 SQL（6表） | `backend/src/main/resources/db/schema.sql` | 全组评审通过，MySQL 执行无报错 |
| Spring Boot 工程骨架 | `backend/` 整个目录 | `GET /api/health` 返回 `{"code":200}` |
| 患者管理 CRUD API | PatientController + Service + Mapper | Postman 调通增删改查+分页 |
| JWT 登录骨架 | AuthController + SecurityConfig | 登录返回 token，带 token 访问接口鉴权通过 |

---

## 一、数据库 Schema（必须用这个，6 张表）

### 1. t_patient（患者表）

```sql
CREATE TABLE t_patient (
    id               BIGINT       PRIMARY KEY AUTO_INCREMENT,
    name             VARCHAR(50)  NOT NULL                   COMMENT '姓名',
    gender           VARCHAR(10)  NOT NULL                   COMMENT '性别：男/女',
    age              INT                                     COMMENT '年龄',
    phone            VARCHAR(20)                             COMMENT '手机号',
    id_card          VARCHAR(18)                             COMMENT '身份证号',
    address          VARCHAR(200)                            COMMENT '住址',
    disease_type     VARCHAR(20)  NOT NULL                   COMMENT '慢病类型：HYPERTENSION/DIABETES/BOTH',
    medical_history  TEXT                                    COMMENT '病史描述',
    medication_info  TEXT                                    COMMENT '用药信息',
    doctor_id        BIGINT                                  COMMENT '责任医生ID，关联 t_user.id',
    status           TINYINT      DEFAULT 1                  COMMENT '1正常/0已删除',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者档案表';
```

### 2. t_follow_up（随访记录表）

```sql
CREATE TABLE t_follow_up (
    id                     BIGINT       PRIMARY KEY AUTO_INCREMENT,
    patient_id             BIGINT       NOT NULL              COMMENT '患者ID',
    follow_up_date         DATE         NOT NULL              COMMENT '随访日期',
    follow_up_type         VARCHAR(20)  NOT NULL              COMMENT '门诊/电话/上门',
    systolic_bp            INT                                COMMENT '收缩压 mmHg',
    diastolic_bp           INT                                COMMENT '舒张压 mmHg',
    fasting_glucose        DECIMAL(4,1)                       COMMENT '空腹血糖 mmol/L',
    postprandial_glucose   DECIMAL(4,1)                       COMMENT '餐后血糖 mmol/L',
    medication_adherence   VARCHAR(20)                        COMMENT '规律/间断/不服药',
    symptoms               TEXT                               COMMENT '症状描述',
    advice                 TEXT                               COMMENT '随访建议',
    next_follow_up_date    DATE                               COMMENT '下次随访日期',
    doctor_id              BIGINT                             COMMENT '随访医生ID',
    create_time            DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_patient_id (patient_id),
    INDEX idx_follow_up_date (follow_up_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访记录表';
```

### 3. t_alert（预警表）

```sql
CREATE TABLE t_alert (
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
    patient_id    BIGINT       NOT NULL              COMMENT '患者ID',
    alert_type    VARCHAR(30)  NOT NULL              COMMENT 'HIGH_RISK/LOST_FOLLOW_UP',
    alert_level   VARCHAR(10)  NOT NULL              COMMENT 'RED/YELLOW',
    alert_reason  TEXT                               COMMENT '触发原因',
    is_resolved   TINYINT      DEFAULT 0             COMMENT '0未处理/1已处理',
    resolve_time  DATETIME                           COMMENT '处理时间',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_patient_alert (patient_id, alert_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录表';
```

### 4. t_alert_rule（异常规则表 — 5号会补充，你先建空表）

```sql
CREATE TABLE t_alert_rule (
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
    rule_name     VARCHAR(100) NOT NULL              COMMENT '规则名称',
    indicator     VARCHAR(50)  NOT NULL              COMMENT '指标：systolic_bp/diastolic_bp/fasting_glucose等',
    operator      VARCHAR(10)  NOT NULL              COMMENT '运算符：>/</>=/<=/=',
    threshold     DECIMAL(8,2) NOT NULL              COMMENT '阈值',
    alert_level   VARCHAR(10)  NOT NULL              COMMENT '触发等级：RED/YELLOW',
    is_active     TINYINT      DEFAULT 1             COMMENT '1启用/0禁用',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常规则表';
```

### 5. t_user（用户表）

```sql
CREATE TABLE t_user (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE         COMMENT '用户名',
    password    VARCHAR(200) NOT NULL                COMMENT 'BCrypt加密',
    real_name   VARCHAR(50)  NOT NULL                COMMENT '真实姓名',
    role        VARCHAR(20)  NOT NULL                COMMENT 'ADMIN/DOCTOR',
    phone       VARCHAR(20)                          COMMENT '手机号',
    status      TINYINT      DEFAULT 1               COMMENT '1正常/0禁用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 6. t_operation_log（操作日志表）

```sql
CREATE TABLE t_operation_log (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT                                COMMENT '操作用户ID',
    username    VARCHAR(50)                           COMMENT '操作用户名',
    operation   VARCHAR(100)                          COMMENT '操作描述',
    target_type VARCHAR(50)                           COMMENT '对象类型',
    target_id   BIGINT                                COMMENT '对象ID',
    ip_address  VARCHAR(50)                           COMMENT 'IP地址',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
```

**规则**：
- 所有表名用 `t_` 前缀
- 所有表必须有 `create_time` 字段
- 主键统一 `id BIGINT AUTO_INCREMENT`
- 软删除用 `status` 字段，不用 `DELETE` 语句
- 索引命名：`idx_表名_字段名`

---

## 二、Spring Boot 项目结构

```
backend/
├── pom.xml                                          # Maven 依赖
├── src/main/java/com/example/followup/
│   ├── FollowUpApplication.java                     # 启动类
│   ├── config/
│   │   ├── SecurityConfig.java                      # Spring Security + JWT
│   │   ├── CorsConfig.java                          # 跨域
│   │   └── Knife4jConfig.java                       # Swagger
│   ├── controller/
│   │   ├── HealthController.java                     # 健康检查（本周必须）
│   │   ├── AuthController.java                       # 登录/登出（本周必须）
│   │   └── PatientController.java                    # 患者 CRUD（本周必须）
│   ├── service/
│   │   ├── PatientService.java                       # 接口
│   │   └── impl/PatientServiceImpl.java              # 实现
│   ├── mapper/
│   │   └── PatientMapper.java                        # MyBatis-Plus BaseMapper
│   ├── entity/
│   │   └── Patient.java                              # 实体类
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   └── PatientQuery.java
│   │   └── response/
│   │       ├── Result.java                           # 统一返回体
│   │       └── PageResponse.java                     # 分页响应
│   ├── exception/
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   └── util/
│       └── JwtUtil.java
└── src/main/resources/
    ├── application.yml                               # 配置
    └── db/
        └── schema.sql                                # 建表脚本
```

---

## 三、代码标准

### 统一返回体 Result（必须用，不要自己发明）

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private T data;
    private String message;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, data, "success");
    }
    public static <T> Result<T> success() {
        return new Result<>(200, null, "success");
    }
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, null, message);
    }
}
```

### 健康检查（本周验收标志）

```java
@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("ok");
    }
}
// 启动后访问 http://localhost:8080/api/health
// 返回 {"code":200,"data":"ok","message":"success"}
```

### 患者 Entity

```java
@Data
@TableName("t_patient")
public class Patient {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String phone;
    private String idCard;
    private String address;
    private String diseaseType;
    private String medicalHistory;
    private String medicationInfo;
    private Long doctorId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

### PatientQuery（请求参数）

```java
@Data
public class PatientQuery {
    private Integer page = 1;
    private Integer size = 20;
    private String name;
    private String diseaseType;
}
```

### Controller 范例

```java
@RestController
@RequestMapping("/api/patients")
@Api(tags = "患者管理")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    @ApiOperation("分页查询患者")
    public Result<PageResponse<Patient>> list(@Valid PatientQuery query) {
        return Result.success(patientService.listPatients(query));
    }

    @PostMapping
    @ApiOperation("新增患者")
    public Result<Void> add(@Valid @RequestBody Patient patient) {
        patientService.addPatient(patient);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑患者")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        patient.setId(id);
        patientService.updatePatient(patient);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除患者（软删除）")
    public Result<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return Result.success();
    }
}
```

### Service 软删除实现

```java
// 删除 = 将 status 设为 0，不是执行 DELETE 语句
public void deletePatient(Long id) {
    Patient patient = new Patient();
    patient.setId(id);
    patient.setStatus(0);
    patientMapper.updateById(patient);
}

// 查询时始终过滤 status = 1
wrapper.eq(Patient::getStatus, 1);
```

---

## 四、JWT 登录标准

### 登录接口规范

```
POST /api/auth/login
请求体: { "username": "admin", "password": "123456" }
返回:   { "code": 200, "data": { "token": "eyJhbG..." }, "message": "success" }
```

### 鉴权规则

- 除 `/api/auth/login` 和 `/api/health` 外，所有接口需要 `Authorization: Bearer <token>` 请求头
- token 有效期 24 小时
- 密码使用 BCrypt 加密存储，不能明文
- 初始化一条管理员数据：`username=admin, password=123456(BCrypt加密), role=ADMIN`

---

## 五、本周禁止事项

- ❌ 不要推迟写异常处理——GlobalExceptionHandler 第一周就写好
- ❌ 不要硬编码数据库连接——全部放在 `application.yml`
- ❌ 不要把所有逻辑写在一个 Controller 方法里——严格分层 Controller→Service→Mapper
- ❌ 不要用 `DELETE` 物理删除——全部软删除

---

## 六、与 5 号的协作点

- 5 号会给你 `t_alert_rule` 的完整字段和初始化数据，本周五前合并
- 5 号的脱敏规则需要你配合验证（接口返回的 `phone` 和 `id_card` 是否真的脱敏了）

## 与 2 号的协作点

- 你定义的接口 URL 和返回字段名 → 2 号直接照用
- 本周四前给 2 号一个可调的患者列表接口（哪怕数据是假的），让 2 号能联调
