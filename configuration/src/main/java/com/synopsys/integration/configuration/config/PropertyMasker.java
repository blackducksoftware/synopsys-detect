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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

public class PropertyMasker {
    public Map<String, String> maskRawValues(Map<String, String> rawValues, Predicate<String> shouldMask) {
        Map<String, String> masked = new HashMap<>();
        for (Map.Entry<String, String> rawKeyValue : rawValues.entrySet()) {
            masked.put(rawKeyValue.getKey(), maskValue(rawKeyValue.getKey(), rawKeyValue.getValue(), shouldMask));
        }
        return masked;
    }

    public String maskValue(String rawKey, String rawValue, Predicate<String> shouldMask) {
        String maskedValue = rawValue;
        if (shouldMask.test(rawKey)) {
            maskedValue = StringUtils.repeat('*', maskedValue.length());
        }
        return maskedValue;
    }
}
