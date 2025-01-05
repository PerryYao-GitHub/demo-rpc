package com.ypy.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * mock proxy, jdk dynamic proxy
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * generate mock object
     *
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        // 8 primitive type
        if (type == boolean.class || type == Boolean.class) return false;
        if (type == byte.class || type == Byte.class) return (byte) 0;
        if (type == short.class || type == Short.class) return (short) 0;
        if (type == char.class || type == Character.class) return (char) 0;
        if (type == int.class || type == Integer.class) return 0;
        if (type == long.class || type == Long.class) return 0L;
        if (type == float.class || type == Float.class) return 0.0f;
        if (type == double.class || type == Double.class) return 0.0d;

        // handle common type
        if (type == String.class) return "//mock";
        if (type == List.class) return Collections.emptyList();
        if (type == Map.class) return Collections.emptyMap();
        if (type == Set.class) return Collections.emptySet();

        // handle self-defined class
        try {
            Object instance = type.getDeclaredConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                field.set(instance, getDefaultObject(fieldType)); // recursion
            }
            return instance;
        } catch (Exception e) {
            log.warn("Failed to create mock object for type: {}", type.getName(), e);
            return null;
        }
    }
}
