/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.help;

import java.util.List;

public class DetectOption extends DetectBaseOption {

    public DetectOption(String key, String fieldName, Class<?> valueType, boolean strictAcceptableValues, boolean caseSensitiveAcceptableValues, List<String> acceptableValues,
            DetectOptionHelp detectOptionHelp, String originalValue, String defaultValue, String resolvedValue) {
        super(key, fieldName, valueType, strictAcceptableValues, caseSensitiveAcceptableValues, acceptableValues, detectOptionHelp, originalValue, defaultValue, resolvedValue);
    }

    public OptionValidationResult isAcceptableValue(final String value) {
        Boolean isValueAcceptable = getAcceptableValues().stream()
                                            .anyMatch(it -> {
                                                if (getCaseSensistiveAcceptableValues()) {
                                                    return it.equals(value);
                                                } else {
                                                    return it.equalsIgnoreCase(value);
                                                }
                                            });
        OptionValidationResult result;
        if (isValueAcceptable) {
            result = new OptionValidationResult(true, "");
        } else {
            result = new OptionValidationResult(false, "unknown value");
        }
        return result;
    }

}
