/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.example.followup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OperationLog 注解定义。
 *
 * @since 2026-08-28
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    /**
     * 执行operation操作。
     *
     * @return 返回值
     */
    String operation();
    /**
     * 执行targetType操作。
     *
     * @return 返回值
     */
    String targetType();
    String detail() default "";
}
