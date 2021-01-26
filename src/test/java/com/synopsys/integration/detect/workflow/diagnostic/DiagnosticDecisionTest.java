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
package com.synopsys.integration.detect.workflow.diagnostic;

import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;

class DiagnosticDecisionTest {
    private static final String TEST_PROPERTY_SOURCE_NAME = "TEST_PROPERTIES";

    @Test
    void commandLineDecision() {
        PropertyConfiguration propertyConfiguration = createPropertyConfiguration(false,false);
        DetectArgumentState detectArgumentState = createDetectArgumentState(true, false);

        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticDecision.shouldCreateDiagnosticSystem());
        Assertions.assertFalse(diagnosticDecision.isExtended());
    }

    @Test
    void commandLineDecisionExtended() {
        PropertyConfiguration propertyConfiguration = createPropertyConfiguration(false,false);
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, true);

        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticDecision.shouldCreateDiagnosticSystem());
        Assertions.assertTrue(diagnosticDecision.isExtended());
    }

    @Test
    void propertyDecision() {
        PropertyConfiguration propertyConfiguration = createPropertyConfiguration(true,false);
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);

        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticDecision.shouldCreateDiagnosticSystem());
        Assertions.assertFalse(diagnosticDecision.isExtended());
    }

    @Test
    void propertyDecisionExtended() {
        PropertyConfiguration propertyConfiguration = createPropertyConfiguration(false,true);
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);

        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);

        Assertions.assertTrue(diagnosticDecision.shouldCreateDiagnosticSystem());
        Assertions.assertTrue(diagnosticDecision.isExtended());
    }

    @Test
    void noDiagnostic() {
        PropertyConfiguration propertyConfiguration = createPropertyConfiguration(false,false);
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);

        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);

        Assertions.assertFalse(diagnosticDecision.shouldCreateDiagnosticSystem());
        Assertions.assertFalse(diagnosticDecision.isExtended());
    }

    private PropertyConfiguration createPropertyConfiguration(boolean isDiagnostic, boolean isExtended) {
        HashMap<String, String> propertySourceMap = new HashMap<>();
        propertySourceMap.put(DetectProperties.DETECT_DIAGNOSTIC.getProperty().getKey(), String.valueOf(isDiagnostic));
        propertySourceMap.put(DetectProperties.DETECT_DIAGNOSTIC_EXTENDED.getProperty().getKey(), String.valueOf(isExtended));
        MapPropertySource mapPropertySource = new MapPropertySource(TEST_PROPERTY_SOURCE_NAME, propertySourceMap);

        return new PropertyConfiguration(Collections.singletonList(mapPropertySource));
    }

    private DetectArgumentState createDetectArgumentState(boolean isDiagnostic, boolean isExtended) {
        return new DetectArgumentState(false,false,false,false,false,null,isDiagnostic,isExtended,false);
    }

}