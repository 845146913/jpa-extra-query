package com.silencew.plugins.jpa.extern.converter;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.util.ReflectionUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Created by IntelliJ IDEA.
 * author: wangshuiping
 * date: 2021/1/22
 */
public class MapToEntityConverter implements GenericConverter {

    private final static Map<Class<?>, Object> converterCache = new ConcurrentHashMap<>(256);

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        ConvertiblePair pair = new ConvertiblePair(Map.class, Object.class);
        return Collections.singleton(pair);
    }

    @Override
    @SuppressWarnings({"unchecked", "raws"})
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            Map<String, Object> sourceMap = (Map<String, Object>) source;
            Class<?> clazz = Class.forName(targetType.getName());
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            // 设置私有构造访问权限
            constructor.setAccessible(true);
            Object target = constructor.newInstance();
            for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String methodName = getMethodName(key);
                Method[] methods = ReflectionUtils.getDeclaredMethods(targetType.getType());
                Method method = Objects.requireNonNull(Stream.of(methods).filter(me -> me.getName().equalsIgnoreCase(methodName))
                        .findFirst().orElse(null));
                AttributeConverter<?, ?> converter = getAttributeConverter(clazz, method);
                Object valueToUse = caseValue(value, method.getParameterTypes()[0], converter);
                method.invoke(target, valueToUse);
            }
            return target;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return source;
    }

    private AttributeConverter<?, ?> getAttributeConverter(Class<?> clazz, Method method) throws InstantiationException, IllegalAccessException {
        Convert ca = AnnotationUtils.findAnnotation(method, Convert.class);
        if (ca == null) {
            Field field = ReflectionUtils.findField(clazz, getFieldName(method));
            ca = field.getAnnotation(Convert.class);
        }
        AttributeConverter<?, ?> converter = null;
        if (ca != null) {
            if (ca.converter() != void.class) {
                Object o = converterCache.get(ca.converter());
                if (Objects.nonNull(o)) {
                    converter = (AttributeConverter<?, ?>) o;
                } else {
                    converter = (AttributeConverter<?, ?>) ca.converter().newInstance();
                }
            }
        }
        return converter;
    }

    private String getFieldName(Method method) {
        String fieldName = method.getName().substring(method.getName().indexOf("set") + 3);
        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    }

    private String getFieldName(String field) {
        String[] split = field.split("_");
        if (split.length > 0) {
            String property = Stream.of(split).reduce((a, b) -> translateUpperCamelCase(a) + translateUpperCamelCase(b)).get();
            return property;
        }
        return translateUpperCamelCase(field);
    }

    private String getMethodName(String field) {
        return "set" + getFieldName(field);
    }

    @SuppressWarnings({"unchecked", "raws"})
    public Object caseValue(Object o, Class<?> type, AttributeConverter converter) {
        if (o.getClass() == type)
            return o;

        if (o instanceof BigInteger) {
            if (Long.class == type)
                return ((BigInteger) o).longValue();
            if (Integer.class == type)
                return ((BigInteger) o).intValue();
            if (Byte.class == type) {
                return ((BigInteger) o).byteValue();
            }
        }
        if (o instanceof BigDecimal) {
            if (type == Double.class)
                return ((BigDecimal) o).doubleValue();
            if (type == Float.class)
                return ((BigDecimal) o).floatValue();
        }
        if (type.isEnum()) {
            if (converter == null)
                return Enum.valueOf((Class<Enum>) type, String.valueOf(o));
            else
                return converter.convertToEntityAttribute(o);
        }
        return o;
    }

    public String translateUpperCamelCase(String input) {
        if (input == null || input.length() == 0) {
            return input; // garbage in, garbage out
        }
        // Replace first lower-case letter with upper-case equivalent
        char c = input.charAt(0);
        char uc = Character.toUpperCase(c);
        if (c == uc) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input);
        sb.setCharAt(0, uc);
        return sb.toString();
    }

    public String translateSnakeCase(String input) {
        if (input == null) return input; // garbage in, garbage out
        int length = input.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = false;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (i > 0 || c != '_') // skip first starting underscore
            {
                if (Character.isUpperCase(c)) {
                    if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
                        result.append('_');
                        resultLength++;
                    }
                    c = Character.toLowerCase(c);
                    wasPrevTranslated = true;
                } else {
                    wasPrevTranslated = false;
                }
                result.append(c);
                resultLength++;
            }
        }
        return resultLength > 0 ? result.toString() : input;
    }
}
