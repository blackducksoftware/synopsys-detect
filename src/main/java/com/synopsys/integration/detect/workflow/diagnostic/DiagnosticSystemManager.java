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
package com.synopsys.integration.detect.workflow.diagnostic;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;

public class DiagnosticSystemManager {
    private final boolean diagnosticExtended;
    private final boolean diagnostic;

    public DiagnosticSystemManager(boolean isDiagnosticArgument, boolean isExtendedArgument, PropertyConfiguration propertyConfiguration) {
        this.diagnostic = isDiagnosticArgument || propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC.getProperty());
        this.diagnosticExtended = isExtendedArgument || propertyConfiguration.getValueOrDefault(DetectProperties.DETECT_DIAGNOSTIC_EXTENDED.getProperty());
    }

    public boolean shouldCreateDiagnosticSystem() {
        return diagnostic || diagnosticExtended;
    }

    public boolean isExtended() {
        return diagnosticExtended;
    }
}
