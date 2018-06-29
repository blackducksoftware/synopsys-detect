/**
 * detect-configuration
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

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

public class DetectConfigurationPrinter {

    public void print(final PrintStream printStream, final List<DetectOption> detectOptions)
            throws IllegalArgumentException, SecurityException {
        printStream.println("");
        printStream.println("Current property values:");
        printStream.println("--property = value [notes]");
        printStream.println(StringUtils.repeat("-", 60));

        final List<DetectOption> sortedOptions = detectOptions.stream()
                .sorted((o1, o2) -> o1.getDetectProperty().getPropertyName().compareTo(o2.getDetectProperty().getPropertyName()))
                .collect(Collectors.toList());

        final List<DetectOption> deprecatedInUse = new ArrayList<>();

        for (final DetectOption option : sortedOptions) {
            final String key = option.getDetectProperty().getPropertyName();
            String fieldValue = option.getFinalValue();
            final DetectOption.FinalValueType fieldType = option.getFinalValueType();
            if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(fieldValue) && "metaClass" != key) {
                final boolean containsPassword = key.toLowerCase().contains("password") || key.toLowerCase().contains("api.token");
                if (containsPassword) {
                    fieldValue = StringUtils.repeat("*", fieldValue.length());
                }

                String text = "";
                final String displayName = option.getDetectProperty().getPropertyName();
                if (fieldType == DetectOption.FinalValueType.SUPPLIED || fieldType == DetectOption.FinalValueType.DEFAULT || containsPassword) {
                    if (fieldValue.trim().length() > 0) {
                        text = displayName + " = " + fieldValue;
                    }
                } else if (fieldType == DetectOption.FinalValueType.INTERACTIVE) {
                    text = displayName + " = " + fieldValue + " [interactive]";
                } else if (fieldType == DetectOption.FinalValueType.LATEST) {
                    text = displayName + " = " + fieldValue + " [latest]";
                } else if (fieldType == DetectOption.FinalValueType.CALCULATED) {
                    text = displayName + " = " + fieldValue + " [calculated]";
                } else if (fieldType == DetectOption.FinalValueType.OVERRIDE) {
                    text = displayName + " = " + fieldValue + " [" + option.getResolvedValue() + "]";
                }

                if (option.getAcceptableValues().size() > 0) {
                    DetectOption.OptionValidationResult validationResult = option.isAcceptableValue(fieldValue);
                    if (!validationResult.isValid()) {
                        text += String.format(" [%s]", validationResult.getValidationMessage());
                    }
                }

                if (option.getWarnings().size() > 0) {
                    deprecatedInUse.add(option);
                    text += "\t *** WARNING ***";
                }
                printStream.println(text);
            }
        }
        final List<DetectOption> allWarnings = sortedOptions.stream().filter(it -> it.getWarnings().size() > 0).collect(Collectors.toList());
        if (allWarnings.size() > 0) {
            printStream.println("");
            printStream.println(StringUtils.repeat("*", 60));
            if (allWarnings.size() == 1) {
                printStream.println("WARNING (" + allWarnings.size() + ")");
            } else {
                printStream.println("WARNINGS (" + allWarnings.size() + ")");
            }
            for (final DetectOption option : allWarnings) {
                for (final String warning : option.getWarnings()) {
                    printStream.println(option.getDetectProperty().getPropertyName() + ": " + warning);
                }
            }
            printStream.println(StringUtils.repeat("*", 60));
            printStream.println("");
        } else {
            printStream.println(StringUtils.repeat("-", 60));
            printStream.println("");
        }
    }

}
