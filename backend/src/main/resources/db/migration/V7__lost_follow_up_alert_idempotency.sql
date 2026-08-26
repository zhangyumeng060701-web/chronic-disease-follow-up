ALTER TABLE t_alert
    ADD COLUMN source_due_date DATE NULL COMMENT '失访预警来源随访到期日' AFTER alert_reason;

CREATE UNIQUE INDEX uk_lost_follow_up_cycle
    ON t_alert (patient_id, alert_type, alert_level, source_due_date);
