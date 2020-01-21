/**
 * configuration
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
package com.synopsys.integration.configuration.deprecation

import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

//This is a simple 'Deprecation Aware' way to access values.
class DeprecationAware(private val propertyConfiguration: PropertyConfiguration) {
    fun <T> getValue(property: ValuedProperty<T>): T {
        return getValueOrNull(property) ?: property.default
    }

    fun <T> getValueOrNull(property: ValuedProperty<T>): T? {
        return if (propertyConfiguration.wasPropertyProvided(property)) {
            propertyConfiguration.getValue(property);
        } else {
            val replacement = property.replacesDeprecatedProperty
            if (replacement != null) {
                getValueOrNull(replacement)
            } else {
                null
            }
        }
    }

    fun <T> getValue(property: NullableProperty<T>): T? {
        return if (propertyConfiguration.wasPropertyProvided(property)) {
            propertyConfiguration.getValue(property);
        } else {
            val replacement = property.replacesDeprecatedProperty
            if (replacement != null) {
                getValue(replacement)
            } else {
                null
            }
        }
    }
}
