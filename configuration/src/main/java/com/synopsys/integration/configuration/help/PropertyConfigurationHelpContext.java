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
package com.synopsys.integration.configuration.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.config.PropertyMap;
import com.synopsys.integration.configuration.config.PropertyMasker;
import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.base.TypedProperty;

//The idea is that this is here to help you log information about a particular property configuration with particular things you want to express.
//  For example you may want to log deprecation warning when a particular property is set.
//  For example you may want to THROW when a particular deprecated property is set.
//  For example you may want to log when an invalid value is set.
//  For example you may want to THROW when an invalid value is set.
//  For example you may want to log help about specific properties.
//  For example you may want to search properties by key and log help.

//Maybe split into 'ValueContext' and a 'HelpContext' 
public class PropertyConfigurationHelpContext {

    private static final Map<String, String> knownSourceDisplayNames;

    static {
        Map<String, String> initializing = new HashMap<>();
        initializing.put("configurationProperties", "cfg");
        initializing.put("systemEnvironment", "env");
        initializing.put("commandLineArgs", "cmd");
        initializing.put("systemProperties", "jvm");
        knownSourceDisplayNames = Collections.unmodifiableMap(initializing);
    }

    private final PropertyConfiguration propertyConfiguration;

    public PropertyConfigurationHelpContext(final PropertyConfiguration propertyConfiguration) {
        this.propertyConfiguration = propertyConfiguration;
    }

    public void printCurrentValues(Consumer<String> logger, PropertyMap<String> maskedRawPropertyValues, Map<String, String> additionalNotes) {
        List<String> sortedPropertyKeys = sortPropertyMapKeys(maskedRawPropertyValues);
        printKnownCurrentValues(logger, sortedPropertyKeys, maskedRawPropertyValues, additionalNotes);
    }

    public void printKnownCurrentValues(Consumer<String> logger, List<String> knownPropertyKeys, PropertyMap<String> maskedRawPropertyValues, Map<String, String> additionalNotes) {
        logger.accept("");
        logger.accept("Current property values:");
        logger.accept("--property = value [notes]");
        logger.accept(StringUtils.repeat("-", 60));

        Map<String, String> maskedRawPropertyValuesMap = maskedRawPropertyValues.getMap();
        knownPropertyKeys.stream().forEach(propertyKey -> {
            String rawMaskedValue = maskedRawPropertyValuesMap.get(propertyKey);
            if (rawMaskedValue != null) {
                String sourceName = propertyConfiguration.getPropertySource(propertyKey).orElse("unknown");
                String sourceDisplayName = knownSourceDisplayNames.getOrDefault(sourceName, sourceName);

                String notes = additionalNotes.getOrDefault(propertyKey, "");

                logger.accept(propertyKey + " = " + rawMaskedValue + " [" + sourceDisplayName + "] " + notes);
            }
        });

        logger.accept(StringUtils.repeat("-", 60));
        logger.accept("");
    }

    public void printPropertyErrors(Consumer<String> logger, PropertyMap<List<String>> errors) {
        List<String> sortedErroryKeys = sortPropertyMapKeys(errors);
        printKnownPropertyErrors(logger, sortedErroryKeys, errors);
    }

    public void printKnownPropertyErrors(Consumer<String> logger, List<String> knownPropertyKeys, PropertyMap<List<String>> errors) {
        Map<String, List<String>> errorsMap = errors.getMap();
        knownPropertyKeys.stream()
            .filter(propertyKey -> errorsMap.containsKey(propertyKey))
            .forEach(propertyKey -> {
                logger.accept(StringUtils.repeat("=", 60));
                List<String> propertyErrors = errorsMap.get(propertyKey);
                int errorCount = propertyErrors.size();
                String header = String.format("%s (%s)", pluralize("ERROR", "ERRORS", errorCount), errorCount);
                logger.accept(header);
                propertyErrors.forEach(errorMessage -> logger.accept(propertyKey + ": " + errorMessage));
            });
    }

    private <T> List<String> sortPropertyMapKeys(PropertyMap<T> propertyMap) {
        return propertyMap.getKeys().stream()
                   .sorted()
                   .collect(Collectors.toList());
    }

    public String pluralize(String singular, String plural, Integer number) {
        if (number == 1) {
            return singular;
        } else {
            return plural;
        }
    }

    public Map<String, List<String>> findPropertyParseErrors(List<Property> knownProperties) {
        Map<String, List<String>> exceptions = new HashMap<>();
        for (Property property : knownProperties) {
            if (property.getClass().isAssignableFrom(TypedProperty.class)) { // TODO: Can we do this without reflection?
                Optional<ValueParseException> exception = propertyConfiguration.getPropertyException((TypedProperty) property);
                if (exception.isPresent()) {
                    List<String> propertyExceptions = exceptions.getOrDefault(property.getKey(), new ArrayList<>());
                    if (StringUtils.isNotBlank(exception.get().getMessage())) {
                        propertyExceptions.add(exception.get().getMessage());
                    } else {
                        propertyExceptions.add(exception.get().toString());
                    }
                    exceptions.put(property.getKey(), propertyExceptions);
                }
            }
        }
        return exceptions;
    }

}