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
package com.synopsys.integration.detect.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;

//import com.synopsys.integration.detect.configuration.DetectInfo;
//import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
//import com.synopsys.integration.detect.configuration.help.DetectOption;
//import com.synopsys.integration.detect.configuration.help.DetectOptionManager;
//import com.synopsys.integration.detect.configuration.enumeration.OperatingSystemType;

public class DetectConfigurationTest {

    @Test
    public void testPhoneHomePassthroughProperties() {
        final String givenKeyPhoneHomePart = "x.y.z";
        final String givenKeyFull = "detect.phone.home.passthrough." + givenKeyPhoneHomePart;
        final String givenValue = "testValue";

        HashMap<String, String> values = new HashMap<>();
        values.put(givenKeyFull, givenValue);
        List<PropertySource> propertySources = new ArrayList<>();
        propertySources.add(new MapPropertySource("test", values));
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);

        Map<String, String> phoneHomePropertiesMap = propertyConfiguration.getRaw(DetectProperties.PHONEHOME_PASSTHROUGH.getProperty());
        Assertions.assertEquals(givenValue, phoneHomePropertiesMap.get(givenKeyPhoneHomePart));
    }

    @Test
    public void testDeprecated() throws DetectUserFriendlyException {
        HashMap<String, String> values = new HashMap<>();
        values.put(DetectProperties.DETECT_BDIO2_ENABLED.getProperty().getKey(), "false");
        List<PropertySource> propertySources = new ArrayList<>();
        propertySources.add(new MapPropertySource("test", values));
        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources);
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(propertyConfiguration, new SimplePathResolver());
        BdioOptions bdioOptions = detectConfigurationFactory.createBdioOptions();
        Assertions.assertFalse(bdioOptions.isBdio2Enabled());
    }
}
