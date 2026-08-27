ALTER TABLE t_patient
    MODIFY COLUMN phone   VARCHAR(255) COMMENT '手机号（加密存储）',
    MODIFY COLUMN id_card VARCHAR(255) COMMENT '身份证号（加密存储）';
