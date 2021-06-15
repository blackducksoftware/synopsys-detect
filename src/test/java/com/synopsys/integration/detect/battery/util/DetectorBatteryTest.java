/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.battery.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.detect.configuration.DetectProperties;

public final class DetectorBatteryTest extends BatteryTest {
    private String toolsValue = "DETECTOR";
    private List<String> additionalProperties;

    public DetectorBatteryTest(final String name) {
        super(name);
    }

    public DetectorBatteryTest(final String testName, final String resourcePrefix) {
        super(testName, resourcePrefix);
    }

    public void withToolsValue(String toolsValue) {
        this.toolsValue = toolsValue;
    }

    public void property(Property property, String value) {
        property(property.getKey(), value);
    }

    public void property(String property, String value) {
        additionalProperties.add("--" + property + "=" + value);
    }

    public List<String> generateArguments() throws IOException {
        List<String> detectArguments = new ArrayList<>();
        Map<Property, String> properties = new HashMap<>();

        properties.put(DetectProperties.DETECT_TOOLS.getProperty(), toolsValue);
        properties.put(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty(), "true");
        properties.put(DetectProperties.DETECT_OUTPUT_PATH.getProperty(), batteryContext.getOutputDirectory().getCanonicalPath());
        properties.put(DetectProperties.DETECT_BDIO_OUTPUT_PATH.getProperty(), batteryContext.getBdioDirectory().getCanonicalPath());
        properties.put(DetectProperties.DETECT_CLEANUP.getProperty(), "false");
        properties.put(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION.getProperty(), "INFO"); // Leave at INFO for Travis. Long logs cause build to fail.
        properties.put(DetectProperties.DETECT_SOURCE_PATH.getProperty(), batteryContext.getSourceDirectory().getCanonicalPath());
        properties.put(DetectProperties.DETECT_BDIO2_ENABLED.getProperty(), "false");
        for (Map.Entry<Property, String> entry : properties.entrySet()) {
            detectArguments.add("--" + entry.getKey().getKey() + "=" + entry.getValue());
        }

        detectArguments.addAll(additionalProperties);

        return detectArguments;
    }

}
