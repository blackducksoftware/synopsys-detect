/**
 * configuration
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
package com.synopsys.integration.configuration.config;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.configuration.help.PropertyInfo;
import com.synopsys.integration.configuration.property.Property;

public class PropertyInfoCollector {
    private PropertyConfiguration propertyConfiguration;

    public PropertyInfoCollector(final PropertyConfiguration propertyConfiguration) {
        this.propertyConfiguration = propertyConfiguration;
    }

    public List<PropertyInfo> collectPropertyInfo(List<Property> knownProperties, Predicate<String> shouldMaskPropertyValue) {
        List<PropertyInfo> propertyValues = new LinkedList<>();
        for (Property property : knownProperties) {
            if (!propertyConfiguration.wasKeyProvided(property.getKey())) {
                continue;
            }

            String value = propertyConfiguration.getRaw(property).orElse("");
            String maskedValue = value;
            if (shouldMaskPropertyValue.test(value)) {
                maskedValue = StringUtils.repeat('*', maskedValue.length());
            }
            propertyValues.add(new PropertyInfo(property.getKey(), maskedValue, property));
        }
        return propertyValues;
    }

    public static Predicate<String> maskPasswordsAndTokensPredicate() {
        return propertyKey -> propertyKey.toLowerCase().contains("password") || propertyKey.toLowerCase().contains("api.token") || propertyKey.toLowerCase().contains("access.token");
    }
}
