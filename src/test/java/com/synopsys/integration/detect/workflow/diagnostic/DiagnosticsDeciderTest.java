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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;

class DiagnosticsDeciderTest {
    @Test
    void commandLineDecision() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(true, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide(propertyConfiguration);

        Assertions.assertTrue(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertTrue(diagnosticsDecision.isDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnosticExtended);
    }

    @Test
    void commandLineDecisionExtended() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, true);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide(propertyConfiguration);

        Assertions.assertTrue(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnostic);
        Assertions.assertTrue(diagnosticsDecision.isDiagnosticExtended);
    }

    @Test
    void propertyDecision() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC.getProperty())).thenReturn(true);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC_EXTENDED.getProperty())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide(propertyConfiguration);

        Assertions.assertTrue(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertTrue(diagnosticsDecision.isDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnosticExtended);
    }

    @Test
    void propertyDecisionExtended() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC.getProperty())).thenReturn(false);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC_EXTENDED.getProperty())).thenReturn(true);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide(propertyConfiguration);

        Assertions.assertTrue(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnostic);
        Assertions.assertTrue(diagnosticsDecision.isDiagnosticExtended);
    }

    @Test
    void noDiagnostic() {
        DetectArgumentState detectArgumentState = createDetectArgumentState(false, false);
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticsDecider diagnosticsDecider = new DiagnosticsDecider(detectArgumentState);
        DiagnosticsDecision diagnosticsDecision = diagnosticsDecider.decide(propertyConfiguration);

        Assertions.assertFalse(diagnosticsDecision.isConfiguredForDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnostic);
        Assertions.assertFalse(diagnosticsDecision.isDiagnosticExtended);
    }

    private DetectArgumentState createDetectArgumentState(boolean isDiagnostic, boolean isDiagnosticExtended) {
        return new DetectArgumentState(false, false, false, false, false, null, isDiagnostic, isDiagnosticExtended, false);
    }
}