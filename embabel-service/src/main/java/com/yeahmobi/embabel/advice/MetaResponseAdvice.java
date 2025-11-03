package com.yeahmobi.embabel.advice;

import com.yeahmobi.embabel.service.MetaFieldGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.ReflectionHelper.isList;

@ControllerAdvice
public class MetaResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private MetaFieldGenerator metaFieldGenerator;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true; // 总是启用
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (body == null) return null;

        String include = request.getHeaders().getFirst("X-Include-Meta");
        boolean withMeta = "true".equalsIgnoreCase(include);

        Object data = body;
        Class<?> targetClass;

        if (body instanceof List<?> list && !list.isEmpty()) {
            targetClass = list.get(0).getClass();
        } else {
            targetClass = body.getClass();
        }
        if(withMeta){
            List<MetaFieldInfo> meta = buildMeta(targetClass);
            return new ResponseWithMeta<>(data, meta);
        }
        return body;
    }

    private List<MetaFieldInfo> buildMeta(Class<?> clazz) {
        return buildMeta(clazz, new HashSet<>());
    }

    private List<MetaFieldInfo> buildMeta(Class<?> clazz, Set<Class<?>> visited) {
        if (clazz == null || isPrimitiveOrWrapper(clazz) || visited.contains(clazz)) {
            return null; // 避免死循环
        }
        visited.add(clazz);

        return metaFieldGenerator.getMetaByClass(clazz).stream()
                .map(f -> {
                    List<MetaFieldInfo> nested = null;
                    Class<?> fieldType = f.typeClass();

                    // 支持 List<T>
                    if (isList(fieldType)) {
                        Class<?> itemType = extractListItemType(clazz, f.fieldName());
                        if (!isPrimitiveOrWrapper(itemType)) {
                            nested = buildMeta(itemType, visited);
                        }
                    } else if (!isPrimitiveOrWrapper(fieldType)) {
                        nested = buildMeta(fieldType, visited);
                    }

                    return new MetaFieldInfo(
                            f.fieldName(), f.type(), f.desc(), nested
                    );
                })
                .collect(Collectors.toList());
    }

    private Class<?> extractListItemType(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            Type type = field.getGenericType();
            if (type instanceof ParameterizedType pt) {
                Type actual = pt.getActualTypeArguments()[0];
                if (actual instanceof Class<?> itemClass) {
                    return itemClass;
                }
            }
        } catch (NoSuchFieldException ignored) {}
        return Object.class; // 没有泛型信息时返回 Object
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == String.class ||
                Number.class.isAssignableFrom(clazz) ||
                clazz == Boolean.class ||
                clazz == Character.class;
    }
}

