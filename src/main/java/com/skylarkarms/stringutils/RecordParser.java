package com.skylarkarms.stringutils;


import com.skylarkarms.compactcollections.CompactHashTable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecordParser {
    private static final Pattern FIELD_PATTERN = Pattern.compile("([^,=]+)=([^,]*)");
    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();


    /**
     * For runtimes where the parameters do not align with the standard order of records.
     * Useful for ART.
     * */
    @SuppressWarnings("unchecked")
    public static <T> T parse(Class<T> recordClass, String recordString) {
        if (recordString == null || recordClass == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }

        int startIdx = recordString.indexOf('[');
        int endIdx = recordString.lastIndexOf(']');
        if (startIdx == -1 || endIdx == -1 || startIdx >= endIdx) {
            throw new IllegalArgumentException("Invalid record format: " + recordString);
        }

        String content = recordString.substring(startIdx + 1, endIdx).trim();
        Matcher matcher = FIELD_PATTERN.matcher(content);

        final CompactHashTable<String, String> fieldValue = new CompactHashTable<>();
        while (matcher.find()) {
            fieldValue.addDistinct(
                    matcher.group(1).trim(),
                    matcher.group(2).trim()
            );
        }

        Constructor<?> c = CONSTRUCTOR_CACHE.computeIfAbsent(recordClass,
                tlass -> findCanonicalConstructor(tlass,
                        fieldValue
                )
        );

        Parameter[] ps = c.getParameters();
        Object[] params = new Object[ps.length];
        Arrays.setAll(
                params,
                value -> {
                    Parameter p = ps[value];
                    return convertValue(fieldValue.get(p.getName()), p.getType());
                }
        );
        try {
            return (T) c.newInstance(params);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            if (e instanceof IllegalAccessException) {
                throw new RuntimeException("The canonical constructor cannot be found if the record is not public.", e);
            }
            throw new RuntimeException(e);
        }
    }

    private static <T> Constructor<?> findCanonicalConstructor(
            Class<T> tClass
            , CompactHashTable<String, String> paramsNames
    ) {
        Constructor<?>[] constructors = tClass.getDeclaredConstructors();
        int l = constructors.length;
        for (int i = 0; i < l; i++) {
            Constructor<?> c = constructors[i];
            Parameter[] parameters;
            if ((parameters = c.getParameters()).length == paramsNames.size()) {
                if (allParametersContained(
                        parameters,
                        paramsNames
                )) return c;
            }
        }
        throw new IllegalStateException("Constructor not found.");
    }

    private static <X, Y> boolean allParametersContained(
            Parameter[] parameters,
            CompactHashTable<String, String> paramsNames
    ) {
        int l = parameters.length;
        for (int i = 0; i < l; i++) {
            if (!paramsNames.contains(parameters[i].getName())) return false;
        }
        return true;
    }

    private static final String nulll_string = "null";

    @SuppressWarnings("unchecked")
    private static Object convertValue(String value, Class<?> targetType) {
        // Handle primitives and enums first
        if (targetType.isPrimitive()
        ) {
            if (value == null
            ) {
                throw new IllegalArgumentException("Null or empty value not allowed for type: " + targetType);
            }

            if (targetType == int.class) return Integer.parseInt(value);
            if (targetType == long.class) return Long.parseLong(value);
            if (targetType == double.class) return Double.parseDouble(value);
            if (targetType == boolean.class) return Boolean.parseBoolean(value);
            if (targetType == float.class) return Float.parseFloat(value);
            if (targetType == byte.class) return Byte.parseByte(value);
            if (targetType == short.class) return Short.parseShort(value);
            if (targetType == char.class) return value.charAt(0);
        }

        // Handle object types (including wrappers)
        // Null is allowed here
        if (
                value == null
                ||
                        value.equals(nulll_string)
        ) {
            return null;
        }

        if (targetType.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) targetType, value);
        }

        // Empty string handling for non-null values
        if (value.isEmpty() && targetType != String.class) {
            throw new IllegalArgumentException("Empty string not allowed for type: " + targetType);
        }

        if (targetType == String.class || targetType == Object.class) return value; //Hmmmmm.....
        if (targetType == Integer.class) return Integer.valueOf(value);
        if (targetType == Long.class) return Long.valueOf(value);
        if (targetType == Double.class) return Double.valueOf(value);
        if (targetType == Boolean.class) return Boolean.valueOf(value);
        if (targetType == Float.class) return Float.valueOf(value);
        if (targetType == Byte.class) return Byte.valueOf(value);
        if (targetType == Short.class) return Short.valueOf(value);
        if (targetType == Character.class) return value.charAt(0);

        throw new IllegalArgumentException("Unsupported type: " + targetType);
    }
}
