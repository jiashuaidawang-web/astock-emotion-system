package com.astock.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PageFieldLineage {
    String sourceType();
    String sourceTable() default "";
    String sourceColumn() default "";
    String formula() default "";
}
