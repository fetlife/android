package com.bitlove.fetlife.nativeapp.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static <T> T getValue(String fieldName,Object object) {
        try {
            Field field = object.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

}
