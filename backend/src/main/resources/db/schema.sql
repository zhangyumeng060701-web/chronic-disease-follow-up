-- ============================================================
-- 慢病随访管理系统 - 数据库建表脚本
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS follow_up
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE follow_up;

-- 1. 患者档案表
CREATE TABLE IF NOT EXISTS t_patient (
    id               BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    name             VARCHAR(50)  NOT NULL                    COMMENT '姓名',
    gender           VARCHAR(10)  NOT NULL                    COMMENT '性别：男/女',
    age              INT                                     COMMENT '年龄',
    phone            VARCHAR(20)                              COMMENT '手机号',
    id_card          VARCHAR(18)                              COMMENT '身份证号',
    address          VARCHAR(200)                             COMMENT '住址',
    disease_type     VARCHAR(20)  NOT NULL                    COMMENT '慢病类型：HYPERTENSION/DIABETES/BOTH',
    medical_history  TEXT                                     COMMENT '病史描述',
    medication_info  TEXT                                     COMMENT '用药信息',
    doctor_id        BIGINT                                   COMMENT '责任医生ID',
    status           TINYINT      DEFAULT 1                   COMMENT '1正常/0已删除',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '建档日期',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者档案表';

-- 2. 随访记录表
CREATE TABLE IF NOT EXISTS t_follow_up (
    id                     BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    patient_id             BIGINT       NOT NULL                    COMMENT '患者ID',
    follow_up_date         DATE         NOT NULL                    COMMENT '随访日期',
    follow_up_type         VARCHAR(20)  NOT NULL                    COMMENT '随访方式：门诊/电话/上门',
    systolic_bp            INT                                     COMMENT '收缩压 mmHg',
    diastolic_bp           INT                                     COMMENT '舒张压 mmHg',
    fasting_glucose        DECIMAL(4,1)                            COMMENT '空腹血糖 mmol/L',
    postprandial_glucose   DECIMAL(4,1)                            COMMENT '餐后血糖 mmol/L',
    medication_adherence   VARCHAR(20)                             COMMENT '用药依从性：规律/间断/不服药',
    symptoms               TEXT                                    COMMENT '症状描述',
    advice                 TEXT                                    COMMENT '随访建议',
    next_follow_up_date    DATE                                    COMMENT '下次随访日期',
    doctor_id              BIGINT                                  COMMENT '随访医生ID',
    create_time            DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    INDEX idx_patient_id (patient_id),
    INDEX idx_follow_up_date (follow_up_date),
    INDEX idx_next_follow_up (next_follow_up_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访记录表';

-- 3. 预警记录表
CREATE TABLE IF NOT EXISTS t_alert (
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    patient_id    BIGINT       NOT NULL                    COMMENT '患者ID',
    alert_type    VARCHAR(30)  NOT NULL                    COMMENT '预警类型：HIGH_RISK/LOST_FOLLOW_UP',
    alert_level   VARCHAR(10)  NOT NULL                    COMMENT '预警等级：RED/YELLOW',
    alert_reason  TEXT                                    COMMENT '触发原因',
    is_resolved   TINYINT      DEFAULT 0                   COMMENT '0未处理/1已处理',
    resolve_time  DATETIME                                COMMENT '处理时间',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    INDEX idx_patient_alert (patient_id, alert_type, is_resolved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录表';

-- 4. 异常规则表
CREATE TABLE IF NOT EXISTS t_alert_rule (
    id            BIGINT        PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    rule_name     VARCHAR(100)  NOT NULL                    COMMENT '规则名称',
    indicator     VARCHAR(50)   NOT NULL                    COMMENT '指标字段',
    operator      VARCHAR(10)   NOT NULL                    COMMENT '运算符：>=',
    threshold     DECIMAL(8,2)  NOT NULL                    COMMENT '阈值',
    alert_level   VARCHAR(10)   NOT NULL                    COMMENT '触发等级：RED/YELLOW',
    is_active     TINYINT       DEFAULT 1                   COMMENT '1启用/0禁用',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常规则表';

-- 初始化异常规则
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

-- 5. 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL UNIQUE              COMMENT '用户名',
    password    VARCHAR(200) NOT NULL                     COMMENT 'BCrypt加密存储',
    real_name   VARCHAR(50)  NOT NULL                     COMMENT '真实姓名',
    role        VARCHAR(20)  NOT NULL                     COMMENT '角色：ADMIN/DOCTOR',
    phone       VARCHAR(20)                              COMMENT '手机号',
    status      TINYINT      DEFAULT 1                    COMMENT '1正常/0禁用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 6. 操作日志表
CREATE TABLE IF NOT EXISTS t_operation_log (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id     BIGINT                                   COMMENT '操作用户ID',
    username    VARCHAR(50)                              COMMENT '操作用户名',
    operation   VARCHAR(100)                             COMMENT '操作描述',
    target_type VARCHAR(50)                              COMMENT '对象类型',
    target_id   BIGINT                                   COMMENT '对象ID',
    ip_address  VARCHAR(50)                              COMMENT 'IP地址',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP    COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
