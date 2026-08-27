ALTER TABLE t_operation_log
    ADD COLUMN detail VARCHAR(500) COMMENT '操作详情，例如导出范围与行数';
