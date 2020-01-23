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
package com.synopsys.integration.configuration.property.types.enumfilterable


fun <T> List<FilterableEnumValue<T>>.containsNone(): Boolean {
    return this.any {
        when (it) {
            is None -> true
            else -> false
        }
    }
}

fun <T> List<FilterableEnumValue<T>>.containsAll(): Boolean {
    return this.any {
        when (it) {
            is All -> true
            else -> false
        }
    }
}

fun <T> List<FilterableEnumValue<T>>.containsValue(value: T): Boolean {
    return this.any {
        when (it) {
            is Value<*> -> it.value == value
            else -> false
        }
    }
}

fun <T> List<FilterableEnumValue<T>>.toValueList(clazz: Class<T>): List<T> {
    return this.flatMap {
        when (it) {
            is Value<*> -> listOf(clazz.cast(it.value))
            else -> emptyList()
        }
    }.toList()
}

fun <T> List<FilterableEnumValue<T>>.populatedValues(clazz: Class<T>): List<T> {
    if (this.containsNone()) {
        return emptyList()
    } else if (this.containsAll()) {
        return clazz.enumConstants.toList()
    } else {
        return this.toValueList(clazz)
    }
}