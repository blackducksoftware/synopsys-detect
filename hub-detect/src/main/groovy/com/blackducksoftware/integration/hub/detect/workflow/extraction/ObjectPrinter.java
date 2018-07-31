/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.workflow.extraction;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPrinter {
    private static final Logger logger = LoggerFactory.getLogger(ObjectPrinter.class);

    public static void printObject(final String prefix, final Object guy) {
        for (final Field field : guy.getClass().getFields()) {
            printField(field, prefix, guy);
        }
    }

    public static void printObjectPrivate(final Object guy) {
        printObjectPrivate(null, guy);
    }

    public static void printObjectPrivate(final String prefix, final Object guy) {
        for (final Field field : guy.getClass().getDeclaredFields()) {
            printField(field, prefix, guy);
        }
    }

    public static void printField(final Field field, final String prefix, final Object guy) {
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
