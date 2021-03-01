/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class ConfigurationReporter {
    public void writeReport(final ReportWriter writer, final DetectInfo detectInfo, final PropertyConfiguration propertyConfiguration, SortedMap<String, String> maskedRawPropertyValues, Set<String> propertyKeys) throws IllegalAccessException {
        writer.writeSeparator();
        writer.writeLine("Detect Info");
        writer.writeSeparator();
        writer.writeLine("Detect Version: " + detectInfo.getDetectVersion());
        writer.writeLine("Operating System: " + detectInfo.getCurrentOs());
        writer.writeSeparator();
        writer.writeLine("Detect Configuration");
        writer.writeSeparator();
        final PropertyConfigurationHelpContext helpContext = new PropertyConfigurationHelpContext(propertyConfiguration);
        helpContext.printKnownCurrentValues(writer::writeLine, propertyKeys, maskedRawPropertyValues, new HashMap<>());
        writer.writeSeparator();
    }
}
