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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOption.FinalValueType;

public class DetectConfigurationPrinter {

    public void print(final PrintStream printStream, final DetectInfo detectInfo, final DetectConfiguration detectConfiguration, final List<DetectOption> detectOptions)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        printStream.println("");
        printStream.println("Current property values:");
        printStream.println("--property = value [notes]");
        printStream.println(StringUtils.repeat("-", 60));

        final List<DetectOption> sortedOptions = detectOptions.stream()
                .sorted((o1, o2)->o1.getKey().compareTo(o2.getKey()))
                .collect(Collectors.toList());

        final List<DetectOption> deprecatedInUse = new ArrayList<>();

        for (final DetectOption option : sortedOptions) {
            final String fieldName = option.getFieldName();
            String fieldValue = option.getFinalValue();
            final FinalValueType fieldType = option.getFinalValueType();
            if (!StringUtils.isEmpty(fieldName) && !StringUtils.isEmpty(fieldValue) && "metaClass" != fieldName) {
                final boolean containsPassword = fieldName.toLowerCase().contains("password") || fieldName.toLowerCase().contains("apitoken");
                if (containsPassword) {
                    fieldValue = StringUtils.repeat("*", fieldValue.length());
                }

                String text = "";
                final String displayName = option.getKey();
                if (fieldType == FinalValueType.SUPPLIED || fieldType == FinalValueType.DEFAULT || containsPassword) {
                    if (fieldValue.trim().length() > 0) {
                        text = displayName + " = " + fieldValue;
                    }
                } else if (fieldType == FinalValueType.INTERACTIVE) {
                    text = displayName + " = " + fieldValue + " [interactive]";
                } else if (fieldType == FinalValueType.LATEST) {
                    text = displayName + " = " + fieldValue + " [latest]";
                } else if (fieldType == FinalValueType.CALCULATED) {
                    text = displayName + " = " + fieldValue + " [calculated]";
                } else if (fieldType == FinalValueType.OVERRIDE) {
                    text = displayName + " = " + fieldValue + " [" + option.getResolvedValue() + "]";
                }

                if (option.getAcceptableValues().size() > 0) {
                    if (!option.isAcceptableValue(fieldValue)) {
                        text += " [unknown value]";
                    }
                }

                if (option.getWarnings().getWarnings().size() > 0) {
                    deprecatedInUse.add(option);
                    text += "\t *** WARNING ***";
                }
                printStream.println(text);
            }
        }
        final List<DetectOption> allWarnings = sortedOptions.stream().filter(it -> it.getWarnings().getWarnings().size() > 0).collect(Collectors.toList());
        if (allWarnings.size() > 0) {
            printStream.println("");
            printStream.println(StringUtils.repeat("*", 60));
            if (allWarnings.size() == 1) {
                printStream.println("WARNING (" + allWarnings.size() + ")");
            } else {
                printStream.println("WARNINGS (" + allWarnings.size() + ")");
            }
            for (final DetectOption option : allWarnings) {
                for (final String warning : option.getWarnings().getWarnings()) {
                    printStream.println(option.getKey() + ": " + warning);
                }
            }
            printStream.println(StringUtils.repeat("*", 60));
            printStream.println("");
        }else {
            printStream.println(StringUtils.repeat("-", 60));
            printStream.println("");
        }
    }

    public DetectOption optionForField(final String fieldName, final List<DetectOption> detectOptions) {
        return detectOptions.stream()
                .filter(it -> it.getFieldName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }

}
