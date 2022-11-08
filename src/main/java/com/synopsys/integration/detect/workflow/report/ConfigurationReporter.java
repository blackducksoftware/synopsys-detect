package com.synopsys.integration.detect.workflow.report;

import java.util.HashMap;
import java.util.SortedMap;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class ConfigurationReporter {
    public void writeReport(
        ReportWriter writer,
        DetectInfo detectInfo,
        PropertyConfiguration propertyConfiguration,
        SortedMap<String, String> maskedRawPropertyValues
    ) throws IllegalAccessException {
        writer.writeSeparator();
        writer.writeLine("Detect Info");
        writer.writeSeparator();
        writer.writeLine("Detect Version: " + detectInfo.getDetectVersion());
        writer.writeLine("Operating System: " + detectInfo.getCurrentOs());
        writer.writeSeparator();
        writer.writeLine("Detect Configuration");
        writer.writeSeparator();
        PropertyConfigurationHelpContext helpContext = new PropertyConfigurationHelpContext(propertyConfiguration);
        helpContext.printCurrentValues(writer::writeLine, maskedRawPropertyValues, new HashMap<>());
        writer.writeSeparator();
    }
}
