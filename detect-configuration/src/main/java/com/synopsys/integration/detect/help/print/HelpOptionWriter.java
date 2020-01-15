package com.synopsys.integration.detect.help.print;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.help.DetectOption;
import com.synopsys.integration.detect.help.DetectOptionHelp;

public class HelpOptionWriter {
    private String getDeprecationText(DetectOption detectOption) {
        return "Will cause failures in version " + detectOption.getDetectOptionHelp().deprecationFailInVersion.getDisplayValue() + ". Will be removed in version " + detectOption.getDetectOptionHelp().deprecationRemoveInVersion
                                                                                                                                                                         .getDisplayValue() + ". ";
    }

    public void printOption(DetectOption detectOption, final HelpTextWriter writer) {
        String description = detectOption.getDetectOptionHelp().description;
        if (detectOption.getDetectOptionHelp().isDeprecated) {
            description = getDeprecationText(detectOption) + description;
        }
        if (detectOption.getValidValues().size() > 0) {
            description += " (" + detectOption.getValidValues().stream().collect(Collectors.joining("|")) + ")";
        }
        String propertyKey = "";
        String defaultValue = "";
        DetectProperty detectProperty = detectOption.getDetectProperty();
        if (StringUtils.isNotBlank(detectProperty.getPropertyKey())) {
            propertyKey = detectProperty.getPropertyKey();
        }
        if (StringUtils.isNotBlank(detectProperty.getDefaultValue())) {
            defaultValue = detectProperty.getDefaultValue();
        }
        writer.printColumns("--" + propertyKey, defaultValue, description);
    }

    public void printDetailedOption(DetectOption detectOption, final HelpTextWriter writer) {
        DetectProperty detectProperty = detectOption.getDetectProperty();

        writer.println("");
        writer.println("Detailed information for " + detectProperty.getPropertyKey());
        writer.println("");
        if (detectOption.getDetectOptionHelp().isDeprecated) {
            writer.println("Deprecated: " + getDeprecationText(detectOption));
            writer.println("Deprecation description: " + detectOption.getDetectOptionHelp().deprecation);
            writer.println("");
        }
        writer.println("Property description: " + detectOption.getDetectOptionHelp().description);
        writer.println("Property default value: " + detectProperty.getDefaultValue());
        if (detectOption.getValidValues().size() > 0) {
            writer.println("Property acceptable values: " + detectOption.getValidValues().stream().collect(Collectors.joining(", ")));
        }
        writer.println("");

        final DetectOptionHelp help = detectOption.getDetectOptionHelp();
        if (StringUtils.isNotBlank(help.detailedHelp)) {
            writer.println("Detailed help:");
            writer.println(help.detailedHelp);
            writer.println();
        }
    }
}
