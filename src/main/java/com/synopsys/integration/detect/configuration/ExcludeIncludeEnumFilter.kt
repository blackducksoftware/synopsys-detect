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
package com.synopsys.integration.detect.configuration

import com.synopsys.integration.configuration.property.types.enumfilterable.All
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue
import com.synopsys.integration.configuration.property.types.enumfilterable.None
import com.synopsys.integration.configuration.property.types.enumfilterable.Value


class ExcludeIncludeEnumFilter<T>(val excluded: List<FilterableEnumValue<T>>, val included: List<FilterableEnumValue<T>>) {
    fun containsAll(list: List<FilterableEnumValue<T>>): Boolean {
        return list.any {
            when (it) {
                is All -> true
                else -> false
            }
        }
    }

    fun containsNone(list: List<FilterableEnumValue<T>>): Boolean {
        return list.any {
            when (it) {
                is None -> true
                else -> false
            }
        }
    }

    fun containsElement(list: List<FilterableEnumValue<T>>, value: T): Boolean {
        return list.any {
            when (it) {
                is Value -> it.value == value
                else -> false
            }
        }
    }

    fun willExclude(value: T): Boolean {
        if (containsAll(excluded)) {
            return true;
        } else if (containsNone(excluded)) {
            return false;
        } else {
            return containsElement(excluded, value);
        }
    }

    fun willInclude(value: T): Boolean {
        if (included.isEmpty()) {
            return true
        } else if (containsAll(included)) {
            return true;
        } else if (containsNone(included)) {
            return false;
        } else {
            return containsElement(included, value);
        }
    }

    fun shouldInclude(value: T): Boolean {
        return if (willExclude(value)) {
            false
        } else {
            willInclude(value)
        }
    }
}