package com.example.followup.constant;

public final class DomainConstants {

    private DomainConstants() {
    }

    public static final String DISEASE_HYPERTENSION = "HYPERTENSION";
    public static final String DISEASE_DIABETES = "DIABETES";
    public static final String DISEASE_BOTH = "BOTH";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DOCTOR = "DOCTOR";

    public static final String ALERT_TYPE_HIGH_RISK = "HIGH_RISK";
    public static final String ALERT_TYPE_LOST_FOLLOW_UP = "LOST_FOLLOW_UP";

    public static final String ALERT_LEVEL_RED = "RED";
    public static final String ALERT_LEVEL_YELLOW = "YELLOW";

    public static final String RISK_LOW = "LOW";
    public static final String RISK_MEDIUM = "MEDIUM";
    public static final String RISK_HIGH = "HIGH";

    public static final String PLAN_STATUS_ACTIVE = "ACTIVE";
    public static final String PLAN_STATUS_PAUSED = "PAUSED";
    public static final String PLAN_STATUS_COMPLETED = "COMPLETED";

    public static final String TASK_STATUS_PENDING = "PENDING";
    public static final String TASK_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String TASK_STATUS_CONTACTED = "CONTACTED";
    public static final String TASK_STATUS_COMPLETED = "COMPLETED";
    public static final String TASK_STATUS_CANCELED = "CANCELED";

    public static final String ALERT_STATUS_PENDING = "PENDING";
    public static final String ALERT_STATUS_CONTACTED = "CONTACTED";
    public static final String ALERT_STATUS_RESOLVED = "RESOLVED";
    public static final String ALERT_STATUS_REFERRED = "REFERRED";
}
