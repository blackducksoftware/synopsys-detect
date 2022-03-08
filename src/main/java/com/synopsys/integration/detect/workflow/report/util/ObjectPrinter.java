package com.synopsys.integration.detect.workflow.report.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class ObjectPrinter {
    private static final Logger logger = LoggerFactory.getLogger(ObjectPrinter.class);

    public static void printObjectPrivate(ReportWriter writer, Object guy) {
        Map<String, String> fieldMap = new HashMap<>();
        populateObjectPrivate(null, guy, fieldMap);
        fieldMap.forEach((key, value) -> writer.writeLine(key + ": " + value));
    }

    public static void populateObjectPrivate(String prefix, Object guy, Map<String, String> fieldMap) {
        for (Field field : guy.getClass().getDeclaredFields()) {
            populateField(field, prefix, guy, fieldMap);
        }
    }

    public static void populateObject(String prefix, Object guy, Map<String, String> fieldMap) {
        for (Field field : guy.getClass().getFields()) {
            populateField(field, prefix, guy, fieldMap);
        }
    }

    public static void populateField(Field field, String prefix, Object guy, Map<String, String> fieldMap) {
        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
            return; // don't print static fields.
        }
        String name = field.getName();
        String value = "unknown";
        Object obj = null;
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            obj = field.get(guy);
        } catch (Exception e) {
            logger.debug("Exception", e);
        }
        boolean shouldPrintObjectsFields = false;
        if (obj == null) {
            value = "null";
        } else {
            value = obj.toString();
            shouldPrintObjectsFields = shouldRecursivelyPrintType(obj.getClass());
        }
        if (!shouldPrintObjectsFields) {
            if (StringUtils.isBlank(prefix)) {
                fieldMap.put(name, value);
            } else {
                fieldMap.put(prefix + "." + name, value);
            }
        } else {
            String nestedPrefix = name;
            if (StringUtils.isNotBlank(prefix)) {
                nestedPrefix = prefix + "." + nestedPrefix;
            }
            populateObject(nestedPrefix, obj, fieldMap);
        }

    }

    public static boolean shouldRecursivelyPrintType(Class<?> clazz) {
        return !NON_NESTED_TYPES.contains(clazz);
    }

    private static final Set<Class<?>> NON_NESTED_TYPES = getNonNestedTypes();

    private static Set<Class<?>> getNonNestedTypes() {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(File.class);
        ret.add(String.class);
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(Optional.class);
        return ret;
    }
}
