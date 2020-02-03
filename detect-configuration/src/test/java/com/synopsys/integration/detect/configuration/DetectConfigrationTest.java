/**
 * detect-configuration
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.help.DetectOption;
import com.synopsys.integration.detect.help.DetectOptionManager;
import com.synopsys.integration.detect.type.OperatingSystemType;
import com.synopsys.integration.detect.util.TildeInPathResolver;

public class DetectConfigrationTest {

    @Test
    public void testPhoneHomePassthroughProperties() {
        final DetectPropertySource detectPropertySource = Mockito.mock(DetectPropertySource.class);
        final DetectPropertyMap detectPropertyMap = Mockito.mock(DetectPropertyMap.class);

        final String givenKeyPhoneHomePart = "x.y.z";
        final String givenKeyFull = "detect.phone.home.passthrough." + givenKeyPhoneHomePart;
        final String givenValue = "testValue";

        final Set<String> phoneHomePropertyKeys = new HashSet<>();
        phoneHomePropertyKeys.add(givenKeyFull);
        Mockito.when(detectPropertySource.getPhoneHomePropertyKeys()).thenReturn(phoneHomePropertyKeys);
        Mockito.when(detectPropertySource.getProperty(givenKeyFull)).thenReturn(givenValue);
        final DetectConfiguration config = new DetectConfiguration(detectPropertySource, detectPropertyMap);

        final Map<String, String> phoneHomePropertiesMap = config.getPhoneHomeProperties();

        assertEquals(givenValue, phoneHomePropertiesMap.get(givenKeyPhoneHomePart));
    }

    @Test
    public void testDeprecated() throws DetectUserFriendlyException {
        //TODO: Proper option tests. This is a work-in-progress sample test for checking properties final values. Feel free to delete if causes you issues.
        final Map<String, String> properties = new HashMap<>();
        properties.put(DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS.getPropertyKey(), "2");

        final FakePropertySource propertySource = new FakePropertySource(properties);
        final DetectPropertySource detectPropertySource = new DetectPropertySource(propertySource);
        final DetectPropertyMap detectPropertyMap = new DetectPropertyMap();
        final DetectConfiguration config = new DetectConfiguration(detectPropertySource, detectPropertyMap);

        final DetectInfo info = new DetectInfo("6.0.0", 6, OperatingSystemType.WINDOWS);
        final DetectOptionManager detectOptionManager = new DetectOptionManager(config, info);

        final TildeInPathResolver tildeInPathResolver = new TildeInPathResolver(DetectConfigurationManager.USER_HOME, info.getCurrentOs());
        final DetectConfigurationManager detectConfigurationManager = new DetectConfigurationManager(tildeInPathResolver, config);
        detectConfigurationManager.process(detectOptionManager.getDetectOptions());
        detectOptionManager.postConfigurationProcessedInit();

        final List<DetectOption> options = detectOptionManager.getDetectOptions();
        options.forEach(option -> {
            if (option.getDetectProperty() == DetectProperty.DETECT_HUB_SIGNATURE_SCANNER_PARALLEL_PROCESSORS) {
                Assertions.assertEquals("2", option.getFinalValue());
                Assertions.assertSame(DetectOption.FinalValueType.SUPPLIED, option.getFinalValueType());
            }
        });

    }
}
