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

class DiagnosticSystemManagerTest {
    @Test
    void commandLineDecision() {
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticSystemManager diagnosticSystemManager = new DiagnosticSystemManager(true, false, propertyConfiguration);

        Assertions.assertTrue(diagnosticSystemManager.shouldCreateDiagnosticSystem());
        Assertions.assertFalse(diagnosticSystemManager.isExtended());
    }

    @Test
    void commandLineDecisionExtended() {
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticSystemManager diagnosticSystemManager = new DiagnosticSystemManager(false, true, propertyConfiguration);

        Assertions.assertTrue(diagnosticSystemManager.shouldCreateDiagnosticSystem());
        Assertions.assertTrue(diagnosticSystemManager.isExtended());
    }

    @Test
    void propertyDecision() {
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC.getProperty())).thenReturn(true);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC_EXTENDED.getProperty())).thenReturn(false);

        DiagnosticSystemManager diagnosticSystemManager = new DiagnosticSystemManager(false, false, propertyConfiguration);

        Assertions.assertTrue(diagnosticSystemManager.shouldCreateDiagnosticSystem());
        Assertions.assertFalse(diagnosticSystemManager.isExtended());
    }

    @Test
    void propertyDecisionExtended() {
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC.getProperty())).thenReturn(false);
        Mockito.when(propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC_EXTENDED.getProperty())).thenReturn(true);

        DiagnosticSystemManager diagnosticSystemManager = new DiagnosticSystemManager(false, false, propertyConfiguration);

        Assertions.assertTrue(diagnosticSystemManager.shouldCreateDiagnosticSystem());
        Assertions.assertTrue(diagnosticSystemManager.isExtended());
    }

    @Test
    void noDiagnostic() {
        PropertyConfiguration propertyConfiguration = Mockito.mock(PropertyConfiguration.class);
        Mockito.when(propertyConfiguration.getValueOrDefault(Mockito.any())).thenReturn(false);

        DiagnosticSystemManager diagnosticSystemManager = new DiagnosticSystemManager(false, false, propertyConfiguration);

        Assertions.assertFalse(diagnosticSystemManager.shouldCreateDiagnosticSystem());
        Assertions.assertFalse(diagnosticSystemManager.isExtended());
    }

}