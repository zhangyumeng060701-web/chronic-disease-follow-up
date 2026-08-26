-- 字典表：统一维护疾病、性别、随访方式、预警等级等业务枚举
CREATE TABLE IF NOT EXISTS t_dictionary (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    dict_type   VARCHAR(50)  NOT NULL                   COMMENT '字典类型',
    dict_code   VARCHAR(50)  NOT NULL                   COMMENT '字典编码',
    dict_label  VARCHAR(100) NOT NULL                   COMMENT '字典名称',
    sort_no     INT          NOT NULL DEFAULT 0         COMMENT '排序',
    is_active   TINYINT      NOT NULL DEFAULT 1         COMMENT '1启用/0停用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP  COMMENT '创建时间',
    UNIQUE KEY uk_dict (dict_type, dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典表';

INSERT INTO t_dictionary (dict_type, dict_code, dict_label, sort_no) VALUES
    ('DISEASE_TYPE', 'HYPERTENSION', '高血压', 1),
    ('DISEASE_TYPE', 'DIABETES', '糖尿病', 2),
    ('DISEASE_TYPE', 'BOTH', '两者皆有', 3),
    ('GENDER', '男', '男', 1),
    ('GENDER', '女', '女', 2),
    ('FOLLOW_UP_TYPE', '门诊', '门诊', 1),
    ('FOLLOW_UP_TYPE', '电话', '电话', 2),
    ('FOLLOW_UP_TYPE', '上门', '上门', 3),
    ('FOLLOW_UP_TYPE', '微信', '微信', 4),
    ('FOLLOW_UP_TYPE', '患者端', '患者端', 5),
    ('ALERT_LEVEL', 'RED', '红色', 1),
    ('ALERT_LEVEL', 'YELLOW', '黄色', 2),
    ('RISK_LEVEL', 'STABLE', '稳定', 1),
    ('RISK_LEVEL', 'LOW', '低风险', 2),
    ('RISK_LEVEL', 'MEDIUM', '中风险', 3),
    ('RISK_LEVEL', 'HIGH', '高风险', 4),
    ('SMOKING', 'NONE', '不吸烟', 1),
    ('SMOKING', 'FORMER', '已戒烟', 2),
    ('SMOKING', 'CURRENT', '吸烟', 3),
    ('DRINKING', 'NONE', '不饮酒', 1),
    ('DRINKING', 'OCCASIONAL', '偶尔饮酒', 2),
    ('DRINKING', 'FREQUENT', '频繁饮酒', 3);

-- 患者档案扩展：体格、生活方式、过敏与用药史
ALTER TABLE t_patient
    ADD COLUMN height_cm DECIMAL(5,1) COMMENT '身高(cm)',
    ADD COLUMN weight_kg DECIMAL(5,1) COMMENT '体重(kg)',
    ADD COLUMN bmi DECIMAL(4,1) COMMENT 'BMI',
    ADD COLUMN smoking VARCHAR(20) COMMENT '吸烟情况',
    ADD COLUMN drinking VARCHAR(20) COMMENT '饮酒情况',
    ADD COLUMN allergy_history VARCHAR(1000) COMMENT '过敏史',
    ADD COLUMN medication_history VARCHAR(1000) COMMENT '用药史';
