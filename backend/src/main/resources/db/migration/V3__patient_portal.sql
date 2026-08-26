-- 患者自测指标：用于趋势分析、预警和随访判断
CREATE TABLE IF NOT EXISTS t_patient_vitals (
    id           BIGINT        PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id   BIGINT        NOT NULL                   COMMENT '患者ID',
    metric_type  VARCHAR(40)   NOT NULL                   COMMENT '指标类型：SYSTOLIC_BP/DIASTOLIC_BP/FASTING_GLUCOSE/POSTPRANDIAL_GLUCOSE',
    metric_value DECIMAL(8,2)  NOT NULL                   COMMENT '指标值',
    measured_at  DATETIME      NOT NULL                   COMMENT '测量时间',
    source_type  VARCHAR(20)   NOT NULL DEFAULT 'PATIENT' COMMENT '来源：PATIENT/DOCTOR/DEVICE',
    remark       VARCHAR(500)                             COMMENT '备注',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    INDEX idx_vitals_patient (patient_id, measured_at),
    INDEX idx_vitals_type (metric_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者指标表';

-- 消息中心：站内信 / 短信 / 微信模板消息
CREATE TABLE IF NOT EXISTS t_message (
    id            BIGINT        PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    recipient_type VARCHAR(20)  NOT NULL                   COMMENT '接收方类型：PATIENT/DOCTOR/ADMIN',
    recipient_id  BIGINT        NOT NULL                   COMMENT '接收方ID',
    channel       VARCHAR(20)   NOT NULL                   COMMENT '渠道：IN_APP/SMS/WECHAT',
    title         VARCHAR(100)  NOT NULL                   COMMENT '标题',
    content       VARCHAR(1000) NOT NULL                   COMMENT '内容',
    template_code VARCHAR(50)                             COMMENT '模板编码',
    status        VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/SENT/FAILED/READ',
    read_time     DATETIME                                COMMENT '阅读时间',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    INDEX idx_message_recipient (recipient_type, recipient_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 问卷定义：content 保存 JSON 结构化题目
CREATE TABLE IF NOT EXISTS t_questionnaire (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    code        VARCHAR(50)   NOT NULL UNIQUE             COMMENT '问卷编码',
    title       VARCHAR(100)  NOT NULL                   COMMENT '问卷标题',
    description VARCHAR(500)                             COMMENT '问卷说明',
    content     TEXT          NOT NULL                   COMMENT '题目JSON',
    is_active   TINYINT       NOT NULL DEFAULT 1         COMMENT '1启用/0停用',
    create_time DATETIME      DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    update_time DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷表';

-- 问卷提交记录
CREATE TABLE IF NOT EXISTS t_questionnaire_submission (
    id               BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    questionnaire_id BIGINT       NOT NULL                   COMMENT '问卷ID',
    patient_id       BIGINT       NOT NULL                   COMMENT '患者ID',
    answer_json      TEXT         NOT NULL                   COMMENT '答案JSON',
    submit_time      DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '提交时间',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    INDEX idx_submission_questionnaire (questionnaire_id),
    INDEX idx_submission_patient (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问卷提交表';

-- 随访来源：支持医生录入和患者端自报
ALTER TABLE t_follow_up
    ADD COLUMN source_type VARCHAR(20) DEFAULT 'DOCTOR' COMMENT '来源：DOCTOR/PATIENT/DEVICE';

INSERT INTO t_questionnaire (code, title, description, content)
VALUES
    ('WEEKLY_SYMPTOM', '每周症状自评', '请根据最近一周情况完成自评',
     '[{"key":"headache","label":"头痛","type":"radio","options":["无","轻度","中度","重度"]},{"key":"dizziness","label":"头晕","type":"radio","options":["无","轻度","中度","重度"]},{"key":"palpitation","label":"心悸","type":"radio","options":["无","轻度","中度","重度"]}]'),
    ('MEDICATION_ADHERENCE', '用药依从性问卷', '请评估本周用药情况',
     '[{"key":"adherence","label":"本周用药情况","type":"radio","options":["规律用药","间断用药","未用药"]},{"key":"sideEffect","label":"是否出现不适","type":"radio","options":["无","轻微","明显"]}]');
