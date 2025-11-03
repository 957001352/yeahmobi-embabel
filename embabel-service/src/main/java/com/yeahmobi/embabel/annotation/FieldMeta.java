package com.yeahmobi.embabel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface FieldMeta {
    String value() default ""; // 字段中文名或简短说明
    String example() default ""; // 示例值
    String unit() default ""; // 单位
    boolean required() default false; // 是否必填
}
