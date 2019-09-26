/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.report;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.help.DetectOption;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;

public class DetectConfigurationReporter {

    private List<DetectOption> sortOptions(final List<DetectOption> detectOptions) {
        return detectOptions.stream()
                   .sorted(Comparator.comparing(o -> o.getDetectProperty().getPropertyKey()))
                   .collect(Collectors.toList());
    }

    public void print(final ReportWriter writer, final List<DetectOption> detectOptions, boolean skipDefaults) throws IllegalArgumentException, SecurityException {
        writer.writeLine("Detect Configuration");
        writer.writeLine(StringUtils.repeat("-", 60));

        final List<DetectOption> sortedOptions = sortOptions(detectOptions);

        boolean atLeaseOneWritten = false;
        for (final DetectOption option : sortedOptions) {
            final String key = option.getDetectProperty().getPropertyKey();
            String fieldValue = option.getFinalValue();
            final DetectOption.FinalValueType fieldType = option.getFinalValueType();
            if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(fieldValue) && !"metaClass".equals(key)) {
                if (fieldType == DetectOption.FinalValueType.DEFAULT && skipDefaults) {
                    continue;
                }
                atLeaseOneWritten = true;
                final boolean containsPassword = key.toLowerCase().contains("password") || key.toLowerCase().contains("api.token") || key.toLowerCase().contains("access.token");
                if (containsPassword) {
                    fieldValue = StringUtils.repeat("*", fieldValue.length());
                }

                String text = "";
                final String displayName = option.getDetectProperty().getPropertyKey();
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
                } else if (fieldType == DetectOption.FinalValueType.COPIED) {
                    text = displayName + " = " + fieldValue + " [copied]";
                }

                if (option.getValidValues().size() > 0) {
                    final DetectOption.OptionValidationResult validationResult = option.validateValue(fieldValue);
                    if (!validationResult.isValid()) {
                        text += String.format(" [%s]", validationResult.getValidationMessage());
                    }
                }

                if (option.getWarnings().size() > 0) {
                    text += "\t *** DEPRECATED ***";
                }
                writer.writeLine(text);
            }
        }
        if (!atLeaseOneWritten) {
            writer.writeLine("All configuration values are the default.");
        }
        writer.writeLine(StringUtils.repeat("-", 60));
        writer.writeLine("");

    }

    public void publishWarnings(EventSystem eventSystem, final List<DetectOption> detectOptions) {
        final List<DetectOption> sortedOptions = sortOptions(detectOptions);

        final List<DetectOption> allWarnings = sortedOptions.stream().filter(it -> it.getWarnings().size() > 0).collect(Collectors.toList());
        for (final DetectOption option : allWarnings) {
            for (final String warning : option.getWarnings()) {
                DetectIssue.publish(eventSystem, DetectIssueType.Deprecation, option.getDetectProperty().getPropertyKey() + ": " + warning);
            }
        }
    }

    public void printFailures(final ReportWriter writer, final List<DetectOption> detectOptions) {
        final List<DetectOption> sortedOptions = sortOptions(detectOptions);

        final List<DetectOption> allWarnings = sortedOptions.stream().filter(it -> it.getWarnings().size() > 0).collect(Collectors.toList());
        if (allWarnings.size() > 0) {
            writer.writeLine(ReportConstants.ERROR_SEPERATOR);
            if (allWarnings.size() == 1) {
                writer.writeLine("ERROR (" + allWarnings.size() + ")");
            } else {
                writer.writeLine("ERRORS (" + allWarnings.size() + ")");
            }
            for (final DetectOption option : allWarnings) {
                for (final String warning : option.getWarnings()) {
                    writer.writeLine(option.getDetectProperty().getPropertyKey() + ": " + warning);
                }
            }
            writer.writeLine(ReportConstants.ERROR_SEPERATOR);
            writer.writeLine("Configuration is using deprecated properties that must be updated for this major version.");
            writer.writeLine("You MUST fix these deprecation issues for detect to proceed.");
            writer.writeLine("To ignore these messages and force detect to exit with success supply --" + DetectProperty.DETECT_FORCE_SUCCESS.getPropertyKey() + "=true");
            writer.writeLine("This will not force detect to run, but it will pretend to have succeeded.");
            writer.writeLine(ReportConstants.ERROR_SEPERATOR);
        }
    }
}
