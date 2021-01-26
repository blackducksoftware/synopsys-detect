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
package com.synopsys.integration.detect.configuration;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.blackduck.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.CustomFieldElement;

public class DetectCustomFieldParser {

    public CustomFieldDocument parseCustomFieldDocument(final Map<String, String> currentProperties) throws DetectUserFriendlyException {
        try {
            final ConfigurationPropertySource source = new MapConfigurationPropertySource(currentProperties);
            final Binder objectBinder = new Binder(source);
            final BindResult<CustomFieldDocument> fieldDocumentBinding = objectBinder.bind("detect.custom.fields", CustomFieldDocument.class);
            final CustomFieldDocument fieldDocument = fieldDocumentBinding.orElse(new CustomFieldDocument());
            fieldDocument.getProject().forEach(this::filterEmptyQuotes);
            fieldDocument.getVersion().forEach(this::filterEmptyQuotes);
            return fieldDocument;
        } catch (final Exception e) {
            throw new DetectUserFriendlyException("Unable to parse custom fields.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    public void filterEmptyQuotes(final CustomFieldElement element) {
        element.setValue(element.getValue().stream().filter(value -> !("\"\"".equals(value) || "''".equals(value))).collect(Collectors.toList()));
    }
}
