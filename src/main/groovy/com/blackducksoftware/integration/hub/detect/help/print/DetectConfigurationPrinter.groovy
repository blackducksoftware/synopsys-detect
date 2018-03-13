/*
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

import java.lang.reflect.Modifier

import org.springframework.beans.factory.annotation.Value

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.DetectInfo
import com.blackducksoftware.integration.hub.detect.help.DetectOption

public class DetectConfigurationPrinter {
    public void print(final PrintStream printStream, DetectInfo detectInfo, DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions) {
        printStream.println('')
        printStream.println('Current property values:')
        printStream.println('--property = value [notes]')
        printStream.println('-'.multiply(60))
        def propertyFields = DetectConfiguration.class.getDeclaredFields().findAll {
            def foundValueAnnotation = it.annotations.find { annotation ->
                annotation.annotationType() == Value.class
            }
            int modifiers = it.modifiers
            !Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers) && foundValueAnnotation
        }.sort { a, b ->
            a.name <=> b.name
        }

        propertyFields.each {
            it.accessible = true
            String fieldName = it.name
            Object rawFieldValue = it.get(detectConfiguration)
            String fieldValue
            if (it.type.isArray()) {
                fieldValue = (rawFieldValue as String[]).join(', ')
            } else {
                fieldValue = rawFieldValue.toString()
            }
            if (fieldName && fieldValue && 'metaClass' != fieldName) {
                boolean containsPassword = fieldName.toLowerCase().contains('password') || fieldName.toLowerCase().contains('apitoken');
                if (containsPassword) {
                    fieldValue = '*'.multiply((fieldValue as String).length())
                }
                DetectOption option;
                for (DetectOption opt : detectOptions) {
                    if (opt.fieldName.equals(fieldName)) {
                        option = opt;
                    }
                }
                if (option != null && !option.resolvedValue.equals(fieldValue) && !containsPassword) {
                    if (option.interactiveValue != null) {
                        printStream.println("${fieldName} = ${fieldValue} [interactive]" as String)
                    } else if (option.resolvedValue.equals("latest")) {
                        printStream.println("${fieldName} = ${fieldValue} [latest]" as String)
                    } else if (option.resolvedValue.trim().size() == 0) {
                        printStream.println("${fieldName} = ${fieldValue} [calculated]" as String)
                    } else {
                        printStream.println("${fieldName} = ${fieldValue} [${option.resolvedValue}]" as String)
                    }
                } else {
                    printStream.println("${fieldName} = ${fieldValue}" as String)
                }
            }
            it.accessible = false
        }
        printStream.println('-'.multiply(60))
        printStream.println('')
    }
}
