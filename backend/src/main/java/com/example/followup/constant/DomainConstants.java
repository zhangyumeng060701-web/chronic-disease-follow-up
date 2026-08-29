/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.constant;

/**
 * DomainConstants 业务组件。
 *
 * @since 2026-08-28
 */
public final class DomainConstants {
    private DomainConstants() {
    }

    /**
     * 常量：DISEASE_HYPERTENSION。
     */
    public static final String DISEASE_HYPERTENSION = "HYPERTENSION";
    /**
     * 常量：DISEASE_DIABETES。
     */
    public static final String DISEASE_DIABETES = "DIABETES";
    /**
     * 常量：DISEASE_BOTH。
     */
    public static final String DISEASE_BOTH = "BOTH";

    /**
     * 常量：ROLE_ADMIN。
     */
    public static final String ROLE_ADMIN = "ADMIN";
    /**
     * 常量：ROLE_DOCTOR。
     */
    public static final String ROLE_DOCTOR = "DOCTOR";
    /**
     * 常量：ROLE_PATIENT。
     */
    public static final String ROLE_PATIENT = "PATIENT";

    /**
     * 常量：ALERT_TYPE_HIGH_RISK。
     */
    public static final String ALERT_TYPE_HIGH_RISK = "HIGH_RISK";
    /**
     * 常量：ALERT_TYPE_LOST_FOLLOW_UP。
     */
    public static final String ALERT_TYPE_LOST_FOLLOW_UP = "LOST_FOLLOW_UP";

    /**
     * 常量：ALERT_LEVEL_RED。
     */
    public static final String ALERT_LEVEL_RED = "RED";
    /**
     * 常量：ALERT_LEVEL_YELLOW。
     */
    public static final String ALERT_LEVEL_YELLOW = "YELLOW";

    /**
     * 常量：RISK_LOW。
     */
    public static final String RISK_LOW = "LOW";
    /**
     * 常量：RISK_MEDIUM。
     */
    public static final String RISK_MEDIUM = "MEDIUM";
    /**
     * 常量：RISK_HIGH。
     */
    public static final String RISK_HIGH = "HIGH";
    /**
     * 常量：RISK_STABLE。
     */
    public static final String RISK_STABLE = "STABLE";

    /**
     * 常量：PLAN_STATUS_ACTIVE。
     */
    public static final String PLAN_STATUS_ACTIVE = "ACTIVE";
    /**
     * 常量：PLAN_STATUS_PAUSED。
     */
    public static final String PLAN_STATUS_PAUSED = "PAUSED";
    /**
     * 常量：PLAN_STATUS_COMPLETED。
     */
    public static final String PLAN_STATUS_COMPLETED = "COMPLETED";

    /**
     * 常量：TASK_STATUS_PENDING。
     */
    public static final String TASK_STATUS_PENDING = "PENDING";
    /**
     * 常量：TASK_STATUS_IN_PROGRESS。
     */
    public static final String TASK_STATUS_IN_PROGRESS = "IN_PROGRESS";
    /**
     * 常量：TASK_STATUS_CONTACTED。
     */
    public static final String TASK_STATUS_CONTACTED = "CONTACTED";
    /**
     * 常量：TASK_STATUS_COMPLETED。
     */
    public static final String TASK_STATUS_COMPLETED = "COMPLETED";
    /**
     * 常量：TASK_STATUS_CANCELED。
     */
    public static final String TASK_STATUS_CANCELED = "CANCELED";

    /**
     * 常量：ALERT_STATUS_PENDING。
     */
    public static final String ALERT_STATUS_PENDING = "PENDING";
    /**
     * 常量：ALERT_STATUS_CONTACTED。
     */
    public static final String ALERT_STATUS_CONTACTED = "CONTACTED";
    /**
     * 常量：ALERT_STATUS_RESOLVED。
     */
    public static final String ALERT_STATUS_RESOLVED = "RESOLVED";
    /**
     * 常量：ALERT_STATUS_REFERRED。
     */
    public static final String ALERT_STATUS_REFERRED = "REFERRED";

    /**
     * 常量：METRIC_SYSTOLIC_BP。
     */
    public static final String METRIC_SYSTOLIC_BP = "SYSTOLIC_BP";
    /**
     * 常量：METRIC_DIASTOLIC_BP。
     */
    public static final String METRIC_DIASTOLIC_BP = "DIASTOLIC_BP";
    /**
     * 常量：METRIC_FASTING_GLUCOSE。
     */
    public static final String METRIC_FASTING_GLUCOSE = "FASTING_GLUCOSE";
    /**
     * 常量：METRIC_POSTPRANDIAL_GLUCOSE。
     */
    public static final String METRIC_POSTPRANDIAL_GLUCOSE = "POSTPRANDIAL_GLUCOSE";

    /**
     * 常量：VITAL_SOURCE_PATIENT。
     */
    public static final String VITAL_SOURCE_PATIENT = "PATIENT";
    /**
     * 常量：VITAL_SOURCE_DOCTOR。
     */
    public static final String VITAL_SOURCE_DOCTOR = "DOCTOR";

    /**
     * 常量：MESSAGE_CHANNEL_IN_APP。
     */
    public static final String MESSAGE_CHANNEL_IN_APP = "IN_APP";
    /**
     * 常量：MESSAGE_CHANNEL_SMS。
     */
    public static final String MESSAGE_CHANNEL_SMS = "SMS";
    /**
     * 常量：MESSAGE_CHANNEL_WECHAT。
     */
    public static final String MESSAGE_CHANNEL_WECHAT = "WECHAT";
    /**
     * 常量：MESSAGE_STATUS_PENDING。
     */
    public static final String MESSAGE_STATUS_PENDING = "PENDING";
    /**
     * 常量：MESSAGE_STATUS_SENT。
     */
    public static final String MESSAGE_STATUS_SENT = "SENT";
    /**
     * 常量：MESSAGE_STATUS_FAILED。
     */
    public static final String MESSAGE_STATUS_FAILED = "FAILED";
    /**
     * 常量：MESSAGE_STATUS_READ。
     */
    public static final String MESSAGE_STATUS_READ = "READ";

    /**
     * 常量：FOLLOW_UP_SOURCE_DOCTOR。
     */
    public static final String FOLLOW_UP_SOURCE_DOCTOR = "DOCTOR";
    /**
     * 常量：FOLLOW_UP_SOURCE_PATIENT。
     */
    public static final String FOLLOW_UP_SOURCE_PATIENT = "PATIENT";
    /**
     * 常量：FOLLOW_UP_TYPE_PATIENT。
     */
    public static final String FOLLOW_UP_TYPE_PATIENT = "患者端";

    /**
     * 常量：ALERT_RULE_TYPE_THRESHOLD。
     */
    public static final String ALERT_RULE_TYPE_THRESHOLD = "THRESHOLD";
    /**
     * 常量：ALERT_RULE_TYPE_TREND。
     */
    public static final String ALERT_RULE_TYPE_TREND = "TREND";
    /**
     * 常量：ALERT_RULE_TYPE_COMORBIDITY。
     */
    public static final String ALERT_RULE_TYPE_COMORBIDITY = "COMORBIDITY";
    /**
     * 常量：ALERT_RULE_TYPE_MEDICATION。
     */
    public static final String ALERT_RULE_TYPE_MEDICATION = "MEDICATION";

    /**
     * 常量：SUGGESTION_STATUS_PENDING。
     */
    public static final String SUGGESTION_STATUS_PENDING = "PENDING";
    /**
     * 常量：SUGGESTION_STATUS_CONFIRMED。
     */
    public static final String SUGGESTION_STATUS_CONFIRMED = "CONFIRMED";
    /**
     * 常量：SUGGESTION_STATUS_REJECTED。
     */
    public static final String SUGGESTION_STATUS_REJECTED = "REJECTED";
}
