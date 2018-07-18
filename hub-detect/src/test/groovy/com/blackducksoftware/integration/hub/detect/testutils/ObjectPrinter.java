package com.blackducksoftware.integration.hub.detect.testutils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.workflow.report.ReportWriter;

public class ObjectPrinter {

    public static void printObject(final ReportWriter writer, final String prefix, final Object guy) {
        for (final Field field : guy.getClass().getFields()) {
            printField(field, writer, prefix, guy);
        }
    }

    public static void printObjectPrivate(final ReportWriter writer, final Object guy) {
        printObjectPrivate(writer, null, guy);
    }

    public static void printObjectPrivate(final ReportWriter writer, final String prefix, final Object guy) {
        for (final Field field : guy.getClass().getDeclaredFields()) {
            printField(field, writer, prefix, guy);
        }
    }

    public static void printField(final Field field, final ReportWriter writer, final String prefix, final Object guy) {
        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
            return; // don't print static fields.
        }
        final String name = field.getName();
        String value = "unknown";
        Object obj = null;
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            obj = field.get(guy);
        } catch (final Exception e) {
            e.printStackTrace();
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
                writer.writeLine(name + " : " + value);
            } else {
                writer.writeLine(prefix + "." + name + " : " + value);
            }
        } else {
            String nestedPrefix = name;
            if (StringUtils.isNotBlank(prefix)) {
                nestedPrefix = prefix + "." + nestedPrefix;
            }
            printObject(writer, nestedPrefix, obj);
        }
    }

    public static boolean shouldRecursivelyPrintType(final Class<?> clazz) {
        return !NON_NESTED_TYPES.contains(clazz);
    }

    private static final Set<Class<?>> NON_NESTED_TYPES = getNonNestedTypes();

    private static Set<Class<?>> getNonNestedTypes() {
        final Set<Class<?>> ret = new HashSet<>();
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
        return ret;
    }
}
