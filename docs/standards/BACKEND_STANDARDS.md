# 后端开发标准（Spring Boot + MyBatis-Plus）

> 后端 D 和 E 必读 | 不按这个规范写的代码，PR 不给过

---

## 一、项目包结构

```
backend/src/main/java/com/example/followup/
├── FollowUpApplication.java        # 启动类
├── config/                         # 配置类
│   ├── SecurityConfig.java         # Spring Security 配置
│   ├── CorsConfig.java             # 跨域配置
│   └── Knife4jConfig.java          # Swagger 文档配置
├── controller/                     # 控制器（接口入口，只做参数校验和调用 Service）
│   ├── AuthController.java
│   ├── PatientController.java
│   ├── FollowUpController.java
│   ├── AlertController.java
│   ├── StatsController.java
│   └── UserController.java
├── service/                        # 业务逻辑层
│   ├── PatientService.java         # 接口
│   ├── impl/
│   │   └── PatientServiceImpl.java # 实现
│   └── ...
├── mapper/                         # 数据库操作（MyBatis-Plus）
│   ├── PatientMapper.java
│   └── ...
├── entity/                         # 数据库表对应的实体类
│   ├── Patient.java
│   ├── FollowUp.java
│   ├── Alert.java
│   ├── User.java
│   └── OperationLog.java
├── dto/                            # 数据传输对象（请求和响应）
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── PatientQuery.java       # 查询参数
│   │   └── PatientSaveRequest.java # 新增/编辑
│   └── response/
│       ├── PageResponse.java       # 统一分页响应
│       └── Result.java             # 统一返回格式
├── exception/                      # 异常处理
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
└── util/
    └── JwtUtil.java                # JWT 工具类
```

**规则**：
- Controller 只做三件事：接收参数、调 Service、返回结果。**业务逻辑不能写在 Controller 里**
- Service 接口 + ServiceImpl 实现，一对一
- Mapper 继承 MyBatis-Plus 的 `BaseMapper<T>`，不用写 XML
- 请求参数用 `dto/request/`，返回数据用 `dto/response/`，禁止直接返回 entity

---

## 二、命名规范

| 东西 | 规范 | 示例 |
|---|---|---|
| 包名 | 全小写 | `com.example.followup.controller` |
| 类名 | 大驼峰 | `PatientController` |
| 方法名 | 小驼峰 | `getPatientList()` |
| 接口 URL | 短横线 | `/api/follow-ups` |
| 数据库表名 | 下划线+小写 | `t_patient` |
| 数据库字段 | 下划线+小写 | `disease_type` |
| Java 实体字段 | 小驼峰 | `diseaseType` |
| JSON 字段 | 小驼峰 | `"diseaseType"` |

---

## 三、统一返回格式

**所有接口返回值都用 `Result` 包装，不允许直接返回字符串或对象。**

```java
// dto/response/Result.java
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

分页返回：

```java
// dto/response/PageResponse.java
@Data
public class PageResponse<T> {
    private List<T> records;
    private Long total;
    private Integer page;
    private Integer size;
}
```

---

## 四、Controller 标准写法

```java
@RestController
@RequestMapping("/api/patients")
@Api(tags = "患者管理")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    @ApiOperation("分页查询患者列表")
    public Result<PageResponse<PatientVO>> list(PatientQuery query) {
        PageResponse<PatientVO> page = patientService.listPatients(query);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取患者详情")
    public Result<PatientVO> getById(@PathVariable Long id) {
        PatientVO vo = patientService.getPatientById(id);
        return Result.success(vo);
    }

    @PostMapping
    @ApiOperation("新增患者")
    public Result<Void> add(@Valid @RequestBody PatientSaveRequest request) {
        patientService.addPatient(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑患者")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody PatientSaveRequest request) {
        patientService.updatePatient(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除患者")
    public Result<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return Result.success();
    }
}
```

**规则**：
- RESTful URL：`GET /patients`（查）、`POST /patients`（增）、`PUT /patients/{id}`（改）、`DELETE /patients/{id}`（删）
- 用 `@Valid` 做参数校验
- `@ApiOperation` 写清楚这个接口是干嘛的（会自动出现在 Swagger 文档页面上）

---

## 五、数据库表结构（确定字段名）

### 5.1 患者表 t_patient

```sql
CREATE TABLE t_patient (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    name            VARCHAR(50)     NOT NULL                    COMMENT '姓名',
    gender          VARCHAR(10)     NOT NULL                    COMMENT '性别：男/女',
    age             INT                                         COMMENT '年龄',
    phone           VARCHAR(20)                                 COMMENT '手机号',
    id_card         VARCHAR(18)                                 COMMENT '身份证号',
    address         VARCHAR(200)                                COMMENT '住址',
    disease_type    VARCHAR(20)     NOT NULL                    COMMENT '慢病类型：HYPERTENSION/DIABETES/BOTH',
    medical_history TEXT                                        COMMENT '病史描述',
    medication_info TEXT                                        COMMENT '用药信息',
    doctor_id       BIGINT                                      COMMENT '责任医生ID',
    status          TINYINT         DEFAULT 1                   COMMENT '状态：1正常/0已删除',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '建档日期',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '患者档案表';
```

### 5.2 随访记录表 t_follow_up

```sql
CREATE TABLE t_follow_up (
    id                      BIGINT      PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    patient_id              BIGINT      NOT NULL                    COMMENT '患者ID',
    follow_up_date          DATE        NOT NULL                    COMMENT '随访日期',
    follow_up_type          VARCHAR(20) NOT NULL                    COMMENT '随访方式：门诊/电话/上门',
    systolic_bp             INT                                     COMMENT '收缩压(mmHg)',
    diastolic_bp            INT                                     COMMENT '舒张压(mmHg)',
    fasting_glucose         DECIMAL(4,1)                            COMMENT '空腹血糖(mmol/L)',
    postprandial_glucose    DECIMAL(4,1)                            COMMENT '餐后血糖(mmol/L)',
    medication_adherence    VARCHAR(20)                             COMMENT '用药依从性：规律/间断/不服药',
    symptoms                TEXT                                    COMMENT '症状描述',
    advice                  TEXT                                    COMMENT '随访建议',
    next_follow_up_date     DATE                                    COMMENT '下次随访日期',
    doctor_id               BIGINT                                  COMMENT '随访医生ID',
    create_time             DATETIME    DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间'
) COMMENT '随访记录表';
```

### 5.3 预警表 t_alert

```sql
CREATE TABLE t_alert (
    id              BIGINT      PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    patient_id      BIGINT      NOT NULL                    COMMENT '患者ID',
    alert_type      VARCHAR(30) NOT NULL                    COMMENT '预警类型：HIGH_RISK/LOST_FOLLOW_UP',
    alert_level     VARCHAR(10) NOT NULL                    COMMENT '预警等级：RED/YELLOW',
    alert_reason    TEXT                                    COMMENT '触发原因',
    is_resolved     TINYINT     DEFAULT 0                   COMMENT '是否已处理：0未处理/1已处理',
    resolve_time    DATETIME                                COMMENT '处理时间',
    create_time     DATETIME    DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间'
) COMMENT '预警记录表';
```

### 5.4 用户表 t_user

```sql
CREATE TABLE t_user (
    id              BIGINT      PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    username        VARCHAR(50) NOT NULL UNIQUE             COMMENT '用户名',
    password        VARCHAR(200)NOT NULL                    COMMENT '密码(加密存储)',
    real_name       VARCHAR(50) NOT NULL                    COMMENT '真实姓名',
    role            VARCHAR(20) NOT NULL                    COMMENT '角色：ADMIN/DOCTOR',
    phone           VARCHAR(20)                             COMMENT '手机号',
    status          TINYINT     DEFAULT 1                   COMMENT '状态：1正常/0禁用',
    create_time     DATETIME    DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间'
) COMMENT '用户表';
```

### 5.5 操作日志表 t_operation_log

```sql
CREATE TABLE t_operation_log (
    id              BIGINT      PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT                                  COMMENT '操作用户ID',
    username        VARCHAR(50)                             COMMENT '操作用户名',
    operation       VARCHAR(100)                            COMMENT '操作描述',
    target_type     VARCHAR(50)                             COMMENT '操作对象类型',
    target_id       BIGINT                                  COMMENT '操作对象ID',
    ip_address      VARCHAR(50)                             COMMENT 'IP地址',
    create_time     DATETIME    DEFAULT CURRENT_TIMESTAMP   COMMENT '操作时间'
) COMMENT '操作日志表';
```

---

## 六、Entity 标准写法

```java
// entity/Patient.java
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

**规则**：
- Java 类字段用小驼峰（`diseaseType`），MyBatis-Plus 自动映射到数据库的下划线字段（`disease_type`）
- `@TableName` 指定对应的数据库表名
- `@TableId(type = IdType.AUTO)` 声明自增主键

---

## 七、Mapper 标准写法

```java
// mapper/PatientMapper.java
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
    // 简单的 CRUD 不需要写任何方法，BaseMapper 已经提供了
    // 复杂查询在这里自定义方法
    @Select("SELECT * FROM t_patient WHERE doctor_id = #{doctorId} AND status = 1")
    List<Patient> findByDoctorId(Long doctorId);
}
```

---

## 八、Service 标准写法

```java
// service/PatientService.java
public interface PatientService {
    PageResponse<PatientVO> listPatients(PatientQuery query);
    PatientVO getPatientById(Long id);
    void addPatient(PatientSaveRequest request);
    void updatePatient(Long id, PatientSaveRequest request);
    void deletePatient(Long id);
}

// service/impl/PatientServiceImpl.java
@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public PageResponse<PatientVO> listPatients(PatientQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getStatus, 1);  // 只查未删除的
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Patient::getName, query.getName());
        }
        if (StringUtils.hasText(query.getDiseaseType())) {
            wrapper.eq(Patient::getDiseaseType, query.getDiseaseType());
        }

        // 分页查询
        Page<Patient> page = new Page<>(query.getPage(), query.getSize());
        patientMapper.selectPage(page, wrapper);

        // 转换为 VO
        List<PatientVO> voList = page.getRecords().stream()
            .map(this::toVO)
            .collect(Collectors.toList());

        PageResponse<PatientVO> response = new PageResponse<>();
        response.setRecords(voList);
        response.setTotal(page.getTotal());
        response.setPage(query.getPage());
        response.setSize(query.getSize());
        return response;
    }

    private PatientVO toVO(Patient p) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(p, vo);
        // 手机号和身份证脱敏在 Controller 返回时处理
        return vo;
    }
}
```

---

## 九、全局异常处理

```java
// exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return Result.error(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleUnknown(Exception e) {
        log.error("未知错误", e);
        return Result.error(500, "服务器内部错误");
    }
}
```

---

## 十、禁止事项

- ❌ 不要在 Controller 里写业务逻辑，Controller 只能调 Service
- ❌ 不要在 Service 里写 SQL 字符串拼接，用 MyBatis-Plus 的 LambdaQueryWrapper
- ❌ 不要直接返回 Entity 给前端，必须转成 VO/DTO
- ❌ 不要硬编码错误信息，用 `BusinessException`
- ❌ 不要 catch 异常后什么都不做（吞异常），至少要打日志
- ❌ 密码不要明文存数据库，必须 BCrypt 加密
- ❌ 删除操作用软删除（改 status 字段），不用 `DELETE` 语句

---

## 十一、提交前自检清单

- [ ] 所有接口在 Swagger 页面上能看到并测试通过
- [ ] 有参数校验（`@Valid` + `@NotBlank`/`@NotNull` 等）
- [ ] 有统一的异常处理和错误返回格式
- [ ] 没有硬编码的配置值（都放在 `application.yml`）
- [ ] 没有 `System.out.println`，用 `@Slf4j` + `log.info`
