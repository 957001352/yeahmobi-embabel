package com.yeahmobi.embabel.service;

import com.yeahmobi.embabel.advice.MetaFieldInfo;
import com.yeahmobi.embabel.annotation.FieldMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Meta 元数据服务
 * 提供类级别与全局的字段说明查询
 *
 * 支持：
 * - 获取所有注册类的 Meta 字段信息
 * - 按类名查询单个 Meta 块
 * - 自动从 MetaFieldGenerator 获取并缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaGeneratorService {

    private final MetaFieldGenerator metaFieldGenerator;

    /**
     * 缓存所有已生成的 Meta 字段信息（避免重复反射）
     * key: simpleClassName
     */
    private final Map<String, Map<String, MetaFieldInfo>> metaCache = new ConcurrentHashMap<>();


    /**
     * 获取所有已注册类的 Meta 字段说明
     */
    public Map<String, Map<String, MetaFieldInfo>> getAllMeta() {
        Map<String, Map<String, MetaFieldInfo>> result = new LinkedHashMap<>();
        metaFieldGenerator.getRegisteredClasses().forEach((simpleName, clazz) -> {
            result.put(simpleName, getMetaFromCacheOrGenerate(simpleName, clazz));
        });
        return result;
    }


    /**
     * 根据简单类名获取 Meta 字段信息
     */
    public Optional<Map<String, MetaFieldInfo>> getMetaBySimpleClassName(String simpleName) {
        try {
            var clazzOpt = Optional.ofNullable(metaFieldGenerator.getRegisteredClasses().get(simpleName));
            if (clazzOpt.isEmpty()) {
                log.warn("未注册的类名: {}", simpleName);
                return Optional.empty();
            }
            var clazz = clazzOpt.get();
            return Optional.of(getMetaFromCacheOrGenerate(simpleName, clazz));
        } catch (Exception e) {
            log.error("获取Meta失败: {}", simpleName, e);
            return Optional.empty();
        }
    }


    /**
     * 从缓存获取或生成 Meta 字段信息
     */
    private Map<String, MetaFieldInfo> getMetaFromCacheOrGenerate(String simpleName, Class<?> clazz) {
        return metaCache.computeIfAbsent(simpleName, key -> {
            log.info("生成 Meta 字段说明块: {}", clazz.getSimpleName());
            var metas = metaFieldGenerator.getMetaByClass(clazz);
            Map<String, MetaFieldInfo> fieldMap = new LinkedHashMap<>();
            for (MetaFieldGenerator.FieldMetaInfo meta : metas) {
                fieldMap.put(meta.fieldName(), new MetaFieldInfo(meta.fieldName(), meta.type(), meta.desc()));
            }
            return fieldMap;
        });
    }

}

