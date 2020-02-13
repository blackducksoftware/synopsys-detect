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
package com.synopsys.integration.detect

import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty
import java.util.*

/**
 * Will get the first property in a list that was provided by the user.
 */
fun <T> PropertyConfiguration.getFirstProvidedValueOrEmpty(vararg properties: NullableProperty<T>): Optional<T> {
    for (property in properties) {
        if (this.wasPropertyProvided(property)) {
            return this.getValue(property)
        }
    }

    return Optional.empty()
}

/**
 * Will get the first property in a list that was provided by the user.
 * If no property was provided, the default value of the first property will be used.
 */
fun <T> PropertyConfiguration.getFirstProvidedValueOrDefault(vararg properties: ValuedProperty<T>): T {
    return getFirstProvidedValueOrEmpty(*properties).orElse(properties.first().defaultValue)
}

/**
 * Will get the first property in a list that was provided by the user.
 * If no property was provided, the default will NOT be used.
 */
fun <T> PropertyConfiguration.getFirstProvidedValueOrEmpty(vararg properties: ValuedProperty<T>): Optional<T> {
    for (property in properties) {
        if (this.wasPropertyProvided(property)) {
            return Optional.of(this.getValue(property))
        }
    }

    return Optional.empty();
}