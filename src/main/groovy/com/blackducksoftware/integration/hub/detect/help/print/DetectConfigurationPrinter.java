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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.FieldWarnings;
import com.blackducksoftware.integration.hub.detect.help.FieldWarnings.FieldWarning;

public class DetectConfigurationPrinter {

    public void print(final PrintStream printStream, final DetectInfo detectInfo, final DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        printStream.println("");
        printStream.println("Current property values:");
        printStream.println("--property = value [notes]");
        printStream.println(StringUtils.repeat("-", 60));
        
        List<DetectOption> sortedOptions = detectOptions.stream()
                .sorted((o1, o2)->o1.getKey().compareTo(o2.getKey()))
                .collect(Collectors.toList());
        
        FieldWarnings warnings = detectConfiguration.getFieldWarnings();
        List<DetectOption> deprecatedInUse = new ArrayList<>();
        
        for (final DetectOption option : sortedOptions) {
            String fieldName = option.getFieldName();
            String fieldValue = getCurrentValue(detectConfiguration, option);
            if (!StringUtils.isEmpty(fieldName) && !StringUtils.isEmpty(fieldValue) && "metaClass" != fieldName) {
                final boolean containsPassword = fieldName.toLowerCase().contains("password") || fieldName.toLowerCase().contains("apitoken");
                if (containsPassword) {
                    fieldValue = StringUtils.repeat("*", fieldValue.length());
                }
                
                String text = "";
                String displayName = option.getKey();
                if (!option.getResolvedValue().equals(fieldValue) && !containsPassword) {
                    if (option.getInteractiveValue() != null) {
                        text = displayName + " = " + fieldValue + " [interactive]";
                    } else if (option.getResolvedValue().equals("latest")) {
                        text = displayName + " = " + fieldValue + " [latest]";
                    } else if (option.getResolvedValue().trim().length() == 0) {
                        text = displayName + " = " + fieldValue + " [calculated]";
                    } else {
                        text = displayName + " = " + fieldValue + " [" + option.getResolvedValue() + "]";
                    }
                } else {
                    text = displayName + " = " + fieldValue;
                    if (option.getHelp().isDeprecated && !fieldValue.equals(option.getDefaultValue())) {
                        warnings.addWarning(fieldName, "As of version " + option.getHelp().deprecationVersion + " this property will be removed: " + option.getHelp().deprecation);
                    }
                }
                
                if (option.getAcceptableValues().size() > 0) {
                    if (!option.getAcceptableValues().contains(fieldValue)) {
                        text += " [unknown value]";
                    }
                }
                
                if (warnings.warningsForField(fieldName).size() > 0) {
                    deprecatedInUse.add(option);
                    text += "\t *** WARNING ***";
                }
                printStream.println(text);
            }
        }
        List<FieldWarning> allWarnings = warnings.getWarnings();
        if (allWarnings.size() > 0) {
            printStream.println("");
            printStream.println(StringUtils.repeat("*", 60));
            if (allWarnings.size() == 1) {
                printStream.println("WARNING (" + allWarnings.size() + ")");
            } else {
                printStream.println("WARNINGS (" + allWarnings.size() + ")");
            }
            for (FieldWarning warning : allWarnings) {
                DetectOption option = optionForField(warning.fieldName, detectOptions);
                printStream.println(option.getKey() + ": " + warning.description);
            }
            printStream.println(StringUtils.repeat("*", 60));
            printStream.println("");
        }else {
            printStream.println(StringUtils.repeat("-", 60));
            printStream.println("");
        }
    }
    
    public DetectOption optionForField(String fieldName, List<DetectOption> detectOptions) {
        return detectOptions.stream()
            .filter(it -> it.getFieldName().equals(fieldName))
            .findFirst()
            .orElse(null);
    }
    
    public String getCurrentValue(DetectConfiguration detectConfiguration, DetectOption detectOption) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field field = detectConfiguration.getClass().getDeclaredField(detectOption.getFieldName());
        field.setAccessible(true);
        Object rawFieldValue;
        rawFieldValue = field.get(detectConfiguration);
        String fieldValue = "";
        if (field.getType().isArray()) {
            fieldValue = String.join(", ", (String[]) rawFieldValue);
        } else {
            if (rawFieldValue != null) {
                fieldValue = rawFieldValue.toString();
            }
        }
        field.setAccessible(false);
        return fieldValue;
    }
}
