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

import java.util.List;

import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumUtils;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;

public class ExcludeIncludeEnumFilter<T extends Enum<T>> {
    private final List<FilterableEnumValue<T>> excluded;
    private final List<FilterableEnumValue<T>> included;

    public ExcludeIncludeEnumFilter(List<FilterableEnumValue<T>> excluded, List<FilterableEnumValue<T>> included) {
        this.excluded = excluded;
        this.included = included;
    }

    private boolean willExclude(T value) {
        if (FilterableEnumUtils.containsAll(excluded)) {
            return true;
        } else if (FilterableEnumUtils.containsNone(excluded)) {
            return false;
        } else {
            return FilterableEnumUtils.containsValue(excluded, value);
        }
    }

    private boolean willInclude(T value) {
        if (included.isEmpty()) {
            return true;
        } else if (FilterableEnumUtils.containsAll(included)) {
            return true;
        } else if (FilterableEnumUtils.containsNone(included)) {
            return false;
        } else {
            return FilterableEnumUtils.containsValue(included, value);
        }
    }

    public boolean shouldInclude(T value) {
        if (willExclude(value)) {
            return false;
        } else {
            return willInclude(value);
        }
    }
}