package com.yeahmobi.embabel.config;

import com.yeahmobi.embabel.annotation.FieldMeta;
import com.yeahmobi.embabel.service.MetaFieldGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Arrays;

/**
 * 自动扫描指定包下的类，并注册到 MetaFieldGenerator
 * 支持扫描：
 *  - @Entity 注解的实体类
 *  - @FieldMeta 注解的 DTO 类
 * 不依赖 Reflections
 */
@Configuration
public class MetaAutoRegisterConfig {

    private final MetaFieldGenerator metaFieldGenerator;

    // 支持多包扫描
    private static final String[] BASE_PACKAGES = {
            "com.yeahmobi.embabel.model",
            "com.yeahmobi.embabel.dto"
    };

    public MetaAutoRegisterConfig(MetaFieldGenerator metaFieldGenerator) {
        this.metaFieldGenerator = metaFieldGenerator;
    }

    @PostConstruct
    public void registerMetaAnnotatedClasses() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class)); // 扫描所有类

        for (BeanDefinition bd : scanner.findCandidateComponents("com.yeahmobi.embabel.model")) {
            Class<?> clazz = Class.forName(bd.getBeanClassName());
            boolean hasFieldMeta = Arrays.stream(clazz.getDeclaredFields())
                    .anyMatch(f -> f.isAnnotationPresent(FieldMeta.class));
            if (hasFieldMeta) {
                metaFieldGenerator.registerClass(clazz);
            }
        }
    }
}


