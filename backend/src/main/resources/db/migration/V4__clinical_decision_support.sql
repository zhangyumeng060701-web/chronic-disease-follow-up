-- 预警规则扩展：支持阈值/趋势/合并症/用药四类规则
ALTER TABLE t_alert_rule
    ADD COLUMN rule_type VARCHAR(20) DEFAULT 'THRESHOLD' COMMENT '规则类型：THRESHOLD/TREND/COMORBIDITY/MEDICATION',
    ADD COLUMN condition_json TEXT COMMENT '结构化条件',
    ADD COLUMN recommended_actions TEXT COMMENT '建议措施',
    ADD COLUMN recheck_items TEXT COMMENT '复查项目',
    ADD COLUMN referral_conditions TEXT COMMENT '转诊条件',
    ADD COLUMN evidence_source VARCHAR(255) COMMENT '指南来源',
    ADD COLUMN risk_level VARCHAR(20) DEFAULT 'MEDIUM' COMMENT '风险分层';

-- 预警记录携带建议、复查、转诊与证据来源
ALTER TABLE t_alert
    ADD COLUMN recommended_actions TEXT COMMENT '建议措施',
    ADD COLUMN recheck_items TEXT COMMENT '复查项目',
    ADD COLUMN referral_conditions TEXT COMMENT '转诊条件',
    ADD COLUMN evidence_source VARCHAR(255) COMMENT '指南来源',
    ADD COLUMN risk_level VARCHAR(20) COMMENT '风险分层';

-- 患者风险分层记录
CREATE TABLE IF NOT EXISTS t_patient_risk_assessment (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id   BIGINT       NOT NULL                   COMMENT '患者ID',
    risk_level   VARCHAR(20)  NOT NULL                   COMMENT '风险等级：HIGH/MEDIUM/STABLE',
    score        INT          NOT NULL DEFAULT 0         COMMENT '风险评分',
    evidence     VARCHAR(500)                            COMMENT '判定依据',
    assessed_by  BIGINT                                  COMMENT '评估人ID',
    assessed_at  DATETIME     NOT NULL                   COMMENT '评估时间',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    INDEX idx_risk_patient (patient_id, assessed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者风险分层表';

-- AI 随访建议：医生确认后才落库到随访记录
CREATE TABLE IF NOT EXISTS t_follow_up_suggestion (
    id            BIGINT        PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_id    BIGINT        NOT NULL                   COMMENT '患者ID',
    follow_up_id  BIGINT                                   COMMENT '关联随访ID',
    content       VARCHAR(1000) NOT NULL                   COMMENT '建议内容',
    source        VARCHAR(20)   NOT NULL DEFAULT 'AI'      COMMENT '来源：AI/DOCTOR',
    status        VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/CONFIRMED/REJECTED',
    doctor_id     BIGINT                                   COMMENT '确认医生ID',
    confirm_time  DATETIME                                 COMMENT '确认时间',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    INDEX idx_suggestion_patient (patient_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI随访建议表';

UPDATE t_alert_rule SET
    rule_type = 'THRESHOLD',
    recommended_actions = '复核血压并评估用药依从性；连续异常时调整随访计划',
    recheck_items = '血压、心率、肾功能',
    referral_conditions = '收缩压≥180或舒张压≥110且药物治疗后仍持续异常',
    evidence_source = '中国高血压防治指南',
    risk_level = 'HIGH'
WHERE rule_name IN ('收缩压≥180高危', '舒张压≥110高危');

UPDATE t_alert_rule SET
    rule_type = 'THRESHOLD',
    recommended_actions = '加强血压监测，复核用药方案与生活方式',
    recheck_items = '血压、体重、血糖',
    referral_conditions = '多次测量仍超过目标值',
    evidence_source = '中国高血压防治指南',
    risk_level = 'MEDIUM'
WHERE rule_name IN ('收缩压≥160警告', '舒张压≥100警告', '收缩压≥140关注', '舒张压≥90关注');

UPDATE t_alert_rule SET
    rule_type = 'THRESHOLD',
    recommended_actions = '复核血糖监测与用药，评估饮食运动依从性',
    recheck_items = '空腹血糖、糖化血红蛋白、肾功能',
    referral_conditions = '空腹血糖≥11.1或餐后血糖≥16.7且持续异常',
    evidence_source = '中国2型糖尿病防治指南',
    risk_level = 'HIGH'
WHERE rule_name IN ('空腹血糖≥11.1高危', '餐后血糖≥16.7高危');

UPDATE t_alert_rule SET
    rule_type = 'THRESHOLD',
    recommended_actions = '加强血糖监测，复核用药与生活方式',
    recheck_items = '血糖、糖化血红蛋白',
    referral_conditions = '持续高于目标值',
    evidence_source = '中国2型糖尿病防治指南',
    risk_level = 'MEDIUM'
WHERE rule_name IN ('空腹血糖≥7.0警告', '餐后血糖≥11.1警告');

-- 趋势、合并症、用药规则
INSERT INTO t_alert_rule
    (rule_name, indicator, operator, threshold, alert_level, rule_type, condition_json,
     recommended_actions, recheck_items, referral_conditions, evidence_source, risk_level)
VALUES
    ('血压连续升高趋势', 'systolic_bp', 'TREND', 20, 'YELLOW', 'TREND',
     '{"indicator":"systolic_bp","delta":20}',
     '复核血压记录，评估药物与生活方式变化', '动态血压、心率', '血压持续升高且药物调整后仍异常',
     '中国高血压防治指南', 'MEDIUM'),
    ('高血压合并糖尿病', 'disease_type', '=', 0, 'YELLOW', 'COMORBIDITY',
     '{"diseaseTypes":["HYPERTENSION","DIABETES"]}',
     '优先控制血压并加强血糖管理，评估心肾风险', '糖化血红蛋白、肾功能、尿微量白蛋白',
     '出现靶器官损害证据', '中国高血压防治指南/中国2型糖尿病防治指南', 'HIGH'),
    ('用药依从性异常', 'medication_adherence', '=', 0, 'YELLOW', 'MEDICATION',
     '{"adherenceValues":["间断","不服药"]}',
     '了解停药原因，调整用药方案并加强健康教育', '血压、血糖、药盒清点',
     '出现明显不适或严重不良反应', '国家基层高血压防治管理指南', 'MEDIUM');
