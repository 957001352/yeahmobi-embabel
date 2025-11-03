package com.yeahmobi.embabel.service;

import com.yeahmobi.embabel.annotation.FieldMeta;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用 Meta 字段说明块生成器
 * 支持：
 *  - 通过注解、反射、缓存自动生成字段语义描述
 *  - 支持 FieldMeta 注解（含描述、示例值、单位、是否必填）
 *  - 提供注册、批量注册与Meta输出功能
 */
@Component
public class MetaFieldGenerator {

    /**
     * 缓存类与字段元数据
     */
    private final Map<Class<?>, List<FieldMetaInfo>> cache = new ConcurrentHashMap<>();

    /**
     * 注册过的类，方便通过简单类名查找
     */
    private final Map<String, Class<?>> registeredClasses = new ConcurrentHashMap<>();

    // ==========================
    // 注册类逻辑
    // ==========================

    /**
     * 注册类，便于通过简单类名查询
     */
    public void registerClass(Class<?> clazz) {
        registeredClasses.put(clazz.getSimpleName(), clazz);
    }

    /**
     * 批量注册类
     */
    public void registerClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            registerClass(clazz);
        }
    }

    /**
     * 获取所有已注册类
     */
    public Map<String, Class<?>> getRegisteredClasses() {
        return Collections.unmodifiableMap(registeredClasses);
    }

    // ==========================
    // 元信息解析逻辑
    // ==========================

    /**
     * 解析并生成某个类的字段说明块
     */
    public List<FieldMetaInfo> getMetaByClass(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, this::parseClass);
    }

    /**
     * 根据类名字符串获取字段元信息
     */
    public Optional<List<FieldMetaInfo>> getMetaBySimpleClassName(String simpleName) {
        Class<?> clazz = registeredClasses.get(simpleName);
        if (clazz != null) {
            return Optional.of(getMetaByClass(clazz));
        }
        return Optional.empty();
    }

    /**
     * 实际解析逻辑
     */
    private List<FieldMetaInfo> parseClass(Class<?> clazz) {
        List<FieldMetaInfo> list = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            String name = field.getName();
            String type = field.getType().getSimpleName();
            // 获取注解
            FieldMeta metaAnn = AnnotationUtils.findAnnotation(field, FieldMeta.class);

            // 填充字段元信息
            if (metaAnn != null) {
                list.add(new FieldMetaInfo(
                        name,
                        type,
                        metaAnn.value().isEmpty() ? inferFieldMeaning(name) : metaAnn.value(),
                        metaAnn.example(),
                        metaAnn.unit(),
                        field.getType(),
                        metaAnn.required()

                ));
            } else {
                // 无注解时兜底推断
                list.add(new FieldMetaInfo(
                        name,
                        type,
                        inferFieldMeaning(name),
                        "",
                        "",
                        field.getType(),
                        false
                ));
            }
        }
        return list;
    }

    // ==========================
    // 兜底逻辑与格式化输出
    // ==========================

    /**
     * 基于字段名推测语义（兜底）
     */
    private String inferFieldMeaning(String fieldName) {
        String lower = fieldName.toLowerCase();
        if (lower.contains("id")) return "唯一标识";
        if (lower.contains("name")) return "名称或姓名";
        if (lower.contains("date") || lower.contains("time")) return "日期或时间";
        if (lower.contains("amount") || lower.contains("price")) return "金额";
        if (lower.contains("status")) return "状态";
        if (lower.contains("email")) return "邮箱";
        if (lower.contains("phone")) return "电话号码";
        return fieldName;
    }

    /**
     * 输出为 Meta 块结构（可供 Agent 直接使用）
     */
    public String toMetaBlock(Class<?> clazz) {
        List<FieldMetaInfo> metas = getMetaByClass(clazz);
        StringBuilder sb = new StringBuilder();
        sb.append("# Meta 字段说明块 for ").append(clazz.getSimpleName()).append("\n\n");
        for (FieldMetaInfo meta : metas) {
            sb.append("- ").append(meta.fieldName())
                    .append(" (类型: ").append(meta.type()).append(")")
                    .append(meta.required() ? " [必填]" : "")
                    .append(" : ").append(meta.desc());
            if (!meta.unit().isEmpty()) sb.append("，单位：").append(meta.unit());
            if (!meta.example().isEmpty()) sb.append("，示例：").append(meta.example());
            sb.append("\n");
        }
        return sb.toString();
    }

    // ==========================
    // 内部结构定义
    // ==========================

    /**
     * 字段元信息（与注解区分）
     */
    public record FieldMetaInfo(
            String fieldName,
            String type,
            String desc,
            String example,
            String unit,
            Class<?> typeClass,
            boolean required
    ) {}
}

