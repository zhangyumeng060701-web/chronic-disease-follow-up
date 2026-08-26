ALTER TABLE t_follow_up_suggestion
    ADD COLUMN confidence DECIMAL(4,2) COMMENT '置信度',
    ADD COLUMN evidence VARCHAR(1000) COMMENT '判定依据',
    ADD COLUMN risk_level VARCHAR(20) COMMENT '风险分层';
