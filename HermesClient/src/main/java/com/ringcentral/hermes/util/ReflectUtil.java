package com.ringcentral.hermes.util;

import java.lang.reflect.Field;

public class ReflectUtil {

    public static Object getValueFromParentClass(Object obj, String parentClassName, String fieldName) {
        try {
            Class<?> parentClass = Class.forName(parentClassName);
            Field[] f = parentClass.getDeclaredFields();
            for (Field field : f) {
                field.setAccessible(true);
                if (fieldName.equals(field.getName())) {
                    return field.get(obj);
                }
            }
            throw new RuntimeException("can't get field value");
        } catch (ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
