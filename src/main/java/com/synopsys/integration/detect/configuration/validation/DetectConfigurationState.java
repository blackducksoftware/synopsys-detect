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
package com.synopsys.integration.detect.configuration.validation;

import java.util.List;
import java.util.Map;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;

public class DetectConfigurationState {
    private final Map<String, String> additionalNotes;
    private final Map<String, List<String>> deprecationMessages;
    private final PropertyConfigurationHelpContext detectConfigurationReporter;
    private final boolean hasNotUsedFailureProperties;

    public DetectConfigurationState(PropertyConfiguration detectConfiguration, Map<String, String> additionalNotes, Map<String, List<String>> deprecationMessages, boolean hasNotUsedFailureProperties) {
        this.additionalNotes = additionalNotes;
        this.deprecationMessages = deprecationMessages;
        this.hasNotUsedFailureProperties = hasNotUsedFailureProperties;
        this.detectConfigurationReporter = new PropertyConfigurationHelpContext(detectConfiguration);
    }

    public Map<String, String> getAdditionalNotes() {
        return additionalNotes;
    }

    public Map<String, List<String>> getDeprecationMessages() {
        return deprecationMessages;
    }

    public PropertyConfigurationHelpContext getDetectConfigurationReporter() {
        return detectConfigurationReporter;
    }

    public boolean hasNotUsedFailureProperties() {
        return hasNotUsedFailureProperties;
    }

}