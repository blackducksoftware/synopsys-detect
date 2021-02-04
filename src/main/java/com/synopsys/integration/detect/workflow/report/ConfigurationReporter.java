/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

public class ConfigurationReporter {
    public void writeReport(final ReportWriter writer, final DetectInfo detectInfo, final PropertyConfiguration propertyConfiguration, Map<String, String> maskedRawPropertyValues) throws IllegalAccessException {
        writer.writeSeparator();
        writer.writeLine("Detect Info");
        writer.writeSeparator();
        writer.writeLine("Detect Version: " + detectInfo.getDetectVersion());
        writer.writeLine("Operating System: " + detectInfo.getCurrentOs());
        writer.writeSeparator();
        writer.writeLine("Detect Configuration");
        writer.writeSeparator();
        final PropertyConfigurationHelpContext helpContext = new PropertyConfigurationHelpContext(propertyConfiguration);
        List<String> sortedPropertyKeys = DetectProperties.allProperties().getSortedPropertyKeys();
        helpContext.printKnownCurrentValues(writer::writeLine, sortedPropertyKeys, new TreeMap(maskedRawPropertyValues), new HashMap<>());
        writer.writeSeparator();
    }
}
