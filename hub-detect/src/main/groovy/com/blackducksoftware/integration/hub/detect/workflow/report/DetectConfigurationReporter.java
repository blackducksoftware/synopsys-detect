package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.workflow.report.writer.ReportWriter;

public class DetectConfigurationReporter {

    private List<DetectOption> sortOptions(final List<DetectOption> detectOptions) {
        return detectOptions.stream()
                   .sorted((o1, o2) -> o1.getDetectProperty().getPropertyName().compareTo(o2.getDetectProperty().getPropertyName()))
                   .collect(Collectors.toList());
    }

    public void print(ReportWriter writer, final List<DetectOption> detectOptions) throws IllegalArgumentException, SecurityException {
        writer.writeLine("");
        writer.writeLine("Current property values:");
        writer.writeLine("--property = value [notes]");
        writer.writeLine(StringUtils.repeat("-", 60));

        final List<DetectOption> sortedOptions = sortOptions(detectOptions);

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
                    text += "\t *** WARNING ***";
                }
                writer.writeLine(text);
            }
        }
        writer.writeLine(StringUtils.repeat("-", 60));
        writer.writeLine("");

    }

    public void printWarnings(ReportWriter writer, final List<DetectOption> detectOptions) {
        final List<DetectOption> sortedOptions = sortOptions(detectOptions);

        final List<DetectOption> allWarnings = sortedOptions.stream().filter(it -> it.getWarnings().size() > 0).collect(Collectors.toList());
        if (allWarnings.size() > 0) {
            writer.writeLine("");
            writer.writeLine(StringUtils.repeat("*", 60));
            if (allWarnings.size() == 1) {
                writer.writeLine("WARNING (" + allWarnings.size() + ")");
            } else {
                writer.writeLine("WARNINGS (" + allWarnings.size() + ")");
            }
            for (final DetectOption option : allWarnings) {
                for (final String warning : option.getWarnings()) {
                    writer.writeLine(option.getDetectProperty().getPropertyName() + ": " + warning);
                }
            }
            writer.writeLine(StringUtils.repeat("*", 60));
            writer.writeLine("");
        }
    }
}
