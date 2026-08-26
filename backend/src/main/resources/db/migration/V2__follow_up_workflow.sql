-- 随访计划：基于患者风险等级与临床模板生成长期随访节奏
CREATE TABLE IF NOT EXISTS t_follow_up_plan (
    id                       BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id               BIGINT       NOT NULL                   COMMENT '患者ID',
    risk_level               VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM'  COMMENT '风险等级：LOW/MEDIUM/HIGH',
    follow_up_frequency_days INT          NOT NULL                   COMMENT '随访频率（天）',
    follow_up_type           VARCHAR(20)  NOT NULL                   COMMENT '随访方式：门诊/电话/上门/微信',
    next_follow_up_date      DATE         NOT NULL                   COMMENT '下次随访日期',
    status                   VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'  COMMENT '计划状态：ACTIVE/PAUSED/COMPLETED',
    doctor_id                BIGINT                                  COMMENT '责任医生ID',
    remark                   VARCHAR(500)                            COMMENT '备注',
    create_time              DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    update_time              DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_plan_patient (patient_id),
    INDEX idx_plan_doctor (doctor_id),
    INDEX idx_plan_next_date (next_follow_up_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访计划表';

-- 随访任务：从计划生成的可执行工作项
CREATE TABLE IF NOT EXISTS t_follow_up_task (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    plan_id        BIGINT                                   COMMENT '随访计划ID',
    patient_id     BIGINT       NOT NULL                   COMMENT '患者ID',
    task_type      VARCHAR(20)  NOT NULL DEFAULT 'FOLLOW_UP' COMMENT '任务类型：FOLLOW_UP/ALERT_ESCALATION',
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/IN_PROGRESS/CONTACTED/COMPLETED/CANCELED',
    owner_id       BIGINT                                  COMMENT '责任人ID',
    channel        VARCHAR(20)                              COMMENT '渠道：门诊/电话/上门/微信/短信',
    due_date       DATE         NOT NULL                   COMMENT '截止日期',
    completed_time DATETIME                                COMMENT '完成时间',
    remark         VARCHAR(500)                            COMMENT '备注',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_plan (plan_id),
    INDEX idx_task_patient (patient_id),
    INDEX idx_task_owner (owner_id),
    INDEX idx_task_due (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访任务表';

-- 随访模板：为不同病种/场景快速创建随访计划
CREATE TABLE IF NOT EXISTS t_follow_up_template (
    id                BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    template_code     VARCHAR(50)  NOT NULL UNIQUE            COMMENT '模板编码',
    template_name     VARCHAR(100) NOT NULL                   COMMENT '模板名称',
    risk_level        VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM'  COMMENT '默认风险等级',
    frequency_days    INT          NOT NULL                   COMMENT '默认随访频率（天）',
    follow_up_type    VARCHAR(20)  NOT NULL                   COMMENT '默认随访方式',
    default_content   TEXT                                    COMMENT '默认随访内容建议',
    is_active         TINYINT      NOT NULL DEFAULT 1         COMMENT '1启用/0停用',
    create_time       DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    update_time       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访模板表';

INSERT INTO t_follow_up_template
    (template_code, template_name, risk_level, frequency_days, follow_up_type, default_content)
VALUES
    ('HYPERTENSION', '高血压随访', 'HIGH', 14, '电话',
     '血压控制评估、用药依从性确认、生活方式指导、下次随访安排。'),
    ('DIABETES', '糖尿病随访', 'HIGH', 14, '门诊',
     '血糖监测评估、用药依从性确认、饮食运动指导、并发症风险筛查。'),
    ('DISCHARGE', '出院后随访', 'MEDIUM', 7, '电话',
     '出院后恢复情况、症状变化、用药执行情况、复诊提醒。'),
    ('MATERNAL', '孕产妇随访', 'MEDIUM', 7, '门诊',
     '孕期风险评估、体重与血压监测、产检计划提醒、异常情况转诊。');

-- 预警工作流：未处理 -> 已联系 -> 已处理 / 转门诊
ALTER TABLE t_alert
    ADD COLUMN alert_status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/CONTACTED/RESOLVED/REFERRED',
    ADD COLUMN contact_time DATETIME NULL COMMENT '首次联系时间',
    ADD COLUMN referral_reason VARCHAR(500) NULL COMMENT '转门诊原因';

UPDATE t_alert
SET alert_status = CASE WHEN is_resolved = 1 THEN 'RESOLVED' ELSE 'PENDING' END
WHERE alert_status IS NULL;
