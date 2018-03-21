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
package com.blackducksoftware.integration.hub.detect.help.print;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;

public class DetectConfigurationPrinter {

    public void print(final PrintStream printStream, final DetectInfo detectInfo, final DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions)
            throws IllegalArgumentException, IllegalAccessException {
        printStream.println("");
        printStream.println("Current property values:");
        printStream.println("--property = value [notes]");
        printStream.println(StringUtils.repeat("-", 60));
        List<Field> annotatedProperties = new ArrayList<>();
        final Field[] propertyFields = DetectConfiguration.class.getDeclaredFields();
        for (final Field propertyField : propertyFields) {
            final Optional<Annotation> foundField = Arrays.stream(propertyField.getAnnotations())
                    .filter(annotation -> annotation.annotationType() == Value.class)
                    .findFirst();
            final int modifiers = propertyField.getModifiers();
            if (foundField.isPresent() && !Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers)) {
                annotatedProperties.add(propertyField);
            }
        }
        annotatedProperties = annotatedProperties.stream()
                .sorted(new Comparator<Field>() {
                    @Override
                    public int compare(final Field field1, final Field field2) {
                        return field1.getName().compareTo(field2.getName());
                    }
                })
                .collect(Collectors.toList());

        for (final Field field : annotatedProperties) {
            field.setAccessible(true);
            final String fieldName = field.getName();
            Object rawFieldValue;
            rawFieldValue = field.get(detectConfiguration);
            String fieldValue;
            if (field.getType().isArray()) {
                fieldValue = String.join(", ", (String[]) rawFieldValue);
            } else {
                if (rawFieldValue == null) {
                    fieldValue = "";
                } else {
                    fieldValue = rawFieldValue.toString();
                }
            }
            if (!StringUtils.isEmpty(fieldName) && !StringUtils.isEmpty(fieldValue) && "metaClass" != fieldName) {
                final boolean containsPassword = fieldName.toLowerCase().contains("password") || fieldName.toLowerCase().contains("apitoken");
                if (containsPassword) {
                    fieldValue = StringUtils.repeat("*", fieldValue.length());
                }
                DetectOption option = null;
                for (final DetectOption opt : detectOptions) {
                    if (opt.getFieldName().equals(fieldName)) {
                        option = opt;
                    }
                }
                if (option != null && !option.getResolvedValue().equals(fieldValue) && !containsPassword) {
                    if (option.interactiveValue != null) {
                        printStream.println(fieldName + " = " + fieldValue + " [interactive]");
                    } else if (option.getResolvedValue().equals("latest")) {
                        printStream.println(fieldName + " = " + fieldValue + " [latest]");
                    } else if (option.getResolvedValue().trim().length() == 0) {
                        printStream.println(fieldName + " = " + fieldValue + " [calculated]");
                    } else {
                        printStream.println(fieldName + " = " + fieldValue + " + [" + option.getResolvedValue() + "]");
                    }
                } else {
                    printStream.println(fieldName + " = " + fieldValue);
                }
            }
            field.setAccessible(false);
        }
        printStream.println(StringUtils.repeat("-", 60));
        printStream.println("");
    }
}
