/**
 * detect-common
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DetectListOption extends DetectOption {

    public DetectListOption(String key, String fieldName, Class<?> valueType, boolean strictAcceptableValues, boolean caseSensitiveAcceptableValues, List<String> acceptableValues,
            DetectOptionHelp detectOptionHelp, String originalValue, String defaultValue, String resolvedValue) {
        super(key, fieldName, valueType, strictAcceptableValues, caseSensitiveAcceptableValues, acceptableValues, detectOptionHelp, originalValue, defaultValue, resolvedValue);
    }

    public OptionValidationResult isAcceptableValue(final String value) {
        OptionValidationResult result;
        if (null != value) {
            String[] splitValues = value.split(",");
            List<String> badValues = new ArrayList<>();
            for (String splitValue : splitValues) {
                Boolean isValueAcceptable = getAcceptableValues().stream()
                                                    .anyMatch(it -> {
                                                        if (getCaseSensistiveAcceptableValues()) {
                                                            return it.equals(splitValue);
                                                        } else {
                                                            return it.equalsIgnoreCase(splitValue);
                                                        }
                                                    });
                if (!isValueAcceptable) {
                    badValues.add(splitValue);
                }
            }
            if (badValues.size() > 0) {
                result = new OptionValidationResult(false, String.format("unknown value(s): %s", StringUtils.join(badValues, ",")));
            } else {
                result = new OptionValidationResult(true, "");
            }
        } else {
            result = new OptionValidationResult(true, "");
        }
        return result;
    }

}
