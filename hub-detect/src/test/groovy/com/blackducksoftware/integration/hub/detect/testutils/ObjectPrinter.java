package com.blackducksoftware.integration.hub.detect.testutils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPrinter {
    private final static Logger logger = LoggerFactory.getLogger(ObjectPrinter.class);

    public static void printObject(final String prefix, final Object guy) {
        for (final Field field : guy.getClass().getFields()) {
            final String name = field.getName();
            String value = "unknown";
            Object obj = null;
            try {
                obj = field.get(guy);
            } catch (final Exception e) {

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
                    logger.info(name + " : " + value);
                } else {
                    logger.info(prefix + "." + name + " : " + value);
                }
            } else {
                String nestedPrefix = name;
                if (StringUtils.isNotBlank(prefix)) {
                    nestedPrefix = prefix + "." + nestedPrefix;
                }
                printObject(nestedPrefix, obj);
            }
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
